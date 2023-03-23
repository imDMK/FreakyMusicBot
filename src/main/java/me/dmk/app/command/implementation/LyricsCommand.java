package me.dmk.app.command.implementation;

import com.jagrosh.jlyrics.LyricsClient;
import me.dmk.app.command.Command;
import me.dmk.app.embed.EmbedMessage;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

/**
 * Created by DMK on 22.03.2023
 */

public class LyricsCommand extends Command {
    public LyricsCommand() {
        super("lyrics", "Wyświetl tekst utworu.");

        this.addOption(
                SlashCommandOption.create(
                        SlashCommandOptionType.STRING,
                        "search",
                        "Podaj nazwę utworu",
                        true
                )
        );
    }

    @Override
    public void execute(SlashCommandInteraction interaction, Server server, User user) {
        String search = interaction.getArgumentStringValueByName("search").orElseThrow();

        LyricsClient lyricsClient = new LyricsClient();

        lyricsClient.getLyrics(search)
                .thenAcceptAsync(lyrics -> {
                    EmbedMessage embedMessage = new EmbedMessage(server).success();

                    String author = lyrics.getAuthor().replace("Lyrics", "");

                    embedMessage.setTitle("\uD83C\uDFB5 " + author + " - " + lyrics.getTitle()); //🎵
                    embedMessage.setUrl(lyrics.getURL());
                    embedMessage.setDescription(lyrics.getContent());

                    embedMessage.createImmediateResponder(interaction);
                })
                .exceptionallyAsync(throwable -> {
                    EmbedMessage embedMessage = new EmbedMessage(server).error();
                    embedMessage.setDescription("Nie udało się znaleźć tekstu.");

                    embedMessage.createImmediateResponder(interaction);
                    return null;
                });
    }
}
