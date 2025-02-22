package constraint.assignment;

import course.Course;
import course.CourseProgram;
import group.Group;
import pair.Pair;
import person.Teacher;
import place.Place;
import triplet.Triplet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssignmentCollectorImpl implements AssignmentCollector {
    private final Map<Course, Set<Teacher>> _availableTeachersForCourse = new ConcurrentHashMap<>();
    private final Map<Group, CourseProgram> _groupCoursePrograms = new ConcurrentHashMap<>();

    private final Set<Triplet<Group, Course, Teacher>> _assignedSet = new HashSet<>();

    private final Map<Course, Set<Place>> _coursePlaceMap =  new ConcurrentHashMap<>();
    private final Map<Group, Set<Place>> _groupPlaceMap =  new ConcurrentHashMap<>();
    private final Map<Teacher, Set<Place>> _teacherPlaceMap =  new ConcurrentHashMap<>();


    @Override
    public Stream<Place> getAvailablePlaces(Group group, Course course, Stream<Teacher> teachers) {
        Set<Place> placesForCourse = _coursePlaceMap.get(course);
        Set<Place> placesForGroup = _groupPlaceMap.get(group);
        List<Set<Place>> placesForTeacher = teachers.map(teacher -> _teacherPlaceMap.getOrDefault(teacher, new HashSet<>())).toList();
        if(placesForCourse == null || placesForGroup == null) {
            return Stream.empty();
        }
        return placesForCourse
                .stream()
                .filter(placesForGroup::contains)
                .filter(place -> placesForTeacher.stream().allMatch(set-> set.contains(place)));
    }


    @Override
    public void setAvailablePlacesForCourse(Course course, Stream<Place> placeStream) {
        Set<Place> places = placeStream.collect(Collectors.toUnmodifiableSet());
        _coursePlaceMap.put(course, places);
    }

    @Override
    public void addAvailablePlaceForCourse(Course course, Place place) {
        Set<Place> places = _coursePlaceMap.get(course);
        places.add(place);
    }

    @Override
    public void setAvailablePlacesForGroup(Group group, Stream<Place> placeStream) {
        Set<Place> places = placeStream.collect(Collectors.toUnmodifiableSet());
        _groupPlaceMap.put(group, places);
    }

    @Override
    public void addAvailablePlaceForGroup(Group group, Place place) {
        Set<Place> places = _groupPlaceMap.get(group);
        places.add(place);
    }



    @Override
    public void setAvailablePlacesForTeacher(Teacher teacher, Stream<Place> placeStream) {
        Set<Place> places = placeStream.collect(Collectors.toUnmodifiableSet());
        _teacherPlaceMap.put(teacher, places);
    }
    @Override
    public void addAvailablePlaceForTeacher(Teacher teacher, Place place) {
        Set<Place> places = _teacherPlaceMap.get(teacher);
        places.add(place);
    }


    @Override
    public Stream<Place> getAvailablePlacesForCourse(Course course) {
        return _coursePlaceMap.containsKey(course) ? _coursePlaceMap.get(course).stream() : Stream.empty();
    }

    @Override
    public Stream<Place> getAvailablePlacesForGroup(Group group) {
        return _groupPlaceMap.containsKey(group) ? _groupPlaceMap.get(group).stream() : Stream.empty();
    }

    @Override
    public Stream<Place> getAvailablePlacesForTeacher(Teacher teacher) {
        return _teacherPlaceMap.containsKey(teacher) ? _teacherPlaceMap.get(teacher).stream() : Stream.empty();
    }

    @Override
    public void setAvailableTeachersForCourse(Course course, Stream<Teacher> teacherStream) {
        Set<Teacher> teachers = teacherStream.collect(Collectors.toSet());
        _availableTeachersForCourse.put(course, teachers);
    }

    @Override
    public void addTeacherForCourse(Course course, Teacher teacher) {
        Set<Teacher> teachers = _availableTeachersForCourse.get(course);
        teachers.add(teacher);
    }


    @Override
    public Stream<Pair<Course, Set<Teacher>>> getCourseTeachersStream() {
        return _availableTeachersForCourse.entrySet()
                .stream()
                .map(Pair::new);
    }

    @Override
    public void setCourseProgramForGroup(Group group, CourseProgram courseProgram) {
        _groupCoursePrograms.put(group, courseProgram);
    }

    @Override
    public void removeGroupCourseProgramAssignment(Group group, CourseProgram courseProgram) {
        _groupCoursePrograms.remove(group);
    }

    @Override
    public Stream<Pair<Group, CourseProgram>> getGroupCourseProgramStream() {
        return _groupCoursePrograms.entrySet()
                .stream()
                .map(Pair::new);
    }


    @Override
    public void setAssignmentStatus(Group group, Course course, Teacher teacher, AssignmentStatus assignmentStatus) {

        var triplet = new Triplet<>(group, course, teacher);
        if(assignmentStatus.equals(AssignmentStatus.ASSIGNED)) {
            _assignedSet.add(triplet);
        } else {
            _assignedSet.remove(triplet);
        }
    }

    @Override
    public Stream<Teacher> getAvailableTeachersForCourse(Course course) {
        if(_availableTeachersForCourse.containsKey(course)) {
            return _availableTeachersForCourse.get(course).stream();
        } else {
            return Stream.empty();
        }
    }


    @Override
    public CourseProgram getCourseProgramForGroup(Group group) {
        return _groupCoursePrograms.getOrDefault(group, CourseProgram.createEmpty());
    }

    @Override
    public AssignmentStatus getGroupCourseTeacherAssignmentStatus(Group group, Course course, Teacher teacher) {
        var triplet = new Triplet<>(group, course, teacher);

        return _assignedSet.contains(triplet) ? AssignmentStatus.ASSIGNED : AssignmentStatus.UNDEFINED;
    }

    @Override
    public Stream<Teacher> getAssignedTeachers(Group group, Course course) {
        return _assignedSet.stream()
                .filter(t -> t.getFirst().equals(group))
                .filter(t -> t.getSecond().equals(course))
                .map(Triplet::getThird);
    }

    @Override
    public Stream<Triplet<Group, Course, Teacher>> getGroupCourseAssignedTeachers() {
        return _assignedSet.stream();
    }
}
