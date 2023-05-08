package me.dmk.app.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AllArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.ActionRowUtil;
import me.dmk.app.util.StringUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.List;

/**
 * Created by DMK on 06.05.2023
 */
@AllArgsConstructor
public class AudioResultHandler implements AudioLoadResultHandler {

    private final Server server;
    private final ServerAudioPlayer serverAudioPlayer;
    private final InteractionOriginalResponseUpdater responseUpdater;

    @Override
    public void trackLoaded(AudioTrack track) {
        this.serverAudioPlayer.getTrackScheduler().queue(track);

        EmbedMessage embedMessage = new EmbedMessage(this.server).success();

        embedMessage.setYouTubeVideoImage(track);
        embedMessage.setDescription(
                "Zakolejkowano utwór:",
                "**" + track.getInfo().title + "**",
                "",
                "**Długość:** " + StringUtil.millisToString(track.getDuration())
        );

        this.responseUpdater
                .addEmbed(embedMessage)
                .addComponents(ActionRowUtil.getControlButtons())
                .update()
                .thenAccept(this.serverAudioPlayer::setMessageUrl)
                .exceptionally(ExceptionLogger.get());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        List<AudioTrack> trackList = playlist.getTracks();
        AudioTrack firstTrack = trackList.get(0);

        String[] embedDescription;

        if (playlist.isSearchResult()) {
            this.serverAudioPlayer.getTrackScheduler().queue(firstTrack);

            embedDescription = new String[] {
                    "Zakolejkowano utwór:",
                    "**" + firstTrack.getInfo().title + "**",
                    "",
                    "Długość: **" + StringUtil.millisToString(firstTrack.getDuration()) + "**"
            };
        } else {
            this.serverAudioPlayer.getTrackScheduler().queue(trackList);

            embedDescription = new String[] {
                    "Zakolejkowano wszystkie utwory z playlisty:",
                    "**" + playlist.getName() + "**"
            };
        }

        EmbedMessage embedMessage = new EmbedMessage(this.server).success();

        embedMessage.setDescription(embedDescription);
        embedMessage.setYouTubeVideoImage(firstTrack);

        this.responseUpdater
                .addEmbed(embedMessage)
                .addComponents(ActionRowUtil.getControlButtons())
                .update()
                .thenAccept(this.serverAudioPlayer::setMessageUrl)
                .exceptionally(ExceptionLogger.get());
    }

    @Override
    public void noMatches() {
        EmbedBuilder embedBuilder = new EmbedMessage(this.server).error()
                .setDescription("Nie znalazłem żadnego pasującego utworu.");

        this.responseUpdater
                .addEmbed(embedBuilder)
                .update()
                .thenAccept(this.serverAudioPlayer::setMessageUrl)
                .exceptionally(ExceptionLogger.get());
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        EmbedMessage embedMessage = new EmbedMessage(this.server).error();

        embedMessage.setDescription("Wystąpił błąd podczas ładowania utworu.");
        embedMessage.addField("Szczegóły błędu", exception.getMessage());

        this.responseUpdater
                .addEmbed(embedMessage)
                .update()
                .thenAccept(this.serverAudioPlayer::setMessageUrl)
                .exceptionally(ExceptionLogger.get());
    }
}
