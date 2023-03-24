package me.dmk.app.util;

import lombok.experimental.UtilityClass;

/**
 * Created by DMK on 24.03.2023
 */

@UtilityClass
public class EmojiUtil {

    public static String getMusialNote() {
        return "\uD83C\uDFB5"; //🎵
    }

    public static String getTrash() {
        return "\uD83D\uDDD1️"; //🗑️
    }

    public static String getRepeat() {
        return "\uD83D\uDD01"; //🔁
    }

    public static String getNextTrack() {
        return "⏭";
    }

    public static String getPlayOrPause() {
        return "⏯";
    }
}