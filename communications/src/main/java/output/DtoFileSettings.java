package output;

import java.io.File;
import java.util.function.Function;

public class DtoFileSettings {
    public File dataDirectory;
    public File teachersFile;
    public File groupsFile;
    public File coursesFile;
    public File placesFile;

    public File schedulerDirectory;
    public File timeSlotsFile;
    public File lessonRequestsFile;
    public File courseProgramsFile;
    public File groupCourseProgramsFile;
    public File courseTeachersFile;
    public File groupCourseTeacherFile;
    public File teacherPlacesFile;
    public File coursePlacesFile;
    public File groupPlacesFile;
    public File teacherTimeSlotsFile;
    public File placeTimeSlotsFile;
    public File groupTimeSlotsFile;
    public File teachersTimeMetricConstraints;

    public File schedulesDirectory;
    public Function<String, File> scheduleNameToFile = str -> {
        return new File(schedulesDirectory.getAbsoluteFile() + "//" + str + ".json");
    };

    public File bufferDirectory;
    public File scriptsDirectory;
    public File groupsTimeMetricConstraints;
    public File setupsDirectory;
    public Function<String, File> setupNameToFile = str -> {
        return new File(setupsDirectory.getAbsoluteFile() + "//" + str + ".json");
    };
}
