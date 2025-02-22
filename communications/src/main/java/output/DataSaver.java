package output;

import collector.SchoolDataCollector;
import constraint.timeConstraint.TimeSlotStatus;
import constraint.timeConstraint.TimeTablesCollector;
import course.Course;
import course.CourseProgram;
import group.Group;
import pair.Pair;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import scheduleBuilder.actions.SchedulingSetupActions;
import time.WeeklyTimeSlot;
import triplet.Triplet;

import java.util.Set;
import java.util.stream.Stream;

public interface DataSaver {
    void setSettings(DtoFileSettings settings);
    void saveTeachers(Stream<Teacher> teacherStream);
    void saveGroups(Stream<Group> teacherStream);
    void saveCourses(Stream<Course> courseStream);
    void savePlaces(Stream<Place> placeStream);
    void saveSchoolData(SchoolDataCollector schoolDataCollector);

    // assignments
    void saveGroupCoursePrograms(Stream<Pair<Group, CourseProgram>> groupCourseProgramPairs);
    void saveCourseTeachers(Stream<Pair<Course, Set<Teacher>>> courseTeachersPairs);
    void saveGroupCourseTeacherAssignments(Stream<Triplet<Group, Course, Teacher>> tripletStream);

    // place assignments
    void saveAvailablePlacesForGroups(Stream<Pair<Group, Stream<Place>>> groupPlaceStream);
    void saveAvailablePlacesForCourses(Stream<Pair<Course, Stream<Place>>> coursePlacesStream);
    void saveAvailablePlacesForTeachers(Stream<Pair<Teacher, Stream<Place>>> teacherPlaceStream);
    //time constraints
    void saveTeacherTimeSlotStatuses(WeeklyTimeSlot[][] timeSlots, Stream<Pair<Teacher, TimeSlotStatus[][]>> teacherStatusesStream);
    void saveGroupTimeSlotStatuses(WeeklyTimeSlot[][] timeSlots, Stream<Pair<Group, TimeSlotStatus[][]>> groupStatusesStream);
    void saveTimeMetricConstraints(TimeTablesCollector collector);
    void saveSchedule(Schedule result, String scheduleName);
    // set up
    void saveSchedulingSetup(SchedulingSetupActions schedulingSetupActions, String setUpName);
}
