package me.dmk.app.command.implementation.player;

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

public class SkipCommand extends PlayerCommand {
    public SkipCommand() {
        super("skip", "Pomiń aktualny grany utwór");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
        if (audioConnectionOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Bot aktualnie nie gra na tym serwerze.");

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

        //Skip track
        serverAudioPlayer.getTrackScheduler().nextTrack();

        AudioTrack nextTrack = serverAudioPlayer.getAudioPlayer().getPlayingTrack();
        String nextTrackTitle = (nextTrack == null ? "Brak" : nextTrack.getInfo().title);

        EmbedMessage embedMessage = new EmbedMessage(server).success();

        embedMessage.setDescription("Pominięto aktualny utwór.\nNastępny utwór: **" + nextTrackTitle + "**");
        embedMessage.setYouTubeVideoImage(nextTrack);

        embedMessage.createImmediateResponder(interaction);
    }
}
