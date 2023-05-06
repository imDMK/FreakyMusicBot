package me.dmk.app.util;

import lombok.experimental.UtilityClass;
import me.dmk.app.listener.button.ButtonInteractionType;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;

/**
 * Created by DMK on 06.05.2023
 */
@UtilityClass
public class ActionRowUtil {

    public ActionRow getControlButtons() {
        return ActionRow.of(
                Button.secondary(ButtonInteractionType.TRACK_PLAY_OR_STOP.getMessageId(), "Wznów/Zatrzymaj utwór", EmojiUtil.getPlayOrPause()),
                Button.secondary(ButtonInteractionType.TRACK_SKIP.getMessageId(), "Pomiń utwór", EmojiUtil.getNextTrack()),
                Button.secondary(ButtonInteractionType.TRACK_REPEAT.getMessageId(), "Włącz/Wyłącz powtarzanie utworu", EmojiUtil.getRepeat())
        );
    }
}
