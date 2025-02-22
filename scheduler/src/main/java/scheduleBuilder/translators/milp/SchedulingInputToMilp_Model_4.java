package scheduleBuilder.translators.milp;

import lesson.Lesson;
import lesson.LessonRequest;
import model.MilpModel;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.SchedulingInputV2;
import solution.MilpSolution;
import solution.SolutionStatus;

import java.util.stream.Stream;

public class SchedulingInputToMilp_Model_4 implements MilpTranslatorV2{
    private SchoolMilpModelBuilder _builder;
    @Override
    public MilpModel createModel(SchedulingInputV2 schedulingInput) {
        _builder = new SchoolMilpModelBuilderImpl(schedulingInput);
        createVariables(schedulingInput);
        createObjectives();
        createConstraints(schedulingInput);
        return _builder.getMilpModel();
    }

    private void createVariables(SchedulingInputV2 schedulingInput) {
        createTeacherTimeSlotsVariables(schedulingInput);
        createTeacherDayVariables(schedulingInput);

        createGroupTimeSlotsVariables(schedulingInput);
        createGroupDayVariables(schedulingInput);
        createLessonRequestVariables(schedulingInput);
    }

    private void crateLessonRequestContinuityConstraints(SchedulingInputV2 schedulingInput) {
        schedulingInput.getLessonRequests()
                .forEach(lessonsRequest -> {
                    schedulingInput.getAvailableDaysForLessonRequest(lessonsRequest)
                            .forEach(dayOfWeek -> _builder.addLessonRequestDailyContinuity(lessonsRequest, dayOfWeek));

                });
    }

    private void createConstraints(SchedulingInputV2 schedulingInput) {
        createMandatoryConstraints(schedulingInput);
        createOptionalConstraints(schedulingInput);
    }

    private void createMandatoryConstraints(SchedulingInputV2 schedulingInput) {
        allRequestsSatisfied(schedulingInput);
        notMoreThanOneLessonPerTeacher(schedulingInput);
        teacherDayTimeSlotVarConnection(schedulingInput);
        notMoreThanOneOccupationInPlace(schedulingInput);

        notMoreThanOneLessonPerGroup(schedulingInput);
        groupDayTimeSlotVarsConnection(schedulingInput);
        teacherDayStartDurationTimeSlotVarsConnection(schedulingInput);
        crateLessonRequestContinuityConstraints(schedulingInput);
    }

    private void createOptionalConstraints(SchedulingInputV2 schedulingInput) {
        maxDaysForTeachers(schedulingInput);
        maxLessonsPerDay(schedulingInput);
        maxDaysPerLesson(schedulingInput);
        eachOccupationInOnePlace(schedulingInput);
        //CUTS
        _builder.addTeacherTotalDurationCut();
    }

    private void maxDaysPerLesson(SchedulingInputV2 schedulingInput) {
        schedulingInput.getLessonRequests()
                .forEach(_builder::addMaxDaysForLessonRequest);

    }

    private void eachOccupationInOnePlace(SchedulingInputV2 schedulingInput) {
        schedulingInput.getLessonRequests()
                .stream()
                .flatMap(LessonRequest::getOccupations)
                .forEach(_builder::addEachOccupationOfRequestShouldBeInTheSamePlace);
    }

    private void maxLessonsPerDay(SchedulingInputV2 schedulingInput) {
        schedulingInput.getLessonRequests()
                .forEach(_builder::addMaxLessonsPerDay);
    }

    private void maxDaysForTeachers(SchedulingInputV2 schedulingInput) {
        int numDays = (int) schedulingInput.getDays().count();
        schedulingInput.getTeachers()
                        .forEach(teacher -> {
                            int maxDays = schedulingInput.getMaxDaysForTeacher(teacher);
                            if(maxDays > numDays) {
                                _builder.addMaxDaysForTeacher(teacher, maxDays);
                            }
                        });
    }

    private void teacherDayTimeSlotVarConnection(SchedulingInputV2 schedulingInput) {
        schedulingInput.getTeachers()
                .forEach(_builder::addTeacherDayTimeSlotVariablesRelations);
    }



    private void groupDayTimeSlotVarsConnection(SchedulingInputV2 schedulingInput) {
        schedulingInput.getGroups()
                .forEach(group -> {
                    schedulingInput.getAvailableDaysForGroup(group)
                            .forEach(dayOfWeek -> _builder.addGroupDayTimeslotVarConnections(group, dayOfWeek));
                });
    }

    private void teacherDayStartDurationTimeSlotVarsConnection(SchedulingInputV2 schedulingInput) {
        schedulingInput.getTeachers()
                .forEach(teacher -> {
                    schedulingInput.getAvailableDaysForTeacher(teacher)
                            .forEach(dayOfWeek -> _builder.addTeacherDayStartDurationTimeslotVarConnections(teacher, dayOfWeek));
                });
    }

    private void notMoreThanOneLessonPerGroup(SchedulingInputV2 schedulingInput) {
        schedulingInput.getGroups()
                .forEach(group -> {
                    schedulingInput.getAvailableSlotsForGroup(group)
                            .forEach(timeSlot -> _builder.addExactlyOneLessonPerGroupAtTimeSlots(group, timeSlot));
                });
    }

    private void notMoreThanOneOccupationInPlace(SchedulingInputV2 schedulingInput) {
        schedulingInput.getPlaces()
                .forEach(place -> {
                    schedulingInput.getTimeslotSequence().forEach(timeSlot ->
                            _builder.addNotMoreThanOneOccupationPerPlaceAtTimeSlot(place, timeSlot));
                });
    }

    private void notMoreThanOneLessonPerTeacher(SchedulingInputV2 schedulingInput) {
        schedulingInput.getTeachers()
                        .forEach(teacher -> {
                            schedulingInput.getAvailableSlotsForTeacher(teacher)
                                    .forEach(timeSlot -> _builder.addNotMoreThanOneLessonPerTeacherAtTimeSlot(teacher, timeSlot));
                        });
    }

    private void allRequestsSatisfied(SchedulingInputV2 schedulingInput) {
        schedulingInput.getLessonRequests().forEach(_builder::addLessonRequestsSatisfied);
    }

    private void createObjectives() {
        _builder.addTeacherMinDays(1.0);
        _builder.addTeacherMinSumDuration(1.0);
    }



    private void createLessonRequestVariables(SchedulingInputV2 schedulingInput) {
        schedulingInput.getLessonRequests()
                .forEach(lessonsRequest -> {
                    schedulingInput.getAvailableSlotsForLessonRequest(lessonsRequest)
                            .forEach(timeSlot -> _builder.addLessonRequestOccupyPlacesAtTimeVariables(lessonsRequest, timeSlot));
                });
    }

    private void createGroupDayVariables(SchedulingInputV2 schedulingInput) {
        schedulingInput.getGroups()
                .forEach(group -> {
                    schedulingInput.getAvailableDaysForGroup(group)
                            .forEach(dayOfWeek -> _builder.addGroupDayVariables(group, dayOfWeek));
                });
    }

    private void createGroupTimeSlotsVariables(SchedulingInputV2 schedulingInput) {
        schedulingInput.getGroups()
                .forEach(group -> {
                    schedulingInput.getAvailableSlotsForGroup(group)
                            .forEach(timeSlot -> _builder.addGroupTimeslotVariable(group, timeSlot));
                });
    }

    private void createTeacherTimeSlotsVariables(SchedulingInputV2 schedulingInput) {
        schedulingInput.getTeachers()
                .forEach(teacher -> {
                    schedulingInput.getAvailableSlotsForTeacher(teacher)
                            .forEach(timeSlot -> _builder.addTeacherTimeslotVariable(teacher, timeSlot));
                });
    }

    private void createTeacherDayVariables(SchedulingInputV2 schedulingInput) {
        schedulingInput.getTeachers()
                .forEach(teacher -> {
                    schedulingInput.getAvailableDaysForTeacher(teacher)
                            .forEach(day -> _builder.addTeacherDayVariable(teacher, day));
                    }
                );
    }

    @Override
    public Schedule createSchedule(MilpSolution solution) {
        if(solution.getSolutionStatus().equals(SolutionStatus.FEASIBLE) ||solution.getSolutionStatus().equals(SolutionStatus.OPTIMAL)) {
            Stream<Lesson> lessonsStream = _builder.createLessonsStream(solution.getNonZeroValues());
            return new ScheduleImpl(lessonsStream);
        }
       return new ScheduleImpl(Stream.empty());
    }
}
