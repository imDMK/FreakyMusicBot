package me.dmk.app.command.implementation;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.LavaplayerAudioSource;
import me.dmk.app.audio.TrackScheduler;
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
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

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
                SlashCommandOption.create(
                        SlashCommandOptionType.STRING,
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

        InteractionOriginalResponseUpdater responseUpdater = interaction.respondLater().join();

        Optional<ServerVoiceChannel> voiceChannelOptional = user.getConnectedVoiceChannel(server);
        if (voiceChannelOptional.isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                    .setDescription("Nie jesteś połączony na kanale głosowym.");

            responseUpdater.addEmbed(embedBuilder).update();
            return;
        }

        ServerVoiceChannel voiceChannel = voiceChannelOptional.get();

        if (!voiceChannel.canYouConnect() || !voiceChannel.canYouSpeak()) {
            EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                    .setDescription("Nie posiadam uprawnień do dołączenia lub mówienia na twoim kanałe głosowym.");

            responseUpdater.addEmbed(embedBuilder).update();
            return;
        }

        DiscordApi discordApi = interaction.getApi();
        User yourself = discordApi.getYourself();

        ServerAudioPlayer serverAudioPlayer = this.serverAudioPlayerMap.getOrElseCreate(server.getId());

        if (voiceChannel.isConnected(yourself) && server.getAudioConnection().isPresent()) {
            AudioConnection audioConnection = server.getAudioConnection().get();

            if (audioConnection.getChannel().getId() != voiceChannel.getId()) {
                EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                        .setDescription("Nie jesteś na tym samym kanale głosowym.");

                responseUpdater.addEmbed(embedBuilder).update();
                return;
            }

            AudioSource audioSource = new LavaplayerAudioSource(discordApi, serverAudioPlayer.getAudioPlayer());
            audioConnection.setAudioSource(audioSource);

            this.queue(responseUpdater, server, query, serverAudioPlayer);
        } else {
            voiceChannel.connect()
                    .thenAcceptAsync(audioConnection -> {
                        AudioSource audioSource = new LavaplayerAudioSource(discordApi, serverAudioPlayer.getAudioPlayer());
                        audioConnection.setAudioSource(audioSource);

                        this.queue(responseUpdater, server, query, serverAudioPlayer);
                    })
                    .exceptionally(throwable -> {
                        EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                                .setDescription("Wystąpił błąd podczas dołączania do twojego kanału głosowego.")
                                .addField("Szczegóły błędu", throwable.getMessage());

                        responseUpdater.addEmbed(embedBuilder).update();
                        return null;
                    });
        }
    }

    private void queue(InteractionOriginalResponseUpdater responseUpdater, Server server, String query, ServerAudioPlayer serverAudioPlayer) {
        TrackScheduler trackScheduler = serverAudioPlayer.getTrackScheduler();

        AudioLoadResultHandler loadResultHandler = new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.queue(track);

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setDescription("Zakolejkowano utwór:\n **" + track.getInfo().title + "**");
                embedMessage.setYouTubeVideoImage(track);

                responseUpdater.addEmbed(embedMessage).update();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getTracks().get(0);
                String embedDescrption;

                if (playlist.isSearchResult()) {
                    trackScheduler.queue(firstTrack);

                    embedDescrption = "Zakolejkowano utwór:\n **" + firstTrack.getInfo().title + "**";
                } else {
                    playlist.getTracks().forEach(trackScheduler::queue);

                    embedDescrption = "Zakolejkowano wszystkie utwory z playlisty:\n **" + playlist.getName() + "**";
                }

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setDescription(embedDescrption);
                embedMessage.setYouTubeVideoImage(firstTrack);

                responseUpdater.addEmbed(embedMessage).update();
            }

            @Override
            public void noMatches() {
                EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                        .setDescription("Nie znalazłem żadnego pasującego utworu.");

                responseUpdater.addEmbed(embedBuilder).update();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder embedBuilder = new EmbedMessage(server).error()
                        .setDescription("Wystąpił błąd podczas ładowania utworu.")
                        .addField("Szczegóły błędu", exception.getMessage());

                responseUpdater.addEmbed(embedBuilder).update();
            }
        };

        this.audioPlayerManager.loadItemOrdered(
                serverAudioPlayer,
                query,
                loadResultHandler
        );
    }
}
