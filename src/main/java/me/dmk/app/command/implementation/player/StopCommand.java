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

public class StopCommand extends PlayerCommand {
    public StopCommand() {
        super("stop", "Zatrzymaj aktualnie grający utwór");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
        if (audioConnectionOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        AudioConnection audioConnection = audioConnectionOptional.get();

        boolean isOnChannelWithBot =
                user.getConnectedVoiceChannel(server).isPresent() &&
                        user.getConnectedVoiceChannel(server).get().getId() == audioConnection.getChannel().getId();

        if (!isOnChannelWithBot) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram lub nie jesteś ze mną na kanale.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        if (audioPlayer.isPaused()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Utwór jest już zatrzymany.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        if (playingTrack == null) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        audioPlayer.setPaused(true);

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription("Zatrzymano utwór:\n**" + playingTrack.getInfo().title + "**");

        embedMessage.createImmediateResponder(interaction);
    }
}
