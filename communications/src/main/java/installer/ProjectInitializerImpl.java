package installer;

import output.DtoFileSettings;
import programSettings.VocabularyFiles;

import java.io.File;
import java.io.IOException;

public class ProjectInitializerImpl implements ProjectInitializer {
    private final VocabularyFiles _vocabularyFiles;

    public ProjectInitializerImpl(VocabularyFiles vocabularyFiles) {
        _vocabularyFiles = vocabularyFiles;
    }

    @Override
    public void initializeProjectFilesFromRoot(File root) {
        root.mkdir();
        DtoFileSettings settings = initializeSettings(root);
        settings.dataDirectory.mkdir();
        settings.schedulerDirectory.mkdir();
        settings.schedulesDirectory.mkdir();
        settings.setupsDirectory.mkdir();
        settings.bufferDirectory.mkdir();
        settings.scriptsDirectory.mkdir();
        createEmptyFile(settings.coursesFile);
        createEmptyFile(settings.teachersFile);
        createEmptyFile(settings.placesFile);
        createEmptyFile(settings.courseProgramsFile);
        createEmptyFile(settings.groupsFile);
        createEmptyFile(settings.timeSlotsFile);
        createEmptyFile(settings.lessonRequestsFile);
        createEmptyFile(settings.groupCourseProgramsFile);
        createEmptyFile(settings.courseTeachersFile);
        createEmptyFile(settings.groupCourseTeacherFile);
        createEmptyFile(settings.teacherPlacesFile);
        createEmptyFile(settings.coursePlacesFile);
        createEmptyFile(settings.groupPlacesFile);
        createEmptyFile(settings.teacherTimeSlotsFile);
        createEmptyFile(settings.groupTimeSlotsFile);
        createEmptyFile(settings.placeTimeSlotsFile);
        createEmptyFile(settings.teachersTimeMetricConstraints);
        createEmptyFile(settings.groupsTimeMetricConstraints);
    }

    private DtoFileSettings initializeSettings(File root) {
        DtoFileSettings settings = new DtoFileSettings();
        // DATA
        settings.dataDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getDataFolderName());
        String dataDirPath = settings.dataDirectory.getAbsolutePath();
        settings.teachersFile = new File(dataDirPath + "//" + _vocabularyFiles.getTeachersFileName());
        settings.coursesFile = new File(dataDirPath + "//" + _vocabularyFiles.getCoursesFileName());
        settings.groupsFile = new File(dataDirPath + "//" + _vocabularyFiles.getGroupsFileName());
        settings.placesFile = new File(dataDirPath + "//" + _vocabularyFiles.getPlacesFileName());
        settings.courseProgramsFile = new File(dataDirPath + "//" + _vocabularyFiles.getCourseProgramFileName());
        // SCHEDULER DATA
        settings.schedulerDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getSchedulerFolderName());
        String schedDirPath = settings.schedulerDirectory.getAbsolutePath();
        settings.timeSlotsFile = new File(schedDirPath + "//" + _vocabularyFiles.getTimeSlotsFileName());
        settings.lessonRequestsFile = new File(schedDirPath + "//" + _vocabularyFiles.getLessonRequestsFileName());
        settings.groupCourseProgramsFile = new File(schedDirPath + "//" + _vocabularyFiles.getGroupCourseProgramsFileName());
        settings.courseTeachersFile = new File(schedDirPath + "//" + _vocabularyFiles.getCourseTeachersFileName());
        settings.groupCourseTeacherFile = new File(schedDirPath + "//" + _vocabularyFiles.getGroupCourseTeacherFileName());

        settings.schedulesDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getSchedulesFolderName());
        settings.setupsDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getSetupsFolderName());
        settings.scriptsDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getScriptsDirectoryName());
        settings.bufferDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getBufferDirectoryName());
        return settings;
    }
    private void createEmptyFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
