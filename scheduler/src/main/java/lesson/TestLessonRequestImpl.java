package lesson;

import course.Course;
import course.CourseImpl;
import course.CourseInProgram;
import course.CourseInProgramImpl;
import group.Group;
import group.GroupImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;
import place.Place;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestLessonRequestImpl {
    LessonRequest request;
    Teacher teacher;
    Group group;
    Course course;
    List<Place> placeList;

    @BeforeEach
    void setUp() {
        int id = 10;
        teacher = new TeacherImpl("Anna");
        group = new GroupImpl( "1A", Set.of());
        course = new CourseImpl("myCourse");
        placeList = List.of();
        CourseInProgram courseInProgram = new CourseInProgramImpl(Stream.of(course));
        courseInProgram.setLessonsPerWeek(2);
        request = new LessonRequestImpl(id, group, courseInProgram);
        request.addOccupation(course,Stream.of(teacher), placeList.stream());
    }

    @Test
    void testGetId() {
        assertEquals(10, request.getId());
    }

  //  @Test
  //  void testGetTeacher() {
  //      assertNotNull(request.getOccupiedTeachers());
  //      assertEquals(teacher, request.getOccupiedTeachers().findFirst().get());
  //  }

    @Test
    void testGetGroup() {
        assertNotNull(request.getGroup());
        assertEquals(group, request.getGroup());
    }


    @Test
    void testGetNumLessons() {
        assertEquals(2, request.getCourseInProgram().getLessonsPerWeek());
    }

 //   @Test
   // void testGetPlaces() {
     //   assertNotNull(request.getPlacesStream());
   /// }
}