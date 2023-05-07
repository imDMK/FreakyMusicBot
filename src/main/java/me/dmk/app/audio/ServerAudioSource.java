package me.dmk.app.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;

/**
 * Created by DMK on 20.03.2023
 */

public class ServerAudioSource extends AudioSourceBase {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    /**
     * Creates a new audio source base.
     *
     * @param api The discord api instance.
     */
    public ServerAudioSource(DiscordApi api, AudioPlayer audioPlayer) {
        super(api);

        this.audioPlayer = audioPlayer;
    }

    @Override
    public byte[] getNextFrame() {
        if (this.lastFrame == null) {
            return null;
        }

        return this.applyTransformers(this.lastFrame.getData());
    }

    @Override
    public boolean hasFinished() {
        return false;
    }

    @Override
    public boolean hasNextFrame() {
        this.lastFrame = this.audioPlayer.provide();
        return this.lastFrame != null;
    }

    @Override
    public AudioSource copy() {
        return new ServerAudioSource(getApi(), this.audioPlayer);
    }
}
