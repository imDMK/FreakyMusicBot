package me.dmk.app.listener;

import lombok.AllArgsConstructor;
import me.dmk.app.command.CommandManager;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

/**
 * Created by DMK on 20.03.2023
 */

@AllArgsConstructor
public class SlashCommandListener implements SlashCommandCreateListener {

    private final CommandManager commandManager;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        String commandName = interaction.getCommandName();
        User user = interaction.getUser();

        interaction.getServer().ifPresent(server ->
                this.commandManager.get(commandName)
                        .ifPresent(command -> command.execute(interaction, server, user))
        );
    }
}
