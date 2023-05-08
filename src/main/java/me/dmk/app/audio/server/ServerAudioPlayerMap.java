package me.dmk.app.audio.server;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import org.javacord.api.DiscordApi;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DMK on 21.03.2023
 */

@RequiredArgsConstructor
public class ServerAudioPlayerMap {

    private final AudioPlayerManager audioPlayerManager;

    private final Map<Long, ServerAudioPlayer> serverAudioPlayerMap = new ConcurrentHashMap<>();

    public ServerAudioPlayer create(long serverId, long requester, DiscordApi discordApi) {
        ServerAudioPlayer serverAudioPlayer = new ServerAudioPlayer(this.audioPlayerManager, requester, discordApi);

        this.serverAudioPlayerMap.put(serverId, serverAudioPlayer);
        return serverAudioPlayer;
    }

    public Optional<ServerAudioPlayer> get(long serverId) {
        return Optional.ofNullable(
                this.serverAudioPlayerMap.get(serverId)
        );
    }

    public ServerAudioPlayer getOrElseCreate(long serverId, long requester, DiscordApi discordApi) {
        return this.get(serverId).orElseGet(
                () -> this.create(serverId, requester, discordApi)
        );
    }

    public void remove(long serverId) {
        this.serverAudioPlayerMap.remove(serverId);
    }
}
