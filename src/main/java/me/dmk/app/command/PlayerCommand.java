package me.dmk.app.command;

import lombok.Getter;
import me.dmk.app.audio.server.ServerAudioPlayer;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Created by DMK on 22.03.2023
 */

public abstract class PlayerCommand extends SlashCommandBuilder {

    @Getter
    private final String name;

    public PlayerCommand(String name, String description) {
        this.name = name;

        this.setName(name);
        this.setDescription(description);
    }

    public abstract void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer);
}
