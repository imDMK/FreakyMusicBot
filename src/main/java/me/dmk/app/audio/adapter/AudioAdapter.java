package me.dmk.app.audio.adapter;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.AllArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.ButtonUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;

/**
 * Created by DMK on 08.05.2023
 */
@AllArgsConstructor
public class AudioAdapter extends AudioEventAdapter {

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
        Message musicMessage = this.serverAudioPlayer.getMusicMessage();

        if (musicMessage == null) {
            return;
        }

        musicMessage.getServer().ifPresent(server -> {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Wystąpił błąd podczas odtwarzania:", "**" + (track == null ? "Nieznanego utworu" : track.getInfo().title) + "**");
            embedMessage.setYouTubeVideoImage(track);

            musicMessage.createUpdater()
                    .setEmbed(embedMessage)
                    .addComponents(ActionRow.of(ButtonUtil.getTrackSkipButton()))
                    .applyChanges();
        });
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        Message musicMessage = this.serverAudioPlayer.getMusicMessage();

        if (musicMessage == null) {
            return;
        }

        musicMessage.getServer().ifPresent(server -> {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Wystąpił błąd podczas odtwarzania:", "**" + (track == null ? "Nieznanego utworu" : track.getInfo().title) + "**");
            embedMessage.setYouTubeVideoImage(track);

            musicMessage.createUpdater()
                    .setEmbed(embedMessage)
                    .addComponents(ActionRow.of(ButtonUtil.getTrackSkipButton()))
                    .applyChanges();
        });
    }
}
