package me.dmk.app.command.implementation;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.StringUtil;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Optional;

/**
 * Created by DMK on 21.03.2023
 */

public class CurrentlyPlayingCommand extends Command {

    private final ServerAudioPlayerMap serverAudioPlayerMap;

    public CurrentlyPlayingCommand(ServerAudioPlayerMap serverAudioPlayerMap) {
        super("currently-playing", "Wyświetla aktualnie grany utwór");

        this.serverAudioPlayerMap = serverAudioPlayerMap;
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user) {
        Optional<ServerAudioPlayer> serverAudioPlayerOptional = this.serverAudioPlayerMap.get(server.getId());
        if (serverAudioPlayerOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        ServerAudioPlayer serverAudioPlayer = serverAudioPlayerOptional.get();
        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();

        if (audioPlayer.getPlayingTrack() == null) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        EmbedMessage embedMessage = new EmbedMessage(server).success();

        embedMessage.setDescription("Aktualnie grany utwór:\n**" + playingTrack.getInfo().title + "**");
        embedMessage.setImage(
                StringUtil.getImageFromYouTubeVideo(playingTrack.getIdentifier())
        );

        embedMessage.createImmediateResponder(interaction);
    }
}
