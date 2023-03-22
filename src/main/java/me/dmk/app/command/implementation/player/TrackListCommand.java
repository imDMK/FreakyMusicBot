package me.dmk.app.command.implementation.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.TrackScheduler;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Created by DMK on 21.03.2023
 */

public class TrackListCommand extends PlayerCommand {
    public TrackListCommand() {
        super("tracklist", "Wyświetla listę zakolejkowanych utworów");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        TrackScheduler trackScheduler = serverAudioPlayer.getTrackScheduler();

        if (trackScheduler.getQueue().isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Lista odtwarzania utworów jest pusta.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        EmbedMessage embedMessage =  new EmbedMessage(server).success();
        embedMessage.setDescription("**Kolejka odtwarzania utworów:**");

        int i = 1;
        for (AudioTrack track : trackScheduler.getQueue()) {
            embedMessage.addField("Pozycja " + i, track.getInfo().title);
            i++;
        }

        embedMessage.createImmediateResponder(interaction);
    }
}
