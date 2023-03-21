package me.dmk.app.util;

import lombok.experimental.UtilityClass;

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
}
