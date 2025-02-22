package scheduleBuilder.translators.milp;

import constraint.timeConstraint.ForbiddenTimeConstraint;
import constraint.timeConstraint.MandatoryTimeConstraint;
import group.Group;
import lesson.Lesson;
import lesson.LessonImpl;
import lesson.LessonRequestOccupation;
import lesson.LessonRequest;
import model.*;
import model.constraint.*;
import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import model.variables.*;
import pair.Pair;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.SchedulingInput;
import solution.MilpSolution;
import time.WeeklyTimeSlot;
import triplet.Triplet;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SchedulingInputToMilp_Model_2 implements MilpTranslator {
    private final static VariableFactory _variableFactory = new VariableFactoryImpl();
    private final static LinearConstraintFactory _constraintFactory = new LinearConstraintFactoryImpl();
    private VariableNameSpace _nameSpace = new VariableNameSpaceImpl();
    private MilpModelBuilder _milpModelBuilder;
    private int _nextConstraintId = 0;

    private SchedulingInput _schedulingInput;
    // time
    private List<WeeklyTimeSlot> _timeSlotSequence;
    private int _numTimeSlots;
    private List<DayOfWeek> _daysOfWeek;
    private Map<DayOfWeek, List<WeeklyTimeSlot>> _dayOfWeekTimeSlots;
    private Map<WeeklyTimeSlot, String> _timeSlotToIdMap;
    private List<Teacher> _teachers;
    private List<Place> _places;
    private List<Group> _groups;
    private Supplier<Stream<Pair<Group, Group>>> _intersectedGroupsStreamSupplier;
    private List<LessonRequest> _lessonRequests;
    private Map<LessonRequest, List<WeeklyTimeSlot>> _lessonRequestAvailableTimeSlots;
    private Map<Teacher, Integer> _maxDaysOfWeekPerTeacher;
    private Map<Group, Integer> _maxDaysOfWeekPerGroup;
    private Map<Teacher, Map<DayOfWeek, Integer>> _teacherDayMaxLoad;
    private Map<Group, Map<DayOfWeek, Integer>> _groupDayMaxLoad;

    private final static String _lesReqOccupIdPlaceVar = "LROP";
    private final static String _lesReqTimeSlotVar = "LRT";
    private final static String _groupTimeSlotVar = "GT";
    private final static String _groupDayVar = "GD";
    private final static String _teacherTimeSlotVar = "WT";
    private final static String _teacherDayVar = "WD";
    private final static String _teacherTimeWindowVar = "WTW";
    private final static String _teacherDayStartVar = "WDS";
    private final static String _teacherDayEndVar = "WDE";
    private final static String _complexityOverloadGroupDay = "COGD";
    private final static String _teacherPlacesNum = "TPN";


    @Override
    public MilpModel createModel(SchedulingInput schedulingInput) {
        _milpModelBuilder = new MilpModelBuilderImpl("MyModel");
        initializePrivateDataStructures(schedulingInput);
        return buildModel();
    }

    @Override
    public Schedule createSchedule(MilpSolution solution) {
        Stream<Lesson> stream = createLessonsStream(solution.getNonZeroValues());
        Schedule schedule = new ScheduleImpl(stream);
        return schedule;
    }

    private Stream<Lesson> createLessonsStream(Stream<Pair<String, Double>> nonZeroValues) {
        var list = nonZeroValues
                .filter(pair -> {
                    String name = pair.getKey();
                    return _nameSpace.ifLabeledBy(name, _lesReqOccupIdPlaceVar) || _nameSpace.ifLabeledBy(name, _lesReqTimeSlotVar);
                })
                .filter(pair -> pair.getValue().equals(1.0))
                .map(Pair::getKey)
                .toList();

        Stream<String> nonZeroLrTimeslotVarNames = list
                .stream()
                .filter(varName -> _nameSpace.ifLabeledBy(varName, _lesReqTimeSlotVar));

        Map<LessonRequest, List<WeeklyTimeSlot>> lessonsRequestTimeSlotMap = createLessonRequestsTimeslotsMap(nonZeroLrTimeslotVarNames);

        Stream<String> nonZeroLrPlaceVarNames = list
                .stream()
                .filter(varName -> _nameSpace.ifLabeledBy(varName, _lesReqOccupIdPlaceVar));
        Map<LessonRequest, Map<Integer,Place>> lessonsRequestPlaceMap = createLessonRequestPlacesMap(nonZeroLrPlaceVarNames);

        return createLessonsStreamByMaps(lessonsRequestTimeSlotMap, lessonsRequestPlaceMap);
    }

    private Map<LessonRequest, Map<Integer,Place>> createLessonRequestPlacesMap(Stream<String> nonZeroLrPlaceVarNames) {
        Map<LessonRequest, List<Triplet<LessonRequest, Place, Integer>>> map = nonZeroLrPlaceVarNames
                .map(varName -> {
                    List<String> indices = _nameSpace.getVariableIndices(varName);
                    int requestId = Integer.parseInt(indices.get(0));
                    var lessonRequest = _lessonRequests.get(requestId);
                    int occupationId = Integer.parseInt(indices.get(1));
                    int placeId = Integer.parseInt(indices.get(2));
                    Place place = _places.get(placeId);
                    return new Triplet<>(lessonRequest, place, occupationId);
                })
                .collect(Collectors.groupingBy(Triplet::getFirst));
        return map.entrySet()
                .stream()
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, entry -> {
                    return entry.getValue()
                            .stream()
                            .collect(Collectors.toConcurrentMap(Triplet::getThird, Triplet::getSecond));
                }));
    }

    private Stream<Lesson> createLessonsStreamByMaps(Map<LessonRequest, List<WeeklyTimeSlot>> lessonsRequestTimeSlotMap, Map<LessonRequest, Map<Integer, Place>> lessonsRequestPlaceMap) {
        return lessonsRequestTimeSlotMap.keySet()
                .stream()
                .flatMap(lessonRequest -> {
                    Map<Integer, Place> occupationIdPlaceMap = lessonsRequestPlaceMap.get(lessonRequest);
                    if (occupationIdPlaceMap == null) {
                        throw new IllegalStateException("Place not found!");
                    }
                    List<WeeklyTimeSlot> timeSlots = lessonsRequestTimeSlotMap.get(lessonRequest);


                    Stream<Lesson> lessonsForRequest = timeSlots
                            .stream()
                            .flatMap(timeSlot -> lessonRequest.getOccupations()
                                    .flatMap(occupation -> {
                                        Place place = occupationIdPlaceMap.get(occupation.getId());
                                        if(place == null) {
                                            throw new IllegalStateException("Place should be presented!");
                                        }
                                        return occupation
                                                .getOccupiedTeachers()
                                                .map(teacher -> new LessonImpl(timeSlot, lessonRequest.getGroup(), place, teacher, occupation.getCourse()));
                                        }));
                    return lessonsForRequest;
                });
    }

    private Map<LessonRequest, List<WeeklyTimeSlot>>  createLessonRequestsTimeslotsMap(Stream<String> nonZeroLrTimeslotVarNames) {
        return
                nonZeroLrTimeslotVarNames
                .map(varName -> {
                    List<String> indices = _nameSpace.getVariableIndices(varName);
                    int requestId = Integer.parseInt(indices.get(0));
                    var lessonRequest = _lessonRequests.get(requestId);
                    int timeslotId = Integer.parseInt(indices.get(1));
                    WeeklyTimeSlot timeSlot = _timeSlotSequence.get(timeslotId);
                    return new Pair<>(lessonRequest, timeSlot);
                })
                .collect(Collectors.groupingBy(Pair::getKey,Collectors.mapping(Pair::getValue,Collectors.toList())));
    }


    private void initializePrivateDataStructures(SchedulingInput schedulingInput) {
        _schedulingInput = schedulingInput;
        _timeSlotSequence = schedulingInput
                .getTimeslotSequence();
        _timeSlotToIdMap = IntStream.range(0, _timeSlotSequence.size())
                .sequential()
                .boxed()
                .collect(Collectors.toConcurrentMap(_timeSlotSequence::get, i -> "" + i));

        _numTimeSlots = _timeSlotSequence.size();
        _daysOfWeek = getDaysOfWeek(_timeSlotSequence);
        _dayOfWeekTimeSlots = getDayOfWeekTimeslotsMap(_timeSlotSequence);

        _teachers = schedulingInput.getTeachers();
        _maxDaysOfWeekPerTeacher = schedulingInput
                .getTeachers()
                .stream()
                .collect(Collectors.toConcurrentMap(t -> t, schedulingInput::getMaxDaysForTeacher));

        _maxDaysOfWeekPerGroup = schedulingInput
                .getGroups()
                .stream()
                .collect(Collectors.toConcurrentMap(t -> t, schedulingInput::getMaxDaysForGroup));

        _groupDayMaxLoad = schedulingInput
                .getGroups()
                .stream()
                .collect(Collectors.toConcurrentMap(t -> t, t ->
                        schedulingInput
                                .getDailyMaxLoadsForGroup(t)
                                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue))));

        _teacherDayMaxLoad = schedulingInput
                .getTeachers()
                .stream()
                .collect(Collectors.toConcurrentMap(t -> t, t ->
                        schedulingInput
                                .getDailyMaxLoadsForTeacher(t)
                                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue))));
        _groups = schedulingInput.getGroups();
        _lessonRequests = schedulingInput.getLessonRequests();
        _lessonRequestAvailableTimeSlots = createLessonRequestsAvailableTimeslotsMap(schedulingInput);
        _places = schedulingInput.getPlaces();
        _intersectedGroupsStreamSupplier = Stream::empty;


    }

    private Map<LessonRequest, List<WeeklyTimeSlot>>  createLessonRequestsAvailableTimeslotsMap(SchedulingInput schedulingInput) {
        return
                schedulingInput.getLessonRequests()
                        .stream()
                        .collect(Collectors.toConcurrentMap(lr -> lr, lr -> getAvailableTimeslotsForLessonRequest(lr).collect(Collectors.toList())));
    }

    private Stream<WeeklyTimeSlot> getAvailableTimeslotsForLessonRequest(LessonRequest lessonRequest) {
        Set<Teacher> teachers = lessonRequest.getOccupations()
                .flatMap(LessonRequestOccupation::getOccupiedTeachers)
                .collect(Collectors.toSet());
        Group group = lessonRequest.getGroup();

        return _timeSlotSequence
                .stream()
                .filter(getGroupFeasibleTimeslotsPredicate(group))
                .filter(ts -> teachers.stream()
                        .allMatch(teacher -> getTeacherTimeslotsPredicate(teacher).test(ts)));

    }

    private Map<DayOfWeek, List<WeeklyTimeSlot>> getDayOfWeekTimeslotsMap(List<WeeklyTimeSlot> timeSlotSequence) {
        return timeSlotSequence.stream()
                .collect(Collectors.groupingBy(WeeklyTimeSlot::getDayOfWeek)).entrySet()
                .stream()
                .map(entry -> {
                    return new Pair<>(entry.getKey(), entry.getValue()
                            .stream()
                            .sorted(WeeklyTimeSlot::compareTo).toList());
                })
                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue));
    }

    private List<DayOfWeek> getDaysOfWeek(List<WeeklyTimeSlot> timeSlotSequence) {
        return timeSlotSequence.stream()
                .map(WeeklyTimeSlot::getDayOfWeek)
                .distinct()
                .sorted(DayOfWeek::compareTo).toList();
    }

    private MilpModel buildModel() {
        // variables
        addTeacherTimeslotVariables();
        addTeacherDayVariables();
        addTeacherDayStartVariables();
        addTeacherDayEndVariables();

        addTeacherPlacesNumVariables();
        //addTeacherTimeWindowVariables();

        addGroupTimeslotVariables();
        addGroupDayVariables();
        addLessonRequestPlaceVariables();
        addLessonRequestTimeslotVariables();


        // constraints
        addTeacherDayVarsConstraints();
        addTeacherTimeslotDayConstraints();
        addTeacherTimeslotDayStartEndConstraints();

        addTeacherOneLessonConstraints();
        addMaxDaysPerTeacherConstraints();
        addTeacherPlacesNumConstrains();

        addGroupOneLessonConstraints();
        addGroupDailyContinuityConstraints();
        addMaxDaysPerGroupConstraints();
        addGroupTimeslotDayConstraints();
        addGroupMaxDailyLoadConstrains();

        addNotMoreThanOneLessonInPlaceConstraints();
        addMaxLessonsPerDayForLessonRequestConstraints();
        addAllRequestsShouldBeSatisfiedByTimeslots();
        addAllRequestsShouldBeSatisfiedByPlaces();
        addLessonRequestDayContinuityConstraints();

        //objective
        //addObjectiveMinTeacherDays();
        //addObjectiveMinTeacherDaysMinTeacherPlaces(10, 1);
        addObjectiveMinTeacherWindows(1);
        //addObjectiveMinTeacherDaysMinTeacherPlacesMinComplexityViolation(10,1,10);
        return _milpModelBuilder.getModel();
    }

    private void addTeacherDayVarsConstraints() {
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _dayOfWeekTimeSlots
                    .forEach((dayOfWeek, timeSlots) -> {
                        String i = "" + _daysOfWeek.indexOf(dayOfWeek);
                        String dayStartVarName = _nameSpace.getVariableName(_teacherDayStartVar, List.of(teacherId, "" + i));
                        String dayEndVarName = _nameSpace.getVariableName(_teacherDayEndVar, List.of(teacherId, "" + i));
                        String dayVarName = _nameSpace.getVariableName(_teacherDayVar, List.of(teacherId, "" + i));
                        int numAvailableTimeSlots = (int)timeSlots.stream()
                                .filter(getTeacherTimeslotsPredicate(teacher))
                                .count();
                        int ub = numAvailableTimeSlots > 0 ? timeSlots.size() : 0;
                        if(ub > 0) {
                            addTeacherDayStartDayConstraint(dayStartVarName, dayVarName, ub);
                            addTeacherDayEndDayConstraint(dayEndVarName, dayVarName, ub);
                        }
                    });
        }
    }

    private void addTeacherDayStartDayConstraint(String dayStartVar, String dayVar, int ub) {
        // dayStart <= ub*dayVar
        Stream<Pair<String, Double>> pairStream = Stream.of(
                new Pair<>(dayStartVar, 1.0),
                new Pair<>(dayVar, -1.0*ub)
        );
        LinearExpression expr = new LinearExpressionImpl(pairStream, 0.0);
        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expr,ConstraintType.LESS_OR_EQUAL,0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void addTeacherDayEndDayConstraint(String dayEndVar, String dayVar, int ub) {
        // dayEnd <= ub*dayVar
        Stream<Pair<String, Double>> pairStream = Stream.of(
                new Pair<>(dayEndVar, 1.0),
                new Pair<>(dayVar, -1.0*ub)
        );
        LinearExpression expr = new LinearExpressionImpl(pairStream, 0.0);
        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expr,ConstraintType.LESS_OR_EQUAL,0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void addTeacherTimeWindowVariables() {
        _nameSpace.registerVariableName(_teacherTimeWindowVar,2);
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _timeSlotSequence.stream()
                    .filter(getTeacherTimeslotsPredicate(teacher))
                    .map(_timeSlotToIdMap::get)
                    .map(i -> _nameSpace.getVariableName(_teacherTimeWindowVar, List.of(teacherId, "" + i)))
                    .map(Variable_BinaryImpl::new)
                    .forEach(_milpModelBuilder::addVariable);
        }
    }


    private void addTeacherPlacesNumVariables() {
        _nameSpace.registerVariableName(_teacherPlacesNum, 1);
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            String varName = _nameSpace.getVariableName(_teacherPlacesNum, List.of(teacherId));
            Variable variable = new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(_places.size()));
            _milpModelBuilder.addVariable(variable);
        }
    }


    private String getVarLabel_GroupTimeslot(Group group, int timeslotId) {
        return "Group_TS_" + group.getName() + "_" + timeslotId;
    }

    // VARIABLES
    private void addTeacherTimeslotVariables() {
        _nameSpace.registerVariableName(_teacherTimeSlotVar,2);
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _timeSlotSequence.stream()
                    .filter(getTeacherTimeslotsPredicate(teacher))
                    .map(_timeSlotToIdMap::get)
                    .map(i -> _nameSpace.getVariableName(_teacherTimeSlotVar, List.of(teacherId, "" + i)))
                    .map(Variable_BinaryImpl::new)
                    .forEach(_milpModelBuilder::addVariable);
        }
    }

    private void addTeacherDayVariables() {
        _nameSpace.registerVariableName(_teacherDayVar,2);
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            IntFunction<String> toVarLabel =
                    i -> _nameSpace.getVariableName(_teacherDayVar, List.of(teacherId, "" + i));
            List<Variable> vars = _variableFactory.createBinaryVariableArray(_daysOfWeek.size(), toVarLabel);
            vars.forEach(_milpModelBuilder::addVariable);
        }
    }

    private void addTeacherDayStartVariables() {
        _nameSpace.registerVariableName(_teacherDayStartVar,2);
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _dayOfWeekTimeSlots
                    .forEach((dayOfWeek, timeSlots) -> {
                        String i = "" + _daysOfWeek.indexOf(dayOfWeek);
                        String varName = _nameSpace.getVariableName(_teacherDayStartVar, List.of(teacherId, "" + i));
                        int numAvailableTimeSlots = (int)timeSlots.stream()
                                .filter(getTeacherTimeslotsPredicate(teacher))
                                .count();
                        int ub = numAvailableTimeSlots > 0 ? timeSlots.size() : 0;
                        Variable variable = new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(ub));
                        _milpModelBuilder.addVariable(variable);
                    });
        }
    }

    private void addTeacherDayEndVariables() {
        _nameSpace.registerVariableName(_teacherDayEndVar,2);
        for(Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _dayOfWeekTimeSlots
                    .forEach((dayOfWeek, timeSlots) -> {
                        String i = "" + _daysOfWeek.indexOf(dayOfWeek);
                        String varName = _nameSpace.getVariableName(_teacherDayEndVar, List.of(teacherId, "" + i));
                        int numAvailableTimeSlots = (int)timeSlots.stream()
                                .filter(getTeacherTimeslotsPredicate(teacher))
                                .count();
                        int ub = numAvailableTimeSlots > 0 ? timeSlots.size() : 0;
                        Variable variable = new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(ub));
                        _milpModelBuilder.addVariable(variable);
                    });
        }
    }

    private Predicate<WeeklyTimeSlot> getTeacherTimeslotsPredicate(Teacher teacher) {
      //  return  wts -> true;

        Optional<ForbiddenTimeConstraint<Teacher>> ftConstraintOpt = _schedulingInput.getTeacherForbiddenTimeSlots(teacher);
        return ftConstraintOpt.isPresent() ? ftConstraintOpt.get()::isNotForbidden : ts -> true;
    }

    private Predicate<WeeklyTimeSlot> getGroupFeasibleTimeslotsPredicate(Group group) {
        Optional<ForbiddenTimeConstraint<Group>> ftConstraintOpt = _schedulingInput.getGroupForbiddenTimeSlots(group);
        return ftConstraintOpt.isPresent() ? ftConstraintOpt.get()::isNotForbidden : ts -> true;
    }

    private Predicate<WeeklyTimeSlot> getGroupMandatoryTimeslotsPredicate(Group group) {
        Optional<MandatoryTimeConstraint> ftConstraintOpt = _schedulingInput.getMandatorySlotsForGroup(group);
        return ftConstraintOpt.isPresent() ? ftConstraintOpt.get()::isMandatory : ts -> false;
    }



    private void addGroupTimeslotVariables() {

        _nameSpace.registerVariableName(_groupTimeSlotVar,2);
        for(Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            // non-fixed
            _timeSlotSequence.stream()
                    .filter(getGroupFeasibleTimeslotsPredicate(group))
                    .filter(ts -> !getGroupMandatoryTimeslotsPredicate(group).test(ts))
                    .map(_timeSlotToIdMap::get)
                    .map(i -> _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + i)))
                    .map(Variable_BinaryImpl::new)
                    .forEach(_milpModelBuilder::addVariable);
            // mandatory fixed
            _timeSlotSequence.stream()
                    .filter(getGroupFeasibleTimeslotsPredicate(group))
                    .filter(getGroupMandatoryTimeslotsPredicate(group))
                    .map(_timeSlotToIdMap::get)
                    .map(i -> _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + i)))
                    .map(varName -> new Variable_IntegerImpl(varName, OptionalInt.of(1),OptionalInt.of(1)))
                    .forEach(_milpModelBuilder::addVariable);
        }
    }

    private void addGroupDayVariables() {
        _nameSpace.registerVariableName(_groupDayVar,2);
        for(Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            IntFunction<String> toVarLabel =
                    i -> _nameSpace.getVariableName(_groupDayVar, List.of(groupId, "" + i));
            List<Variable> vars = _variableFactory.createBinaryVariableArray(_daysOfWeek.size(), toVarLabel);
            vars.forEach(_milpModelBuilder::addVariable);
        }
    }

    private void addLessonRequestPlaceVariables() {
        _nameSpace.registerVariableName(_lesReqOccupIdPlaceVar,3);
        for(LessonRequest lessonRequest : _lessonRequests) {
            lessonRequest.getOccupations()
                    .forEach(occupation -> {
                        occupation
                                .getCandidatePlaces()
                                .forEachOrdered(place -> {
                                    String placeId = "" + _places.indexOf(place);
                                    var list = List.of("" + lessonRequest.getId(), "" + occupation.getId(), placeId);
                                    String varLabel = _nameSpace.getVariableName(_lesReqOccupIdPlaceVar, list);
                                    Variable variable = new Variable_BinaryImpl(varLabel);
                                    _milpModelBuilder.addVariable(variable);
                                });
                    });
        }
    }

    private void addLessonRequestTimeslotVariables() {
        _nameSpace.registerVariableName(_lesReqTimeSlotVar,2);

        for(LessonRequest lessonRequest : _lessonRequests) {
            _lessonRequestAvailableTimeSlots.get(lessonRequest)
                    .stream()
                    .map(_timeSlotToIdMap::get)
                    .map(t -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of("" + lessonRequest.getId(), "" + t)))
                    .map(Variable_BinaryImpl::new)
                    .forEach(_milpModelBuilder::addVariable);
        }
    }



    // CONSTRAINTS
    private void addGroupMaxDailyLoadConstrains() {
        for(var entry : _groupDayMaxLoad.entrySet()) {
            Group group = entry.getKey();
            var groupMap = entry.getValue();
            groupMap.forEach((key, value) -> createGroupDailyLoadConstraint(group, key, value));
        }
    }

    private void createGroupDailyLoadConstraint(Group group, DayOfWeek dayOfWeek, Integer ub) {
        String groupId = "" + _groups.indexOf(group);
        Stream<WeeklyTimeSlot> timeSlotStream = _dayOfWeekTimeSlots.containsKey(dayOfWeek) ?
                _dayOfWeekTimeSlots.get(dayOfWeek).stream() : Stream.empty();
        Stream<String> groupTimeslotVarNamesStream = timeSlotStream
                .map(_timeSlotToIdMap::get)
                .map(tsId -> List.of(groupId, tsId))
                .map(list -> _nameSpace.getVariableName(_groupTimeSlotVar,list));
        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), groupTimeslotVarNamesStream, ub);
        _milpModelBuilder.addConstraint(constraint);
    }


    private void addMaxDaysPerTeacherConstraints() {
        int maxDays = _dayOfWeekTimeSlots.keySet().size();

        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            int maxDaysForTeacher = _maxDaysOfWeekPerTeacher.get(teacher);
            if(maxDaysForTeacher < maxDays) {
                Stream<String> teacherDayVarsStream = _dayOfWeekTimeSlots.keySet()
                        .stream()
                        .map(_daysOfWeek::indexOf)
                        .map(i -> List.of(teacherId, "" + i))
                        .map(list -> _nameSpace.getVariableName(_teacherDayVar, list));
                var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), teacherDayVarsStream, maxDaysForTeacher);
                _milpModelBuilder.addConstraint(constraint);
            }
        }
    }

    private void addMaxDaysPerGroupConstraints() {
        int maxDays = _dayOfWeekTimeSlots.keySet().size();

        for (Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            int maxDaysForGroup = _maxDaysOfWeekPerGroup.get(group);
            if(maxDaysForGroup < maxDays) {
                Stream<String> groupDayVarsStream = _dayOfWeekTimeSlots.keySet()
                        .stream()
                        .map(_daysOfWeek::indexOf)
                        .map(i -> List.of(groupId, "" + i))
                        .map(list -> _nameSpace.getVariableName(_groupDayVar, list));
                var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), groupDayVarsStream, maxDaysForGroup);
                _milpModelBuilder.addConstraint(constraint);
            }
        }
    }

    private void addTeacherTimeslotDayConstraints() {
        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            for(var entry : _dayOfWeekTimeSlots.entrySet()) {
                DayOfWeek dayOfWeek = entry.getKey();
                List<String> tdList = List.of(teacherId, "" + _daysOfWeek.indexOf(dayOfWeek));
                String teacherDayOfWeekVarLabel = _nameSpace.getVariableName(_teacherDayVar, tdList);

                List<WeeklyTimeSlot> timeSlots = entry.getValue();

                Stream<String> timeSlotVarLabels = timeSlots
                        .stream()
                        .filter(getTeacherTimeslotsPredicate(teacher))
                        .map(timeSlot -> List.of(teacherId, _timeSlotToIdMap.get(timeSlot)))
                        .map(list -> _nameSpace.getVariableName(_teacherTimeSlotVar, list));
                var constraint = _constraintFactory.createBinaryBlockingConstraint(getNextConstraintLabel(), teacherDayOfWeekVarLabel, timeSlotVarLabels);
                _milpModelBuilder.addConstraint(constraint);
            }
        }
    }

    private void addTeacherTimeslotDayStartEndConstraints() {
        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            for(var entry : _dayOfWeekTimeSlots.entrySet()) {
                DayOfWeek dayOfWeek = entry.getKey();
                int i = _daysOfWeek.indexOf(dayOfWeek);
                String dayStartVarName = _nameSpace.getVariableName(_teacherDayStartVar, List.of(teacherId, "" + i));
                String dayEndVarName = _nameSpace.getVariableName(_teacherDayEndVar, List.of(teacherId, "" + i));

                List<WeeklyTimeSlot> timeSlots = entry.getValue();
                var predicate = getTeacherTimeslotsPredicate(teacher);
                for(int t=0; t < timeSlots.size(); t++) {
                    WeeklyTimeSlot timeSlot = timeSlots.get(t);
                    if(predicate.test(timeSlot)) {
                        var list = List.of(teacherId, _timeSlotToIdMap.get(timeSlot));
                       var timeSlotVarName =  _nameSpace.getVariableName(_teacherTimeSlotVar, list);
                        addTimeSlotDayEndConstraint(timeSlotVarName, dayEndVarName, t);
                        addTimeSlotDayStartConstraint(timeSlotVarName, dayStartVarName, t, timeSlots.size());
                    }
                }
                addTimeSlotsSumConstraint(teacherId, timeSlots, dayStartVarName, dayEndVarName);
            }
        }
    }

    private void addTimeSlotsSumConstraint(String teacherId, List<WeeklyTimeSlot> timeSlots, String dayStartVar, String dayEndVar) {
        //sum ts <= dayEndVarName - dayStartVarName
        Stream<Pair<String, Double>> timeSlotsStream = timeSlots
                .stream()
                .map(timeSlot -> List.of(teacherId, _timeSlotToIdMap.get(timeSlot)))
                .map(list -> _nameSpace.getVariableName(_teacherTimeSlotVar, list))
                .map(str -> new Pair<>(str, 1.0));

        Stream<Pair<String, Double>> dayStartEndStream = Stream.of(
                new Pair<>(dayStartVar, 1.0),
                new Pair<>(dayEndVar, -1.0)
        );

        Stream<Pair<String, Double>> stream = Stream.concat(timeSlotsStream, dayStartEndStream);
        LinearExpression expr = new LinearExpressionImpl(stream, 0.0);
        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expr,ConstraintType.LESS_OR_EQUAL,0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void addTimeSlotDayEndConstraint(String timeSlotVar, String dayEndVar, int timeSlotDayOrdNum) {
        //ts * (t+1)    <= dayEndVar
        Stream<Pair<String, Double>> pairStream = Stream.of(
                new Pair<>(timeSlotVar, 1.0*(timeSlotDayOrdNum + 1)),
                new Pair<>(dayEndVar, -1.0)
        );
        LinearExpression expr = new LinearExpressionImpl(pairStream, 0.0);
        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expr,ConstraintType.LESS_OR_EQUAL,0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void addTimeSlotDayStartConstraint(String timeSlotVar, String dayStartVar, int timeSlotDayOrdNum, int slotsInDay) {
        //(slotsInDay - t) * ts <= slotsInDay - dayStartVar
        Stream<Pair<String, Double>> pairStream = Stream.of(
                new Pair<>(timeSlotVar, 1.0*(slotsInDay - timeSlotDayOrdNum)),
                new Pair<>(dayStartVar, 1.0)
        );
        LinearExpression expr = new LinearExpressionImpl(pairStream, 0.0);
        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expr,ConstraintType.LESS_OR_EQUAL,1.0*slotsInDay);
        _milpModelBuilder.addConstraint(constraint);
    }


    private void addGroupTimeslotDayConstraints() {
        for (Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            for(var entry : _dayOfWeekTimeSlots.entrySet()) {
                DayOfWeek dayOfWeek = entry.getKey();
                List<String> gdList = List.of(groupId, "" + _daysOfWeek.indexOf(dayOfWeek));
                String groupDayOfWeekVarLabel = _nameSpace.getVariableName(_groupDayVar, gdList);

                List<WeeklyTimeSlot> timeSlots = entry.getValue();
                Stream<String> timeSlotVarLabels = timeSlots
                        .stream()
                        .filter(getGroupFeasibleTimeslotsPredicate(group))
                        .map(timeSlot -> List.of(group.getName(), _timeSlotToIdMap.get(timeSlot)))
                        .map(list -> _nameSpace.getVariableName(_groupTimeSlotVar, list));
                var constraint = _constraintFactory.createBinaryBlockingConstraint(getNextConstraintLabel(), groupDayOfWeekVarLabel, timeSlotVarLabels);
                _milpModelBuilder.addConstraint(constraint);
            }
        }
    }

    private void addNotMoreThanOneLessonInPlaceConstraints() {
        for(Place place : _places) {
            String placeId = "" + _places.indexOf(place);
            for(int t=0; t < _numTimeSlots; t++) {
                final String timeSlot = "" + t;

                List<String> auxVarLabelsList = _lessonRequests
                        .stream()
                        .filter(request -> ifTimeslotAvailableForLessonRequest(request, timeSlot))
                        .flatMap(lessonsRequest -> {
                            String requestId = "" + lessonsRequest.getId();
                            return lessonsRequest.getOccupations()

                                    .filter(occupation -> occupation.getCandidatePlaces().anyMatch(place::equals))
                                    .map(occupation -> {
                                        String auxVar = _nameSpace.getNextAuxVarName();
                                        _milpModelBuilder.addVariable(new Variable_BinaryImpl(auxVar));
                                        String occupationId = "" + occupation.getId();
                                        String lessonOccupPlaceVar = _nameSpace.getVariableName(_lesReqOccupIdPlaceVar, List.of(requestId, occupationId, placeId));
                                        String lessonTimeVar = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(requestId, timeSlot));
                                        // add linearizing constraint
                                        Stream<Pair<String, Double>> pairStream = Stream.of(
                                                new Pair<>(lessonTimeVar, 1.0),
                                                new Pair<>(lessonOccupPlaceVar, 1.0),
                                                new Pair<>(auxVar, -1.0)
                                        );
                                        LinearExpression expr = new LinearExpressionImpl(pairStream, 0.0);
                                        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expr,ConstraintType.LESS_OR_EQUAL,1.0);
                                        _milpModelBuilder.addConstraint(constraint);
                                        return auxVar;
                                    });


                        })
                        .toList();
                var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), auxVarLabelsList.stream(),1.0);
                _milpModelBuilder.addConstraint(constraint);
            }
        }
    }

    private void addTeacherOneLessonConstraints() {
        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _timeSlotSequence
                    .stream()
                    .filter(getTeacherTimeslotsPredicate(teacher))
                    .map(_timeSlotToIdMap::get)
                    .forEach(t -> {
                        final String timeSlot = "" + t;
                        List<String> teacherTimelist = List.of(teacherId, timeSlot);
                        String teacherVarLabel = _nameSpace.getVariableName(_teacherTimeSlotVar, teacherTimelist);

                        Stream<String> lesReqVarLabels = _lessonRequests
                                .stream()
                                .filter(request -> request.getOccupations()
                                        .flatMap(LessonRequestOccupation::getOccupiedTeachers)
                                        .anyMatch(teacher::equals))
                                .filter(request -> ifTimeslotAvailableForLessonRequest(request, timeSlot))
                                .map(request -> List.of("" + request.getId(), timeSlot))
                                .map(list -> _nameSpace.getVariableName(_lesReqTimeSlotVar, list));
                        var constraint = _constraintFactory.createSelectionConstraint(getNextConstraintLabel(), teacherVarLabel, lesReqVarLabels);
                        _milpModelBuilder.addConstraint(constraint);
                    });
        }
    }

    private boolean ifTimeslotAvailableForLessonRequest(LessonRequest request, String t) {
       return  _lessonRequestAvailableTimeSlots.get(request)
                .stream()
                .map(_timeSlotToIdMap::get)
                .anyMatch(t::equals);
    }

    private void addGroupOneLessonConstraints() {
        for (Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            _timeSlotSequence
                    .stream()
                    .filter(getGroupFeasibleTimeslotsPredicate(group))
                    .map(_timeSlotToIdMap::get)
                    .forEach( t -> {
                        final String timeSlot = "" + t;
                        List<String> groupTimeList = List.of(groupId, timeSlot);
                        String groupTimeVarLabel = _nameSpace.getVariableName(_groupTimeSlotVar, groupTimeList);

                        Stream<String> lesReqVarLabels = _lessonRequests
                                .stream()
                                .filter(request -> request.getGroup().equals(group))
                                .filter(request -> ifTimeslotAvailableForLessonRequest(request, timeSlot))
                                .map(request -> List.of("" + request.getId(), timeSlot))
                                .map(list -> _nameSpace.getVariableName(_lesReqTimeSlotVar, list));
                        var constraint = _constraintFactory.createSelectionConstraint(getNextConstraintLabel(), groupTimeVarLabel, lesReqVarLabels);
                        _milpModelBuilder.addConstraint(constraint);
                    });
        }
    }

    private void addGroupDailyContinuityConstraints() {
        for (Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            var predicate = getGroupFeasibleTimeslotsPredicate(group);
            for(var entry : _dayOfWeekTimeSlots.entrySet()) {
                List<WeeklyTimeSlot> timeSlots = entry.getValue();
                int nSlots = timeSlots.size();
                for(int i = 1; i < nSlots-1; i++) {
                    WeeklyTimeSlot timeSlot = timeSlots.get(i);
                    if(predicate.test(timeSlot)) {
                        int timeSlotId = Integer.parseInt(_timeSlotToIdMap.get(timeSlot));
                        create1before0implies0after_group(groupId, timeSlots, predicate, nSlots, i, timeSlotId);
                        create1after0implies0before_group(groupId, timeSlots, predicate, nSlots, i, timeSlotId);
                        create0before0afterImpliesAllZeros_group(groupId, timeSlots, predicate, nSlots, i, timeSlotId);
                    } else {
                        if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                            int timeSlotId = Integer.parseInt(_timeSlotToIdMap.get(timeSlot));

                            String prev = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId - 1)));
                            String next = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId + 1)));
                            // x_{i-1} - x_i + x_{i+1} <= 1
                            Stream<Pair<String, Double>> stream = Stream.of(
                                    new Pair<>(prev, 1.0),
                                    new Pair<>(next, 1.0)
                            );
                            LinearExpression expression = new LinearExpressionImpl(stream, 0.0);
                            LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.LESS_OR_EQUAL, 1);
                            _milpModelBuilder.addConstraint(constraint);
                        }
                    }
                }
            }
        }
    }
    private void addLessonRequestDayContinuityConstraints() {
        for (LessonRequest lessonRequest : _lessonRequests) {
            String lrId = "" + lessonRequest.getId();
            Predicate<WeeklyTimeSlot> predicate = wts -> ifTimeslotAvailableForLessonRequest(lessonRequest, _timeSlotToIdMap.get(wts));
            for(var entry : _dayOfWeekTimeSlots.entrySet()) {
                List<WeeklyTimeSlot> timeSlots = entry.getValue();
                int nSlots = timeSlots.size();
                for(int i = 1; i < nSlots-1; i++) {
                    WeeklyTimeSlot timeSlot = timeSlots.get(i);
                    if(predicate.test(timeSlot)) {
                        if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                            int timeSlotId = Integer.parseInt(_timeSlotToIdMap.get(timeSlot));
                            create0before0afterImpliesAllZeros_lessonRequest(lrId, timeSlots, predicate, nSlots, i, timeSlotId);
                            create1before0implies0after_lessonRequest(lrId, timeSlots, predicate, nSlots, i, timeSlotId);
                            create1after0implies0before_lessonRequest(lrId, timeSlots, predicate, nSlots, i, timeSlotId);
                        }
                    } else {
                        if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                            int timeSlotId = Integer.parseInt(_timeSlotToIdMap.get(timeSlot));

                            String prev = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lrId, "" + (timeSlotId - 1)));
                            String next = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lrId, "" + (timeSlotId + 1)));
                            // x_{i-1} - x_i + x_{i+1} <= 1
                            Stream<Pair<String, Double>> stream = Stream.of(
                                    new Pair<>(prev, 1.0),
                                    new Pair<>(next, 1.0)
                            );
                            LinearExpression expression = new LinearExpressionImpl(stream, 0.0);
                            LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.LESS_OR_EQUAL, 1);
                            _milpModelBuilder.addConstraint(constraint);
                        }
                    }
                }
            }
        }
    }


    private void create1after0implies0before_group(String groupId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (x_{i} + 1 - x{i+1})*(i) >= \sum_{t = 0}^i x_{t}
        String current = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + timeSlotId));
        String next = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId + 1)));
        Stream<String> allBefore = IntStream.range(0, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_timeSlotToIdMap::get)
                .map(tsId -> _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + tsId)));
        int numAllBefore = (int)IntStream.range(0, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(
                        new Pair<>(current,  1.0*numAllBefore),
                        new Pair<>(next, -1.0*numAllBefore)),
                allBefore
                        .map(tsId -> new Pair<>(tsId, -1.0)));
        LinearExpression expression = new LinearExpressionImpl(stream, numAllBefore);

        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void create1before0implies0after_group(String groupId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (x_{i} + 1 - x{i-1})*(n-i-1) >= \sum_{t = i+1}^n x_{t}
        String prev = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId - 1)));
        String current = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + timeSlotId));
        Stream<String> allNext = IntStream.range(i +1, nSlots)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_timeSlotToIdMap::get)
                .map(tsId -> _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + tsId)));
        int numAllNext = (int)IntStream.range(i +1, nSlots)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(
                new Pair<>(prev,  1.0*numAllNext),
                new Pair<>(current, 1.0*numAllNext)),
                allNext
                        .map(tsId -> new Pair<>(tsId, -1.0)));
        LinearExpression expression = new LinearExpressionImpl(stream, numAllNext);

        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void create0before0afterImpliesAllZeros_group(String groupId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (1 + x_{i-1} + x_{i+1} - x_{i})*(n-3) >= \sum_{t = 1}^{i-2} x_{t} + \sum{t = i+2}^{n} x_{t}
        String prev = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId - 1)));
        String current = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + timeSlotId));
        String next = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId + 1)));
        Stream<String> allOthers = IntStream.concat(IntStream.range(0, i-1), IntStream.range(i +2, nSlots))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_timeSlotToIdMap::get)
                .map(tsId -> _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + tsId)));
        int numOthers = (int)IntStream.concat(IntStream.range(0, i-1), IntStream.range(i +2, nSlots))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();
        if(numOthers > 0) {
            Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(
                            new Pair<>(prev, 1.0 * numOthers),
                            new Pair<>(next, 1.0 * numOthers),
                            new Pair<>(current, -1.0 * numOthers)),
                    allOthers
                            .map(tsId -> new Pair<>(tsId, -1.0))
            );
            LinearExpression expression = new LinearExpressionImpl(stream, numOthers);

            LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private void create1after0implies0before_lessonRequest(String lesReqId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (x_{i} + 1 - x{i+1})*(i) >= \sum_{t = 0}^i x_{t}
        String current = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + timeSlotId));
        String next = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + (timeSlotId + 1)));
        Stream<String> allBefore = IntStream.range(0, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_timeSlotToIdMap::get)
                .map(tsId -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + tsId)));
        int numAllBefore = (int)IntStream.range(0, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(
                        new Pair<>(current,  1.0*numAllBefore),
                        new Pair<>(next, -1.0*numAllBefore)),
                allBefore
                        .map(tsId -> new Pair<>(tsId, -1.0)));
        LinearExpression expression = new LinearExpressionImpl(stream, numAllBefore);

        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void create0before0afterImpliesAllZeros_lessonRequest(String lesReqId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (1 + x_{i-1} + x_{i+1} - x_{i})*(n-3) >= \sum_{t = 1}^{i-2} x_{t} + \sum{t = i+2}^{n} x_{t}
        String prev = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + (timeSlotId - 1)));
        String current = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + timeSlotId));
        String next = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + (timeSlotId + 1)));
        Stream<String> allOthers = IntStream.concat(IntStream.range(0, i-1), IntStream.range(i +2, nSlots))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_timeSlotToIdMap::get)
                .map(tsId -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + tsId)));
        int numOthers = (int)IntStream.concat(IntStream.range(0, i-1), IntStream.range(i +2, nSlots))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        if(numOthers > 0) {
            Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(
                            new Pair<>(prev, 1.0 * numOthers),
                            new Pair<>(next, 1.0 * numOthers),
                            new Pair<>(current, -1.0 * numOthers)),
                    allOthers
                            .map(tsId -> new Pair<>(tsId, -1.0))
            );
            LinearExpression expression = new LinearExpressionImpl(stream, numOthers);

            LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private void create1before0implies0after_lessonRequest(String lesReqId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (x_{i} + 1 - x{i-1})*(n-i-1) >= \sum_{t = i+1}^n x_{t}
        String prev = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + (timeSlotId - 1)));
        String current = _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + timeSlotId));
        Stream<String> allNext = IntStream.range(i +1, nSlots)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_timeSlotToIdMap::get)
                .map(tsId -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of(lesReqId, "" + tsId)));
        int numAllNext = (int)IntStream.range(i +1, nSlots)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(
                        new Pair<>(prev,  1.0*numAllNext),
                        new Pair<>(current, 1.0*numAllNext)),
                allNext
                        .map(tsId -> new Pair<>(tsId, -1.0)));
        LinearExpression expression = new LinearExpressionImpl(stream, numAllNext);

        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void addTeacherPlacesNumConstrains() {
        for(Teacher teacher: _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            Stream<Pair<String, Double>> placeVars = _lessonRequests.stream()
                    .flatMap(lessonsRequest -> {
                        String lrName = "" + lessonsRequest.getId();
                        return lessonsRequest.getOccupations()
                                .filter(occupation -> occupation.getOccupiedTeachers().anyMatch(teacher::equals))
                                .flatMap(occupation -> {
                                    String occupationId = "" + occupation.getId();
                                    return occupation.getCandidatePlaces()
                                            .map(_places::indexOf)
                                            .map(Object::toString)
                                            .map(placeId -> List.of(lrName, occupationId, placeId))
                                            .map(list -> _nameSpace.getVariableName(_lesReqOccupIdPlaceVar, list));
                                });
                    })
                    .map(varName -> new Pair<>(varName, 1.0));
            String placesPerTeacherVarName = _nameSpace.getVariableName(_teacherPlacesNum, List.of(teacherId));
            Pair<String, Double> placesPerTeacherVar = new Pair<>(placesPerTeacherVarName, -1.0);
            Stream<Pair<String, Double>> stream = Stream.concat(placeVars, Stream.of(placesPerTeacherVar));
            LinearExpression expression = new LinearExpressionImpl(stream, 0.0);
            LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.LESS_OR_EQUAL, 0);
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private void addAllRequestsShouldBeSatisfiedByTimeslots() {
        for(LessonRequest lessonRequest :_lessonRequests) {
            Stream<String> lessonRequestVar = _lessonRequestAvailableTimeSlots.get(lessonRequest)
                    .stream()
                    .map(_timeSlotToIdMap::get)
                    .map(t -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of("" + lessonRequest.getId(), t)));
            var constraint = _constraintFactory.createVarSumEquals(getNextConstraintLabel(),lessonRequestVar, lessonRequest.getCourseInProgram().getLessonsPerWeek());
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private void addAllRequestsShouldBeSatisfiedByPlaces() {
        for(LessonRequest lessonRequest :_lessonRequests) {
            String lrName = "" + lessonRequest.getId();
            lessonRequest.getOccupations()
                    .forEach(occupation -> {
                        String occupationId = "" + occupation.getId();
                        Stream<String> lessonRequestOccupationVars = occupation.getCandidatePlaces()
                                .map(_places::indexOf)
                                .map(Object::toString)
                                .map(placeId -> List.of(lrName, occupationId, placeId))
                                .map(list -> _nameSpace.getVariableName(_lesReqOccupIdPlaceVar, list));
                        var constraint = _constraintFactory.createAssignmentConstraint(getNextConstraintLabel(),lessonRequestOccupationVars);
                        _milpModelBuilder.addConstraint(constraint);
                    });
        }
    }

    private void addMaxLessonsPerDayForLessonRequestConstraints() {
        for(LessonRequest lessonRequest :_lessonRequests) {
            int maxPerDay = lessonRequest.getCourseInProgram().getMaxLessonsPerDay();
            _dayOfWeekTimeSlots.values()
                    .forEach(list -> {
                        List<String> lessonRequestVarList = list.stream()
                                .map(_timeSlotToIdMap::get)
                                .filter(tsId -> ifTimeslotAvailableForLessonRequest(lessonRequest,tsId))
                                .map(t -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of("" + lessonRequest.getId(), t)))
                                .toList();
                        if(lessonRequestVarList.size() > 0) {
                            var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(),lessonRequestVarList.stream(), maxPerDay);
                            _milpModelBuilder.addConstraint(constraint);
                        }
                    });
        }
    }

    private void addIntersectedGroupsSameTimeConstraints() {
        for(int t=0; t < _numTimeSlots; t++) {
            final int timeSlot = t;
            _intersectedGroupsStreamSupplier
                    .get()
                    .forEach(pair -> {
                        String varGroup1Label = getVarLabel_GroupTimeslot(pair.getKey(), timeSlot);
                        String varGroup2Label = getVarLabel_GroupTimeslot(pair.getValue(), timeSlot);
                        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(),Stream.of(varGroup1Label, varGroup2Label), 1);
                        _milpModelBuilder.addConstraint(constraint);
                    });
        }
    }

    private void addObjectiveMinTeacherDays() {
        Stream<Pair<String, Double>> stream = _teachers.stream()
                .flatMap(teacher -> {
                    return IntStream.range(0, _daysOfWeek.size())
                            .mapToObj(i -> _nameSpace.getVariableName(_teacherDayVar, List.of(teacher.getName(), "" + i)));
                })
                .map(str -> new Pair<>(str,1.0));
        LinearExpression expression = new LinearExpressionImpl(stream,0.0);
        _milpModelBuilder.setObjective(expression, ObjectiveType.MINIMIZATION);
    }

    private void addObjectiveMinTeacherDaysMinTeacherPlaces(double coeff1, double coeff2) {
        Stream<Pair<String, Double>> teacherDaysStream = _teachers.stream()
                .flatMap(teacher -> {
                    String teacherId = "" + _teachers.indexOf(teacher);
                    return IntStream.range(0, _daysOfWeek.size())
                            .mapToObj(i -> _nameSpace.getVariableName(_teacherDayVar, List.of(teacherId, "" + i)));
                })
                .map(str -> new Pair<>(str,coeff1));

        Stream<Pair<String, Double>> teacherPlacesStream = _teachers.stream()
                .map(_teachers::indexOf)
                .map(Object::toString)
                .map(teacherId -> _nameSpace.getVariableName(_teacherPlacesNum, List.of(teacherId)))
                .map(str -> new Pair<>(str,coeff2));
        Stream<Pair<String, Double>> stream = Stream.concat(teacherDaysStream, teacherPlacesStream);
        LinearExpression expression = new LinearExpressionImpl(stream,0.0);
        _milpModelBuilder.setObjective(expression, ObjectiveType.MINIMIZATION);
    }

    private void addObjectiveMinTeacherDaysMinTeacherPlacesMinComplexityViolation(double coeffTeacherDays, double coeffTeacherCabinets, double coeffComplexityViolation) {
        createComplexityViolationVariables();
        Stream<Pair<String, Double>> complexityViolationsStream = _groups.stream()
                .flatMap(group -> {
                        String groupId = "" + _groups.indexOf(group);
                        return   IntStream.range(0, _daysOfWeek.size())
                                .boxed()
                                .map(i -> _nameSpace.getVariableName(_complexityOverloadGroupDay, List.of(groupId, "" + i)));
                })
                .map(varName -> new Pair<>(varName, coeffComplexityViolation));

        Stream<Pair<String, Double>> teacherDaysStream = _teachers.stream()
                .flatMap(teacher -> {
                    String teacherId = "" + _teachers.indexOf(teacher);
                    return IntStream.range(0, _daysOfWeek.size())
                            .mapToObj(i -> _nameSpace.getVariableName(_teacherDayVar, List.of(teacherId, "" + i)));
                })
                .map(str -> new Pair<>(str,coeffTeacherDays));

        Stream<Pair<String, Double>> teacherPlacesStream = _teachers.stream()
                .map(_teachers::indexOf)
                .map(Object::toString)
                .map(teacherId -> _nameSpace.getVariableName(_teacherPlacesNum, List.of(teacherId)))
                .map(str -> new Pair<>(str,coeffTeacherCabinets));
        Stream<Pair<String, Double>> stream = Stream.concat(Stream.concat(complexityViolationsStream, teacherDaysStream), teacherPlacesStream);
        LinearExpression expression = new LinearExpressionImpl(stream,0.0);
        _milpModelBuilder.setObjective(expression, ObjectiveType.MINIMIZATION);
    }

    private void addObjectiveMinTeacherWindows(double coeffTeacherWindows) {
        Stream<Pair<String, Double>> teacherDaysStartEndStream = _teachers.stream()
                .flatMap(teacher -> {
                    String teacherId = "" + _teachers.indexOf(teacher);
                    return IntStream.range(0, _daysOfWeek.size())
                            .boxed()
                            .flatMap(i -> {
                                var dayStart = _nameSpace.getVariableName(_teacherDayStartVar, List.of(teacherId, "" + i));
                                var dayEnd = _nameSpace.getVariableName(_teacherDayEndVar, List.of(teacherId, "" + i));
                                return Stream.of(
                                    new Pair<>(dayStart, -1.0*coeffTeacherWindows),
                                    new Pair<>(dayEnd, coeffTeacherWindows)
                                );
                            });
                });

        Stream<Pair<String, Double>> stream = teacherDaysStartEndStream;
        LinearExpression expression = new LinearExpressionImpl(stream,0.0);
        _milpModelBuilder.setObjective(expression, ObjectiveType.MINIMIZATION);
    }

    private void createComplexityViolationVariables() {
        _nameSpace.registerVariableName(_complexityOverloadGroupDay,2);
        for(Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            for(int i=0; i < _daysOfWeek.size(); i++) {
                String label = _nameSpace.getVariableName(_complexityOverloadGroupDay, List.of(groupId, "" + i));
                Variable variable = new Variable_IntegerImpl(label,OptionalInt.of(0), OptionalInt.of(100)); //TODO 200 should be chaged on max complexity violation ub
                _milpModelBuilder.addVariable(variable);
                addGroupDayComplexityViolationConstraint(group, i);
            }
        }
    }

    private void addGroupDayComplexityViolationConstraint(Group group, int dayId) {
        DayOfWeek dayOfWeek = _daysOfWeek.get(dayId);
        List<WeeklyTimeSlot> dayTimeSlots = _dayOfWeekTimeSlots.get(dayOfWeek)
                .stream()
                .filter(getGroupFeasibleTimeslotsPredicate(group))
                .toList();
        String groupId = "" + _groups.indexOf(group);
        String label = _nameSpace.getVariableName(_complexityOverloadGroupDay, List.of(groupId, "" + dayId));

        // z_{g,d} <= \sum
        Stream<Pair<String, Double>> lesReqPairs = _lessonRequests.stream()
                .filter(lessonsRequest -> lessonsRequest.getGroup().equals(group))
                .flatMap(lessonsRequest -> {
                    String lesReqId = "" +lessonsRequest.getId();
                    return dayTimeSlots
                            .stream()
                            .map(_timeSlotToIdMap::get)
                            .filter(ts -> ifTimeslotAvailableForLessonRequest(lessonsRequest,ts))
                            .map(ts -> List.of(lesReqId, ts))
                            .map(list -> _nameSpace.getVariableName(_lesReqTimeSlotVar, list))
                            .map(varName -> new Pair<>(varName, -1.0 *lessonsRequest.getCourseInProgram().getComplexity()));
                });
        Stream<Pair<String, Double>> stream = Stream.concat(Stream.of(new Pair<>(label, 1.0)),lesReqPairs);
        LinearExpression expression = new LinearExpressionImpl(stream, 0.0);
        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.LESS_OR_EQUAL, 0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private synchronized String getNextConstraintLabel() {
        String result = "C_"+ _nextConstraintId;
        _nextConstraintId++;
        return result;
    }


}

