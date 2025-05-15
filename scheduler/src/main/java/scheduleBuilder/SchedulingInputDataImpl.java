package scheduleBuilder;

import constraint.OptionalConstraintsSettings;
import constraint.OptionalSchedulingConstraint;
import course.Course;
import course.CourseInProgram;
import group.Group;
import lesson.Lesson;
import lesson.LessonImpl;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import logger.MyLogger;
import pair.Pair;
import person.Teacher;
import place.Place;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.occupationSynchonizer.OccupationSynchronizer;
import scheduleBuilder.occupationSynchonizer.OccupationSynchronizerImpl;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedulingInputDataImpl implements SchedulingInputData {
    private final OccupationSynchronizer _occupationSynchronizer = new OccupationSynchronizerImpl();
    private final OptionalConstraintsSettings _optionalConstraintsSettings;
    private final List<WeeklyTimeSlot> _timeslotSequence;
    private final List<Teacher> _teachers;
    private final List<Group> _groups;
    private final List<Place> _places;
    private final List<LessonRequest> _lessonRequests;
    private final Map<DayOfWeek, List<WeeklyTimeSlot>> _dayOfWeekTimeSlots;
    private final Map<Teacher, Integer> _teacherMaxDaysMap;
    private final Map<Group, Map<DayOfWeek, Integer>> _groupDailyMaxLoad;
    // TEMP MAPS
    private final Map<Teacher, Set<WeeklyTimeSlot>> _teacherAvailableTimeSlots;
    private final Map<Teacher, Set<DayOfWeek>> _teacherScheduledDays;
    private final Map<Group, Set<WeeklyTimeSlot>> _groupAvailableTimeSlots;
    private final Map<Place, Set<WeeklyTimeSlot>> _placeAvailableTimeSlots;

    private final Map<LessonRequest, Set<WeeklyTimeSlot>> _lessonRequestsAvailableTimeSlots;
    private final Map<LessonRequestOccupation, Integer> _unscheduledLessonRequestOccupations;
    private final Map<WeeklyTimeSlot, Map<LessonRequestOccupation, Set<Place>>> _occupationsWithDefinedTimes = new ConcurrentHashMap<>();
    private final Set<Lesson> _scheduledLessons = new HashSet<>();


    public SchedulingInputDataImpl(OptionalConstraintsSettings optionalConstraintsSettings,
                                   List<WeeklyTimeSlot> timeslotSequence,
                                   List<Teacher> teachers,
                                   List<LessonRequest> lessonRequests,
                                   List<Group> groups,
                                   List<Place> places,
                                   ScheduleConstraintsAccumulator constraintsAccumulator) {
        _optionalConstraintsSettings = optionalConstraintsSettings;
        _timeslotSequence = timeslotSequence;
        _teachers = teachers;
        _teacherScheduledDays = createTeacherScheduledDays();
        _groups = groups;
        _places = places;
        _lessonRequests = lessonRequests;
        _placeAvailableTimeSlots = createPlacesAvailableTimeSlots();
        _unscheduledLessonRequestOccupations = createUnscheduledLessonRequestOccupationMap(lessonRequests.stream());
        _dayOfWeekTimeSlots = getDayOfWeekTimeslotsMap(timeslotSequence.stream());
        _teacherMaxDaysMap = createTeacherMaxDaysMap(constraintsAccumulator);
        _groupDailyMaxLoad = createGroupDailyMaxLoadMap(constraintsAccumulator);
        _teacherAvailableTimeSlots = createInitialTeacherAvailableTimeslotsMap(constraintsAccumulator);
        _groupAvailableTimeSlots = createInitialGroupAvailableTimeslotsMap(constraintsAccumulator);
        _lessonRequestsAvailableTimeSlots = createInitialLessonRequestsAvailableTimeslotsMap(lessonRequests.stream());
        clarifyTeachersAvailableTimeSlotMap();  // TODO WE CAN CONSIDER TEACHERS WITH JOINT LESSONS MORE CAREFULLY
        clarifyGroupsAvailableTimeSlotMap();
        checkTeachersLoad();
        checkGroupsLoad();


    }

    private Map<Teacher, Set<DayOfWeek>> createTeacherScheduledDays() {
        return _teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher -> new HashSet<>()));
    }

    private Map<Place, Set<WeeklyTimeSlot>> createPlacesAvailableTimeSlots() {
        return _places
                .stream()
                .collect(Collectors.toConcurrentMap(place -> place, place -> new HashSet<>(_timeslotSequence)));
    }

    private Map<LessonRequestOccupation, Integer> createUnscheduledLessonRequestOccupationMap(Stream<LessonRequest> stream) {
        return stream
                .flatMap(LessonRequest::getOccupations)
                .collect(Collectors.toConcurrentMap(occupation -> occupation, occupation -> occupation.getLessonRequest().getCourseInProgram().getLessonsPerWeek()*(int)occupation.getOccupiedTeachers().count()));
    }

    private void checkGroupsLoad() {
        _groups.forEach(this::checkGroupLoad);
    }

    private void checkTeachersLoad() {
        _teachers.forEach(this::checkTeacherLoad);
    }

    private void checkGroupLoad(Group group) {
        int numSlots = _groupAvailableTimeSlots.get(group).size();
        int workload = getLessonRequestsForGroup(group)
                .map(LessonRequest::getCourseInProgram)
                .mapToInt(CourseInProgram::getLessonsPerWeek)
                .sum();

        if(numSlots <= workload) {
            if(numSlots < workload) {
                getLessonRequestsForGroup(group)
                        .forEach(lr -> lr.getCourseInProgram().getCourses()
                                .map(course -> "" + lr.getGroup() + "\t" + course.getName() + "\t" + lr.getCourseInProgram().getLessonsPerWeek())
                                .forEach(MyLogger::warn)
                        );
            }
        }
    }

    private Stream<LessonRequest> getLessonRequestsForGroup(Group group) {
        return getUnscheduledRequestsForGroup(group)
                .map(Pair::getKey)
                .map(LessonRequestOccupation::getLessonRequest)
                .distinct();
    }

    private void checkTeacherLoad(Teacher teacher) {
        int numSlots = _teacherAvailableTimeSlots.get(teacher).size();
        int workload = getLessonRequestsForTeacher(teacher)
                .map(LessonRequest::getCourseInProgram)
                .mapToInt(CourseInProgram::getLessonsPerWeek)
                .sum();
        if(numSlots <= workload) {

            if(numSlots < workload) {
                getLessonRequestsForTeacher(teacher)
                        .forEach(lr -> lr.getCourseInProgram().getCourses()
                                .map(course -> "" + lr.getGroup() + "\t" + course.getName() + "\t" + lr.getCourseInProgram().getLessonsPerWeek())
                                .forEach(MyLogger::warn)
                        );
            }
        }
    }

    private Stream<LessonRequest> getLessonRequestsForTeacher(Teacher teacher) {
        return getUnscheduledRequestsForTeacher(teacher)
                .map(Pair::getKey)
                .map(LessonRequestOccupation::getLessonRequest)
                .distinct();
    }

    private void clarifyGroupsAvailableTimeSlotMap() {
        _groups.forEach(group -> {
            var availableSlots = _groupAvailableTimeSlots.get(group);
            Set<WeeklyTimeSlot> newAvailableSlots = availableSlots
                    .stream()
                    .filter(timeSlot -> _lessonRequestsAvailableTimeSlots.keySet().stream()
                            .anyMatch(lr -> lr.getGroup().equals(group)))
                    .collect(Collectors.toSet());
            _groupAvailableTimeSlots.replace(group, newAvailableSlots);
        });
    }

    private void clarifyTeachersAvailableTimeSlotMap() {
        _teachers.forEach(teacher -> {
            var availableSlots = _teacherAvailableTimeSlots.get(teacher);
            var availableSlotsForTeachersRequests = getUnscheduledRequestsForTeacher(teacher)
                    .filter(pair -> pair.getValue() > 0)
                    .map(Pair::getKey)
                    .map(LessonRequestOccupation::getLessonRequest)
                    .distinct()
                    .flatMap(this::getAvailableSlotsForLessonRequest)
                    .collect(Collectors.toSet());
            Set<WeeklyTimeSlot> newAvailableSlots = availableSlots
                    .stream()
                    .filter(availableSlotsForTeachersRequests::contains)
                    .collect(Collectors.toSet());
            _teacherAvailableTimeSlots.replace(teacher, newAvailableSlots);
        });
    }

    private Map<LessonRequest, Set<WeeklyTimeSlot>> createInitialLessonRequestsAvailableTimeslotsMap(Stream<LessonRequest> lessonRequestsStream) {
        return  lessonRequestsStream
                .collect(Collectors.toConcurrentMap(lr -> lr, lr -> getAvailableTimeslotsForLessonRequest(lr).collect(Collectors.toSet())));
    }


    private Stream<WeeklyTimeSlot> getAvailableTimeslotsForLessonRequest(LessonRequest lessonRequest) {
        final Set<Teacher> teachers = lessonRequest.getOccupations()
                .flatMap(LessonRequestOccupation::getOccupiedTeachers)
                .collect(Collectors.toSet());
        Group group = lessonRequest.getGroup();

        return _groupAvailableTimeSlots.get(group)
                .stream()
                .filter(ts -> teachers.stream()
                        .allMatch(teacher -> ifTimeSlotAvailableForTeacher(teacher, ts)));
    }

    private boolean ifTimeSlotAvailableForTeacher(Teacher teacher, WeeklyTimeSlot timeSlot) {
        return _teacherAvailableTimeSlots.get(teacher).contains(timeSlot);
    }


    private Map<Group, Set<WeeklyTimeSlot>> createInitialGroupAvailableTimeslotsMap(ScheduleConstraintsAccumulator constraintsAccumulator) {
        return  _groups
                .stream()
                .collect(Collectors.toConcurrentMap(group -> group, group -> {
                    var ftConstraintOpt = constraintsAccumulator.getForbiddenSlotsForGroup(group);
                    Predicate<WeeklyTimeSlot> predicate =  ftConstraintOpt.isPresent() ? ftConstraintOpt.get()::isNotForbidden : ts -> true;
                    return _timeslotSequence
                            .stream()
                            .filter(predicate)
                            .collect(Collectors.toSet());
                }));
    }

    private Map<Teacher, Set<WeeklyTimeSlot>> createInitialTeacherAvailableTimeslotsMap(final ScheduleConstraintsAccumulator constraintsAccumulator) {
        return  _teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher -> {
                    var ftConstraintOpt = constraintsAccumulator.getForbiddenSlotsForTeacher(teacher);
                    Predicate<WeeklyTimeSlot> predicate =  ftConstraintOpt.isPresent() ? ftConstraintOpt.get()::isNotForbidden : ts -> true;
                    return _timeslotSequence
                            .stream()
                            .filter(predicate)
                            .collect(Collectors.toSet());
                }));
    }

    private Map<Group, Map<DayOfWeek, Integer>> createGroupDailyMaxLoadMap(ScheduleConstraintsAccumulator constraintsAccumulator) {
        return _groups
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher,
                        group -> constraintsAccumulator.getDailyMaxLoadsForGroup(group)
                                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue))));
    }

    private Map<Teacher, Integer> createTeacherMaxDaysMap(ScheduleConstraintsAccumulator constraintsAccumulator) {
        return _teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher -> {
                    var ndc =  constraintsAccumulator.getNumDaysConstraintForTeacher(teacher);
                    return ndc.orElse(_dayOfWeekTimeSlots.keySet().size());
                }));

    }


    private Map<DayOfWeek, List<WeeklyTimeSlot>> getDayOfWeekTimeslotsMap(Stream<WeeklyTimeSlot> timeSlots) {
        return timeSlots
                .collect(Collectors.groupingBy(WeeklyTimeSlot::getDayOfWeek)).entrySet()
                .stream()
                .map(entry -> {
                    return new Pair<>(entry.getKey(), entry.getValue()
                            .stream()
                            .sorted(WeeklyTimeSlot::compareTo).toList());
                })
                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue));
    }


    @Override
    public Stream<Teacher> getTeachers() {
        return _teachers.stream();
    }

    @Override
    public Stream<Group> getGroups() {
        return _groups.stream();
    }

    @Override
    public Stream<Place> getPlaces() {
        return _places.stream();
    }

    @Override
    public Stream<WeeklyTimeSlot> getOccupiedTimeSlotsForPlace(Place place) {
        var availableSet = _placeAvailableTimeSlots.get(place);
        return _timeslotSequence
                .stream()
                .filter(timeSlot -> !availableSet.contains(timeSlot));
    }

    @Override
    public Stream<WeeklyTimeSlot> getOccupiedTimeSlotsForGroup(Group group) {
        var availableSet = _groupAvailableTimeSlots.get(group);
        return _timeslotSequence
                .stream()
                .filter(timeSlot -> !availableSet.contains(timeSlot));
    }

    @Override
    public Stream<WeeklyTimeSlot> getOccupiedTimeSlotsForTeacher(Teacher teacher) {
        var availableSet = _teacherAvailableTimeSlots.get(teacher);
        return _timeslotSequence
                .stream()
                .filter(timeSlot -> !availableSet.contains(timeSlot));
    }

    @Override
    public Stream<WeeklyTimeSlot> getAvailableSlotsForLessonRequest(LessonRequest lessonRequest) {
        Group group = lessonRequest.getGroup();
        return  _groupAvailableTimeSlots.get(group)
                .stream()
                .filter(timeSlot -> lessonRequest.getOccupations()
                        .allMatch( occupation -> occupation.getOccupiedTeachers()
                        .allMatch(teacher -> ifTimeSlotAvailableForTeacher(teacher, timeSlot)))
                )
                .filter(timeSlot -> lessonRequest.getOccupations()
                        .allMatch(occupation -> occupation.getCandidatePlaces()
                        .anyMatch(place -> _placeAvailableTimeSlots.get(place).contains(timeSlot)))
                )
                .filter(_lessonRequestsAvailableTimeSlots.get(lessonRequest)::contains);
    }


    @Override
    public Stream<Pair<LessonRequestOccupation, Integer>> getUnscheduledRequestsForTeacher(Teacher teacher) {
        return _unscheduledLessonRequestOccupations.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getOccupiedTeachers().anyMatch(teacher::equals))
                .map(entry -> {
                    var occupation = entry.getKey();
                    int numTeachers = (int)occupation.getOccupiedTeachers().count();
                    return new Pair<>(occupation, entry.getValue()/numTeachers);
                });
    }

    @Override
    public Stream<Pair<LessonRequestOccupation, Integer>> getUnscheduledRequestsForGroup(Group group) {
        return _unscheduledLessonRequestOccupations.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getLessonRequest().getGroup().equals(group))
                .map(entry -> {
                    var occupation = entry.getKey();
                    int numTeachers = (int)occupation.getOccupiedTeachers().count();
                    return new Pair<>(occupation, entry.getValue()/(numTeachers+1));
                });
    }

    @Override
    public Stream<Lesson> getScheduledLessonsForTeacher(Teacher teacher) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getTeachers().anyMatch(teacher::equals));
    }

    @Override
    public Stream<Lesson> getScheduledLessonsForGroup(Group group) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getGroup().equals(group));
    }

    @Override
    public Stream<Lesson> getScheduleLessonsForPlace(Place place) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getPlace().equals(place));
    }

    @Override
    public void scheduleLesson(Lesson lesson) {
        // OCCUPY
        WeeklyTimeSlot timeSlot = lesson.getTimeSlot();

        Group group = lesson.getGroup();
        occupyGroup(group, timeSlot);
        Place place = lesson.getPlace();
        occupyPlace(place, timeSlot);

        _scheduledLessons.add(lesson);

        Optional<LessonRequestOccupation> lessonRequestOccupationOpt  = getRelatedOccupation(lesson);
        if(lessonRequestOccupationOpt.isPresent()){
            LessonRequestOccupation lessonRequestOccupation = lessonRequestOccupationOpt.get();
            lessonRequestOccupation.getOccupiedTeachers().forEach(teacher -> occupyTeacher(teacher, timeSlot));
            updateToScheduleStructuresByScheduling(lessonRequestOccupation, timeSlot);

            updateStructuresByRelatedConstraints(lessonRequestOccupation, place, timeSlot);
        }
    }



    private Optional<LessonRequestOccupation> getRelatedOccupation(Lesson lesson) {
        Optional<Teacher> teacher = lesson.getTeachers().findAny();
        return _unscheduledLessonRequestOccupations.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .filter(lro -> lro.getCourse().equals(lesson.getCourse()))
                .filter(lro -> lro.getLessonRequest().getGroup().equals(lesson.getGroup()))
                .filter(lro -> teacher.map(value ->
                        lro.getOccupiedTeachers().anyMatch(value::equals)).orElse(true)
                )
                .findAny();
    }

    private void updateStructuresByRelatedConstraints(LessonRequestOccupation lessonRequestOccupation, Place place, WeeklyTimeSlot timeSlot) {
        updateByTeacherMaxDaysInWeekConstraint(lessonRequestOccupation, timeSlot);
        Group group = lessonRequestOccupation.getLessonRequest().getGroup();
        updateGroupByMaxDailyLoadConstraint(group, timeSlot);
        LessonRequest lessonRequest = lessonRequestOccupation.getLessonRequest();
        updateLessonByDailyContinuityAndMaxNumberConstraint(lessonRequest, timeSlot);
        updateLessonInPlaceBySelectedPlace(lessonRequestOccupation, place);
    }

    private void updateLessonInPlaceBySelectedPlace(LessonRequestOccupation lessonRequestOccupation, Place place) {
        //TODO
    }

    private void updateLessonByDailyContinuityAndMaxNumberConstraint(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot) {
        int maxLessonsPerDay = lessonRequest.getCourseInProgram().getMaxLessonsPerDay();
        DayOfWeek dayOfWeek = timeSlot.getDayOfWeek();
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        int currentId = dayTimeSlotSequence.indexOf(timeSlot);

        int forbidBefore = currentId - maxLessonsPerDay + 1;
        forbidLessonRequestBefore(lessonRequest, dayOfWeek, forbidBefore);

        int forbidAfter = currentId + maxLessonsPerDay-1;
        forbidLessonRequestAfter(lessonRequest, dayOfWeek, forbidAfter);

    }

    private void forbidLessonRequestBefore(LessonRequest lessonRequest, DayOfWeek dayOfWeek, int forbidBefore) {
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        var availableSet = _lessonRequestsAvailableTimeSlots.get(lessonRequest);
        for(int i=0; i < forbidBefore; i++) {
            availableSet.remove(dayTimeSlotSequence.get(i));
        }
    }
    private void forbidLessonRequestAfter(LessonRequest lessonRequest, DayOfWeek dayOfWeek, int forbidAfter) {
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        var availableSet = _lessonRequestsAvailableTimeSlots.get(lessonRequest);
        for(int i=forbidAfter+1; i < dayTimeSlotSequence.size(); i++) {
            availableSet.remove(dayTimeSlotSequence.get(i));
        }
    }


    private void updateGroupByMaxDailyLoadConstraint(Group group, WeeklyTimeSlot timeSlot) {
        if(_optionalConstraintsSettings.ifHard(OptionalSchedulingConstraint.MAX_DAILY_LOAD_GROUP)) {
            DayOfWeek dayOfWeek = timeSlot.getDayOfWeek();
            int maxLoad = _groupDailyMaxLoad.get(group).get(dayOfWeek);
            OptionalInt firstLessonOpt = getGroupFirstScheduledLessonOfDay(group, dayOfWeek);
            if(firstLessonOpt.isPresent()) {
                int first = firstLessonOpt.getAsInt();
                int last = getGroupLastScheduledLessonOfDay(group, dayOfWeek).getAsInt();
                int forbidBefore = last - maxLoad+1;
                forbidGroupLessonsBefore(group, dayOfWeek, forbidBefore);
                int forbidAfter = first + maxLoad-1;
                forbidGroupLessonsAfter(group, dayOfWeek, forbidAfter);
            }

        }

    }

    private void forbidGroupLessonsBefore(Group group, DayOfWeek dayOfWeek, int forbidBefore) {
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        for(int i=0; i < forbidBefore; i++) {
            WeeklyTimeSlot currentTimeSlot = dayTimeSlotSequence.get(i);
            occupyGroup(group, currentTimeSlot);
        }
    }

    private void forbidGroupLessonsAfter(Group group, DayOfWeek dayOfWeek, int forbidAfter) {
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        for(int i=forbidAfter+1; i < dayTimeSlotSequence.size(); i++) {
            WeeklyTimeSlot currentTimeSlot = dayTimeSlotSequence.get(i);
            occupyGroup(group, currentTimeSlot);
        }
    }

    private OptionalInt getGroupLastScheduledLessonOfDay(Group group, DayOfWeek dayOfWeek) {
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getGroup().equals(group))
                .map(Lesson::getTimeSlot)
                .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                .mapToInt(dayTimeSlotSequence::indexOf)
                .max();
    }

    private OptionalInt getGroupFirstScheduledLessonOfDay(Group group, DayOfWeek dayOfWeek) {
        List<WeeklyTimeSlot> dayTimeSlotSequence = _dayOfWeekTimeSlots.get(dayOfWeek);
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getGroup().equals(group))
                .map(Lesson::getTimeSlot)
                .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                .mapToInt(dayTimeSlotSequence::indexOf)
                .min();
    }

    private void updateByTeacherMaxDaysInWeekConstraint(LessonRequestOccupation lessonRequestOccupation, WeeklyTimeSlot timeSlot) {
        if(_optionalConstraintsSettings.ifHard(OptionalSchedulingConstraint.MAX_DAYS_PER_TEACHER)) {
            lessonRequestOccupation.getOccupiedTeachers()
                            .forEach(teacher -> {
                                int maxDays = _teacherMaxDaysMap.get(teacher);
                                Set<DayOfWeek> scheduledDays = _teacherScheduledDays.get(teacher);
                                if(scheduledDays.size() == maxDays) {
                                    forbidDaysForTeacherByPredicate(teacher, day -> !scheduledDays.contains(day));
                                    //makeCascadeUpdateByTeachersTimeSlots(teacher);
                                    //TODO WHAT REMOVING OF TEACHER'S AVAILABLE TIME SLOTS CAN IMPLY?
                                    //1. FIXATION OF COURSES TIME SLOTS FOR GROUPS
                                }
                            });

        }
    }

    private void forbidDaysForTeacherByPredicate(Teacher teacher, Predicate<DayOfWeek> predicate) {
        _teacherAvailableTimeSlots.get(teacher).removeIf(timeSlot -> predicate.test(timeSlot.getDayOfWeek()));
    }

    private void updateToScheduleStructuresByScheduling(LessonRequestOccupation lessonRequestOccupation, WeeklyTimeSlot timeSlot) {
        int numOccupationsToSchedule = _unscheduledLessonRequestOccupations.get(lessonRequestOccupation);
        if(numOccupationsToSchedule > 1) {
            _unscheduledLessonRequestOccupations.replace(lessonRequestOccupation, numOccupationsToSchedule-1);
        } else {
            _unscheduledLessonRequestOccupations.remove(lessonRequestOccupation);
        }
        LessonRequest lessonRequest = lessonRequestOccupation.getLessonRequest();
        if(lessonRequest.getOccupationsNum() > 1) {
            addOccupationSynchronization(lessonRequestOccupation, timeSlot);
        }
    }

    private void addOccupationSynchronization(LessonRequestOccupation lessonRequestOccupation, WeeklyTimeSlot timeSlot) {
        _occupationSynchronizer.addLessonRequestSynchronization(lessonRequestOccupation,timeSlot);
        LessonRequest lessonRequest = lessonRequestOccupation.getLessonRequest();
        removeFormOccupationWithDefinedTimesMap(lessonRequestOccupation, timeSlot);

        lessonRequest.getOccupations()
                .filter(occupation -> !occupation.equals(lessonRequestOccupation))
                .filter(occupation -> !ifOccupationScheduledInTimeSlot(occupation, timeSlot))
                .forEach(occupation -> addToOccupationWithDefinedTimesMap(occupation,timeSlot));
    }

    private void addToOccupationWithDefinedTimesMap(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot) {
        _occupationsWithDefinedTimes.putIfAbsent(timeSlot, new HashMap<>());
        var map = _occupationsWithDefinedTimes.get(timeSlot);
        var placesSet = getAvailablePlacesForOccupationInTimeSlot(occupation, timeSlot).collect(Collectors.toSet());
        map.put(occupation, placesSet);
    }

    private boolean ifOccupationScheduledInTimeSlot(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getTimeSlot().equals(timeSlot))
                .filter(lesson -> lesson.getCourse().equals(occupation.getCourse()))
                .anyMatch(lesson -> lesson.getGroup().equals(occupation.getLessonRequest().getGroup()));
    }

    private void removeFormOccupationWithDefinedTimesMap(LessonRequestOccupation lessonRequestOccupation, WeeklyTimeSlot timeSlot) {
        if(!_occupationsWithDefinedTimes.containsKey(timeSlot)) {
            return;
        }
        var map = _occupationsWithDefinedTimes.get(timeSlot);
        map.remove(lessonRequestOccupation);
    }


    private void occupyTeacher(Teacher teacher, WeeklyTimeSlot timeSlot) {
        _teacherAvailableTimeSlots.get(teacher).remove(timeSlot);
        _teacherScheduledDays.get(teacher).add(timeSlot.getDayOfWeek());
    }

    private void occupyGroup(Group group, WeeklyTimeSlot timeSlot) {
        _groupAvailableTimeSlots.get(group).remove(timeSlot);
    }

    private void occupyPlace(Place place, WeeklyTimeSlot timeSlot) {
        _placeAvailableTimeSlots.get(place).remove(timeSlot);
        triggerMandatoryOccupationByDefinedPlace(place, timeSlot);
        triggerTeacherAvailableTimeslotsModification(place, timeSlot);
    }

    private void triggerTeacherAvailableTimeslotsModification(Place place, WeeklyTimeSlot timeSlot) {
        //TODO
    }

    private void triggerMandatoryOccupationByDefinedPlace(Place place, WeeklyTimeSlot timeSlot) {
        var lesReqPlaceMap = _occupationsWithDefinedTimes.get(timeSlot);
        if(lesReqPlaceMap != null) {
            lesReqPlaceMap.values()
                    .forEach(set -> set.remove(place));
            Map<LessonRequestOccupation, Place> triggeredOccupations = lesReqPlaceMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().size() == 1)
                    .collect(Collectors.toConcurrentMap(Map.Entry::getKey, entry -> entry.getValue().stream().findAny().get()));
            triggeredOccupations.keySet().forEach(lesReqPlaceMap::remove);
            triggeredOccupations.forEach(((occupation, occupationPlace) -> scheduleOccupation(occupation, occupationPlace, timeSlot)));
        }
    }

    private void scheduleOccupation(LessonRequestOccupation occupation, Place place, WeeklyTimeSlot timeSlot) {
        Group group = occupation.getLessonRequest().getGroup();
        Course course = occupation.getCourse();
        occupation
                .getOccupiedTeachers()
                .map(teacher -> new LessonImpl(timeSlot,group,place, teacher,course))
                .forEach(this::scheduleLesson);
    }

    @Override
    public void forbidLessonRequest(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot) {
        var set = _lessonRequestsAvailableTimeSlots.get(lessonRequest);
        set.remove(timeSlot);
    }

    @Override
    public Stream<Place> getAvailablePlacesForOccupationInTimeSlot(LessonRequestOccupation lessonRequestOccupation, WeeklyTimeSlot timeSlot) {
        boolean ifSamePlaceForLessons = _optionalConstraintsSettings.ifHard(OptionalSchedulingConstraint.LESSON_SAME_PLACES);
        if(ifSamePlaceForLessons) {
            Optional<Place> scheduledPlace = getLessonsRelatedToOccupation(lessonRequestOccupation)
                    .findAny()
                    .map(Lesson::getPlace);
            if(scheduledPlace.isPresent()) {
                Place place = scheduledPlace.get();
                return ifPlaceAvailableInTimeSlot(place, timeSlot) ? Stream.of(place) : Stream.empty();
            }
        }

        return lessonRequestOccupation
                .getCandidatePlaces()
                .filter(place -> ifPlaceAvailableInTimeSlot(place, timeSlot));
    }

    @Override
    public Map<Teacher, Set<WeeklyTimeSlot>> getTeacherMandatoryTimeSlotsMap() {
        return _teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, this::getMandatoryTimeslotsForTeacher));
    }

    @Override
    public Stream<Place> getAvailablePlacesForOccupation(LessonRequestOccupation occupation) {
        boolean ifSamePlaceForLessons = _optionalConstraintsSettings.ifHard(OptionalSchedulingConstraint.LESSON_SAME_PLACES);
        if(ifSamePlaceForLessons) {
            Optional<Place> scheduledPlace = getLessonsRelatedToOccupation(occupation)
                    .findAny()
                    .map(Lesson::getPlace);
            if(scheduledPlace.isPresent()) {
                Place place = scheduledPlace.get();
                return Stream.of(place);
            }
        }

        return occupation
                .getCandidatePlaces();
    }

    private Set<WeeklyTimeSlot> getMandatoryTimeslotsForTeacher(Teacher teacher) {
        return _scheduledLessons
                .stream()
                .map(lesson -> new Pair<>(getLessonRequestForLesson(lesson), lesson.getTimeSlot()))
                .filter(pair -> pair.getKey()
                        .getOccupations()
                        .flatMap(LessonRequestOccupation::getOccupiedTeachers).anyMatch(teacher::equals))
                .map(Pair::getValue)
                .collect(Collectors.toSet());
    }
    private boolean ifPlaceAvailableInTimeSlot(Place place, WeeklyTimeSlot timeSlot) {
        return _placeAvailableTimeSlots.get(place).contains(timeSlot);
    }

    private Stream<Lesson> getLessonsRelatedToOccupation(LessonRequestOccupation occupation) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getGroup().equals(occupation.getLessonRequest().getGroup()))
                .filter(lesson -> lesson.getCourse().equals(occupation.getCourse()));

    }

    @Override
    public Map<Teacher, Map<DayOfWeek, Integer>> getTeacherDailyMaxLoadMap() {
        throw new UnsupportedOperationException("Currently not supported!");
    }

    @Override
    public Map<Group, Map<DayOfWeek, Integer>> getGroupDailyMaxLoadMap() {
        return _groupDailyMaxLoad;
    }

    @Override
    public Map<Teacher, Set<WeeklyTimeSlot>> getTeacherAvailableTimeSlotsMap() {
        return _teacherAvailableTimeSlots;
    }

    @Override
    public Map<Group, Set<WeeklyTimeSlot>> getGroupAvailableTimeSlotsMap() {
        return _groupAvailableTimeSlots;
    }

    @Override
    public Map<Group, Set<WeeklyTimeSlot>> getGroupMandatoryTimeSlotsMap() {
        return _groups
                .stream()
                .collect(Collectors.toConcurrentMap(group -> group, this::getMandatoryTimeslotsForGroup));
    }

    private Set<WeeklyTimeSlot> getMandatoryTimeslotsForGroup(Group group) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getGroup().equals(group))
                .map(Lesson::getTimeSlot)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Place, Set<WeeklyTimeSlot>> getPlaceAvailableTimeSlotsMap() {
        return _placeAvailableTimeSlots;
    }

    @Override
    public Map<Teacher, Set<DayOfWeek>> getTeacherMandatoryDaysMap() {
       return _teachers
               .stream()
               .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher ->
                       getScheduledLessonsForTeacher(teacher)
                       .map(Lesson::getTimeSlot)
                       .map(WeeklyTimeSlot::getDayOfWeek)
                       .collect(Collectors.toSet())));
        // return createTeacherMandatoryDaysMap();
    }

    /*TODO
    private Map<Teacher, Set<DayOfWeek>> createTeacherMandatoryDaysMap() {
        return _teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher,teacher -> {
                    int numAvailableSlots = _teacherAvailableTimeSlots.get(teacher).size();
                    var teacherDayAvailableSlotsMap = _teacherAvailableTimeSlots.get(teacher)
                            .stream()
                            .collect(Collectors.groupingBy(WeeklyTimeSlot::getDayOfWeek));
                    OptionalInt minLessonsInDay = teacherDayAvailableSlotsMap
                            .values()
                            .stream()
                            .mapToInt(List::size)
                            .min();
                    if(minLessonsInDay.isEmpty()) {
                        return new HashSet<>();
                    }
                    int teacherWeeklyLoad = _lessonRequests
                            .stream()
                            .filter(lr -> lr.getOccupations()
                                    .anyMatch(lro -> lro.getOccupiedTeachers()
                                            .anyMatch(teacher::equals)))
                            .map(LessonRequest::getCourseInProgram)
                            .mapToInt(CourseInProgram::getLessonsPerWeek)
                            .sum();
                    // ПРИНЦИП ДИРЕХЛЕ
                    if(teacherWeeklyLoad + minLessonsInDay.getAsInt() > numAvailableSlots) {
                        // all days are mandatory
                        return new HashSet<>(_teacherAvailableDays.get(teacher));
                    } else {
                        return new HashSet<>();
                    }
                }));
    }

     */

    @Override
    public Map<Teacher, Integer> getTeacherMaxDaysMap() {
        return _teacherMaxDaysMap;
    }

    @Override
    public Map<LessonRequest, Set<WeeklyTimeSlot>> getLessonRequestTimeSlotsMap() {
        // TODO THIS WILL ALSO INCLUDES SCHEDULED LESSONS
        Map<LessonRequest, Set<WeeklyTimeSlot>> result = new ConcurrentHashMap<>(_lessonRequestsAvailableTimeSlots);
        _scheduledLessons
                .forEach(lesson -> {
                    LessonRequest lessonRequest = getLessonRequestForLesson(lesson);
                    if(!result.containsKey(lessonRequest)) {
                        result.put(lessonRequest, new HashSet<>());
                    }
                    WeeklyTimeSlot timeSlot = lesson.getTimeSlot();
                    result.get(lessonRequest).add(timeSlot);
                });

        return result;
    }

    private LessonRequest getLessonRequestForLesson(Lesson lesson) {
        return _lessonRequests
                .stream()
                .filter(lessonRequest -> lesson.getGroup().equals(lessonRequest.getGroup()))
                .filter(lessonRequest -> lessonRequest.getCourseInProgram().getCourses().anyMatch(lesson.getCourse()::equals))
                .findAny()
                .get();
    }

    @Override
    public Stream<Lesson> getScheduledLessons() {
        return _scheduledLessons.stream();
    }

    @Override
    public Stream<LessonRequest> getLessonRequests() {
        return _lessonRequests.stream();
    }
}
