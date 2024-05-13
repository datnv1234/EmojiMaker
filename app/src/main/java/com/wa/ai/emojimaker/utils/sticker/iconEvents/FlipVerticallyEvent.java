package com.wa.ai.emojimaker.utils.sticker.iconEvents;

import com.wa.ai.emojimaker.utils.sticker.StickerView;

/**
 * @author wa
 */

public class FlipVerticallyEvent extends AbstractFlipEvent {

    @Override
    @StickerView.Flip
    protected int getFlipDirection() {
        return StickerView.FLIP_VERTICALLY;
    }
}
