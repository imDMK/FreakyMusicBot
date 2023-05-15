package me.dmk.app.command.implementation.player;

import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

/**
 * Created by DMK on 15.05.2023
 */
public class BassBoostCommand extends PlayerCommand {
    public BassBoostCommand() {
        super("bass-boost", "Zwiększ podbicie basu");

        this.addOption(
                SlashCommandOption.createLongOption("percentage", "Wartość podbicia basu w %", true)
        );
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        int percentage = interaction.getArgumentLongValueByName("percentage").orElseThrow().intValue();

        if (percentage == 0) {
            serverAudioPlayer.setBassBoost(0);
            serverAudioPlayer.disableEqualizer();

            EmbedMessage embedMessage = new EmbedMessage(server).success();
            embedMessage.setDescription("**Wyłączono** bass boost.");
            embedMessage.createImmediateResponder(interaction);
            return;
        }

        serverAudioPlayer.setBassBoost(percentage);
        serverAudioPlayer.enableEqualizer();

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription("Zmieniono bass boost na **" + percentage + "%**");
        embedMessage.createImmediateResponder(interaction);
    }
}
