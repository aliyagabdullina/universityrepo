package scheduleBuilder.data;

import course.Course;
import group.Group;
import lesson.LessonRequest;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ScheduleBuilderDataAccumulatorImpl implements ScheduleBuilderDataAccumulator {
    private boolean _ifTeachersReady = false;
    private boolean _ifTimeSlotsReady = false;
    private boolean _ifPlacesReady = false;
    private boolean _ifGroupsReady = false;
    private boolean _ifCoursesReady = false;


    @Override
    public void setGroupsSupplier(Supplier<Stream<Group>> groupsSupplier) {
        _ifGroupsReady = true;
    }

    @Override
    public void setPlacesSupplier(Supplier<Stream<Place>> placesSupplier) {
        _ifPlacesReady = true;
    }

    @Override
    public void setTimeSlotsSupplier(Supplier<Stream<WeeklyTimeSlot>> timeSlotsSupplier) {
        _ifTimeSlotsReady = true;
    }

    @Override
    public void setTeachersSupplier(Supplier<Stream<Teacher>> teachersSupplier) {
        _ifTeachersReady = true;
    }

    @Override
    public void setLessonRequestsSupplier(Supplier<Stream<LessonRequest>> lessonRequestSupplier) {

    }

    @Override
    public void checkState() {
        checkTeachersReady();
        checkGroupsReady();
        checkPlacesReady();
        checkTimeSlotsReady();
        checkCoursesReady();
    }

    private void checkTeachersReady() {
        if(!_ifTeachersReady) {
            throw new IllegalStateException("Mandatory data supplier not given: " + Teacher.class.getSimpleName() + "s.");
        }
    }

    private void checkTimeSlotsReady() {
        if(!_ifTimeSlotsReady) {
            throw new IllegalStateException("Mandatory data supplier not given: " + WeeklyTimeSlot.class.getSimpleName() + "s.");
        }
    }

    private void checkGroupsReady() {
        if(!_ifGroupsReady) {
            throw new IllegalStateException("Mandatory data supplier not given: " + Group.class.getSimpleName() + "s.");
        }
    }

    private void checkPlacesReady() {
        if(!_ifPlacesReady) {
            throw new IllegalStateException("Mandatory data supplier not given: " + Place.class.getSimpleName() + "s.");
        }
    }

    private void checkCoursesReady() {
        if(!_ifCoursesReady) {
            throw new IllegalStateException("Mandatory data supplier not given: " + Course.class.getSimpleName() + "s.");
        }
    }

}
