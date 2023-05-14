package me.dmk.app.command;

import lombok.Getter;
import lombok.Setter;
import me.dmk.app.audio.server.ServerAudioPlayer;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

/**
 * Created by DMK on 22.03.2023
 */

@Getter
@Setter
public abstract class PlayerCommand extends SlashCommandBuilder {

    private final String name;

    /**
     * Whether the user must be with the bot on the voice channel when executing the command.
     */
    private boolean requiredUserOnChannel = true;

    public PlayerCommand(String name, String description) {
        this.name = name;

        this.setName(name);
        this.setDescription(description);
    }

    public abstract void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer);


    public void addOptions(SlashCommandOption... slashCommandOptions) {
        for (SlashCommandOption slashCommandOption : slashCommandOptions) {
            this.addOption(slashCommandOption);
        }
    }
}
