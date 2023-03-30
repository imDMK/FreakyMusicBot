package me.dmk.app;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.service.CommandService;
import me.dmk.app.configuration.ClientConfiguration;
import me.dmk.app.listener.SlashCommandListener;
import me.dmk.app.listener.button.ButtonInteractionListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Created by DMK on 19.03.2023
 */

@Slf4j
@Getter
public class MusicApp {

    private final Instant startInstant;

    private final ClientConfiguration clientConfiguration;

    private final AudioPlayerManager audioPlayerManager;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    private final CommandService commandService;

    protected MusicApp() {
        this.startInstant = Instant.now();

        this.clientConfiguration = ConfigManager.create(ClientConfiguration.class, (config) -> {
            config.withConfigurer(new JsonGsonConfigurer());
            config.withBindFile("configuration.json");
            config.withRemoveOrphans(true);
            config.saveDefaults();
            config.load(true);
        });

        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager(true)); //True to allow search

        this.serverAudioPlayerMap = new ServerAudioPlayerMap(this.audioPlayerManager);

        this.commandService = new CommandService(this, this.audioPlayerManager, this.serverAudioPlayerMap);

        FallbackLoggerConfiguration.setDebug(this.clientConfiguration.isDebug());

        new DiscordApiBuilder()
                .setToken(this.clientConfiguration.getToken())
                .setAllIntents()
                .setRecommendedTotalShards()
                .join()
                .loginAllShards()
                .forEach(discordApiCompletableFuture -> discordApiCompletableFuture
                        .thenAccept(this::onShardLogin)
                        .exceptionally(ExceptionLogger.get())
                );
    }

    private void onShardLogin(DiscordApi discordApi) {
        int currentShard = discordApi.getCurrentShard();

        log.info("Connected to shard " + currentShard);

        this.commandService.registerCommands(discordApi);

        Stream.of(
                new ButtonInteractionListener(this.serverAudioPlayerMap),
                new SlashCommandListener(this.commandService, this.serverAudioPlayerMap)
        ).forEach(discordApi::addListener);

        discordApi.setMessageCacheSize(10, 60 * 60); //1 hour

        discordApi.updateActivity(
                this.clientConfiguration.getActivityType(),
                this.clientConfiguration.getActivityName()
        );

        log.info("Shard " + currentShard + " ready.");
    }
}
