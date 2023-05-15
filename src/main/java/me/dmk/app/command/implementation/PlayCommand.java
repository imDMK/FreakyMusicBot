package me.dmk.app.command.implementation;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.dmk.app.audio.ServerAudioSource;
import me.dmk.app.audio.handler.AudioResultHandler;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.StringUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.Optional;

/**
 * Created by DMK on 19.03.2023
 */

public class PlayCommand extends Command {

    private final AudioPlayerManager audioPlayerManager;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    public PlayCommand(AudioPlayerManager audioPlayerManager, ServerAudioPlayerMap serverAudioPlayerMap) {
        super("play", "Puść ulubiony utwór");

        this.audioPlayerManager = audioPlayerManager;
        this.serverAudioPlayerMap = serverAudioPlayerMap;

        this.addOption(
                SlashCommandOption.createStringOption(
                        "search",
                        "Podaj nazwę lub link do utworu",
                        true
                )
        );
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user) {
        String search = interaction.getArgumentStringValueByName("search").orElseThrow();
        String query = StringUtil.isUrl(search) ? search : "ytsearch: " + search;

        DiscordApi discordApi = interaction.getApi();
        User yourself = discordApi.getYourself();

        Optional<ServerVoiceChannel> userVoiceChannelOptional = user.getConnectedVoiceChannel(server);
        if (userVoiceChannelOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Nie jesteś połączony na kanale głosowym.");
            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        ServerVoiceChannel userVoiceChannel = userVoiceChannelOptional.get();

        if (!userVoiceChannel.canYouConnect() || !userVoiceChannel.canYouSpeak()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Nie posiadam uprawnień do dołączenia lub mówienia na twoim kanałe głosowym.");
            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        ServerAudioPlayer serverAudioPlayer = this.serverAudioPlayerMap.getOrElseCreate(server.getId(), user.getId());

        if (!userVoiceChannel.isConnected(yourself) && !serverAudioPlayer.isRequester(user)) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Inny użytkownik aktualnie słucha na tym serwerze.");
            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        AudioSource audioSource = new ServerAudioSource(discordApi, serverAudioPlayer.getAudioPlayer());

        Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
        if (audioConnectionOptional.isPresent() && userVoiceChannel.isConnected(yourself)) {
            audioConnectionOptional.get().setAudioSource(audioSource);

            this.respond(serverAudioPlayer, query, server, interaction);
        } else {
            userVoiceChannel.connect()
                    .thenAcceptAsync(audioConnection -> {
                        audioConnection.setAudioSource(audioSource);

                        this.respond(serverAudioPlayer, query, server, interaction);
                    })
                    .exceptionallyAsync(throwable -> {
                        EmbedMessage embedMessage = new EmbedMessage(server).error();

                        embedMessage.setDescription("Wystąpił błąd podczas dołączania do twojego kanału głosowego.");
                        embedMessage.addField("Szczegóły błędu", throwable.getMessage());

                        embedMessage.createImmediateResponder(interaction, true);

                        throwable.printStackTrace();
                        return null;
                    });
        }
    }

    private void respond(ServerAudioPlayer serverAudioPlayer, String query, Server server, SlashCommandInteraction interaction) {
        interaction.respondLater()
                .thenAcceptAsync(responseUpdater -> this.audioPlayerManager.loadItemOrdered(
                        serverAudioPlayer,
                        query,
                        new AudioResultHandler(server, serverAudioPlayer, responseUpdater)
                ))
                .exceptionally(ExceptionLogger.get());
    }
}
