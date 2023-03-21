package me.dmk.app.command;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.implementation.*;
import org.javacord.api.DiscordApi;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DMK on 19.03.2023
 */

@RequiredArgsConstructor
public class CommandManager {

    private final DiscordApi discordApi;
    private final AudioPlayerManager audioPlayerManager;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    private final Map<String, Command> commandMap = new ConcurrentHashMap<>();

    public void registerCommands() {
        Command currentlyPlayingCommand = new CurrentlyPlayingCommand(this.serverAudioPlayerMap);
        Command leaveCommand = new LeaveCommand(this.serverAudioPlayerMap);
        Command joinCommand = new PlayCommand(this.audioPlayerManager, this.serverAudioPlayerMap);
        Command resumeCommand = new ResumeCommand(this.serverAudioPlayerMap);
        Command skipCommand = new SkipCommand(this.serverAudioPlayerMap);
        Command stopCommand = new StopCommand(this.serverAudioPlayerMap);

        this.registerCommand(
                currentlyPlayingCommand,
                leaveCommand,
                joinCommand,
                resumeCommand,
                skipCommand,
                stopCommand
        );
    }

    public void registerCommand(Command... commands) {
        for (Command command : commands) {
            this.commandMap.put(command.getName(), command);
            command.createGlobal(this.discordApi);
        }
    }

    public Optional<Command> get(String commandName) {
        return Optional.ofNullable(this.commandMap.get(commandName));
    }
}
