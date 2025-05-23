package me.dmk.app.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DMK on 21.03.2023
 */

@Getter
@Setter
public class TrackScheduler {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    private boolean repeat;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.add(track);
        }
    }

    public void queue(List<AudioTrack> trackList) {
        trackList.forEach(this::queue);
    }

    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }

    public boolean switchRepeat() {
        return this.repeat = !this.repeat;
    }
}
