package me.dmk.app.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import org.javacord.api.entity.activity.ActivityType;

/**
 * Created by DMK on 19.03.2023
 */

@Getter
public class ClientConfiguration extends OkaeriConfig {

    public String token = "MTA4NzEyNjI5MzQ0NDgzNzUxOA.GuWA8P.hK9G_C1WdAKiMXcTo4NDrlQTJQK1USNTI_aqxA";

    public ActivityType activityType = ActivityType.LISTENING;
    public String activityName = "I'm just playing music.";
}
