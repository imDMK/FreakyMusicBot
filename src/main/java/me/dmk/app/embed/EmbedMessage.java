package me.dmk.app.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.dmk.app.util.StringUtil;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.awt.Color;

/**
 * Created by DMK on 20.03.2023
 */

public class EmbedMessage extends EmbedBuilder {

    private final Server server;

    private final Color successColor = new Color(50, 255, 0);
    private final Color errorColor = new Color(255, 0, 0);

    private HighLevelComponent[] highLevelComponents;

    public EmbedMessage(Server server) {
        this.server = server;
        this.setTimestampToNow();

        server.getIcon()
                .ifPresentOrElse(icon ->
                                this.setFooter(server.getName(), icon),
                        () -> this.setFooter(server.getName())
                );
    }

    public EmbedMessage success() {
        String successEmoji = this.server.getCustomEmojisByNameIgnoreCase("success")
                .stream()
                .map(KnownCustomEmoji::getMentionTag)
                .findFirst()
                .orElse("✅");

        this.setTitle(successEmoji + " Wykonano!");
        this.setColor(this.successColor);

        return this;
    }

    public EmbedMessage error() {
        String errorEmoji = this.server.getCustomEmojisByNameIgnoreCase("error")
                .stream()
                .map(KnownCustomEmoji::getMentionTag)
                .findFirst().orElse("❌");

        this.setTitle(errorEmoji + " Błąd!");
        this.setColor(this.errorColor);

        return this;
    }

    public void setDescription(String... strings) {
        this.setDescription(
                String.join("\n", strings)
        );
    }

    public void setYouTubeVideoImage(AudioTrack audioTrack) {
        if (audioTrack != null) {
            this.setImage(
                    StringUtil.getImageFromYouTubeVideo(audioTrack.getIdentifier())
            );
        }
    }

    public void setHighLevelComponents(HighLevelComponent... highLevelComponents) {
        this.highLevelComponents = highLevelComponents;
    }
    
    public void createImmediateResponder(InteractionBase interactionBase) {
        this.createImmediateResponder(interactionBase, false);
    }

    public void createImmediateResponder(InteractionBase interactionBase, boolean ephemeral) {
        InteractionImmediateResponseBuilder responseBuilder = interactionBase.createImmediateResponder();

        responseBuilder.addEmbed(this);

        if (this.highLevelComponents != null) {
            responseBuilder.addComponents(this.highLevelComponents);
        }

        if (ephemeral) {
            responseBuilder.setFlags(MessageFlag.EPHEMERAL);
        }

        responseBuilder.respond();
    }
}
