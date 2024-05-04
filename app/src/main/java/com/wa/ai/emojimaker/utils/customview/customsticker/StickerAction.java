package com.wa.ai.emojimaker.utils.customview.customsticker;

public class StickerAction {
    boolean status;
    Sticker sticker;

    public StickerAction(boolean z, Sticker sticker2) {
        this.status = z;
        this.sticker = sticker2;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean z) {
        this.status = z;
    }

    public Sticker getSticker() {
        return this.sticker;
    }

    public void setSticker(Sticker sticker2) {
        this.sticker = sticker2;
    }
}
