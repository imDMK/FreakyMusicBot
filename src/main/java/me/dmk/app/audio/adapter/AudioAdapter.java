package me.dmk.app.audio.adapter;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.AllArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.DiscordApi;

/**
 * Created by DMK on 08.05.2023
 */
@AllArgsConstructor
public class AudioAdapter extends AudioEventAdapter {

    private final DiscordApi discordApi;
    private final ServerAudioPlayer serverAudioPlayer;

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.serverAudioPlayer.getTrackScheduler().isRepeat() && endReason == AudioTrackEndReason.FINISHED) {
            this.serverAudioPlayer.getAudioPlayer().playTrack(track.makeClone());
        } else if (endReason.mayStartNext) {
            this.serverAudioPlayer.getTrackScheduler().nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        if (this.serverAudioPlayer.getMessageUrl() == null) {
            return;
        }

        this.discordApi.getMessageByLink(this.serverAudioPlayer.getMessageUrl())
                .ifPresent(messageCompletableFuture -> messageCompletableFuture
                        .thenAcceptAsync(message -> message.getServer().ifPresent(server -> {
                            AudioTrack playingTrack = this.serverAudioPlayer.getAudioPlayer().getPlayingTrack();

                            EmbedMessage embedMessage = new EmbedMessage(server).error();

                            embedMessage.setDescription("Wystąpił błąd podczas odtwarzania:", "**" + (playingTrack == null ? "Nieznanego utworu" : playingTrack.getInfo().title) + "**");
                            embedMessage.setYouTubeVideoImage(playingTrack);

                            message.edit(embedMessage);
                        }))
                );
    }
}
