package logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyLogger {

    private final static String _warnPrefix = "WARNING: ";

    public static void log(String str) {
        System.out.println(getTimePrefix() + str);
    }

    private static String getTimePrefix() {
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return "[" + dtf.format(now) + "] ";
    }

    public static void warn(String s) {
        System.out.println(getTimePrefix() + _warnPrefix + s);
    }
}
