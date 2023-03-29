package me.dmk.app.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.stream.Stream;

/**
 * Created by DMK on 20.03.2023
 */

@UtilityClass
public class StringUtil {

    public boolean isUrl(String string) {
        return string.startsWith("http://") || string.startsWith("https://");
    }

    public String volumeToIcon(int volume) {
        if (volume == 0) {
            return EmojiUtil.getSpeakerWithCancellationStroke();
        }

        if (volume < 30) {
            return EmojiUtil.getSpeaker();
        }

        if (volume < 60) {
            return EmojiUtil.getSpeakerWithOneWaves();
        }

        return EmojiUtil.getSpeakerWithThreeWaves();
    }

    public String getImageFromYouTubeVideo(String videoIdentifier) {
        return String.format(
                "https://img.youtube.com/vi/%s/mqdefault.jpg",
                videoIdentifier
        );
    }

    public static String durationToString(Duration duration) {
        if (duration.isNegative() || duration.isZero()) {
            return "<1s";
        }

        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder stringBuilder = new StringBuilder();

        if (hours > 0) {
            if (hours < 10) {
                stringBuilder
                        .append("0")
                        .append(hours)
                        .append(":");
            } else {
                stringBuilder
                        .append(hours)
                        .append(":");
            }
        }

        if (minutes < 10) {
            stringBuilder
                    .append("0")
                    .append(minutes)
                    .append(":");
        } else {
            stringBuilder
                    .append(minutes)
                    .append(":");
        }

        if (seconds < 10) {
            stringBuilder
                    .append("0")
                    .append(seconds);
        } else {
            stringBuilder
                    .append(seconds);
        }

        return stringBuilder.toString();
    }

    public static String millisToString(long millis) {
        return durationToString(
                Duration.ofMillis(millis)
        );
    }

    public static String createProgressBar(int progress, int length) {
        if (progress > length) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        Stream.generate(() -> "━")
                .limit(length)
                .forEach(stringBuilder::append);

        for (int i = 0; i < progress; i++) {
            stringBuilder.replace(i, i + 1, "⎯");

            if (i == (progress - 1)) {
                stringBuilder.replace(progress, progress, "⬤");
            }
        }

        return stringBuilder.toString();
    }
}
