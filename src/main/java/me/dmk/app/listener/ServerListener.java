package me.dmk.app.listener;

import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

import java.awt.Color;

/**
 * Created by DMK on 20.05.2023
 */
public class ServerListener implements ServerJoinListener {

    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server server = event.getServer();

        server.getModeratorsOnlyChannel().ifPresent(serverTextChannel -> {
            EmbedMessage embedMessage = new EmbedMessage(server);

            embedMessage.setColor(Color.GREEN);
            embedMessage.setDescription(
                    "**Dziękuję za dodanie mnie na twój serwer.**",
                    "Aby odtworzyć piosenkę użyj polecenia **/play**",
                    "Mój status zobaczysz używając polecenia **/status**",
                    "",
                    "Dodatkowo obsługuję niestandardowe emoji w moich wiadomościach embed, aby je zmienić dodaj na ten serwer emoji o nazwie:",
                    "Nazwa emoji powodzenia: **success**",
                    "Nazwa emoji błędu: **error**"
            );

            serverTextChannel.sendMessage(embedMessage);
        });
    }
}
