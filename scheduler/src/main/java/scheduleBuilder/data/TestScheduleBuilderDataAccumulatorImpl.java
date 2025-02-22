package scheduleBuilder.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestScheduleBuilderDataAccumulatorImpl {
    ScheduleBuilderDataAccumulator scheduleBuilderDataAccumulator;

    @BeforeEach
    void setUp() {
        scheduleBuilderDataAccumulator = new ScheduleBuilderDataAccumulatorImpl();
    }

    @Test
    void testBuildIllegalStateException_NoGroups() {
        scheduleBuilderDataAccumulator.setPlacesSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTimeSlotsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTeachersSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setLessonRequestsSupplier(Stream::empty);
        assertThrows(IllegalStateException.class, scheduleBuilderDataAccumulator::checkState);
    }

    @Test
    void testSetGroupsSupplier() {
        assertDoesNotThrow(() -> scheduleBuilderDataAccumulator.setGroupsSupplier(Stream::empty));
    }

    @Test
    void testBuildIllegalStateException_NoPlaces() {
        scheduleBuilderDataAccumulator.setGroupsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTimeSlotsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTeachersSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setLessonRequestsSupplier(Stream::empty);
        assertThrows(IllegalStateException.class, scheduleBuilderDataAccumulator::checkState);
    }

    @Test
    void testSetPlacesSupplier() {
        assertDoesNotThrow(() -> scheduleBuilderDataAccumulator.setPlacesSupplier(Stream::empty));
    }

    @Test
    void testBuildIllegalStateException_NoTimeslots() {
        scheduleBuilderDataAccumulator.setGroupsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setPlacesSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTeachersSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setLessonRequestsSupplier(Stream::empty);
        assertThrows(IllegalStateException.class, scheduleBuilderDataAccumulator::checkState);
    }

    @Test
    void testSetTimeSlotsSupplier() {
        assertDoesNotThrow(() -> scheduleBuilderDataAccumulator.setTimeSlotsSupplier(Stream::empty));
    }

    @Test
    void testBuildIllegalStateException_NoTeachers() {
        scheduleBuilderDataAccumulator.setGroupsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setPlacesSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTimeSlotsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setLessonRequestsSupplier(Stream::empty);
        assertThrows(IllegalStateException.class, scheduleBuilderDataAccumulator::checkState);
    }

    @Test
    void testSetTeachersSupplier() {
        assertDoesNotThrow(() -> scheduleBuilderDataAccumulator.setTeachersSupplier(Stream::empty));
    }

    @Test
    void testBuildIllegalStateException_NoCourses() {
        scheduleBuilderDataAccumulator.setGroupsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setPlacesSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTimeSlotsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTeachersSupplier(Stream::empty);
        assertThrows(IllegalStateException.class, scheduleBuilderDataAccumulator::checkState);
    }

    @Test
    void testSetLessonRequestsSupplier() {
        assertDoesNotThrow(() -> scheduleBuilderDataAccumulator.setLessonRequestsSupplier(Stream::empty));
    }

    @Test
    void testBuildNoException() {
        scheduleBuilderDataAccumulator.setGroupsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setPlacesSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTimeSlotsSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setTeachersSupplier(Stream::empty);
        scheduleBuilderDataAccumulator.setLessonRequestsSupplier(Stream::empty);
        assertDoesNotThrow(scheduleBuilderDataAccumulator::checkState);
    }


}