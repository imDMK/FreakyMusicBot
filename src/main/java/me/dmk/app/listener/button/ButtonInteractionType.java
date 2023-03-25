package me.dmk.app.listener.button;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by DMK on 25.03.2023
 */

@AllArgsConstructor
@Getter
public enum ButtonInteractionType {

    TRACK_LIST_CLEAR("track-list-clear"),
    TRACK_PLAY_OR_STOP("track-play-or-stop"),
    TRACK_SKIP("track-skip"),
    TRACK_REPEAT("track-toggle-repeat");

    private final String messageId;
}
