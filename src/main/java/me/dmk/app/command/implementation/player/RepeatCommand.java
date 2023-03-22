package me.dmk.app.command.implementation.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Created by DMK on 22.03.2023
 */

public class RepeatCommand extends PlayerCommand {
    public RepeatCommand() {
        super("repeat", "Włącz powtarzanie jednego utworu");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        String playingTrackTitle = (playingTrack == null ? "Następnego utworu" : playingTrack.getInfo().title);

        boolean isRepeat = serverAudioPlayer.getTrackScheduler().switchRepeat();

        EmbedMessage embedMessage = new EmbedMessage(server).success();

        embedMessage.setDescription((isRepeat ? "Włączono" : "Wyłączono") + " powtarzanie utworu:\n**" + playingTrackTitle + "**");

        embedMessage.createImmediateResponder(interaction);
    }
}
