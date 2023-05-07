package me.dmk.app.audio.server;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;
import me.dmk.app.audio.TrackScheduler;
import org.javacord.api.entity.user.User;

/**
 * Created by DMK on 21.03.2023
 */

@Getter
public class ServerAudioPlayer {

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;

    private final long requester;

    public ServerAudioPlayer(AudioPlayerManager audioPlayerManager, long requester) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);

        this.audioPlayer.addListener(this.trackScheduler);
        this.requester = requester;
    }

    public boolean isRequester(User user) {
        return user.getId() == this.requester;
    }
 }
