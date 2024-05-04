package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class DrawableSticker extends Sticker {
    private Drawable drawable;
    private String drawablePath;
    private Drawable originalDrawable;
    private Rect realBounds = new Rect(0, 0, getWidth(), getHeight());

    public DrawableSticker(Drawable drawable2, String str) {
        this.drawable = drawable2;
        this.drawablePath = str;
        this.originalDrawable = drawable2.getConstantState().newDrawable().mutate();
        clearDirty();
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public String getDrawablePath() {
        return this.drawablePath;
    }

    public DrawableSticker setDrawable(Drawable drawable2) {
        this.drawable = drawable2;
        markAsDirty();
        return this;
    }

    public Drawable getOriginalDrawable() {
        return this.originalDrawable.getConstantState().newDrawable().mutate();
    }

    public void setOriginalDrawable(Drawable drawable2) {
        this.originalDrawable = drawable2.getConstantState().newDrawable().mutate();
        markAsDirty();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            drawable2.setBounds(this.realBounds);
        }
        this.drawable.draw(canvas);
        canvas.restore();
        clearDirty();
    }

    public DrawableSticker setAlpha(int i2) {
        this.drawable.setAlpha(i2);
        markAsDirty();
        return this;
    }

    public int getWidth() {
        return this.drawable.getIntrinsicWidth();
    }

    public int getHeight() {
        return this.drawable.getIntrinsicHeight();
    }

    public void release() {
        super.release();
        if (this.drawable != null) {
            this.drawable = null;
        }
    }
}
