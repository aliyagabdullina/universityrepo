package lesson;

import collector.SchoolDataCollector;
import constraint.assignment.AssignmentCollector;
import course.Course;
import course.CourseInProgram;
import course.CourseProgram;
import group.Group;
import person.Teacher;
import place.Place;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LessonRequestsBuilderImpl implements LessonRequestsBuilder {
    private final SchoolDataCollector _dataCollector;
    private final AssignmentCollector _assignmentCollector;
    private int _nextId = 0;

    public LessonRequestsBuilderImpl(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        _dataCollector = dataCollector;
        _assignmentCollector = assignmentCollector;
    }

    @Override
    public Stream<LessonRequest> getFeasibleLessonRequestsStream() {
        return _dataCollector.getGroups()
                .sequential()
                .flatMap(group -> {
                    CourseProgram program = _assignmentCollector.getCourseProgramForGroup(group);
                    return program
                            .getCoursesInProgram()
                            .sequential()
                            .map(courseInProgram -> createLessonRequest(group, courseInProgram))
                            .filter(lessonsRequest -> lessonsRequest.getCourseInProgram().getLessonsPerWeek() > 0);
                });
    }


    private LessonRequest createLessonRequest(Group group, CourseInProgram courseInProgram) {
        List<Course> courses = courseInProgram.getCourses().toList();
        int id = getNextId();
        LessonRequest result = new LessonRequestImpl(id, group, courseInProgram);

        IntStream.range(0,courses.size())
                .boxed()
                .forEach(i -> {
                    Course course = courses.get(i);
                    Stream<Teacher> assignedTeachers = _assignmentCollector.getAssignedTeachers(group, course);
                    Stream<Place> availablePlaces = _assignmentCollector.getAvailablePlaces(group, course, assignedTeachers);
                    try {
                        result.addOccupation(course,_assignmentCollector.getAssignedTeachers(group, course),availablePlaces);
                    } catch (RuntimeException e) {
                        _assignmentCollector.getAvailablePlaces(group, course, _assignmentCollector.getAssignedTeachers(group, course));
                        throw new RuntimeException("Unsatisfiable request for: \n" +
                                "Course: " + course.getName() + "\n" +
                                "Group: " + group.getName() + "\n" +
                                "Teachers:" + getObjString(_assignmentCollector.getAssignedTeachers(group, course)) + ".\n" +
                                "Reason: " + e.getMessage());
                    }
                });

        return result;
    }

    private <T> String getObjString(Stream<T> objStream) {
        StringBuilder builder = new StringBuilder();
        objStream.forEach(obj -> builder.append("\n\t" + obj.toString()));
        return builder.toString();
    }

    private synchronized int getNextId() {
        int result = _nextId;
        _nextId++;
        return result;
    }
}
