package me.dmk.app.audio.server;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import lombok.Getter;
import me.dmk.app.audio.TrackScheduler;
import me.dmk.app.audio.adapter.AudioAdapter;
import me.dmk.app.util.MessageUtil;
import org.javacord.api.DiscordApi;
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

    private String messageUrl;

    public ServerAudioPlayer(AudioPlayerManager audioPlayerManager, long requester, DiscordApi discordApi) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);
        this.requester = requester;

        this.audioPlayer.addListener(new AudioAdapter(discordApi, this));
    }

    public boolean isRequester(User user) {
        return user.getId() == this.requester;
    }

    public void setMessageUrl(Message message) {
        this.messageUrl = MessageUtil.getMessageUrl(message);
    }
}
