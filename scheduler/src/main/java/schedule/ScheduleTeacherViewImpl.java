package schedule;

import java.util.Date;

public class ScheduleTeacherViewImpl implements ScheduleTeacherView {

    @Override
    public boolean ifWorkday(Date date) {
        return false;
    }
}
