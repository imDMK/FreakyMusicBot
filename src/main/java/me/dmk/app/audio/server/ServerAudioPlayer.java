package me.dmk.app.audio.server;

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

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final long requester;

    private Message musicMessage;

    public ServerAudioPlayer(AudioPlayerManager audioPlayerManager, long requester) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);
        this.requester = requester;

        this.audioPlayer.addListener(
                new AudioAdapter(this)
        );
    }

    public boolean isRequester(User user) {
        return user.getId() == this.requester;
    }

    public void setMusicMessage(Message musicMessage) {
        this.musicMessage = musicMessage;
    }
}
