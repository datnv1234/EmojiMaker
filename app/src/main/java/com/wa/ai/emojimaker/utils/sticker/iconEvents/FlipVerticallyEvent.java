package com.wa.ai.emojimaker.utils.sticker.iconEvents;

import com.wa.ai.emojimaker.utils.sticker.StickerView;

/**
 * @author wupanjie
 */

public class FlipVerticallyEvent extends AbstractFlipEvent {

    @Override
    @StickerView.Flip
    protected int getFlipDirection() {
        return StickerView.FLIP_VERTICALLY;
    }
}
