package scheduleBuilder;

import course.CourseInProgram;
import group.Group;
import lesson.Lesson;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import logger.MyLogger;
import pair.Pair;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedulingInputV2Impl implements SchedulingInputV2 {
    private final List<WeeklyTimeSlot> _timeslotSequence;
    private Map<DayOfWeek, List<WeeklyTimeSlot>> _dayOfWeekTimeSlots;
    private final List<Teacher> _teachers;
    private final List<Group> _groups;
    private final List<Place> _places;
    private final List<LessonRequest> _lessonRequests;
    private final List<Lesson> _scheduledLessons;

    private Map<Teacher, Set<WeeklyTimeSlot>> _teacherAvailableTimeSlots;
    private Map<Group, Set<WeeklyTimeSlot>> _groupAvailableTimeSlots;
    private Map<Place, Set<WeeklyTimeSlot>> _placeAvailableTimeSlots;
    private Map<Teacher, Set<DayOfWeek>> _teacherAvailableDays;
    private Map<Group, Set<DayOfWeek>> _groupAvailableDays;
    private Map<Teacher, Set<WeeklyTimeSlot>> _teacherMandatoryTimeSlots;
    private Map<Teacher, Set<DayOfWeek>> _teacherMandatoryDays;
    private Map<Group, Set<WeeklyTimeSlot>> _groupMandatoryTimeSlots;

    private Map<LessonRequest, Set<WeeklyTimeSlot>> _lessonRequestsAvailableTimeSlots;



    private Optional<Map<Group, Map<DayOfWeek, Integer>>> _groupDailyMaxLoad = Optional.empty();
    private Optional<Map<Teacher, Map<DayOfWeek, Integer>>> _teacherDailyMaxLoad = Optional.empty();
    private Optional<Map<Teacher, Integer>> _teacherMaxDaysMap = Optional.empty();
    private int _maxOccupations;
    private final int _maxDays;



    public SchedulingInputV2Impl(List<WeeklyTimeSlot> timeslotSequence, List<Teacher> teachers, List<Group> groups, List<Place> places, List<LessonRequest> lessonRequests, List<Lesson> scheduledLessons){
        _timeslotSequence = timeslotSequence;
        _dayOfWeekTimeSlots = getDayOfWeekTimeslotsMap(timeslotSequence.stream());
        _maxDays = _dayOfWeekTimeSlots.keySet().size();
        _teachers = teachers;
        _lessonRequests = lessonRequests;
        _scheduledLessons = scheduledLessons;

        _groups = groups;
        _places = places;
        _maxOccupations = calculateMaxOccupations();
        _teacherAvailableTimeSlots = teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher -> new HashSet<>(timeslotSequence)));

        _teacherMandatoryTimeSlots = teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher -> new HashSet<>()));
        _teacherAvailableDays = createTeacherAvailableDaysMap();

        _groupAvailableTimeSlots = groups
                .stream()
                .collect(Collectors.toConcurrentMap(group -> group, group -> new HashSet<>(timeslotSequence)));
        _groupMandatoryTimeSlots = groups
                .stream()
                .collect(Collectors.toConcurrentMap(group -> group, group -> new HashSet<>()));

        _groupAvailableDays = createGroupAvailableDaysMap();





        _teacherMandatoryDays = teachers
                .stream()
                .collect(Collectors.toConcurrentMap(group -> group, group -> new HashSet<>()));

        _placeAvailableTimeSlots = places
                .stream()
                .collect(Collectors.toConcurrentMap(place -> place, place -> new HashSet<>(timeslotSequence)));

        _lessonRequestsAvailableTimeSlots = createInitialLessonRequestsAvailableTimeslotsMap();

        //TODO CREATE MODEL WHICH CAN TAKE INTO ACCOUNT ALREADY SCHEDULED LESSON REQUEST OCCUPATIONS
    }


    @Override
    public boolean ifDayMandatoryForLessonRequest(LessonRequest lessonRequest, DayOfWeek day) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getTimeSlot().getDayOfWeek().equals(day))
                .filter(lesson -> lesson.getGroup().equals(lessonRequest.getGroup()))
                .anyMatch(lesson -> lessonRequest.getCourseInProgram().getCourses().anyMatch(lesson.getCourse()::equals));
    }

    @Override
    public int getTotalTeacherLessonsLoad(Teacher teacher) {
        return _lessonRequests
                .stream()
                .flatMap(LessonRequest::getOccupations)
                .filter(occupation -> occupation.getOccupiedTeachers().anyMatch(teacher::equals))
                .mapToInt(occupation -> occupation.getLessonRequest().getCourseInProgram().getLessonsPerWeek())
                .sum();
    }

    @Override
    public boolean ifDayAvailableForLessonRequest(LessonRequest lessonRequest, DayOfWeek dayOfWeek) {
        return getAvailableDaysForLessonRequest(lessonRequest)
                .anyMatch(dayOfWeek::equals);
    }

    @Override
    public void setTeachersDailyMaxLoadConstraint(Map<Teacher, Map<DayOfWeek, Integer>> teacherDailyMaxLoad) {
        _teacherDailyMaxLoad = Optional.of(teacherDailyMaxLoad);
    }

    @Override
    public void setGroupDailyMaxLoadConstraint(Map<Group, Map<DayOfWeek, Integer>> groupDailyMaxLoad) {
        _groupDailyMaxLoad = Optional.of(groupDailyMaxLoad);
    }

    @Override
    public void setTeacherAvailableTimeSlots(Map<Teacher, Set<WeeklyTimeSlot>> teacherAvailableTimeSlots) {
        _teacherAvailableTimeSlots = teacherAvailableTimeSlots;
        _teacherAvailableDays = createTeacherAvailableDaysMap();
        checkTeachersLoad();
    }

    @Override
    public void setGroupAvailableTimeSlots(Map<Group, Set<WeeklyTimeSlot>> groupAvailableTimeSlots) {
        _groupAvailableTimeSlots = groupAvailableTimeSlots;
        _groupAvailableDays = createGroupAvailableDaysMap();
        checkGroupsLoad();
    }

    @Override
    public void setGroupMandatoryTimeSlots(Map<Group, Set<WeeklyTimeSlot>> groupMandatoryTimeSlots) {
        _groupMandatoryTimeSlots = groupMandatoryTimeSlots;
    }

    @Override
    public void setTeacherMandatoryTimeSlots(Map<Teacher, Set<WeeklyTimeSlot>> teacherMandatoryTimeSlots) {
        _teacherMandatoryTimeSlots = teacherMandatoryTimeSlots;
    }

    @Override
    public void setPlaceAvailableTimeSlots(Map<Place, Set<WeeklyTimeSlot>> placeAvailableTimeSlots) {
        _placeAvailableTimeSlots = placeAvailableTimeSlots;
    }

    @Override
    public void setTeacherMandatoryDays(Map<Teacher, Set<DayOfWeek>> teacherMandatoryDays) {
        _teacherMandatoryDays = teacherMandatoryDays;
    }

    @Override
    public void setTeacherMaxDaysMap(Map<Teacher, Integer> teacherMaxDaysMap) {
        _teacherMaxDaysMap = Optional.of(teacherMaxDaysMap);
    }

    @Override
    public void setLessonRequestAvailableTimeSlots(Map<LessonRequest, Set<WeeklyTimeSlot>> lessonRequestTimeSlotsMap) {
        _lessonRequestsAvailableTimeSlots = lessonRequestTimeSlotsMap;
    }

    private void checkGroupsLoad() {
        _groups.forEach(this::checkGroupLoad);
    }

    private void checkTeachersLoad() {
        _teachers.forEach(this::checkTeacherLoad);
    }

    private void checkGroupLoad(Group group) {
        int numSlots = (int)getAvailableSlotsForGroup(group).count();
        int workload = getLessonRequestsForGroup(group)
                .map(LessonRequest::getCourseInProgram)
                .mapToInt(CourseInProgram::getLessonsPerWeek)
                .sum();

        if(numSlots <= workload) {
            MyLogger.warn("Group " + group.getName() + " has " + numSlots + " slots and " + workload + " workload.");
            if(numSlots < workload) {
                getLessonRequestsForGroup(group)
                        .forEach(lr -> lr.getCourseInProgram().getCourses()
                                .map(course -> "" + lr.getGroup() + "\t" + course.getName() + "\t" + lr.getCourseInProgram().getLessonsPerWeek())
                                .forEach(MyLogger::warn)
                        );
            }
        }
    }

    private void checkTeacherLoad(Teacher teacher) {
        int numSlots = (int)getAvailableSlotsForTeacher(teacher).count();
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

    private int calculateMaxOccupations() {
        return _lessonRequests
                .stream()
                .mapToInt(lr -> {
                    return (int)lr.getOccupations().count();
                })
                .max()
                .orElse(0);

    }

    private Map<Group, Set<DayOfWeek>> createGroupAvailableDaysMap() {
       return  _groups.stream()
                .collect(Collectors.toConcurrentMap(group -> group, group -> getAvailableSlotsForGroup(group)
                        .map(WeeklyTimeSlot::getDayOfWeek)
                        .collect(Collectors.toSet())));
    }


    @Override
    public Optional<Place> occupationScheduledInPlace(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot) {
        return _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getTimeSlot().equals(timeSlot))
                .filter(lesson -> lesson.getGroup().equals(occupation.getLessonRequest().getGroup()) && lesson.getCourse().equals(occupation.getCourse()))
                .map(Lesson::getPlace)
                .findAny();
    }

    @Override
    public Stream<LessonRequest> getLessonRequestsForGroup(Group group) {
        return _lessonRequests
                .stream()
                .filter(lessonsRequest -> lessonsRequest.getGroup().equals(group));
    }

    @Override
    public Stream<LessonRequest> getLessonRequestsForPlace(Place place) {
        return _lessonRequests
                .stream()
                .filter(lessonsRequest -> lessonsRequest
                        .getOccupations()
                        .anyMatch(occupation -> occupation
                                .getCandidatePlaces()
                                .anyMatch(place::equals)));
    }

    @Override
    public Stream<DayOfWeek> getAvailableDaysForTeacher(Teacher teacher) {
        var mandatory = _teacherMandatoryDays.get(teacher).stream();
        var available =  _teacherAvailableDays.get(teacher).stream();
        return Stream.concat(mandatory, available)
                .distinct();
    }

    @Override
    public Stream<DayOfWeek> getAvailableDaysForGroup(Group group) {
        return _groupAvailableDays.get(group)
                .stream();
    }

    @Override
    public WeeklyTimeSlot getTimeSlot(String timeSlotId) {
        return _timeslotSequence.get(Integer.parseInt(timeSlotId));
    }

    @Override
    public int getMaxOccupationsInSlot() {
        return _maxOccupations;
    }

    @Override
    public Stream<DayOfWeek> getAvailableDaysForLessonRequest(LessonRequest lessonRequest) {
        return getAvailableTimeslotsForLessonRequest(lessonRequest)
                .map(WeeklyTimeSlot::getDayOfWeek)
                .distinct();
    }

    @Override
    public int getLessonRequestDayStartTimeSlotLb(LessonRequest lessonRequest, DayOfWeek dayOfWeek) {
        var list = _dayOfWeekTimeSlots.get(dayOfWeek);

        return _lessonRequestsAvailableTimeSlots.get(lessonRequest)
                .stream()
                .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                .mapToInt(list::indexOf)
                .min()
                .orElse(0);
    }

    @Override
    public int getLessonRequestLatestTimeSlot(LessonRequest lessonRequest, DayOfWeek dayOfWeek) {
        var list = _dayOfWeekTimeSlots.get(dayOfWeek);
        if(list.size() > 0) {
            return _lessonRequestsAvailableTimeSlots.get(lessonRequest)
                    .stream()
                    .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                    .mapToInt(list::indexOf)
                    .max()
                    .orElse(0);

        } else {
            return 0;
        }
    }

    @Override
    public Stream<Place> getAvailablePlacesForOccupation(LessonRequestOccupation occupation) {
        return occupation.getCandidatePlaces();
    }

    @Override
    public Stream<Place> getAvailablePlacesForOccupationAtTimeSlot(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot) {
        return occupation.getCandidatePlaces()
                .filter(place -> _placeAvailableTimeSlots.get(place).contains(timeSlot));
    }

    @Override
    public LessonRequest getLessonRequest(String lessonId) {
        if(Integer.parseInt(lessonId) >= _lessonRequests.size()){
            return _lessonRequests.get(0);
        }
        return _lessonRequests.get(Integer.parseInt(lessonId));
    }

    @Override
    public Place getPlace(String placeId) {
        return _places.get(Integer.parseInt(placeId));
    }

    @Override
    public String getTeacherId(Teacher teacher) {
        return "" + _teachers.indexOf(teacher);
    }

    @Override
    public String getTimeSlotId(WeeklyTimeSlot timeSlot) {
        return "" + _timeslotSequence.indexOf(timeSlot);
    }

    @Override
    public Stream<WeeklyTimeSlot> getAvailableSlotsForTeacher(Teacher teacher) {
        var available = _teacherAvailableTimeSlots.get(teacher).stream();
        var mandatory = _teacherMandatoryTimeSlots.get(teacher).stream();
        return Stream.concat(available, mandatory)
                .distinct();
    }

    @Override
    public Stream<WeeklyTimeSlot> getAvailableSlotsForGroup(Group group) {
        var available =  _groupAvailableTimeSlots.get(group).stream();
        var mandatory = _groupMandatoryTimeSlots.get(group).stream();
        return Stream.concat(available, mandatory);
    }


    @Override
    public String getGroupId(Group group) {
        return "" + _groups.indexOf(group);
    }

    @Override
    public boolean ifDayAvailableForGroup(Group group, DayOfWeek dow) {
        return _groupAvailableDays.get(group).contains(dow);
    }

    @Override
    public Stream<WeeklyTimeSlot> getAvailableSlotsForLessonRequest(LessonRequest lessonRequest) {
        return _lessonRequestsAvailableTimeSlots.get(lessonRequest).stream();
    }

    @Override
    public int getMaxDaysForTeacher(Teacher teacher) {
        return _teacherMaxDaysMap.map(map -> map.get(teacher)).orElse(_maxDays);
    }

    @Override
    public int getTeacherInDayStartTimeslotLb(Teacher teacher, DayOfWeek dayOfWeek) {
        if(ifDayAvailableForTeacher(teacher, dayOfWeek)) {
            var availableSlots = _teacherAvailableTimeSlots.get(teacher);
            availableSlots.addAll(_teacherMandatoryTimeSlots.get(teacher));
            var dayTimeSlots = _dayOfWeekTimeSlots.get(dayOfWeek);
            for(int i=0; i< dayTimeSlots.size(); i++) {
                WeeklyTimeSlot timeSlot = dayTimeSlots.get(i);
                if(availableSlots.contains(timeSlot)) {
                    return i;
                }
            }
            throw new IllegalStateException("Day is not available for teacher!");
        } else {
            return 0;
        }
    }

    @Override
    public int getTeacherInDayStartTimeslotUb(Teacher teacher, DayOfWeek dayOfWeek) {
        if(ifDayAvailableForTeacher(teacher, dayOfWeek)) {
            var dayTimeSlots = _dayOfWeekTimeSlots.get(dayOfWeek);
            List<Lesson> teachersLessonInDay = _scheduledLessons
                    .stream()
                    .filter(lesson -> lesson.getTeachers().anyMatch(teacher::equals))
                    .filter(lesson -> lesson.getTimeSlot().getDayOfWeek().equals(dayOfWeek))
                    .toList();
            if(teachersLessonInDay.size() > 0) {
                return teachersLessonInDay
                        .stream()
                        .map(Lesson::getTimeSlot)
                        .mapToInt(dayTimeSlots::indexOf)
                        .min()
                        .getAsInt();
            } else {
                var availableSlots = _teacherAvailableTimeSlots.get(teacher);
                for (int i = dayTimeSlots.size() - 1; i >= 0; i--) {
                    WeeklyTimeSlot timeSlot = dayTimeSlots.get(i);
                    if (availableSlots.contains(timeSlot)) {
                        return i;
                    }
                }
            }
            throw new IllegalStateException("Day is not available for teacher!");
        } else {
            return 0;
        }
    }

    @Override
    public int getTeacherDailyMinTimeslotLoad(Teacher teacher, DayOfWeek dayOfWeek) {
        List<Lesson> teachersLessonInDay = _scheduledLessons
                .stream()
                .filter(lesson -> lesson.getTeachers().anyMatch(teacher::equals))
                .filter(lesson -> lesson.getTimeSlot().getDayOfWeek().equals(dayOfWeek))
                .toList();
        return teachersLessonInDay.size();
    }

    @Override
    public Stream<Place> getAvailablePlacesForTeacher(Teacher teacher) {
        return _lessonRequests
                .stream()
                .flatMap(lessonsRequest ->
                    lessonsRequest.getOccupations()
                            .filter(occupation -> occupation.getOccupiedTeachers().anyMatch(teacher::equals))
                            .flatMap(LessonRequestOccupation::getCandidatePlaces)
                )
                .distinct();

    }

    @Override
    public String getPlaceId(Place place) {
        return "" +_places.indexOf(place);
    }

    @Override
    public Stream<DayOfWeek> getDays() {
        return _dayOfWeekTimeSlots.keySet()
                .stream();
    }

    @Override
    public int getGroupDailyMinLoad(Group group, DayOfWeek dayOfWeek) {
        return 0;
    }

    @Override
    public int getGroupInDayStartLb(Group group, DayOfWeek dayOfWeek) {
        var list = _dayOfWeekTimeSlots.get(dayOfWeek);
        return getAvailableSlotsForGroup(group)
                .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                .mapToInt(list::indexOf)
                .min()
                .orElse(0);
    }

    @Override
    public int getGroupInDayStartUb(Group group, DayOfWeek dayOfWeek) {
        var list = _dayOfWeekTimeSlots.get(dayOfWeek);
        if(list.size() > 0) {
            return getLastAvailableSlotIndex(group, dayOfWeek);
        } else {
            return 0;
        }
    }

    private int getLastAvailableSlotIndex(Group group, DayOfWeek dayOfWeek) {
        var list = _dayOfWeekTimeSlots.get(dayOfWeek);
        return getAvailableSlotsForGroup(group)
                .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                .mapToInt(list::indexOf)
                .max()
                .orElse(0);
    }

    private Map<Teacher, Set<DayOfWeek>> createTeacherAvailableDaysMap() {
        return _teachers
                .stream()
                .collect(Collectors.toConcurrentMap(teacher -> teacher, teacher -> getAvailableSlotsForTeacher(teacher)
                        .map(WeeklyTimeSlot::getDayOfWeek)
                        .collect(Collectors.toSet())));
    }


    @Override
    public Stream<LessonRequest> getLessonRequestsForTeacher(Teacher teacher) {
        return _lessonRequests.stream()
                .filter(lr -> lr.getOccupations()
                        .anyMatch(lro -> lro.getOccupiedTeachers()
                                .anyMatch(teacher::equals))
                );
    }


    private Map<LessonRequest, Set<WeeklyTimeSlot>> createInitialLessonRequestsAvailableTimeslotsMap() {
        return  _lessonRequests.stream()
                        .collect(Collectors.toConcurrentMap(lr -> lr, lr -> getAvailableTimeslotsForLessonRequest(lr).collect(Collectors.toSet())));
    }

    private Stream<WeeklyTimeSlot> getAvailableTimeslotsForLessonRequest(LessonRequest lessonRequest) {
        final Set<Teacher> teachers = lessonRequest.getOccupations()
                .flatMap(LessonRequestOccupation::getOccupiedTeachers)
                .collect(Collectors.toSet());
        Group group = lessonRequest.getGroup();

        return getAvailableSlotsForGroup(group)
                .filter(ts -> teachers.stream()
                        .allMatch(teacher -> ifTimeSlotAvailableForTeacher(teacher, ts)));
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
    public List<WeeklyTimeSlot> getTimeslotSequence() {
        return _timeslotSequence;
    }

    @Override
    public List<WeeklyTimeSlot> getTimeslotsSequenceForDay(DayOfWeek dayOfWeek) {
        return _dayOfWeekTimeSlots.get(dayOfWeek);
    }

    @Override
    public List<LessonRequest> getLessonRequests() {
        return _lessonRequests;
    }

    @Override
    public List<Teacher> getTeachers() {
        return _teachers;
    }

    @Override
    public List<Group> getGroups() {
        return _groups;
    }

    @Override
    public List<Place> getPlaces() {
        return _places;
    }

    @Override
    public int getGroupDailyMaxLoad(Group group, DayOfWeek dayOfWeek) {
        return _groupDailyMaxLoad.isPresent() ?
                _groupDailyMaxLoad.get().get(group).getOrDefault(dayOfWeek, getMaxSlotsInDay(dayOfWeek)) :
                getMaxSlotsInDay(dayOfWeek);
    }

    private int getMaxSlotsInDay(DayOfWeek dayOfWeek) {
        return _dayOfWeekTimeSlots.get(dayOfWeek).size();
    }

    @Override
    public int getTeacherDailyMaxTimeslotLoad(Teacher teacher, DayOfWeek dayOfWeek) {
        if(ifDayAvailableForTeacher(teacher, dayOfWeek)) {
            var dayList = _dayOfWeekTimeSlots.get(dayOfWeek);
            int first = getAvailableSlotsForTeacher(teacher)
                   .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                   .mapToInt(dayList::indexOf)
                   .min().orElse(0);
            int last = getAvailableSlotsForTeacher(teacher)
                    .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                    .mapToInt(dayList::indexOf)
                    .max().orElse(0);
            return last - first + 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean ifTimeSlotAvailableForLessonRequest(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot) {
        return _lessonRequestsAvailableTimeSlots.get(lessonRequest).contains(timeSlot);
    }

    @Override
    public boolean ifTimeSlotAvailableForGroup(Group group, WeeklyTimeSlot timeSlot) {
        return _groupAvailableTimeSlots.get(group).contains(timeSlot) || _groupMandatoryTimeSlots.get(group).contains(timeSlot);
    }

    @Override
    public boolean ifTimeSlotAvailableForTeacher(Teacher teacher, WeeklyTimeSlot timeSlot) {
        return _teacherAvailableTimeSlots.get(teacher).contains(timeSlot) || _teacherMandatoryTimeSlots.get(teacher).contains(timeSlot);
    }

    @Override
    public boolean ifTimeSlotMandatoryForTeacher(Teacher teacher, WeeklyTimeSlot timeSlot) {
        return _teacherMandatoryTimeSlots.get(teacher).contains(timeSlot);
    }

    @Override
    public boolean ifTimeSlotMandatoryForGroup(Group group, WeeklyTimeSlot timeSlot) {
        return _groupMandatoryTimeSlots.get(group).contains(timeSlot);
    }

    @Override
    public boolean ifDayAvailableForTeacher(Teacher teacher, DayOfWeek day) {
        return _teacherAvailableDays.get(teacher).contains(day) ||  _teacherMandatoryDays.get(teacher).contains(day) ;
    }

    @Override
    public boolean ifDayMandatoryForTeacher(Teacher teacher, DayOfWeek day) {
        return _teacherMandatoryDays.get(teacher).contains(day);
    }
}
