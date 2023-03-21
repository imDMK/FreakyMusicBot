package me.dmk.app.command.implementation;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.TrackScheduler;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Optional;

/**
 * Created by DMK on 21.03.2023
 */

public class SkipCommand extends Command {

    private final ServerAudioPlayerMap serverAudioPlayerMap;

    public SkipCommand(ServerAudioPlayerMap serverAudioPlayerMap) {
        super("skip", "Pomiń aktualny grany utwór");

        this.serverAudioPlayerMap = serverAudioPlayerMap;
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user) {
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

        ServerAudioPlayer serverAudioPlayer = this.serverAudioPlayerMap.get(server.getId()).orElseThrow();
        TrackScheduler trackScheduler = serverAudioPlayer.getTrackScheduler();

        trackScheduler.nextTrack();

        AudioTrack playingTrack = serverAudioPlayer.getAudioPlayer().getPlayingTrack();

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription("Pominięto aktualny utwór.\nNastępny utwór: **" + (playingTrack == null ? "Brak następnego utworu" : playingTrack.getInfo().title) + "**");

        embedMessage.createImmediateResponder(interaction);
    }
}
