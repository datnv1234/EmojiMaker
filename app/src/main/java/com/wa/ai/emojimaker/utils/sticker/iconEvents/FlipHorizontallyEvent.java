package com.wa.ai.emojimaker.utils.sticker.iconEvents;

import com.wa.ai.emojimaker.utils.sticker.StickerView;

/**
 * @author wupanjie
 */

public class FlipHorizontallyEvent extends AbstractFlipEvent {

    @Override
    @StickerView.Flip
    protected int getFlipDirection() {
        return StickerView.FLIP_HORIZONTALLY;
    }
}
