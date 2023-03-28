package me.dmk.app.command.implementation;

import me.dmk.app.MusicApp;
import me.dmk.app.command.Command;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.StringUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by DMK on 23.03.2023
 */

public class StatusCommand extends Command {

    private final MusicApp musicApp;

    public StatusCommand(MusicApp musicApp) {
        super("status", "Wyświetla informacje o mnie");

        this.musicApp = musicApp;
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user) {
        DiscordApi discordApi = interaction.getApi();

        int totalShards = discordApi.getTotalShards();
        int currentShard = discordApi.getCurrentShard();

        long gatewayLatency = discordApi.getLatestGatewayLatency().toMillis();
        long start = System.currentTimeMillis();

        Duration uptime = Duration.between(this.musicApp.getStartInstant(), Instant.now());

        interaction.respondLater()
                .thenAcceptAsync(responseUpdater -> {
                    long end = System.currentTimeMillis();
                    long elapsedTime = end - start;

                    EmbedMessage embedMessage = new EmbedMessage(server).success();

                    embedMessage.setDescription(
                            "Ilość shardów: **" + totalShards + "**",
                            "Aktualny shard: **" + currentShard + "**",
                            "",
                            "Opóźnienie bramy Discord: **" + gatewayLatency + "ms**",
                            "Opóźnienie klienta: **" + elapsedTime + "ms**",
                            "",
                            "Działam od: **" + StringUtil.durationToString(uptime) + "**",
                            "",
                            "Zaproś mnie na swój serwer używając: " + discordApi.createBotInvite()
                    );

                    responseUpdater.addEmbed(embedMessage).update();
                })
                .exceptionallyAsync(throwable -> {
                    EmbedMessage embedMessage = new EmbedMessage(server).success();

                    embedMessage.setDescription("Wystąpił błąd");
                    embedMessage.addField("Szczegóły błędu", throwable.getMessage());

                    embedMessage.createImmediateResponder(interaction);
                    return null;
                });
    }
}
