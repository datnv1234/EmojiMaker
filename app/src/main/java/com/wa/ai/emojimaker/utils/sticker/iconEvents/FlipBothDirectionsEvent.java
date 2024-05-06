package com.wa.ai.emojimaker.utils.sticker.iconEvents;


import com.wa.ai.emojimaker.utils.sticker.StickerView;

/**
 * @author wupanjie
 */

public class FlipBothDirectionsEvent extends AbstractFlipEvent {

    @Override
    @StickerView.Flip
    protected int getFlipDirection() {
        return StickerView.FLIP_VERTICALLY | StickerView.FLIP_HORIZONTALLY;
    }
}
