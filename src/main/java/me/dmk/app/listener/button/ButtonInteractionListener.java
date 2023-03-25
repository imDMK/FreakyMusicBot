package me.dmk.app.listener.button;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AllArgsConstructor;
import me.dmk.app.audio.server.ServerAudioPlayer;
import me.dmk.app.audio.server.ServerAudioPlayerMap;
import me.dmk.app.embed.EmbedMessage;
import me.dmk.app.util.StringUtil;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by DMK on 24.03.2023
 */

@AllArgsConstructor
public class ButtonInteractionListener implements ButtonClickListener {

    private final ServerAudioPlayerMap serverAudioPlayerMap;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction interaction = event.getButtonInteraction();
        User user = interaction.getUser();
        Message message = interaction.getMessage();

        String customId = interaction.getCustomId();

        Optional<Server> serverOptional = interaction.getServer();
        if (serverOptional.isEmpty()) {
            return;
        }

        Server server = serverOptional.get();

        Optional<AudioConnection> audioConnectionOptional = server.getAudioConnection();
        if (audioConnectionOptional.isEmpty()) {
            return;
        }

        AudioConnection audioConnection = audioConnectionOptional.get();

        if (!audioConnection.getChannel().isConnected(user)) {
            return;
        }

        Arrays.stream(ButtonInteractionType.values())
                .filter(interactionType -> interactionType.getMessageId().equals(customId))
                .forEachOrdered(interactionType ->
                        this.onButtonClick(interaction, interactionType, server, user, message)
                );
    }

    public void onButtonClick(ButtonInteraction interaction, ButtonInteractionType interactionType, Server server, User user, Message message) {
        Optional<ServerAudioPlayer> serverAudioPlayerOptional = this.serverAudioPlayerMap.get(server.getId());
        if (serverAudioPlayerOptional.isEmpty()) {
            return;
        }

        ServerAudioPlayer serverAudioPlayer = serverAudioPlayerOptional.get();
        AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        switch (interactionType) {
            case TRACK_LIST_CLEAR -> {
                serverAudioPlayer.getTrackScheduler().getQueue().clear();

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setAuthor(user);
                embedMessage.setDescription("Wyczyszczono kolejkę.");

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }

            case TRACK_PLAY_OR_STOP -> {
                audioPlayer.setPaused(!audioPlayer.isPaused());

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setAuthor(user);
                embedMessage.setDescription(
                        "**" + (audioPlayer.isPaused() ? "Zatrzymano" : "Wznowiono") + "** odtwarzanie utworu:",
                        "**" + audioPlayer.getPlayingTrack().getInfo().title + "**"
                );

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }

            case TRACK_SKIP -> {
                if (playingTrack == null) {
                    return;
                }

                //Skip track
                serverAudioPlayer.getTrackScheduler().nextTrack();

                AudioTrack nextTrack = audioPlayer.getPlayingTrack();

                String[] embedDescrption;
                if (nextTrack == null) {
                    embedDescrption = new String[]{
                            "Pomięto utwór:",
                            "**" + playingTrack.getInfo().title + "**",
                            "",
                            "Następny utwór:",
                            "**Brak**"
                    };
                } else {
                    embedDescrption = new String[]{
                            "Pomięto utwór:",
                            "**" + playingTrack.getInfo().title + "**",
                            "",
                            "Następny utwór:",
                            "**" + nextTrack.getInfo().title + "**",
                            "",
                            "Długość: **" + StringUtil.millisToString(nextTrack.getDuration()) + "**"
                    };
                }

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setAuthor(user);
                embedMessage.setDescription(embedDescrption);
                embedMessage.setYouTubeVideoImage(nextTrack);

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }

            case TRACK_REPEAT -> {
                String playingTrackTitle = (playingTrack == null ? "Następnego utworu" : playingTrack.getInfo().title);

                boolean isRepeat = serverAudioPlayer.getTrackScheduler().switchRepeat();

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setAuthor(user);
                embedMessage.setDescription(
                        "**" + (isRepeat ? "Włączono" : "Wyłączono") + "** powtarzanie utworu:",
                        "**" + playingTrackTitle + "**"
                );

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }
        }
    }
}
