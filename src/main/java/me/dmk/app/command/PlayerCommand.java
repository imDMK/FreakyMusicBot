package me.dmk.app.command;

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

@Setter
public abstract class PlayerCommand extends SlashCommandBuilder {

    private final String name;
    private boolean requiredUserOnChannel = true;

    public PlayerCommand(String name, String description) {
        this.name = name;

        this.setName(name);
        this.setDescription(description);
    }

    public abstract void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer);

    public String getName() {
        return this.name;
    }

    /**
     * Whether the user must be with the bot on the voice channel to execute the command with default true.
     */
    public boolean isRequiredUserOnChannel() {
        return this.requiredUserOnChannel;
    }

    public void addOptions(SlashCommandOption... slashCommandOptions) {
        for (SlashCommandOption slashCommandOption : slashCommandOptions) {
            this.addOption(slashCommandOption);
        }
    }
}
