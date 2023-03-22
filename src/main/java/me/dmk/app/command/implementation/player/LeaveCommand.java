package me.dmk.app.command.implementation.player;

import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Optional;

/**
 * Created by DMK on 21.03.2023
 */

public class LeaveCommand extends PlayerCommand {

    private final ServerAudioPlayerMap serverAudioPlayerMap;

    public LeaveCommand(ServerAudioPlayerMap serverAudioPlayerMap) {
        super("leave", "Całkowicie zatrzymuje oraz wyczyszcza kolejkę utworów - Opuszczę kanał");

        this.serverAudioPlayerMap = serverAudioPlayerMap;
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        User yourself = interaction.getApi().getYourself();

        Optional<ServerVoiceChannel> voiceChannelOptional = server.getConnectedVoiceChannel(yourself);
        if (voiceChannelOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        ServerVoiceChannel voiceChannel = voiceChannelOptional.get();

        //Disconnect from voiceChannel
        voiceChannel.disconnect();

        //Destroy and remove server player from map
        serverAudioPlayer.getAudioPlayer().destroy();
        this.serverAudioPlayerMap.remove(server.getId());

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription("Opuściłem kanał oraz kolejka utworów została wyczyszczona.");

        embedMessage.createImmediateResponder(interaction);
    }
}
