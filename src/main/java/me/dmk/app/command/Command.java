package me.dmk.app.command;

import lombok.Getter;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Created by DMK on 19.03.2023
 */
public abstract class Command extends SlashCommandBuilder {

    @Getter
    private final String name;

    public Command(String name, String description) {
        this.name = name;

        this.setName(name);
        this.setDescription(description);
    }

    public abstract void execute(SlashCommandInteraction interaction, Server server, User user);
}
