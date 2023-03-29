package me.dmk.app.command.implementation.player;

import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.StringUtil;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.Optional;

/**
 * Created by DMK on 22.03.2023
 */

public class VolumeCommand extends PlayerCommand {
    public VolumeCommand() {
        super("volume", "Zmień głośność odtwarzania utworów");

        this.addOption(
                SlashCommandOption.createLongOption("volume", "Głośność w %", true)
        );
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        int volume = interaction.getArgumentLongValueByName("volume").orElseThrow().intValue();

        Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
        if (audioConnectionOptional.isEmpty()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram.");

            embedMessage.createImmediateResponder(interaction);
            return;
        }

        serverAudioPlayer.getAudioPlayer().setVolume(volume);

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription(StringUtil.volumeToIcon(volume) + " Zmieniono głośność na " + volume + "%.");

        embedMessage.createImmediateResponder(interaction);
    }
}
