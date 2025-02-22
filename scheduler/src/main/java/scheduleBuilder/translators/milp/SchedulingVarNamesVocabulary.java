package scheduleBuilder.translators.milp;

public class SchedulingVarNamesVocabulary {
    private final static String _lesReqOccupIdPlaceTimeVar = "LROPT";
    private final static String _groupDayStartVar = "GDS";
    private final static String _groupDayDurationVar = "GDD";
    private final static String _groupTimeSlotVar = "GT";
    private final static String _teacherDayVar = "WD";
    private final static String _teacherTimeSlotVar = "WT";
    private final static String _teacherDayStartVar = "WDS";
    private final static String _teacherDayEndVar = "WDE";
    private final static String _teacherDayDurationVar ="WDD";
    private final static String _lesReqOccupIdPlaceVar = "LROP";
    private final static String _lesReqDayVar = "LRD";


    public static String getTeacherTimeSlotVarPrefix() {
        return _teacherTimeSlotVar;
    }
    public static String getTeacherDayVarPrefix() {
        return _teacherDayVar;
    }

    public static String getLessonRequestOccupationIdPlaceTimeSlotPrefix() {
        return _lesReqOccupIdPlaceTimeVar;
    }

    public static String getGroupDayNumLessonsVarPrefix() {
        return _groupDayDurationVar;
    }

    public static String getGroupDayStartVarPrefix() {
        return _groupDayStartVar;
    }

    public static String getGroupTimeSlotVarPrefix() {
        return _groupTimeSlotVar;
    }

    public static String getTeacherDayStartVarPrefix() {
        return _teacherDayStartVar;
    }

    public static String getTeacherDayDurationVarPrefix() {
        return _teacherDayDurationVar;
    }

    public static String getLessonRequestOccupationIdPlacePrefix() {
       return  _lesReqOccupIdPlaceVar;
    }

    public static String getLessonRequestDayPrefix() {
        return _lesReqDayVar;
    }
}
