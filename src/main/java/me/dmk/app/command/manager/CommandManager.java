package me.dmk.app.command.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.Command;
import me.dmk.app.command.PlayerCommand;
import me.dmk.app.command.implementation.PlayCommand;
import me.dmk.app.command.implementation.player.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DMK on 19.03.2023
 */

@RequiredArgsConstructor
public class CommandManager {

    private final DiscordApi discordApi;
    private final AudioPlayerManager audioPlayerManager;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    private final Map<String, SlashCommandBuilder> commandBuilderMap = new ConcurrentHashMap<>();

    public void registerCommands() {
        PlayerCommand currentlyPlayingCommand = new CurrentlyPlayingCommand();
        PlayerCommand leaveCommand = new LeaveCommand(this.serverAudioPlayerMap);
        PlayerCommand repeatCommand = new RepeatCommand();
        PlayerCommand resumeCommand = new ResumeCommand();
        PlayerCommand skipCommand = new SkipCommand();
        PlayerCommand stopCommand = new StopCommand();
        PlayerCommand trackListCommand = new TrackListCommand();
        PlayerCommand volumeCommand = new VolumeCommand();

        Command playCommand = new PlayCommand(this.audioPlayerManager, this.serverAudioPlayerMap);

        this.register(
                currentlyPlayingCommand,
                leaveCommand,
                repeatCommand,
                resumeCommand,
                skipCommand,
                stopCommand,
                trackListCommand,
                volumeCommand,
                playCommand
        );
    }

    public void register(SlashCommandBuilder... slashCommandBuilders) {
        for (SlashCommandBuilder commandBuilder : slashCommandBuilders) {
            if (commandBuilder instanceof PlayerCommand playerCommand) {
                this.commandBuilderMap.put(playerCommand.getName(), playerCommand);
            }

            if (commandBuilder instanceof Command command) {
                this.commandBuilderMap.put(command.getName(), command);
            }
        }

        this.discordApi.bulkOverwriteGlobalApplicationCommands(
                Set.of(slashCommandBuilders)
        );
    }

    public Optional<SlashCommandBuilder> get(String commandName) {
        return Optional.ofNullable(this.commandBuilderMap.get(commandName));
    }
}
