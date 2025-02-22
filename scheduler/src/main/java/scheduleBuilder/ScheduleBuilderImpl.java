package scheduleBuilder;

import collector.SchoolDataCollector;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import group.Group;
import lesson.LessonRequest;
import model.MilpModel;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;
import scheduleBuilder.engines.milp.orTools.*;
import scheduleBuilder.engines.settings.DtoScheduleBuilderSettings;
import scheduleBuilder.translators.milp.MilpTranslatorV2;
import scheduleBuilder.translators.milp.SchedulingInputToMilp_Model_4;
import solution.MilpSolution;
import time.WeeklyTimeSlot;

import java.util.ArrayList;
import java.util.List;

public class ScheduleBuilderImpl implements ScheduleBuilder {
    static {
        Loader.loadNativeLibraries();
    }
    private SchoolDataCollector _dataCollector;
    private ScheduleConstraintsAccumulator _constraintAccumulator;
    private ScheduleObjectiveAccumulator _objectiveAccumulator;
    private SchedulingInputData _inputData;
    private DtoScheduleBuilderSettings _settings;
    private List<WeeklyTimeSlot> _timeSlotSequence = new ArrayList<>();

    @Override
    public void setScheduleInputData(SchedulingInputData inputData) {
        _inputData = inputData;
    }

    @Override
    public void setTimeSlotSequence(List<WeeklyTimeSlot> timeSlotSequence) {
        _timeSlotSequence = timeSlotSequence;
    }

    @Override
    public void setSchoolDataCollector(SchoolDataCollector dataCollector) {
        _dataCollector = dataCollector;
    }


    @Override
    public void setConstraintAccumulator(ScheduleConstraintsAccumulator constraintAccumulator) {
        _constraintAccumulator = constraintAccumulator;
    }

    @Override
    public void setObjectiveAccumulator(ScheduleObjectiveAccumulator objectiveAccumulator) {
        _objectiveAccumulator = objectiveAccumulator;
    }

    @Override
    public void setSettings(DtoScheduleBuilderSettings settings) {
        _settings = settings;
    }

    @Override
    public Schedule solve() {

        //SchedulingInput schedulingInput = createSchedulingInput();
        SchedulingInputV2 schedulingInputV2 = createSchedulingInputV2();
        //MilpTranslator milpTranslator = new SchedulingInputToMilp_Model1();
       // MilpTranslator milpTranslator = new SchedulingInputToMilp_Model();
        //MilpTranslator milpTranslator = new SchedulingInputToMilp_Model_2();
        MilpTranslatorV2 milpTranslatorV2 = new SchedulingInputToMilp_Model_4();


        MilpModel model = milpTranslatorV2.createModel(schedulingInputV2);
        var settings = new MilpOrToolsSettingsImpl(MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        settings.setTimeLimitMs(_settings.timeLimitInSec * 1000);
        MilpOrToolsInput input = new MilpOrToolsInputImpl(model, settings);
        MilpOrToolsRunner runner = new MilpOrToolsRunnerImpl(input);
        MilpOrToolsResult milpResult = runner.run();
        MilpSolution solution = milpResult.getSolution();

        //return milpTranslator.createSchedule(solution);
        return milpTranslatorV2.createSchedule(solution);
    }


    private SchedulingInput createSchedulingInput() {
        List<Teacher> teachers = _dataCollector.getTeachers().toList();
        List<Group> groups = _dataCollector.getGroups().toList();
        List<Place> places = _dataCollector.getPlaces().toList();
        List<LessonRequest> lessonRequests = _inputData.getLessonRequests().toList();
        return new SchedulingInputImpl(_timeSlotSequence, teachers, lessonRequests, groups, places, _constraintAccumulator, _objectiveAccumulator);
    }

    private SchedulingInputV2 createSchedulingInputV2() {
        List<Teacher> teachers = _dataCollector.getTeachers().toList();
        List<Group> groups = _dataCollector.getGroups().toList();
        List<Place> places = _dataCollector.getPlaces().toList();
        var scheduledLessons = _inputData.getScheduledLessons().toList();
        List<LessonRequest> lessonRequests = _inputData.getLessonRequests().toList();
        SchedulingInputV2 input = new SchedulingInputV2Impl(_timeSlotSequence, teachers, groups, places, lessonRequests, scheduledLessons);

        input.setGroupDailyMaxLoadConstraint(_inputData.getGroupDailyMaxLoadMap());
        input.setTeacherAvailableTimeSlots(_inputData.getTeacherAvailableTimeSlotsMap());
        input.setGroupAvailableTimeSlots(_inputData.getGroupAvailableTimeSlotsMap());
        input.setGroupMandatoryTimeSlots(_inputData.getGroupMandatoryTimeSlotsMap());
        input.setTeacherMandatoryTimeSlots(_inputData.getTeacherMandatoryTimeSlotsMap());
        input.setPlaceAvailableTimeSlots(_inputData.getPlaceAvailableTimeSlotsMap());
        input.setTeacherMandatoryDays(_inputData.getTeacherMandatoryDaysMap());
        input.setTeacherMaxDaysMap(_inputData.getTeacherMaxDaysMap());
        input.setLessonRequestAvailableTimeSlots(_inputData.getLessonRequestTimeSlotsMap());
        // NOT SUPPORTED NOW
        // input.setTeachersDailyMaxLoadConstraint(_inputData.getTeacherDailyMaxLoadMap());
        return input;
    }

}
