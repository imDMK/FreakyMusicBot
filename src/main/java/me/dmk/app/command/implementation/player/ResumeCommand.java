package me.dmk.app.command.implementation.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Optional;

/**
 * Created by DMK on 21.03.2023
 */

public class ResumeCommand extends PlayerCommand {
    public ResumeCommand() {
        super("resume", "Wznów zatrzymany utwór");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
        if (audioConnectionOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        if (playingTrack == null) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        if (!audioPlayer.isPaused()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Utwór nie jest zatrzymany.");

            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        audioPlayer.setPaused(false);

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription(
                "Wznowiono odtwarzanie utworu:",
                "**" + playingTrack.getInfo().title + "**"
        );

        embedMessage.createImmediateResponder(interaction);
    }
}
