package scheduleBuilder.translators.milp;

import course.Course;
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
import scheduleBuilder.SchedulingInputV2;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SchoolMilpModelBuilderImpl implements SchoolMilpModelBuilder {
    private final static LinearConstraintFactory _constraintFactory = new LinearConstraintFactoryImpl();

    private final VariableNameSpace _nameSpace = new VariableNameSpaceImpl();
    private final MilpModelBuilder _milpModelBuilder = new MilpModelBuilderImpl("model_4");
    private final SchedulingInputV2 _schedulingInput;

    private int _nextConstraintId = 0;

    public SchoolMilpModelBuilderImpl(SchedulingInputV2 schedulingInput) {
        _schedulingInput = schedulingInput;
        registerVarNames();
    }

    private void registerVarNames() {
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getTeacherDayVarPrefix(),2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getTeacherDayStartVarPrefix(),2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getTeacherDayDurationVarPrefix(), 2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getLessonRequestOccupationIdPlaceTimeSlotPrefix(),4);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getTeacherTimeSlotVarPrefix(), 2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getGroupTimeSlotVarPrefix(), 2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getGroupDayNumLessonsVarPrefix(),2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getGroupDayStartVarPrefix(),2);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getLessonRequestOccupationIdPlacePrefix(),3);
        _nameSpace.registerVariableName(SchedulingVarNamesVocabulary.getLessonRequestDayPrefix(),2);
    }

    @Override
    public void addTeacherDayVariable(Teacher teacher, DayOfWeek dayOfWeek) {
        if(!_schedulingInput.ifDayAvailableForTeacher(teacher, dayOfWeek)) {
            return;
        }
        Variable variable = createTeacherDayVariable(teacher, dayOfWeek);
        _milpModelBuilder.addVariable(variable);

        Variable variableDayStart = createTeacherDayStartVariable(teacher, dayOfWeek);
        _milpModelBuilder.addVariable(variableDayStart);
        Variable variableNumLessons = createTeacherNumLessonsVariable(teacher, dayOfWeek);
        _milpModelBuilder.addVariable(variableNumLessons);
    }


    private Variable createTeacherDayStartVariable(Teacher teacher, DayOfWeek dayOfWeek) {
        int getDayStartLb = _schedulingInput.getTeacherInDayStartTimeslotLb(teacher, dayOfWeek);
        int getDayStartUb = _schedulingInput.getTeacherInDayStartTimeslotUb(teacher, dayOfWeek);
        if(getDayStartLb > getDayStartUb) {
            _schedulingInput.getTeacherInDayStartTimeslotLb(teacher, dayOfWeek);
            _schedulingInput.getTeacherInDayStartTimeslotUb(teacher, dayOfWeek);
        }
        String varName = getTeacherDayStartVarName(teacher, dayOfWeek);
        return new Variable_IntegerImpl(varName, OptionalInt.of(getDayStartLb), OptionalInt.of(getDayStartUb));
    }

    private Variable createTeacherNumLessonsVariable(Teacher teacher, DayOfWeek dayOfWeek) {
        int minLessons = _schedulingInput.getTeacherDailyMinTimeslotLoad(teacher, dayOfWeek);
        int maxLessons = _schedulingInput.getTeacherDailyMaxTimeslotLoad(teacher, dayOfWeek);
        String varName = getTeacherDayDurationVarName(teacher, dayOfWeek);
        return new Variable_IntegerImpl(varName, OptionalInt.of(minLessons), OptionalInt.of(maxLessons));
    }


    @Override
    public void addGroupDayVariables(Group group, DayOfWeek dayOfWeek) {
        if(!_schedulingInput.ifDayAvailableForGroup(group, dayOfWeek)) {
            return;
        }
        Variable variableDayStart = createGroupDayStartVariable(group, dayOfWeek);
        _milpModelBuilder.addVariable(variableDayStart);
        Variable variableNumLessons = createGroupNumLessonsVariable(group, dayOfWeek);
        _milpModelBuilder.addVariable(variableNumLessons);
    }

    private Variable createGroupNumLessonsVariable(Group group, DayOfWeek dayOfWeek) {
        int minLessons = _schedulingInput.getGroupDailyMinLoad(group, dayOfWeek);
        int maxLessons = _schedulingInput.getGroupDailyMaxLoad(group, dayOfWeek);
        String varName = getGroupDayNumLessonsVarName(group, dayOfWeek);
        return new Variable_IntegerImpl(varName, OptionalInt.of(minLessons), OptionalInt.of(maxLessons));
    }

    private String getGroupDayNumLessonsVarName(Group group, DayOfWeek dayOfWeek) {
        String prefix = SchedulingVarNamesVocabulary.getGroupDayNumLessonsVarPrefix();
        String groupId = _schedulingInput.getGroupId(group);
        return _nameSpace.getVariableName(prefix, List.of(groupId, dayOfWeek.name()));
    }

    private Variable createGroupDayStartVariable(Group group, DayOfWeek dayOfWeek) {
        String varName = getGroupDayStartVarName(group, dayOfWeek);

        int getDayStartLb = _schedulingInput.getGroupInDayStartLb(group, dayOfWeek);
        int getDayStartUb = _schedulingInput.getGroupInDayStartUb(group, dayOfWeek);

        return new Variable_IntegerImpl(varName, OptionalInt.of(getDayStartLb), OptionalInt.of(getDayStartUb));
    }

    private String getGroupDayStartVarName(Group group, DayOfWeek dayOfWeek) {
        String prefix = SchedulingVarNamesVocabulary.getGroupDayStartVarPrefix();
        String groupId = _schedulingInput.getGroupId(group);
        return _nameSpace.getVariableName(prefix, List.of(groupId, dayOfWeek.name()));
    }

    @Override
    public void addTeacherTimeslotVariable(Teacher teacher, WeeklyTimeSlot timeSlot) {
        Variable variable = createTeacherTimeSlotVariable(teacher, timeSlot);
        _milpModelBuilder.addVariable(variable);
    }

    @Override
    public void addGroupTimeslotVariable(Group group, WeeklyTimeSlot timeSlot) {
        Variable variable = createGroupTimeSlotVariable(group, timeSlot);
        _milpModelBuilder.addVariable(variable);
    }

    private Variable createGroupTimeSlotVariable(Group group, WeeklyTimeSlot timeSlot) {
        var varName = getGroupTimeSlotVarName(group, timeSlot);
        if(_schedulingInput.ifTimeSlotMandatoryForGroup(group, timeSlot)) {
            return new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1));
        } else if (_schedulingInput.ifTimeSlotAvailableForGroup(group, timeSlot)) {
            return new Variable_BinaryImpl(varName);
        } else {
            return new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(0));
        }
    }

    private Variable createTeacherTimeSlotVariable(Teacher teacher, WeeklyTimeSlot timeSlot) {
        var varName = getTeacherTimeSlotVarName(teacher, timeSlot);
        if(_schedulingInput.ifTimeSlotMandatoryForTeacher(teacher, timeSlot)) {
            return new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1));
        } else if (_schedulingInput.ifTimeSlotAvailableForTeacher(teacher, timeSlot)) {
            return new Variable_BinaryImpl(varName);
        } else {
            return new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(0));
        }
    }

    private String getTeacherTimeSlotVarName(Teacher teacher, WeeklyTimeSlot timeSlot) {
        String prefix = SchedulingVarNamesVocabulary.getTeacherTimeSlotVarPrefix();
        String teacherId = _schedulingInput.getTeacherId(teacher);
        String timeSlotId = _schedulingInput.getTimeSlotId(timeSlot);
        return _nameSpace.getVariableName(prefix, List.of(teacherId, timeSlotId));
    }

    @Override
    public void addLessonRequestOccupyPlacesAtTimeVariables(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot) {
        if(!_schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonRequest, timeSlot)) {
            return;
        }

        Map<LessonRequestOccupation, List<Variable>> occupationVariablesMap = lessonRequest.getOccupations()
                .collect(Collectors.toConcurrentMap(occupation -> occupation, occupation -> {
                    Optional<Place> placeOpt = _schedulingInput.occupationScheduledInPlace(occupation, timeSlot);
                    if(placeOpt.isPresent()) {
                        String varLabel = getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, placeOpt.get(), timeSlot);
                        return List.of(new Variable_IntegerImpl(varLabel, OptionalInt.of(1), OptionalInt.of(1)));
                    } else {
                        //TODO THIS CAN BE DONE BETTER BY SCHEDULED OCCUPATIONS IN THE SAME PLACE
                        return _schedulingInput.getAvailablePlacesForOccupationAtTimeSlot(occupation, timeSlot)
                                .map(place -> {
                                    String varLabel = getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, place, timeSlot);
                                    return (Variable)new Variable_BinaryImpl(varLabel);
                                })
                                .toList();
                    }
                }));

        if(lessonRequest.getOccupationsNum() == occupationVariablesMap.size()) {
            occupationVariablesMap.values()
                    .stream()
                    .flatMap(List::stream)
                    .forEach(_milpModelBuilder::addVariable);

            if(occupationVariablesMap.size() > 1) {
                addConstraintOccupationsSynchronized(occupationVariablesMap);
            }
        }
    }



    private void addConstraintOccupationsSynchronized(Map<LessonRequestOccupation, List<Variable>> occupationVariablesMap) {
        for(var occupation1 : occupationVariablesMap.keySet()) {
            List<Variable> occupation1Variables = occupationVariablesMap.get(occupation1);
            for(var occupation2 : occupationVariablesMap.keySet()) {
                if(occupation1.getId() < occupation2.getId()) {
                    List<Variable> occupation2Variables = occupationVariablesMap.get(occupation2);
                    var constraint = _constraintFactory.createVarGroupsSumEquality(getNextConstraintLabel(),
                            occupation1Variables
                                    .stream()
                                    .map(Variable::getLabel),
                            occupation2Variables
                                    .stream()
                                    .map(Variable::getLabel));
                    _milpModelBuilder.addConstraint(constraint);
                }
            }
        }
    }

    private synchronized String getNextConstraintLabel() {
        String result = "C_"+ _nextConstraintId;
        if(_nextConstraintId == 210) {
            System.out.println();
        }
        _nextConstraintId++;
        return result;
    }


    @Override
    public void addLessonRequestsSatisfied(LessonRequest lessonRequest) {
        var timeSlots = _schedulingInput.getAvailableSlotsForLessonRequest(lessonRequest).toList();
        int numLessons = lessonRequest.getCourseInProgram().getLessonsPerWeek();
        // all occupations should be satisfied exactly numLessons times
        lessonRequest
                .getOccupations()
                .forEach(occupation -> {
                    var varNames = occupation
                            .getCandidatePlaces()
                            .flatMap(place -> timeSlots.stream()
                                    .map(timeSlot -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, place, timeSlot)));
                    var constraint = _constraintFactory.createVarSumEquals(getNextConstraintLabel(), varNames, numLessons);
                    _milpModelBuilder.addConstraint(constraint);
                });
        // all occupations require not more than one place at any time
        lessonRequest
                .getOccupations()
                .forEach(occupation -> {
                    timeSlots.forEach(timeSlot -> {
                        var varNames = occupation
                                .getCandidatePlaces()
                                .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, place, timeSlot));
                        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), varNames, 1.0);
                        _milpModelBuilder.addConstraint(constraint);
                    });
                });


    }

    @Override
    public void addNotMoreThanOneLessonPerTeacherAtTimeSlot(Teacher teacher, WeeklyTimeSlot timeSlot) {
        Stream<LessonRequest> lessonRequests = _schedulingInput.getLessonRequestsForTeacher(teacher);
        String teacherTimeSlotVar = getTeacherTimeSlotVarName(teacher, timeSlot);
        if(_schedulingInput.ifTimeSlotAvailableForTeacher(teacher, timeSlot) || !_schedulingInput.ifTimeSlotMandatoryForTeacher(teacher, timeSlot)) {
            var varNames = lessonRequests
                    .filter(lessonsRequest -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonsRequest, timeSlot))
                    .flatMap(lessonsRequest -> lessonsRequest.getOccupations()
                            .filter(occupation -> occupation.getOccupiedTeachers().anyMatch(teacher::equals))
                            .flatMap(occupation ->
                                    occupation.getCandidatePlaces()
                                            .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonsRequest, occupation, place, timeSlot)))
                    );
            var list = varNames.toList();
            var constraint = _constraintFactory.createVarGroupsSumEquality(getNextConstraintLabel(), Stream.of(teacherTimeSlotVar), list.stream());
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    @Override
    public void addNotMoreThanOneOccupationPerPlaceAtTimeSlot(Place place, WeeklyTimeSlot timeSlot) {
        Stream<LessonRequest> lessonRequests = _schedulingInput.getLessonRequestsForPlace(place);
        var varNames = lessonRequests
                .filter(lessonsRequest -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonsRequest, timeSlot))
                .flatMap(lessonsRequest ->
                        lessonsRequest
                                .getOccupations()
                                .filter(occupation -> occupation.getCandidatePlaces().anyMatch(place::equals))
                                .map(occupation -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonsRequest, occupation, place, timeSlot)));

        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), varNames, 1.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    @Override
    public void addExactlyOneLessonPerGroupAtTimeSlots(Group group, WeeklyTimeSlot timeSlot) {
        int maxOccupationsInSlot = _schedulingInput.getMaxOccupationsInSlot();
        String groupTimeSlotVar = getGroupTimeSlotVarName(group, timeSlot);
        if(_schedulingInput.ifTimeSlotAvailableForGroup(group, timeSlot)) {
            // IF ANY LESSON SCHEDULED THAN TIMESLOT VAR = 1
            {
                Stream<LessonRequest> groupLessonRequests = _schedulingInput.getLessonRequestsForGroup(group);
                var lessonVarsStream = groupLessonRequests
                        .filter(lessonsRequest -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonsRequest, timeSlot))
                        .flatMap(lessonsRequest -> {
                            return lessonsRequest
                                    .getOccupations()
                                    .flatMap(occupation ->
                                            occupation.getCandidatePlaces()
                                                    .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonsRequest, occupation, place, timeSlot))
                                    );
                        })
                        .map(varName -> new Pair<>(varName, 1.0));
                Stream<Pair<String, Double>> stream = Stream.concat(
                        lessonVarsStream,
                        Stream.of(new Pair<>(groupTimeSlotVar, -1.0 * maxOccupationsInSlot))
                );
                LinearExpression expression = new LinearExpressionImpl(stream, 0.0);
                var constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.LESS_OR_EQUAL, 0.0);
                _milpModelBuilder.addConstraint(constraint);
            }
            // IF TIMESLOT VARIABLE IS 1 THAN AT LEAST ONE LESSON SCHEDULED IN IT
            {
                Stream<LessonRequest> groupLessonRequests = _schedulingInput.getLessonRequestsForGroup(group);
                var lessonVarsStream = groupLessonRequests
                        .filter(lessonsRequest -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonsRequest, timeSlot))
                        .flatMap(lessonsRequest -> {
                            return lessonsRequest
                                    .getOccupations()
                                    .flatMap(occupation ->
                                            occupation.getCandidatePlaces()
                                                    .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonsRequest, occupation, place, timeSlot))
                                    );
                        })
                        .map(varName -> new Pair<>(varName, 1.0));
                Stream<Pair<String, Double>> stream = Stream.concat(
                        lessonVarsStream,
                        Stream.of(new Pair<>(groupTimeSlotVar, -1.0))
                );
                LinearExpression expression = new LinearExpressionImpl(stream, 0.0);
                var constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
                _milpModelBuilder.addConstraint(constraint);
            }

        }
    }




    @Override
    public void addGroupDayTimeslotVarConnections(Group group, DayOfWeek dayOfWeek) {
        if(!_schedulingInput.ifDayAvailableForGroup(group, dayOfWeek)) {
            return;
        }
        String dayStartVarName = getGroupDayStartVarName(group, dayOfWeek);
        String dayNumLessonsVarName = getGroupDayNumLessonsVarName(group, dayOfWeek);
        var timeSlotSequence = _schedulingInput.getTimeslotsSequenceForDay(dayOfWeek);
        int numSlotsInDay = timeSlotSequence.size();
        for (int timeSlotInDayIndex = 0; timeSlotInDayIndex < numSlotsInDay; timeSlotInDayIndex++) {
            WeeklyTimeSlot timeSlot = timeSlotSequence.get(timeSlotInDayIndex);
            if(_schedulingInput.ifTimeSlotAvailableForGroup(group, timeSlot)) {
                //TODO THIS CONSTRAINT CAN BE STRENGTHENED.
                // ДОБАВИТЬ ИМПЛИКАЦИЮ, ЧТО ЕСЛИ СЛОТ МЕЖДУ СТАРТОМ И СТАРТ + ДЮРЭЙШН, ТО ОН ЗАНЯТ
                String varTimeSlotName = getGroupTimeSlotVarName(group, timeSlot);
                // day start constraint
                {
                    Stream<Pair<String, Double>> varCoefficientPair = Stream.of(
                            new Pair<>(dayStartVarName, 1.0),
                            new Pair<>(varTimeSlotName, 1.0 * (numSlotsInDay - timeSlotInDayIndex))
                    );
                    LinearExpression linearExpression = new LinearExpressionImpl(varCoefficientPair, 0.0);
                    var constraint = new LinearConstraintImpl(getNextConstraintLabel(), linearExpression, ConstraintType.LESS_OR_EQUAL, numSlotsInDay);
                    _milpModelBuilder.addConstraint(constraint);
                }
                // day end constraint
                {
                    Stream<Pair<String, Double>> varCoefficientPair = Stream.of(
                            new Pair<>(dayStartVarName, 1.0),
                            new Pair<>(dayNumLessonsVarName, 1.0),
                            new Pair<>(varTimeSlotName, -1.0 * (timeSlotInDayIndex+1))
                    );
                    LinearExpression linearExpression = new LinearExpressionImpl(varCoefficientPair, 0.0);
                    var constraint = new LinearConstraintImpl(getNextConstraintLabel(), linearExpression, ConstraintType.MORE_OR_EQUAL, 0.0);
                    _milpModelBuilder.addConstraint(constraint);
                }
            }
        }
        addGroupDayContinuityConstraint(group, dayOfWeek);
    }

    @Override
    public void addTeacherDayStartDurationTimeslotVarConnections(Teacher teacher, DayOfWeek dayOfWeek) {
        if(!_schedulingInput.ifDayAvailableForTeacher(teacher, dayOfWeek)) {
            return;
        }
        String dayStartVarName = getTeacherDayStartVarName(teacher, dayOfWeek);
        String dayNumLessonsVarName = getTeacherDayDurationVarName(teacher, dayOfWeek);
        var timeSlotSequence = _schedulingInput.getTimeslotsSequenceForDay(dayOfWeek);
        int numSlotsInDay = timeSlotSequence.size();
        for (int timeSlotInDayIndex = 0; timeSlotInDayIndex < numSlotsInDay; timeSlotInDayIndex++) {
            WeeklyTimeSlot timeSlot = timeSlotSequence.get(timeSlotInDayIndex);
            if(_schedulingInput.ifTimeSlotAvailableForTeacher(teacher, timeSlot)) {
                String varTimeSlotName = getTeacherTimeSlotVarName(teacher, timeSlot);
                // day start constraint
                {
                    Stream<Pair<String, Double>> varCoefficientPair = Stream.of(
                            new Pair<>(dayStartVarName, 1.0),
                            new Pair<>(varTimeSlotName, 1.0 * (numSlotsInDay - timeSlotInDayIndex))
                    );
                    LinearExpression linearExpression = new LinearExpressionImpl(varCoefficientPair, 0.0);
                    var constraint = new LinearConstraintImpl(getNextConstraintLabel(), linearExpression, ConstraintType.LESS_OR_EQUAL, numSlotsInDay);
                    _milpModelBuilder.addConstraint(constraint);
                }
                // day end constraint
                {
                    Stream<Pair<String, Double>> varCoefficientPair = Stream.of(
                            new Pair<>(dayStartVarName, 1.0),
                            new Pair<>(dayNumLessonsVarName, 1.0),
                            new Pair<>(varTimeSlotName, -1.0 * (timeSlotInDayIndex+1))
                    );
                    LinearExpression linearExpression = new LinearExpressionImpl(varCoefficientPair, 0.0);
                    var constraint = new LinearConstraintImpl(getNextConstraintLabel(), linearExpression, ConstraintType.MORE_OR_EQUAL, 0.0);
                    _milpModelBuilder.addConstraint(constraint);
                }
            }
        }
    }

    @Override
    public void addLessonRequestDailyContinuity(LessonRequest lessonRequest, DayOfWeek dayOfWeek) {

        int maxDuration = Math.min(lessonRequest.getCourseInProgram().getMaxLessonsPerDay(), lessonRequest.getCourseInProgram().getLessonsPerWeek());
        if(maxDuration < 2) {
            return;
        }
        int numSlots = (int)_schedulingInput.getAvailableSlotsForLessonRequest(lessonRequest)
                .filter(timeSlot -> timeSlot.getDayOfWeek().equals(dayOfWeek))
                .count();
        if(numSlots < 2) {
            return;
        }

        var timeSlots = _schedulingInput.getTimeslotsSequenceForDay(dayOfWeek);
        int lessonRequestEarliestStartTime = _schedulingInput.getLessonRequestDayStartTimeSlotLb(lessonRequest, dayOfWeek);
        int lessonRequestLatestStartTime = _schedulingInput.getLessonRequestLatestTimeSlot(lessonRequest, dayOfWeek);
        Predicate<WeeklyTimeSlot> predicate = timeSlot -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonRequest,timeSlot);
        for (int i = lessonRequestEarliestStartTime+1; i < lessonRequestLatestStartTime; i++) {
            final int ii = i;
            WeeklyTimeSlot prevTimeSlot = timeSlots.get(i-1);
            WeeklyTimeSlot timeSlot = timeSlots.get(i);
            WeeklyTimeSlot nextTimeSlot = timeSlots.get(i+1);

            if(predicate.test(timeSlot)) {
                if(predicate.test(prevTimeSlot) && predicate.test(nextTimeSlot)) {
                lessonRequest.getOccupations()
                        .forEach(occupation -> {
                                        create1before0implies0after_lessonRequest(occupation, timeSlots, predicate, ii, lessonRequestLatestStartTime);
                                        create1after0implies0before_lessonRequest(occupation, timeSlots, predicate, ii, lessonRequestEarliestStartTime);
                                        create0before0afterImpliesAllZeros_lessonRequest(occupation, timeSlots, predicate, ii, lessonRequestEarliestStartTime, lessonRequestLatestStartTime);
                                    }
                        );
                }

            } else {
                if(predicate.test(timeSlots.get(i-1)) && predicate.test(timeSlots.get(i+1))) {
                    lessonRequest.getOccupations()
                            .forEach(occupation -> {
                                create0Imlplies0LeftOr0Right(lessonRequest, prevTimeSlot, nextTimeSlot, occupation);
                            });
                }
            }
        }
    }

    private void create0Imlplies0LeftOr0Right(LessonRequest lessonRequest, WeeklyTimeSlot prevTimeSlot, WeeklyTimeSlot nextTimeSlot, LessonRequestOccupation occupation) {
        var prevStream = _schedulingInput.getAvailablePlacesForOccupation(occupation)
                .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, place, prevTimeSlot));
        var nextStream = _schedulingInput.getAvailablePlacesForOccupation(occupation)
                .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, place, nextTimeSlot));

        LinearConstraint constraint =_constraintFactory.createVarSumBoundUb(getNextConstraintLabel(),Stream.concat(prevStream,nextStream),1.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void create1after0implies0before_lessonRequest(LessonRequestOccupation occupation, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int i, int firstSlot) {
        // (x_{i} + 1 - x{i+1})*(i) >= \sum_{t = 0}^i x_{t}
        WeeklyTimeSlot timeSlot = timeSlots.get(i);
        WeeklyTimeSlot nextTimeSlot = timeSlots.get(i+1);

        Stream<String> current = getOccupationTimeSlotLROPT(occupation, timeSlot);
        Stream<String> next = getOccupationTimeSlotLROPT(occupation, nextTimeSlot);

        Stream<String> allBefore = IntStream.range(firstSlot, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .flatMap(ts -> getOccupationTimeSlotLROPT(occupation, ts));

        int numAllBefore = (int)IntStream.range(firstSlot, i)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        Stream<Pair<String, Double>> stream = Stream.of(
                        current.map(tsId -> new Pair<>(tsId, 1.0*numAllBefore)),
                        next.map(tsId -> new Pair<>(tsId, -1.0*numAllBefore)),
                        allBefore
                                .map(tsId -> new Pair<>(tsId, -1.0))
                )
                .flatMap(s -> s);

        LinearExpression expression = new LinearExpressionImpl(stream, numAllBefore);

        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void create1before0implies0after_lessonRequest(LessonRequestOccupation occupation, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int i, int lastSlot) {
        // (x_{i} + 1 - x{i-1})*(n-i-1) >= \sum_{t = i+1}^n x_{t}
        WeeklyTimeSlot prevTimeSlot = timeSlots.get(i-1);
        WeeklyTimeSlot timeSlot = timeSlots.get(i);

        Stream<String> prev = getOccupationTimeSlotLROPT(occupation, prevTimeSlot);
        Stream<String> current = getOccupationTimeSlotLROPT(occupation, timeSlot);

        Stream<String> allNext = IntStream.range(i+1, lastSlot)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .flatMap(ts -> getOccupationTimeSlotLROPT(occupation, ts));

        int numAllNext = (int)IntStream.range(i+1, lastSlot)
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        Stream<Pair<String, Double>> stream = Stream.of(
                        prev.map(tsId -> new Pair<>(tsId, 1.0*numAllNext)),
                        current.map(tsId -> new Pair<>(tsId, 1.0*numAllNext)),
                        allNext
                                .map(tsId -> new Pair<>(tsId, -1.0))
                )
                .flatMap(s -> s);
        LinearExpression expression = new LinearExpressionImpl(stream, numAllNext);

        LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
        _milpModelBuilder.addConstraint(constraint);
    }

    private void create0before0afterImpliesAllZeros_lessonRequest(LessonRequestOccupation occupation, List<WeeklyTimeSlot> timeSlots, Predicate<WeeklyTimeSlot> predicate, int i, int firstSlot, int lastSlot) {
        // (1 + x_{i-1} + x_{i+1} - x_{i})*(n-3) >= \sum_{t = 1}^{i-2} x_{t} + \sum{t = i+2}^{n} x_{t}
        WeeklyTimeSlot prevTimeSlot = timeSlots.get(i-1);
        WeeklyTimeSlot timeSlot = timeSlots.get(i);
        WeeklyTimeSlot nextTimeSlot = timeSlots.get(i+1);

        Stream<String> prev = getOccupationTimeSlotLROPT(occupation, prevTimeSlot);
        Stream<String> current = getOccupationTimeSlotLROPT(occupation, timeSlot);
        Stream<String> next = getOccupationTimeSlotLROPT(occupation, nextTimeSlot);

        Stream<String> allOthers = IntStream.concat(IntStream.range(firstSlot, i-1), IntStream.range(i +2, lastSlot))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .flatMap(ts -> getOccupationTimeSlotLROPT(occupation, ts));

        int numOthers = (int)IntStream.concat(IntStream.range(firstSlot, i-1), IntStream.range(i +2, lastSlot))
                .mapToObj(timeSlots::get)
                .filter(predicate)
                .count();

        if(numOthers > 0) {
            Stream<Pair<String, Double>> stream = Stream.of(
                    prev.map(tsId -> new Pair<>(tsId, 1.0)),
                    current.map(tsId -> new Pair<>(tsId, -1.0)),
                    next.map(tsId -> new Pair<>(tsId, 1.0)),
                    allOthers
                            .map(tsId -> new Pair<>(tsId, -1.0))
            )
                    .flatMap(s -> s);
            LinearExpression expression = new LinearExpressionImpl(stream, numOthers);

            LinearConstraint constraint = new LinearConstraintImpl(getNextConstraintLabel(), expression, ConstraintType.MORE_OR_EQUAL, 0.0);
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private Stream<String> getOccupationTimeSlotLROPT(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot) {
        return _schedulingInput.getAvailablePlacesForOccupationAtTimeSlot(occupation, timeSlot)
                .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(occupation.getLessonRequest(), occupation, place, timeSlot));
    }


    @Override
    public void addMaxLessonsPerDay(LessonRequest lessonRequest) {
        _schedulingInput.getDays()
                        .forEach(dayOfWeek -> {
                            lessonRequest
                                    .getOccupations()
                                    .forEach(occupation -> {
                                        Stream<String> varNames = _schedulingInput.getTimeslotsSequenceForDay(dayOfWeek)
                                                .stream()
                                                .filter(timeSlot -> _schedulingInput.ifTimeSlotAvailableForLessonRequest(lessonRequest, timeSlot))
                                                .flatMap(timeSlot -> occupation
                                                        .getCandidatePlaces()
                                                        .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest, occupation, place, timeSlot))
                                                );
                                        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), varNames, lessonRequest.getCourseInProgram().getMaxLessonsPerDay());
                                        _milpModelBuilder.addConstraint(constraint);
                                    });
                        });

    }

    private void addGroupDayContinuityConstraint(Group group, DayOfWeek dayOfWeek) {
        String dayNumLessonsVarName = getGroupDayNumLessonsVarName(group, dayOfWeek);
        var timeSlotVars = _schedulingInput.getTimeslotsSequenceForDay(dayOfWeek).stream()
                .filter(timeSlot -> _schedulingInput.ifTimeSlotAvailableForGroup(group, timeSlot))
                .map(timeSlot -> getGroupTimeSlotVarName(group, timeSlot));

        var constraint = _constraintFactory.createVarGroupsSumEquality(getNextConstraintLabel(), timeSlotVars, Stream.of(dayNumLessonsVarName));
        _milpModelBuilder.addConstraint(constraint);
    }

    private String getGroupTimeSlotVarName(Group group, WeeklyTimeSlot timeSlot) {
        String prefix = SchedulingVarNamesVocabulary.getGroupTimeSlotVarPrefix();
        String groupId = _schedulingInput.getGroupId(group);
        String timeSlotId = _schedulingInput.getTimeSlotId(timeSlot);
        return _nameSpace.getVariableName(prefix, List.of(groupId, timeSlotId));
    }

    @Override
    public void addMaxDaysForTeacher(Teacher teacher, int maxDays) {
        Stream<DayOfWeek> days = _schedulingInput.getDays();
        Stream<String> varNames = days
                .filter(day -> _schedulingInput.ifDayAvailableForTeacher(teacher, day))
                .map(day -> getTeacherDayVarName(teacher, day));
        var constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), varNames, maxDays);
        _milpModelBuilder.addConstraint(constraint);

    }

    @Override
    public void addTeacherMinDays(double coefficient) {
        Stream<Pair<String, Double>> varCoefficientStream = _schedulingInput.getTeachers()
                .stream()
                .flatMap(teacher -> _schedulingInput.getAvailableDaysForTeacher(teacher)
                        .map(day -> getTeacherDayVarName(teacher, day))
                )
                .map(varName -> new Pair<>(varName, coefficient));
        _milpModelBuilder.addToObjective(varCoefficientStream);
        _milpModelBuilder.setObjectiveType(ObjectiveType.MINIMIZATION);
    }

    @Override
    public void addTeacherMinSumDuration(double coefficient) {
        Stream<Pair<String, Double>> varCoefficientStream = _schedulingInput.getTeachers()
                .stream()
                .flatMap(teacher -> _schedulingInput.getAvailableDaysForTeacher(teacher)
                        .map(day -> getTeacherDayDurationVarName(teacher, day))
                )
                .map(varName -> new Pair<>(varName, coefficient));
        _milpModelBuilder.addToObjective(varCoefficientStream);
        _milpModelBuilder.setObjectiveType(ObjectiveType.MINIMIZATION);
    }

    @Override
    public void addMaxDaysForLessonRequest(LessonRequest lessonRequest) {
        int max = lessonRequest.getCourseInProgram().getCourseDaysPerWeek();
        Map<DayOfWeek, Variable> map = _schedulingInput.getAvailableDaysForLessonRequest(lessonRequest)
                        .collect(Collectors.toConcurrentMap(day -> day, day -> createLessonRequestDayVariable(lessonRequest, day)));
        map.values().forEach(_milpModelBuilder::addVariable);

        Stream<String> varNames = map.values()
                .stream()
                .map(Variable::getLabel);
        LinearConstraint constraint = _constraintFactory.createVarSumBoundUb(getNextConstraintLabel(), varNames, 1.0*max);
        _milpModelBuilder.addConstraint(constraint);
        // add lesson day var and lropt vars relationships
        map.keySet()
                .forEach(day -> addLessonRequestDayVariablesConstraints(lessonRequest, day));


    }

    @Override
    public void addTeacherTotalDurationCut() {
        _schedulingInput.getTeachers()
                .forEach(teacher -> {
                    int totalTeacherLoad = _schedulingInput.getTotalTeacherLessonsLoad(teacher);
                    var varNames = _schedulingInput.getAvailableDaysForTeacher(teacher)
                            .map(day -> getTeacherDayDurationVarName(teacher, day));
                    LinearConstraint constraint = _constraintFactory.createVarSumBoundLb(getNextConstraintLabel(),varNames, totalTeacherLoad*1.0);
                    _milpModelBuilder.addConstraint(constraint);
                });
    }

    private void addLessonRequestDayVariablesConstraints(LessonRequest lessonRequest, DayOfWeek day) {
        var dayVarName = getLessonRequestDayVarName(lessonRequest, day);
        lessonRequest.getOccupations()
                .forEach(occupation -> {
                    _schedulingInput.getAvailableSlotsForLessonRequest(lessonRequest)
                            .filter(timeSlot -> timeSlot.getDayOfWeek().equals(day))
                            .forEach(timeSlot -> _schedulingInput.getAvailablePlacesForOccupationAtTimeSlot(occupation, timeSlot)
                                    .map(place -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest,occupation,place,timeSlot))
                                    .forEach(varName -> {
                                        LinearConstraint constraint = _constraintFactory.createBinaryBlockingConstraint(getNextConstraintLabel(),dayVarName, Stream.of(varName));
                                        _milpModelBuilder.addConstraint(constraint);
                                    })
                            );
                });
    }
    //TODO GLOBAL NOTE! BLOCKING CONSTRAINTS ARE NOT VERY EFFICIENT!!!

    private Variable createLessonRequestDayVariable(LessonRequest lessonRequest, DayOfWeek day) {
        boolean ifMandatory = _schedulingInput.ifDayMandatoryForLessonRequest(lessonRequest, day);
        String varName = getLessonRequestDayVarName(lessonRequest, day);
        if(ifMandatory) {
            return new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1));
        } else {
            return new Variable_BinaryImpl(varName);
        }
    }

    private String getLessonRequestDayVarName(LessonRequest lessonRequest, DayOfWeek dayOfWeek) {
        String prefix = SchedulingVarNamesVocabulary.getLessonRequestDayPrefix();
        String lrId = "" + lessonRequest.getId();
        return _nameSpace.getVariableName(prefix, List.of(lrId, dayOfWeek.name()));
    }

    @Override
    public Stream<Lesson> createLessonsStream(Stream<Pair<String, Double>> varValues) {
        String filterPrefix = SchedulingVarNamesVocabulary.getLessonRequestOccupationIdPlaceTimeSlotPrefix();
        return varValues
                .filter(pair -> Double.compare(pair.getValue(), 0.0) != 0)
                .filter(pair -> _nameSpace.ifLabeledBy(pair.getKey(), filterPrefix))
                .map(Pair::getKey)
                .flatMap(this::createLessons);
    }

    private Stream<Lesson> createLessons(String lessonRequestOccupationIdPlaceTimeSlot) {
        var indices = _nameSpace.getVariableIndices(lessonRequestOccupationIdPlaceTimeSlot);
        String lessonId = indices.get(0);

        LessonRequest lessonRequest = _schedulingInput.getLessonRequest(lessonId);
        Group group = lessonRequest.getGroup();

        String occupationId = indices.get(1);

        Course course = lessonRequest.getOccupation(occupationId).getCourse();
        Stream<Teacher> teachers = lessonRequest.getOccupation(occupationId)
                .getOccupiedTeachers();

        String placeId = indices.get(2);
        Place place = _schedulingInput.getPlace(placeId);
        String timeSlotId = indices.get(3);
        WeeklyTimeSlot timeSlot = _schedulingInput.getTimeSlot(timeSlotId);
        return teachers
                .map(teacher -> new LessonImpl(timeSlot, group, place, teacher, course));

    }

    @Override
    public void addTeacherDayTimeSlotVariablesRelations(Teacher teacher) {
        Stream<DayOfWeek> days = _schedulingInput.getDays();
        days
            .filter(day -> _schedulingInput.ifDayAvailableForTeacher(teacher, day))
            .forEach(day -> {
                String dayVarName = getTeacherDayVarName(teacher, day);
                _schedulingInput.getTimeslotsSequenceForDay(day)
                        .stream()
                        .filter(timeSlot -> _schedulingInput.ifTimeSlotAvailableForTeacher(teacher, timeSlot))
                        .forEach(timeSlot -> {
                            String timeSlotVarName = getTeacherTimeSlotVarName(teacher, timeSlot);
                            var constraint = _constraintFactory.createBinaryBlockingConstraint(getNextConstraintLabel(), dayVarName, Stream.of(timeSlotVarName));
                            _milpModelBuilder.addConstraint(constraint);
                        });

        });
    }

    @Override
    public void addEachOccupationOfRequestShouldBeInTheSamePlace(LessonRequestOccupation occupation) {
        Set<Place> availablePlaces =  _schedulingInput.getAvailablePlacesForOccupation(occupation)
                .collect(Collectors.toSet());
        addOccupationPlaceVariables(occupation, availablePlaces);
        availablePlaces.forEach(place -> addConstraintOccupationInPlaceImpliesOccupationPlaceVariables(occupation, place));
    }

    private void addConstraintOccupationInPlaceImpliesOccupationPlaceVariables(LessonRequestOccupation occupation, Place place) {
        String placeVarName = getLessonRequestOccupationPlaceVarName(occupation, place);
        LessonRequest lessonRequest = occupation.getLessonRequest();
        _schedulingInput.getAvailableSlotsForLessonRequest(occupation.getLessonRequest())
                .map(timeSlot -> getLessonRequestOccupationIdPlaceTimeSlotName(lessonRequest,occupation, place, timeSlot))
                .forEach(varName -> {
                    LinearConstraint linearConstraint = _constraintFactory.createBinaryBlockingConstraint(getNextConstraintLabel(),placeVarName,Stream.of(varName));
                    _milpModelBuilder.addConstraint(linearConstraint);
                });
    }

    private void addOccupationPlaceVariables(LessonRequestOccupation occupation, Set<Place> availablePlaces) {
        if(availablePlaces.size() == 1) {
            Place place = availablePlaces.stream().findAny().get();
            String varName = getLessonRequestOccupationPlaceVarName(occupation, place);
            var variable =  new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1));
            _milpModelBuilder.addVariable(variable);
        } else {
            Map<Place, Variable> map = availablePlaces
                    .stream()
                    .collect(Collectors.toConcurrentMap(place -> place, place -> createOccupationBinaryPlaceVariable(occupation, place)));
            map.values().forEach(_milpModelBuilder::addVariable);
            Stream<String> varLabels = map.values()
                    .stream()
                    .map(Variable::getLabel);
            LinearConstraint constraint = _constraintFactory.createVarSumEquals(getNextConstraintLabel(), varLabels, 1.0);
            _milpModelBuilder.addConstraint(constraint);
        }
    }

    private Variable createOccupationBinaryPlaceVariable(LessonRequestOccupation occupation, Place place) {
        String varName = getLessonRequestOccupationPlaceVarName(occupation, place);
        return new Variable_BinaryImpl(varName);
    }

    private String getLessonRequestOccupationPlaceVarName(LessonRequestOccupation occupation, Place place) {
        var lessonRequest = occupation.getLessonRequest();
        String prefix = SchedulingVarNamesVocabulary.getLessonRequestOccupationIdPlacePrefix(); ;

        String placeId = _schedulingInput.getPlaceId(place);
        var list = List.of("" + lessonRequest.getId(), "" + occupation.getId(), placeId);

        return  _nameSpace.getVariableName(prefix, list);
    }


    private String getLessonRequestOccupationIdPlaceTimeSlotName(LessonRequest lessonRequest, LessonRequestOccupation occupation, Place place, WeeklyTimeSlot timeSlot) {
        String prefix = SchedulingVarNamesVocabulary.getLessonRequestOccupationIdPlaceTimeSlotPrefix(); ;

        String placeId = _schedulingInput.getPlaceId(place);
        String timeSlotId =  _schedulingInput.getTimeSlotId(timeSlot);
        var list = List.of("" + lessonRequest.getId(), "" + occupation.getId(), placeId, timeSlotId);

        return  _nameSpace.getVariableName(prefix, list);
    }

    @Override
    public MilpModel getMilpModel() {
        return _milpModelBuilder.getModel();
    }

    private Variable createTeacherDayVariable(Teacher teacher, DayOfWeek dow) {
        var varName = getTeacherDayVarName(teacher, dow);
        if (!_schedulingInput.ifDayAvailableForTeacher(teacher, dow)) {
            return new Variable_IntegerImpl(varName, OptionalInt.of(0), OptionalInt.of(0));
        } else {
            if (_schedulingInput.ifDayMandatoryForTeacher(teacher, dow)) {
           //     return new Variable_BinaryImpl(varName);
                return new Variable_IntegerImpl(varName, OptionalInt.of(1), OptionalInt.of(1));
            } else {
                return new Variable_BinaryImpl(varName);
            }
        }
    }

    private String getTeacherDayVarName(Teacher teacher, DayOfWeek dayOfWeek) {
        String prefix = SchedulingVarNamesVocabulary.getTeacherDayVarPrefix();
        String teacherId = _schedulingInput.getTeacherId(teacher);
        return _nameSpace.getVariableName(prefix, List.of(teacherId, dayOfWeek.name()));
    }

    private String getTeacherDayStartVarName(Teacher teacher, DayOfWeek dayOfWeek) {
        String prefix = SchedulingVarNamesVocabulary.getTeacherDayStartVarPrefix();
        String teacherId = _schedulingInput.getTeacherId(teacher);
        return _nameSpace.getVariableName(prefix, List.of(teacherId, dayOfWeek.name()));
    }

    private String getTeacherDayDurationVarName(Teacher teacher, DayOfWeek dayOfWeek) {
        String prefix = SchedulingVarNamesVocabulary.getTeacherDayDurationVarPrefix();
        String teacherId = _schedulingInput.getTeacherId(teacher);
        return _nameSpace.getVariableName(prefix, List.of(teacherId, dayOfWeek.name()));
    }


}
