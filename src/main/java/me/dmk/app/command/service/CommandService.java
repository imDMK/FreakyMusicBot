package me.dmk.app.command.service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import me.dmk.app.MusicApp;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.command.implementation.LyricsCommand;
import me.dmk.app.command.implementation.PlayCommand;
import me.dmk.app.command.implementation.StatusCommand;
import me.dmk.app.command.implementation.player.BassBoostCommand;
import me.dmk.app.command.implementation.player.LeaveCommand;
import me.dmk.app.command.implementation.player.NowPlayingCommand;
import me.dmk.app.command.implementation.player.PositionCommand;
import me.dmk.app.command.implementation.player.RepeatCommand;
import me.dmk.app.command.implementation.player.ResumeCommand;
import me.dmk.app.command.implementation.player.SkipCommand;
import me.dmk.app.command.implementation.player.StopCommand;
import me.dmk.app.command.implementation.player.TrackListCommand;
import me.dmk.app.command.implementation.player.VolumeCommand;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DMK on 19.03.2023
 */

@RequiredArgsConstructor
public class CommandService {

    private final MusicApp musicApp;
    private final AudioPlayerManager audioPlayerManager;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    private final Map<String, SlashCommandBuilder> commandBuilderMap = new ConcurrentHashMap<>();

    public void registerCommands() {
        PlayerCommand bassBoostCommand = new BassBoostCommand();
        PlayerCommand leaveCommand = new LeaveCommand(this.serverAudioPlayerMap);
        PlayerCommand nowPlayingCommand = new NowPlayingCommand();
        PlayerCommand positionCommand = new PositionCommand();
        PlayerCommand repeatCommand = new RepeatCommand();
        PlayerCommand resumeCommand = new ResumeCommand();
        PlayerCommand skipCommand = new SkipCommand();
        PlayerCommand stopCommand = new StopCommand();
        PlayerCommand trackListCommand = new TrackListCommand();
        PlayerCommand volumeCommand = new VolumeCommand();

        Command lyricsCommand = new LyricsCommand();
        Command playCommand = new PlayCommand(this.audioPlayerManager, this.serverAudioPlayerMap);
        Command statusCommand = new StatusCommand(this.musicApp);

        this.put(
                bassBoostCommand,
                leaveCommand,
                nowPlayingCommand,
                positionCommand,
                repeatCommand,
                resumeCommand,
                skipCommand,
                stopCommand,
                trackListCommand,
                volumeCommand,

                lyricsCommand,
                playCommand,
                statusCommand
        );
    }

    private void put(SlashCommandBuilder... slashCommandBuilders) {
        for (SlashCommandBuilder commandBuilder : slashCommandBuilders) {
            if (commandBuilder instanceof PlayerCommand playerCommand) {
                this.commandBuilderMap.put(playerCommand.getName(), playerCommand);
            }

            if (commandBuilder instanceof Command command) {
                this.commandBuilderMap.put(command.getName(), command);
            }
        }
    }

    public void bulkOverwriteGlobalApplicationCommands(DiscordApi discordApi) {
        discordApi.bulkOverwriteGlobalApplicationCommands(
                new HashSet<>(
                        this.commandBuilderMap.values()
                )
        );
    }

    public Optional<SlashCommandBuilder> get(String commandName) {
        return Optional.ofNullable(
                this.commandBuilderMap.get(commandName)
        );
    }
}
