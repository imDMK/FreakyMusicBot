package me.dmk.app.audio.server;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import lombok.Getter;
import me.dmk.app.audio.TrackScheduler;
import me.dmk.app.audio.adapter.AudioAdapter;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

/**
 * Created by DMK on 21.03.2023
 */

@Getter
public class ServerAudioPlayer extends AudioEventAdapter {

    private static final float[] BASS_BOOST = {
            0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f
    };

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final EqualizerFactory equalizerFactory;
    private final long requester;

    private Message musicMessage;

    public ServerAudioPlayer(AudioPlayerManager audioPlayerManager, long requester) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);
        this.equalizerFactory = new EqualizerFactory();
        this.requester = requester;

        this.audioPlayer.addListener(
                new AudioAdapter(this)
        );
    }

    public void enableEqualizer() {
        this.audioPlayer.setFilterFactory(this.equalizerFactory);
    }

    public void disableEqualizer() {
        this.audioPlayer.setFilterFactory(null);
    }

    public boolean isRequester(User user) {
        return user.getId() == this.requester;
    }

    public void setBassBoost(int percentage) {
        float multiple = percentage / 100.f;
        for (int i = 0; i < BASS_BOOST.length; i++) {
            this.equalizerFactory.setGain(i, BASS_BOOST[i] * multiple);
        }
    }

    public void setMusicMessage(Message musicMessage) {
        this.musicMessage = musicMessage;
    }
}
