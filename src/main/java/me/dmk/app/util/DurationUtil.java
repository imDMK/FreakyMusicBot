package me.dmk.app.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public final class DurationUtil {

    /**
     * Formats duration to human-readable
     * @param duration duration to format
     * @author EternalCodeTeam
     */
    public static String format(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
