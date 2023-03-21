package me.dmk.app.audio.server;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;
import me.dmk.app.audio.TrackScheduler;

/**
 * Created by DMK on 21.03.2023
 */

@Getter
public class ServerAudioPlayer {

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;

    public ServerAudioPlayer(AudioPlayerManager audioPlayerManager) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);

        this.audioPlayer.addListener(this.trackScheduler);
    }
}
