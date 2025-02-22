package lesson;

import java.util.stream.Stream;

public interface LessonRequestsBuilder {
    Stream<LessonRequest> getFeasibleLessonRequestsStream();
}
