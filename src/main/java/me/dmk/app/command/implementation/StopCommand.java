package me.dmk.app.command.implementation;

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

public class StopCommand extends Command {

    private final ServerAudioPlayerMap serverAudioPlayerMap;

    public StopCommand(ServerAudioPlayerMap serverAudioPlayerMap) {
        super("stop", "Zatrzymaj aktualnie grający utwór");

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

        this.serverAudioPlayerMap.get(server.getId())
                .map(ServerAudioPlayer::getAudioPlayer)
                .ifPresent(audioPlayer -> audioPlayer.setPaused(true));

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription("Zatrzymano utwór.");

        embedMessage.createImmediateResponder(interaction);
    }
}
