package me.dmk.app.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;

/**
 * Created by DMK on 20.03.2023
 */

@UtilityClass
public class StringUtil {

    public boolean isUrl(String string) {
        return string.startsWith("http://") || string.startsWith("https://");
    }

    public String getImageFromYouTubeVideo(String videoIdentifier) {
        return String.format(
                "https://img.youtube.com/vi/%s/sddefault.jpg",
                videoIdentifier
        );
    }

    public static String formatLong(long i, String single, String second, String many) {
        long iDivided = i % 10L;

        return (i == 1 ? single : (i < 5 || i > 20 && iDivided < 5 && iDivided != 1) ? second : many);
    }

    public static String millisToString(long milliseconds) {
        Duration duration = Duration.ofMillis(milliseconds);
        if (duration.isNegative() || duration.isZero()) {
            return "<1s";
        }

        long millis = duration.toMillis();
        long seconds = duration.toSecondsPart();
        long minutes = duration.toMinutesPart();
        long hours = duration.toHoursPart();
        long days = duration.toDays();

        StringBuilder stringBuilder = new StringBuilder();

        if (days > 0) {
            stringBuilder.append(days)
                    .append(" ")
                    .append(formatLong(days, "dzień", "dni", "dni"))
                    .append(", ");
        }

        if (hours > 0) {
            stringBuilder.append(hours)
                    .append(" ")
                    .append(formatLong(hours, "godzinę", "godziny", "godzin"))
                    .append(", ");
        }

        if (minutes > 0) {
            stringBuilder.append(minutes)
                    .append(" ")
                    .append(formatLong(minutes, "minutę", "minuty", "minut"))
                    .append(", ");
        }

        if (seconds > 0) {
            stringBuilder.append(seconds)
                    .append(" ")
                    .append(formatLong(seconds, "sekundę", "sekundy", "sekund"));
        }

        if (stringBuilder.isEmpty() && millis > 0) {
            stringBuilder.append(millis)
                    .append(" ")
                    .append("ms");
        }

        return stringBuilder.toString();
    }
}
