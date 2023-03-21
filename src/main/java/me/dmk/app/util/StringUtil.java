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
}
