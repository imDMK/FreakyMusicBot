package me.dmk.app.listener;

import lombok.AllArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.command.service.CommandService;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerVoiceChannel;
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
        DiscordApi discordApi = event.getApi();
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
            Optional<ServerAudioPlayer> serverAudioPlayerOptional = this.serverAudioPlayerMap.get(server.getId());
            if (serverAudioPlayerOptional.isEmpty()) {
                EmbedMessage embedMessage = new EmbedMessage(server).error();

                embedMessage.setDescription("Aktualnie nie gram.");
                embedMessage.createImmediateResponder(interaction, true);
                return;
            }

            ServerAudioPlayer serverAudioPlayer = serverAudioPlayerOptional.get();

            if (playerCommand.isRequiredUserOnChannel()) {
                Optional<ServerVoiceChannel> userVoiceChannelOptional = server.getConnectedVoiceChannel(discordApi.getYourself());
                if (userVoiceChannelOptional.isEmpty()) {
                    EmbedMessage embedMessage = new EmbedMessage(server).error();

                    embedMessage.setDescription("Aktualnie nie gram.");
                    embedMessage.createImmediateResponder(interaction, true);
                    return;
                }

                ServerVoiceChannel userVoiceChannel = userVoiceChannelOptional.get();

                if (!userVoiceChannel.isConnected(user) && !serverAudioPlayer.isRequester(user)) {
                    EmbedMessage embedMessage = new EmbedMessage(server).error();

                    embedMessage.setDescription("Nie jesteś ze mną na kanale.");
                    embedMessage.createImmediateResponder(interaction, true);
                    return;
                }
            }

            playerCommand.execute(interaction, server, user, serverAudioPlayer);
        }

        if (commandBuilder instanceof Command command) {
            command.execute(interaction, server, user);
        }
    }
}
