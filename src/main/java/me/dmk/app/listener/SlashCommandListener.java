package me.dmk.app.listener;

import lombok.AllArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.command.service.CommandService;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.Optional;

/**
 * Created by DMK on 20.03.2023
 */

@AllArgsConstructor
public class SlashCommandListener implements SlashCommandCreateListener {

    private final CommandService commandService;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        User user = interaction.getUser();

        String commandName = interaction.getCommandName();

        Optional<Server> serverOptional = interaction.getServer();
        if (serverOptional.isEmpty()) {
            return;
        }

        Server server = serverOptional.get();

        Optional<SlashCommandBuilder> commandBuilderOptional = this.commandService.get(commandName);
        if (commandBuilderOptional.isEmpty()) {
            return;
        }

        SlashCommandBuilder commandBuilder = commandBuilderOptional.get();

        if (commandBuilder instanceof PlayerCommand playerCommand) {
            if (playerCommand.isRequiredUserOnChannel()) {
                Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
                if (audioConnectionOptional.isEmpty()) {
                    EmbedMessage embedMessage = new EmbedMessage(server).error();

                    embedMessage.setDescription("Aktualnie nie gram.");
                    embedMessage.createImmediateResponder(interaction);
                    return;
                }

                AudioConnection audioConnection = audioConnectionOptional.get();

                if (!audioConnection.getChannel().isConnected(user)) {
                    EmbedMessage embedMessage = new EmbedMessage(server).error();

                    embedMessage.setDescription("Nie jesteś ze mną na kanale.");
                    embedMessage.createImmediateResponder(interaction);
                    return;
                }
            }

            this.serverAudioPlayerMap.get(server.getId())
                    .ifPresentOrElse(serverAudioPlayer ->
                                    playerCommand.execute(interaction, server, user, serverAudioPlayer),
                            () -> {
                        EmbedMessage embedMessage = new EmbedMessage(server).error();
                        embedMessage.setDescription("Aktualnie nie gram.");

                        embedMessage.createImmediateResponder(interaction);
                    });
        }

        if (commandBuilder instanceof Command command) {
            command.execute(interaction, server, user);
        }
    }
}
