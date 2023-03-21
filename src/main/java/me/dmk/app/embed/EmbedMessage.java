package me.dmk.app.embed;

import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.util.Optional;

/**
 * Created by DMK on 20.03.2023
 */

public class EmbedMessage extends EmbedBuilder {

    private final Server server;

    private final Color defaultColor = new Color(255, 255, 255);
    private final Color successColor = new Color(50, 255, 0);
    private final Color warningColor = new Color(255, 150, 0);
    private final Color errorColor = new Color(255, 0, 0);

    public EmbedMessage(Server server) {
        this.server = server;
        this.setTimestampToNow();

        server.getIcon()
                .ifPresentOrElse(icon -> 
                        this.setFooter(server.getName(), icon), 
                        () -> this.setFooter(server.getName())
                );
    }

    public EmbedMessage defaultEmbed() {
        this.setColor(this.defaultColor);
        return this;
    }

    public EmbedMessage success() {
        Optional<KnownCustomEmoji> successEmoji = this.server.getCustomEmojisByNameIgnoreCase("success").stream().findFirst();

        this.setTitle((successEmoji.map(KnownCustomEmoji::getMentionTag).orElse("✅")) + " Wykonano!");
        this.setColor(this.successColor);

        return this;
    }

    public EmbedMessage warning() {
        Optional<KnownCustomEmoji> successEmoji = this.server.getCustomEmojisByNameIgnoreCase("warning").stream().findFirst();

        this.setTitle((successEmoji.map(KnownCustomEmoji::getMentionTag).orElse("⚠")) + " Ostrzeżenie!");
        this.setColor(this.warningColor);

        return this;
    }

    public EmbedMessage error() {
        Optional<KnownCustomEmoji> successEmoji = this.server.getCustomEmojisByNameIgnoreCase("error").stream().findFirst();

        this.setTitle((successEmoji.map(KnownCustomEmoji::getMentionTag).orElse("❌")) + " Błąd!");
        this.setColor(this.errorColor);

        return this;
    }
    
    public void createImmediateResponder(SlashCommandInteraction slashCommandInteraction) {
        slashCommandInteraction.createImmediateResponder().addEmbed(this).respond();
    }
}
