package me.dmk.app.command.implementation.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.DurationUtil;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.time.Duration;

/**
 * Created by DMK on 11.05.2023
 */
public class PositionCommand extends PlayerCommand {
    public PositionCommand() {
        super("position", "Zmień pozycję utworu");

        this.setRequiredUserOnChannel(true);
        this.addOptions(
                SlashCommandOption.createLongOption("second", "Podaj sekundę", true),
                SlashCommandOption.createLongOption("minute", "Podaj minutę", false),
                SlashCommandOption.createLongOption("hour", "Podaj godzinę", false)
        );
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user, ServerAudioPlayer serverAudioPlayer) {
        long second = interaction.getArgumentLongValueByName("second").orElseThrow();
        long minute = interaction.getArgumentLongValueByName("minute").orElse(0L);
        long hour = interaction.getArgumentLongValueByName("hour").orElse(0L);

        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        if (playingTrack == null) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Aktualnie nie gram żadnego utworu.");

            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        Duration newPosition = Duration
                .ofSeconds(second)
                .plusMinutes(minute)
                .plusHours(hour);

        if (newPosition.isNegative()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Podano nieprawidłowy czas.");

            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        if (newPosition.toMillis() > playingTrack.getDuration()) {
            EmbedMessage embedMessage = new EmbedMessage(server).error();
            embedMessage.setDescription("Podano wyższy czas niż utwór trwa.");

            embedMessage.createImmediateResponder(interaction, true);
            return;
        }

        playingTrack.setPosition(newPosition.toMillis());

        EmbedMessage embedMessage = new EmbedMessage(server).success();
        embedMessage.setDescription(
                "Zmieniono pozycję utworu",
                "**" + playingTrack.getInfo().title + "**",
                "na " + DurationUtil.format(newPosition) + "."
        );

        embedMessage.createImmediateResponder(interaction);
    }
}
