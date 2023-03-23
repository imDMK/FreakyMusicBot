package me.dmk.app.command.implementation.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.StringUtil;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Created by DMK on 21.03.2023
 */

public class CurrentlyPlayingCommand extends PlayerCommand {
    public CurrentlyPlayingCommand() {
        super("currently-playing", "Wyświetla aktualnie grany utwór");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        String embedDescription;
        if (playingTrack == null) {
            embedDescription = "**Brak**";
        } else {
            long trackDuration = playingTrack.getDuration();
            long trackPosition = playingTrack.getPosition();

            long remainingTrackTime = trackDuration - trackPosition;

            embedDescription =
                    "**" + playingTrack.getInfo().title + "**\n" +
                    "**Długość:** " + StringUtil.millisToString(trackDuration) + "**\n" +
                    "**Pozostały czas:** " + StringUtil.millisToString(remainingTrackTime);
        }

        EmbedMessage embedMessage = new EmbedMessage(server).success();

        embedMessage.setDescription(embedDescription);
        embedMessage.setYouTubeVideoImage(playingTrack);

        embedMessage.createImmediateResponder(interaction);
    }
}
