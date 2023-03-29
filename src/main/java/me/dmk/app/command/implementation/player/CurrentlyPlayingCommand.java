package me.dmk.app.command.implementation.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.EmojiUtil;
import me.dmk.app.util.StringUtil;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Created by DMK on 21.03.2023
 */

public class CurrentlyPlayingCommand extends PlayerCommand {
    public CurrentlyPlayingCommand() {
        super("currently-playing", "Wyświetla aktualnie grany utwór");

        this.setRequiredUserOnChannel(false);
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        if (playingTrack == null) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();

            embedMessage.setDescription("Aktualnie nie gram.");
            embedMessage.createImmediateResponder(interaction);
            return;
        }

        long trackDuration = playingTrack.getDuration();
        long trackPosition = playingTrack.getPosition();

        int volume = audioPlayer.getVolume();
        int progress = (int) Math.round(((double) trackPosition / trackDuration) * 20);

        String progressBar = StringUtil.createProgressBar(progress, 20);
        String trackDurationFormatted = StringUtil.millisToString(trackDuration);
        String trackPositionFormatted = StringUtil.millisToString(trackPosition);

        EmbedMessage embedMessage = new EmbedMessage(server).success();

        embedMessage.setDescription(
                StringUtil.volumeToIcon(volume) + " Aktualnie gram:",
                "",
                "**" + playingTrack.getInfo().title + "**",
                trackPositionFormatted + " " + progressBar + " " + trackDurationFormatted
        );
        embedMessage.setYouTubeVideoImage(playingTrack);

        ActionRow buttons = ActionRow.of(
                Button.secondary("track-play-or-stop", "Wznów/Zatrzymaj utwór", EmojiUtil.getPlayOrPause()),
                Button.secondary("track-skip", "Pomiń utwór", EmojiUtil.getNextTrack()),
                Button.secondary("track-toggle-repeat", "Włącz/Wyłącz powtarzanie utworu", EmojiUtil.getRepeat())
        );

        embedMessage.createImmediateResponder(interaction, buttons);
    }
}
