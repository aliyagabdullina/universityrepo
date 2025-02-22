package time;

import java.util.function.BiFunction;

public interface TimeTranslator {
    static Long getMsTimeByHoursAndMins(int h, int m) {
        return (h* 3600L + m* 60L) *1000;
    }
    static int getHours(long timeInMs){
        return (int)(timeInMs /3600000);
    }

    static int getMinutes(long timeInMs){
        return (int)(timeInMs % 3600000) / 60000;
    }
}
