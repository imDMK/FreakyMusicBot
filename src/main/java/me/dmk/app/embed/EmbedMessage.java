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

import java.awt.*;
import java.util.Optional;

/**
 * Created by DMK on 20.03.2023
 */

public class EmbedMessage extends EmbedBuilder {

    private final Server server;

    private final Color successColor = new Color(50, 255, 0);
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

    public EmbedMessage success() {
        Optional<KnownCustomEmoji> successEmoji = this.server.getCustomEmojisByNameIgnoreCase("success").stream().findFirst();

        this.setTitle((successEmoji.map(KnownCustomEmoji::getMentionTag).orElse("✅")) + " Wykonano!");
        this.setColor(this.successColor);

        return this;
    }

    public EmbedMessage error() {
        Optional<KnownCustomEmoji> successEmoji = this.server.getCustomEmojisByNameIgnoreCase("error").stream().findFirst();

        this.setTitle((successEmoji.map(KnownCustomEmoji::getMentionTag).orElse("❌")) + " Błąd!");
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
    
    public void createImmediateResponder(InteractionBase interactionBase) {
        interactionBase.createImmediateResponder()
                .addEmbed(this)
                .respond();
    }

    public void createImmediateResponder(InteractionBase interactionBase, HighLevelComponent... highLevelComponents) {
        interactionBase.createImmediateResponder()
                .addEmbed(this)
                .addComponents(highLevelComponents)
                .respond();
    }

    public void createImmediateResponder(InteractionBase interactionBase, boolean ephermal, HighLevelComponent... highLevelComponents) {
        InteractionImmediateResponseBuilder responseBuilder = interactionBase.createImmediateResponder();

        responseBuilder.addEmbed(this);
        responseBuilder.addComponents(highLevelComponents);

        if (ephermal) {
            responseBuilder.setFlags(MessageFlag.EPHEMERAL);
        }

        responseBuilder.respond();
    }
}
