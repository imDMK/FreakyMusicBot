package me.dmk.app.command.implementation;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.dmk.app.audio.LavaplayerAudioSource;
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
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
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

        Optional<ServerVoiceChannel> voiceChannelOptional = user.getConnectedVoiceChannel(server);
        if (voiceChannelOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Nie jesteś połączony na kanale głosowym.");
            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        ServerVoiceChannel voiceChannel = voiceChannelOptional.get();

        if (!voiceChannel.canYouConnect() || !voiceChannel.canYouSpeak()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Nie posiadam uprawnień do dołączenia lub mówienia na twoim kanałe głosowym.");
            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        InteractionOriginalResponseUpdater responseUpdater = interaction.respondLater().join();

        ServerAudioPlayer serverAudioPlayer = this.serverAudioPlayerMap.getOrElseCreate(server.getId());

        if (voiceChannel.isConnected(yourself) && server.getAudioConnection().isPresent()) {
            AudioConnection audioConnection = server.getAudioConnection().get();

            if (audioConnection.getChannel().getId() != voiceChannel.getId()) {
                EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                        .setDescription("Nie jesteś na tym samym kanale głosowym.");

                responseUpdater.addEmbed(embedBuilder)
                        .update()
                        .exceptionally(ExceptionLogger.get());
                return;
            }

            AudioSource audioSource = new LavaplayerAudioSource(discordApi, serverAudioPlayer.getAudioPlayer());
            audioConnection.setAudioSource(audioSource);

            this.queue(serverAudioPlayer, query, server, responseUpdater);
        } else {
            voiceChannel.connect()
                    .thenAcceptAsync(audioConnection -> {
                        AudioSource audioSource = new LavaplayerAudioSource(discordApi, serverAudioPlayer.getAudioPlayer());
                        audioConnection.setAudioSource(audioSource);

                        this.queue(serverAudioPlayer, query, server, responseUpdater);
                    })
                    .exceptionallyAsync(throwable -> {
                        EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                                .setDescription("Wystąpił błąd podczas dołączania do twojego kanału głosowego.")
                                .addField("Szczegóły błędu", throwable.getMessage());

                        responseUpdater.addEmbed(embedBuilder)
                                .update()
                                .exceptionally(ExceptionLogger.get());

                        throwable.printStackTrace();
                        return null;
                    });
        }
    }

    private void queue(ServerAudioPlayer serverAudioPlayer, String query, Server server, InteractionOriginalResponseUpdater responseUpdater) {
        AudioResultHandler audioResultHandler = new AudioResultHandler(
                server,
                serverAudioPlayer.getTrackScheduler(),
                responseUpdater
        );

        this.audioPlayerManager.loadItemOrdered(
                serverAudioPlayer,
                query,
                audioResultHandler
        );
    }
}
