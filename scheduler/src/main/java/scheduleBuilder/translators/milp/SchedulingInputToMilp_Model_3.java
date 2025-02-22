package scheduleBuilder.translators.milp;

import group.Group;
import lesson.Lesson;
import lesson.LessonImpl;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import model.*;
import model.constraint.*;
import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import model.variables.Variable;
import model.variables.Variable_BinaryImpl;
import model.variables.Variable_IntegerImpl;
import pair.Pair;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.SchedulingInputV2;
import solution.MilpSolution;
import time.WeeklyTimeSlot;
import triplet.Triplet;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SchedulingInputToMilp_Model_3 implements MilpTranslatorV2 {
    private final static LinearConstraintFactory _constraintFactory = new LinearConstraintFactoryImpl();
    private int _nextConstraintId = 0;
    private VariableNameSpace _nameSpace = new VariableNameSpaceImpl();
    private MilpModelBuilder _milpModelBuilder;

    private List<WeeklyTimeSlot> _timeSlotSequence;
    private Set<DayOfWeek> _daysOfWeek;
    private List<Teacher> _teachers;
    private List<Place> _places;
    private List<Group> _groups;
    private List<LessonRequest> _lessonRequests;
    private SchedulingInputV2 _schedulingInput;

    private final static String _lesReqOccupIdPlaceVar = "LROP";
    private final static String _lesReqTimeSlotVar = "LRT";
    private final static String _groupTimeSlotVar = "GT";
    private final static String _teacherTimeSlotVar = "WT";
    private final static String _teacherDayVar = "WD";
    private final static String _teacherTimeWindowVar = "WTW";
    private final static String _teacherDayStartVar = "WDS";
    private final static String _teacherDayEndVar = "WDE";
    private final static String _complexityOverloadGroupDay = "COGD";
    private final static String _teacherPlacesNum = "TPN";


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

    public MilpModel createModel(SchedulingInputV2 schedulingInput) {
        _milpModelBuilder = new MilpModelBuilderImpl("MyModel");
        _schedulingInput = schedulingInput;
        _timeSlotSequence = schedulingInput.getTimeslotSequence();
        _daysOfWeek = _timeSlotSequence.stream()
                .map(WeeklyTimeSlot::getDayOfWeek)
                .collect(Collectors.toSet());
        _teachers = schedulingInput.getTeachers();
        _places = schedulingInput.getPlaces();
        _groups = schedulingInput.getGroups();
        _lessonRequests = schedulingInput.getLessonRequests();
        return buildModel();
    }

    private MilpModel buildModel() {
        // variables
        addTeacherTimeslotVariables();
        addTeacherDayVariables();
        addTeacherPlacesNumVariables();
        addGroupTimeslotVariables();
        addLessonRequestPlaceVariables();
        addLessonRequestTimeslotVariables();

        // constraints
        addTeacherTimeslotDayConstraints();
       // addTeacherPlacesNumConstrains();
        addTeacherOneLessonConstraints();
        addMaxDaysPerTeacherConstraints();

        addGroupOneLessonConstraints();
        addGroupDailyContinuityConstraints();
        addGroupMaxDailyLoadConstrains();

        addNotMoreThanOneLessonInPlaceConstraints();
        addMaxLessonsPerDayForLessonRequestConstraints();
        addAllRequestsShouldBeSatisfiedByTimeslots();
        addAllRequestsShouldBeSatisfiedByPlaces();
        addLessonRequestDayContinuityConstraints();

        //objective
        //addObjectiveMinTeacherDays();
        //addObjectiveMinTeacherDaysMinTeacherPlaces(10, 1);
        //addObjectiveMinTeacherWindows(1);
       // addObjectiveMinTeacherDaysMinTeacherPlacesMinComplexityViolation(10,1,10);
        return _milpModelBuilder.getModel();
    }

    private void addTeacherPlacesNumVariables() {
        _nameSpace.registerVariableName(_teacherPlacesNum, 1);
        for(Teacher teacher : _teachers) {
            String teacherId = _schedulingInput.getTeacherId(teacher);
            int maxPlaces = (int)_schedulingInput.getAvailablePlacesForTeacher(teacher).count();
            String varName = _nameSpace.getVariableName(_teacherPlacesNum, List.of(teacherId));
            Variable variable = new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(maxPlaces));
            _milpModelBuilder.addVariable(variable);
        }
    }

    private void addTeacherPlacesNumConstrains() {
        for(Teacher teacher: _teachers) {
            String teacherId = _schedulingInput.getTeacherId(teacher);
            Stream<Pair<String, Double>> placeVars = _lessonRequests
                    .stream()
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

    private void addObjectiveMinTeacherDaysMinTeacherPlaces(double coeff1, double coeff2) {
        Stream<Pair<String, Double>> teacherDaysStream = _teachers.stream()
                .flatMap(teacher -> {
                    String teacherId = "" + _teachers.indexOf(teacher);
                    return _daysOfWeek.stream()
                            .filter(dow -> _schedulingInput.ifDayAvailableForTeacher(teacher, dow))
                            .map(dow -> _nameSpace.getVariableName(_teacherDayVar, List.of(teacherId, dow.name())));
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

    private void addLessonRequestDayContinuityConstraints() {
        for (LessonRequest lessonRequest : _lessonRequests) {
            String lrId = "" + lessonRequest.getId();
            Predicate<WeeklyTimeSlot> predicate = wts -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonRequest, wts);
            for(DayOfWeek dow: _daysOfWeek) {
                List<WeeklyTimeSlot> timeSlots = _schedulingInput.getTimeslotsSequenceForDay(dow);
                int nSlots = timeSlots.size();
                for(int i = 1; i < nSlots-1; i++) {
                    WeeklyTimeSlot wts = timeSlots.get(i);
                    if(predicate.test(wts)) {
                        if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                            int timeSlotId = Integer.parseInt(_schedulingInput.getTimeSlotId(wts));
                            create0before0afterImpliesAllZeros_lessonRequest(lrId, timeSlots, predicate, nSlots, i, timeSlotId);
                            create1before0implies0after_lessonRequest(lrId, timeSlots, predicate, nSlots, i, timeSlotId);
                            create1after0implies0before_lessonRequest(lrId, timeSlots, predicate, nSlots, i, timeSlotId);
                        }
                    } else {
                        if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                            int timeSlotId = Integer.parseInt(_schedulingInput.getTimeSlotId(wts));

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

    private void addAllRequestsShouldBeSatisfiedByTimeslots() {
        for(LessonRequest lessonRequest :_lessonRequests) {
            Stream<String> lessonRequestVar = _schedulingInput.getAvailableSlotsForLessonRequest(lessonRequest)
                    .map(_schedulingInput::getTimeSlotId)
                    .map(t -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of("" + lessonRequest.getId(), "" + t)));
            var constraint = _constraintFactory.createVarSumEquals(getNextConstraintLabel(),lessonRequestVar, lessonRequest.getCourseInProgram().getLessonsPerWeek());
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private void addMaxLessonsPerDayForLessonRequestConstraints() {
        for(LessonRequest lessonRequest :_lessonRequests) {
            int maxPerDay = lessonRequest.getCourseInProgram().getMaxLessonsPerDay();
            _daysOfWeek
                    .forEach(dow -> {
                        var list= _schedulingInput.getTimeslotsSequenceForDay(dow);
                        List<String> lessonRequestVarList = list.stream()
                                .filter(wts -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonRequest, wts))
                                .map(_schedulingInput::getTimeSlotId)
                                .map(t -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of("" + lessonRequest.getId(), "" + t)))
                                .toList();
                        if(lessonRequestVarList.size() > maxPerDay) {
                            var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(),lessonRequestVarList.stream(), maxPerDay);
                            _milpModelBuilder.addConstraint(constraint);
                        }
                    });
        }
    }

    private void addNotMoreThanOneLessonInPlaceConstraints() {
        for(Place place : _places) {
            String placeId = "" + _places.indexOf(place);
            _timeSlotSequence
                    .forEach(wts -> {
                        final String timeSlot = "" + _schedulingInput.getTimeSlotId(wts);
                        List<String> auxVarLabelsList = _lessonRequests
                                .stream()
                                .filter(request -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(request, wts))
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
                    });
        }
    }

    private void addGroupMaxDailyLoadConstrains() {
        for(Group group : _groups) {
            _daysOfWeek.forEach(dow -> {
                int maxLoad = _schedulingInput.getGroupDailyMaxLoad(group, dow);
                int maxLoadUb = (int)_schedulingInput.getAvailableSlotsForGroup(group)
                        .filter(wts -> wts.getDayOfWeek().equals(dow))
                        .count();
                if(maxLoad < maxLoadUb) {
                    createGroupDailyLoadConstraint(group, dow, maxLoad);
                }

            });

        }
    }

    private void createGroupDailyLoadConstraint(Group group, DayOfWeek dayOfWeek, int ub) {
        String groupId = "" + _groups.indexOf(group);
        Stream<String> groupTimeslotVarNamesStream = _schedulingInput.getAvailableSlotsForGroup(group)
                .filter(wts -> wts.getDayOfWeek().equals(dayOfWeek))
                .map(_schedulingInput::getTimeSlotId)
                .map(tsId -> List.of(groupId, "" + tsId))
                .map(list -> _nameSpace.getVariableName(_groupTimeSlotVar,list));
        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), groupTimeslotVarNamesStream, ub);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void addGroupDailyContinuityConstraints() {
        for (Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            Predicate<WeeklyTimeSlot> predicate = wts -> _schedulingInput.ifTimeSlotAvailableForGroup(group, wts);
            for(DayOfWeek dayOfWeek: _daysOfWeek) {
                if(_schedulingInput.ifDayAvailableForGroup(group, dayOfWeek)) {
                    List<WeeklyTimeSlot> timeSlots = _schedulingInput.getTimeslotsSequenceForDay(dayOfWeek);
                    int nSlots = timeSlots.size();

                    for(int i = 1; i < nSlots-1; i++) {
                        WeeklyTimeSlot wts = timeSlots.get(i);
                        if(predicate.test(wts)) {
                            int timeSlotId = Integer.parseInt(_schedulingInput.getTimeSlotId(wts));
                            create1before0implies0after_group(groupId, timeSlots, predicate, nSlots, i, timeSlotId);
                            create1after0implies0before_group(groupId, timeSlots, predicate, nSlots, i, timeSlotId);
                            create0before0afterImpliesAllZeros_group(groupId, timeSlots, predicate, nSlots, i, timeSlotId);
                        } else {
                            if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                                int timeSlotId = Integer.parseInt(_schedulingInput.getTimeSlotId(wts));

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
    }

    private void create1after0implies0before_group(String groupId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (x_{i} + 1 - x{i+1})*(i) >= \sum_{t = 0}^i x_{t}
        String current = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + timeSlotId));
        String next = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId + 1)));
        Stream<String> allBefore = IntStream.range(0, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_schedulingInput::getTimeSlotId)
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

    private void create0before0afterImpliesAllZeros_group(String groupId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (1 + x_{i-1} + x_{i+1} - x_{i})*(n-3) >= \sum_{t = 1}^{i-2} x_{t} + \sum{t = i+2}^{n} x_{t}
        String prev = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId - 1)));
        String current = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + timeSlotId));
        String next = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId + 1)));
        Stream<String> allOthers = IntStream.concat(IntStream.range(0, i-1), IntStream.range(i +2, nSlots))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_schedulingInput::getTimeSlotId)
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
                .map(_schedulingInput::getTimeSlotId)
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
                .map(_schedulingInput::getTimeSlotId)
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
                .map(_schedulingInput::getTimeSlotId)
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

    private void create1before0implies0after_group(String groupId, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int nSlots, int i, int timeSlotId) {
        // (x_{i} + 1 - x{i-1})*(n-i-1) >= \sum_{t = i+1}^n x_{t}
        String prev = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + (timeSlotId - 1)));
        String current = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, "" + timeSlotId));
        Stream<String> allNext = IntStream.range(i +1, nSlots)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .map(_schedulingInput::getTimeSlotId)
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

    private void addGroupOneLessonConstraints() {
        for (Group group : _groups) {
            String groupId = "" + _groups.indexOf(group);
            _schedulingInput.getAvailableSlotsForGroup(group)
                    .forEach(wts -> {
                        final String timeSlot = "" + _schedulingInput.getTimeSlotId(wts);
                        List<String> groupTimeList = List.of(groupId, timeSlot);
                        String groupTimeVarLabel = _nameSpace.getVariableName(_groupTimeSlotVar, groupTimeList);

                        Stream<String> lesReqVarLabels = _lessonRequests
                                .stream()
                                .filter(request -> request.getGroup().equals(group))
                                .filter(request -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(request, wts))
                                .map(request -> List.of("" + request.getId(), timeSlot))
                                .map(list -> _nameSpace.getVariableName(_lesReqTimeSlotVar, list));
                        var constraint = _constraintFactory.createSelectionConstraint(getNextConstraintLabel(), groupTimeVarLabel, lesReqVarLabels);
                        _milpModelBuilder.addConstraint(constraint);
                    });
        }
    }

    private void addMaxDaysPerTeacherConstraints() {
        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            int numDaysUb = (int)_schedulingInput.getAvailableSlotsForTeacher(teacher)
                    .map(WeeklyTimeSlot::getDayOfWeek)
                    .distinct()
                    .count();
            int maxDaysForTeacher = _schedulingInput.getMaxDaysForTeacher(teacher);
            if(maxDaysForTeacher < numDaysUb) {
                Stream<String> teacherDayVarsStream = _daysOfWeek
                        .stream()
                        .map(DayOfWeek::name)
                        .map(i -> List.of(teacherId, "" + i))
                        .map(list -> _nameSpace.getVariableName(_teacherDayVar, list));
                var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), teacherDayVarsStream, maxDaysForTeacher);
                _milpModelBuilder.addConstraint(constraint);
            }
        }
    }

    private void addTeacherOneLessonConstraints() {
        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            _schedulingInput.getAvailableSlotsForTeacher(teacher)
                    .forEach(wts -> {
                        final String timeSlot = "" + _schedulingInput.getTimeSlotId(wts);
                        List<String> teacherTimeList = List.of(teacherId, timeSlot);
                        String teacherVarLabel = _nameSpace.getVariableName(_teacherTimeSlotVar, teacherTimeList);

                        Stream<String> lesReqVarLabels = _lessonRequests
                                .stream()
                                .filter(request -> request.getOccupations()
                                        .flatMap(LessonRequestOccupation::getOccupiedTeachers)
                                        .anyMatch(teacher::equals))
                                .filter(request -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(request, wts))
                                .map(request -> List.of("" + request.getId(), timeSlot))
                                .map(list -> _nameSpace.getVariableName(_lesReqTimeSlotVar, list));
                        var constraint = _constraintFactory.createSelectionConstraint(getNextConstraintLabel(), teacherVarLabel, lesReqVarLabels);
                        _milpModelBuilder.addConstraint(constraint);
                    });
        }
    }

    private void addTeacherTimeslotDayConstraints() {
        for (Teacher teacher : _teachers) {
            String teacherId = "" + _teachers.indexOf(teacher);
            for(DayOfWeek dayOfWeek : _daysOfWeek) {
                if(_schedulingInput.ifDayAvailableForTeacher(teacher, dayOfWeek)) {
                    List<String> tdList = List.of(teacherId, "" + dayOfWeek.name());
                    String teacherDayOfWeekVarLabel = _nameSpace.getVariableName(_teacherDayVar, tdList);

                    Stream<String> timeSlotVarLabels = _schedulingInput.getAvailableSlotsForTeacher(teacher)
                            .filter(wts -> wts.getDayOfWeek().equals(dayOfWeek))
                            .map(timeSlot -> List.of(teacherId, "" + _schedulingInput.getTimeSlotId(timeSlot)))
                            .map(list -> _nameSpace.getVariableName(_teacherTimeSlotVar, list));
                    var constraint = _constraintFactory.createBinaryBlockingConstraint(getNextConstraintLabel(), teacherDayOfWeekVarLabel, timeSlotVarLabels);
                    _milpModelBuilder.addConstraint(constraint);
                }
            }
        }
    }


    private void addLessonRequestTimeslotVariables() {
        _nameSpace.registerVariableName(_lesReqTimeSlotVar,2);

        for(LessonRequest lessonRequest : _lessonRequests) {
            _schedulingInput.getAvailableSlotsForLessonRequest(lessonRequest)
                    .map(_schedulingInput::getTimeSlotId)
                    .map(t -> _nameSpace.getVariableName(_lesReqTimeSlotVar, List.of("" + lessonRequest.getId(), "" + t)))
                    .map(Variable_BinaryImpl::new)
                    .forEach(_milpModelBuilder::addVariable);
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

    private void addGroupTimeslotVariables() {
        _nameSpace.registerVariableName(_groupTimeSlotVar,2);
        for(Group group : _schedulingInput.getGroups()) {
            String groupId = _schedulingInput.getGroupId(group);
            _schedulingInput.getAvailableSlotsForGroup(group)
                    .map(wts -> {
                        String wtsId = "" + _timeSlotSequence.indexOf(wts);
                        String varName = _nameSpace.getVariableName(_groupTimeSlotVar, List.of(groupId, wtsId));
                        return _schedulingInput.ifTimeSlotMandatoryForGroup(group, wts) ?
                            new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1)) :
                                new Variable_BinaryImpl(varName);
                    })
                    .forEach(_milpModelBuilder::addVariable);
        }
    }


    private void addTeacherTimeslotVariables() {
        _nameSpace.registerVariableName(_teacherTimeSlotVar,2);
        for(Teacher teacher : _schedulingInput.getTeachers()) {
            String teacherId = _schedulingInput.getTeacherId(teacher);
            _schedulingInput.getAvailableSlotsForTeacher(teacher)
                    .map(wts -> {
                        String wtsId = "" + _timeSlotSequence.indexOf(wts);
                       return _nameSpace.getVariableName(_teacherTimeSlotVar, List.of(teacherId, wtsId));
                    })
                    .map(Variable_BinaryImpl::new)
                    .forEach(_milpModelBuilder::addVariable);
        }
    }

    private void addTeacherDayVariables() {
        _nameSpace.registerVariableName(_teacherDayVar,2);
        for(Teacher teacher : _teachers) {
            _daysOfWeek
                    .stream()
                    .map(dow -> createTeacherDayVariable(teacher, dow))
                    .forEach(_milpModelBuilder::addVariable);
        }
    }



    private Variable createTeacherDayVariable(Teacher teacher ,DayOfWeek dow) {
        String teacherId = "" + _teachers.indexOf(teacher);
        var varName = _nameSpace.getVariableName(_teacherDayVar, List.of(teacherId, dow.name()));
        if (!_schedulingInput.ifDayAvailableForTeacher(teacher, dow)) {
            return new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(0));
        } else {
            if (_schedulingInput.ifDayMandatoryForTeacher(teacher, dow)) {
                return new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1));
            } else {
                return new Variable_BinaryImpl(varName);
            }
        }
    }

    private synchronized String getNextConstraintLabel() {
        String result = "C_"+ _nextConstraintId;
        _nextConstraintId++;
        return result;
    }

}
