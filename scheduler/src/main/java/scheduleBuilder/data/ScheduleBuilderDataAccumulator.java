package scheduleBuilder.data;

import group.Group;
import lesson.LessonRequest;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ScheduleBuilderDataAccumulator {
    void checkState() throws IllegalStateException;
    void setGroupsSupplier(Supplier<Stream<Group>> groupsSupplier);
    void setPlacesSupplier(Supplier<Stream<Place>> placesSupplier);
    void setTimeSlotsSupplier(Supplier<Stream<WeeklyTimeSlot>> timeSlotsSupplier);
    void setTeachersSupplier(Supplier<Stream<Teacher>> teachersSupplier);
    void setLessonRequestsSupplier(Supplier<Stream<LessonRequest>> lessonRequestSupplier);
}
