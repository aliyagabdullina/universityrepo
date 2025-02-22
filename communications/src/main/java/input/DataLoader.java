package input;

import collector.SchoolDataCollector;
import constraint.assignment.AssignmentCollector;
import constraint.timeConstraint.TimeTablesCollector;
import lesson.LessonRequest;
import output.DtoFileSettings;
import schedule.Schedule;
import scheduleBuilder.actions.SchedulingSetupActions;
import time.WeeklyTimeSlot;

import java.io.File;
import java.util.Map;

public interface DataLoader {
    void setSettings(DtoFileSettings settings);
    void loadSchoolData(SchoolDataCollector dataCollector);
    void loadAssignmentData(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector);

    void loadTimeTables(SchoolDataCollector dataCollector, WeeklyTimeSlot[][] timeSlots, TimeTablesCollector collector);

    Schedule loadSchedule(SchoolDataCollector dataCollector, File scheduleFile);

    SchedulingSetupActions loadSchedulingSetup(SchoolDataCollector dataCollector, File file, Map<Integer, LessonRequest> lessonRequestMap);
}
