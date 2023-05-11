package me.dmk.app.listener.button;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AllArgsConstructor;
import me.dmk.app.audio.TrackScheduler;
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

        Optional<ServerAudioPlayer> serverAudioPlayerOptional = this.serverAudioPlayerMap.get(server.getId());
        if (serverAudioPlayerOptional.isEmpty()) {
            return;
        }

        AudioConnection audioConnection = audioConnectionOptional.get();
        ServerAudioPlayer serverAudioPlayer = serverAudioPlayerOptional.get();

        if (!audioConnection.getChannel().isConnected(user) && !serverAudioPlayer.isRequester(user)) {
            return;
        }

        Arrays.stream(ButtonInteractionType.values())
                .filter(interactionType -> interactionType.getMessageId().equals(customId))
                .findAny().ifPresent(interactionType ->
                        this.onButtonClick(interaction, interactionType, serverAudioPlayer, server, user, message)
                );
    }

    private void onButtonClick(ButtonInteraction interaction, ButtonInteractionType interactionType, ServerAudioPlayer serverAudioPlayer, Server server, User user, Message message) {
        switch (interactionType) {
            case TRACK_LIST_CLEAR -> {
                serverAudioPlayer.getTrackScheduler().getQueue().clear();

                //Respond
                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setDescription("Wyczyszczono kolejkę.");
                embedMessage.setAuthor(user);

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }

            case TRACK_PLAY_OR_STOP -> {
                AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();

                audioPlayer.setPaused(!audioPlayer.isPaused());

                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setDescription(
                        "**" + (audioPlayer.isPaused() ? "Zatrzymano" : "Wznowiono") + "** odtwarzanie utworu:",
                        "**" + audioPlayer.getPlayingTrack().getInfo().title + "**"
                );
                embedMessage.setAuthor(user);

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }

            case TRACK_SKIP -> {
                AudioPlayer audioPlayer = serverAudioPlayer.getAudioPlayer();
                TrackScheduler trackScheduler = serverAudioPlayer.getTrackScheduler();

                AudioTrack playingTrack = audioPlayer.getPlayingTrack();
                if (playingTrack == null) {
                    return;
                }

                //Skip track
                trackScheduler.nextTrack();

                //Get next track
                AudioTrack nextTrack = audioPlayer.getPlayingTrack();

                //Respond
                String[] embedDescription;
                if (nextTrack == null) {
                    embedDescription = new String[]{
                            "Pomięto utwór:",
                            "**" + playingTrack.getInfo().title + "**",
                            "",
                            "Następny utwór:",
                            "**Brak**"
                    };
                } else {
                    embedDescription = new String[]{
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

                embedMessage.setDescription(embedDescription);
                embedMessage.setYouTubeVideoImage(nextTrack);
                embedMessage.setAuthor(user);

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }

            case TRACK_REPEAT -> {
                TrackScheduler trackScheduler = serverAudioPlayer.getTrackScheduler();
                AudioTrack playingTrack = serverAudioPlayer.getAudioPlayer().getPlayingTrack();

                //Switch
                trackScheduler.switchRepeat();

                //Respond
                EmbedMessage embedMessage = new EmbedMessage(server).success();

                embedMessage.setAuthor(user);
                embedMessage.setDescription(
                        "**" + (trackScheduler.isRepeat() ? "Włączono" : "Wyłączono") + "** powtarzanie utworu:",
                        "**" + (playingTrack == null ? "Następnego utworu" : playingTrack.getInfo().title) + "**"
                );

                message.edit(embedMessage);
                interaction.createImmediateResponder().respond();
            }
        }
    }
}
