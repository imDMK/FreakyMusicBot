package me.dmk.app.util;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.message.Message;

/**
 * Created by DMK on 08.05.2023
 */
@UtilityClass
public class MessageUtil {

    public static String getMessageUrl(Message message) {
        return String.format(
                "https://discordapp.com/channels/%s/%s/%s",
                message.getServer().map(DiscordEntity::getIdAsString).orElse("@me"), //Supports private messages
                message.getChannel().getIdAsString(),
                message.getIdAsString()
        );
    }
}
