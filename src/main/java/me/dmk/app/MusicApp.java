package me.dmk.app;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import lombok.Getter;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.command.service.CommandService;
import me.dmk.app.configuration.AppConfiguration;
import me.dmk.app.listener.SlashCommandListener;
import me.dmk.app.listener.button.ButtonInteractionListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Created by DMK on 19.03.2023
 */

@Getter
public class MusicApp {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Instant startInstant;
    private final AppConfiguration appConfiguration;

    private final AudioPlayerManager audioPlayerManager;
    private final ServerAudioPlayerMap serverAudioPlayerMap;

    private final CommandService commandService;

    protected MusicApp() {
        this.startInstant = Instant.now();

        /* Configuration */
        this.appConfiguration = ConfigManager.create(AppConfiguration.class, (config) -> {
            config.withConfigurer(new JsonGsonConfigurer());
            config.withBindFile("configuration.json");
            config.withRemoveOrphans(true);
            config.saveDefaults();
            config.load(true);
        });

        /* Managers */
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());

        /* Maps */
        this.serverAudioPlayerMap = new ServerAudioPlayerMap(this.audioPlayerManager);

        /* Services */
        this.commandService = new CommandService(this, this.audioPlayerManager, this.serverAudioPlayerMap);
        this.commandService.registerCommands();

        /* DiscordApi */
        new DiscordApiBuilder()
                .setToken(this.appConfiguration.getToken())
                .setAllIntents()
                .setRecommendedTotalShards()
                .join()
                .loginAllShards()
                .forEach(discordApiCompletableFuture -> discordApiCompletableFuture
                        .thenAccept(this::onShardLogin)
                        .exceptionally(ExceptionLogger.get())
                );
    }

    protected void onShardLogin(DiscordApi discordApi) {
        int currentShard = discordApi.getCurrentShard();

        this.logger.info("Connected to shard " + currentShard);

        /* Bulk commands */
        this.commandService.bulkOverwriteGlobalApplicationCommands(discordApi);

        /* Listeners */
        Stream.of(
                new ButtonInteractionListener(this.serverAudioPlayerMap),
                new SlashCommandListener(this.commandService, this.serverAudioPlayerMap)
        ).forEach(discordApi::addListener);

        discordApi.setMessageCacheSize(10, 60 * 60); //1 hour

        discordApi.updateActivity(
                this.appConfiguration.getActivityType(),
                this.appConfiguration.getActivityName()
        );

        this.logger.info("Shard " + currentShard + " ready.");
    }
}
