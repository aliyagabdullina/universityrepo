package programSettings;

public class VocabularyFilesImpl implements VocabularyFiles {
    private final String _dataFolderName = "data";
    private final String _schedulerFolderName = "scheduler";
    private final String _teachersFileName = "teachers.json";
    private final String _coursesFileName = "courses.json";
    private final String _groupsFileName = "groups.json";
    private final String _placesFileName = "places.json";
    private final String _timeSlotsFileName = "timeSlots.csv";
    private String _lessonRequestsFileName = "lessonRequests.json";
    private String _courseProgramFileName = "coursePrograms.json";
    private String _groupCourseProgramsFileName = "groupCoursePrograms.json";
    private String _courseTeachersFileName = "courseTeachers.json";
    private String _groupCourseTeacherFileName = "groupCourseTeacherAssignments.json";
    private String _teacherPlacesFileName = "teacherPlaces.json";
    private String _coursePlacesFileName = "coursePlaces.json";
    private String _groupPlacesFileName = "groupPlaces.json";
    private String _teacherTimeslotsFileName = "teacherTimeSlots.json";
    private String _placeTimeslotsFileName = "placeTimeSlots.json";
    private String _groupTimeslotsFileName = "groupTimeSlots.json";
    private String _schedulesFolderName = "schedules";
    private String _teacherTimeMetricConstraintsFileName = "teacherTimeMetricConstraints.json";
    private String _bufferDirectoryName = "buf";
    private String _scriptsDirectory = "scripts";
    private String _groupTimeMetricConstraintsFileName = "groupTimeMetricConstraints.json";
    private String _setupsFolderName = "setups";


    @Override
    public String getDataFolderName() {
        return _dataFolderName;
    }

    @Override
    public String getTeachersFileName() {
        return _teachersFileName;
    }

    @Override
    public String getCoursesFileName() {
        return _coursesFileName;
    }

    @Override
    public String getGroupsFileName() {
        return _groupsFileName;
    }

    @Override
    public String getPlacesFileName() {
        return _placesFileName;
    }

    @Override
    public String getSchedulerFolderName() {
        return _schedulerFolderName;
    }

    @Override
    public String getTimeSlotsFileName() {
        return _timeSlotsFileName;
    }

    @Override
    public String getLessonRequestsFileName() {
        return _lessonRequestsFileName;
    }

    @Override
    public String getCourseProgramFileName() {
        return _courseProgramFileName;
    }

    @Override
    public String getGroupCourseProgramsFileName() {
        return _groupCourseProgramsFileName;
    }

    @Override
    public String getCourseTeachersFileName() {
        return _courseTeachersFileName;
    }

    @Override
    public String getGroupCourseTeacherFileName() {
        return _groupCourseTeacherFileName;
    }

    @Override
    public String getCoursePlacesFileName() {
        return _coursePlacesFileName;
    }

    @Override
    public String getGroupPlacesFileName() {
        return _groupPlacesFileName;
    }

    @Override
    public String getTeacherPlacesFileName() {
        return _teacherPlacesFileName;
    }

    @Override
    public String getTeacherTimeslotsFileName() {
        return _teacherTimeslotsFileName;
    }
    @Override
    public String getPlaceTimeslotsFileName() {
        return _placeTimeslotsFileName;
    }
    @Override
    public String getGroupTimeslotsFileName() {
        return _groupTimeslotsFileName;
    }

    @Override
    public String getTeacherTimeMetricConstraintsFileName() {
        return _teacherTimeMetricConstraintsFileName;
    }

    @Override
    public String getBufferDirectoryName() {
        return _bufferDirectoryName;
    }

    @Override
    public String getGroupTimeMetricConstraintsFileName() {
        return _groupTimeMetricConstraintsFileName;
    }

    @Override
    public String getGroupsTimeslotsFileName() {
        return _groupTimeslotsFileName;
    }

    @Override
    public String getSetupsFolderName() {
        return _setupsFolderName;
    }

    @Override
    public String getSchedulesFolderName() {
        return _schedulesFolderName;
    }

    @Override
    public String getScriptsDirectoryName() {
        return _scriptsDirectory;
    }
}
