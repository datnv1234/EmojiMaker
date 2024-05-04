package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.core.content.ContextCompat;

import com.wa.ai.emojimaker.R;

public class TextSticker extends Sticker {
    private static final String mEllipsis = "…";
    private Layout.Alignment alignment;
    private final Context context;
    private Drawable drawable;
    private float lineSpacingExtra;
    private float lineSpacingMultiplier;
    private float maxTextSizePixels;
    private float minTextSizePixels;
    private final Rect realBounds;
    private StaticLayout staticLayout;
    private String text;
    private final TextPaint textPaint;
    private final Rect textRect;

    public String getDrawablePath() {
        return "no";
    }

    public TextSticker(Context context2) {
        this(context2, (Drawable) null);
    }

    public TextSticker(Context context2, Drawable drawable2) {
        this.lineSpacingMultiplier = 1.0f;
        this.lineSpacingExtra = 0.0f;
        this.context = context2;
        this.drawable = drawable2;
        if (drawable2 == null) {
            this.drawable = ContextCompat.getDrawable(context2, R.drawable.sticker_transparent_background);
        }
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        this.realBounds = new Rect(0, 0, getWidth(), getHeight());
        this.textRect = new Rect(0, 0, getWidth(), getHeight());
        this.minTextSizePixels = convertSpToPx(6.0f);
        this.maxTextSizePixels = convertSpToPx(32.0f);
        this.alignment = Layout.Alignment.ALIGN_CENTER;
        textPaint2.setTextSize(this.maxTextSizePixels);
    }

    public void draw(Canvas canvas) {
        Matrix matrix = getMatrix();
        canvas.save();
        canvas.concat(matrix);
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            drawable2.setBounds(this.realBounds);
            this.drawable.draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        if (this.textRect.width() == getWidth()) {
            canvas.translate(0.0f, (float) ((getHeight() / 2) - (this.staticLayout.getHeight() / 2)));
        } else {
            canvas.translate((float) this.textRect.left, (float) ((this.textRect.top + (this.textRect.height() / 2)) - (this.staticLayout.getHeight() / 2)));
        }
        this.staticLayout.draw(canvas);
        canvas.restore();
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

    public TextSticker setAlpha(int i2) {
        this.textPaint.setAlpha(i2);
        return this;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public TextSticker setDrawable(Drawable drawable2) {
        this.drawable = drawable2;
        this.realBounds.set(0, 0, getWidth(), getHeight());
        this.textRect.set(0, 0, getWidth(), getHeight());
        return this;
    }

    public TextSticker setDrawable(Drawable drawable2, Rect rect) {
        this.drawable = drawable2;
        this.realBounds.set(0, 0, getWidth(), getHeight());
        if (rect == null) {
            this.textRect.set(0, 0, getWidth(), getHeight());
        } else {
            this.textRect.set(rect.left, rect.top, rect.right, rect.bottom);
        }
        return this;
    }

    public TextSticker setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        return this;
    }

    public TextSticker setTextColor(int i2) {
        this.textPaint.setColor(i2);
        return this;
    }

    public TextSticker setTextAlign(Layout.Alignment alignment2) {
        this.alignment = alignment2;
        return this;
    }

    public TextSticker setMaxTextSize(float f2) {
        this.textPaint.setTextSize(convertSpToPx(f2));
        this.maxTextSizePixels = this.textPaint.getTextSize();
        return this;
    }

    public TextSticker setMinTextSize(float f2) {
        this.minTextSizePixels = convertSpToPx(f2);
        return this;
    }

    public TextSticker setLineSpacing(float f2, float f3) {
        this.lineSpacingMultiplier = f3;
        this.lineSpacingExtra = f2;
        return this;
    }

    public TextSticker setText(String str) {
        this.text = str;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public TextSticker resizeText() {
        int lineForVertical = 0;
        int height = this.textRect.height();
        int width = this.textRect.width();
        String text2 = getText();
        if (text2 != null && text2.length() > 0 && height > 0 && width > 0) {
            float f2 = this.maxTextSizePixels;
            if (f2 > 0.0f) {
                int textHeightPixels = getTextHeightPixels(text2, width, f2);
                float f3 = f2;
                while (textHeightPixels > height) {
                    float f4 = this.minTextSizePixels;
                    if (f3 <= f4) {
                        break;
                    }
                    f3 = Math.max(f3 - 2.0f, f4);
                    textHeightPixels = getTextHeightPixels(text2, width, f3);
                }
                if (f3 == this.minTextSizePixels && textHeightPixels > height) {
                    TextPaint textPaint2 = new TextPaint(this.textPaint);
                    textPaint2.setTextSize(f3);
                    StaticLayout staticLayout2 = new StaticLayout(text2, textPaint2, width, Layout.Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier, this.lineSpacingExtra, false);
                    if (staticLayout2.getLineCount() > 0 && staticLayout2.getLineForVertical(height) - 1 >= 0) {
                        int lineStart = staticLayout2.getLineStart(lineForVertical);
                        int lineEnd = staticLayout2.getLineEnd(lineForVertical);
                        float lineWidth = staticLayout2.getLineWidth(lineForVertical);
                        float measureText = textPaint2.measureText(mEllipsis);
                        while (((float) width) < lineWidth + measureText) {
                            lineEnd--;
                            lineWidth = textPaint2.measureText(text2.subSequence(lineStart, lineEnd + 1).toString());
                        }
                        setText(text2.subSequence(0, lineEnd) + mEllipsis);
                    }
                }
                this.textPaint.setTextSize(f3);
                this.staticLayout = new StaticLayout(this.text, this.textPaint, this.textRect.width(), this.alignment, this.lineSpacingMultiplier, this.lineSpacingExtra, true);
            }
        }
        return this;
    }

    public float getMinTextSizePixels() {
        return this.minTextSizePixels;
    }

    /* access modifiers changed from: protected */
    public int getTextHeightPixels(CharSequence charSequence, int i2, float f2) {
        this.textPaint.setTextSize(f2);
        return new StaticLayout(charSequence, this.textPaint, i2, Layout.Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier, this.lineSpacingExtra, true).getHeight();
    }

    private float convertSpToPx(float f2) {
        return f2 * this.context.getResources().getDisplayMetrics().scaledDensity;
    }

    public TextSticker resizeText2(int i2, int i3) {
        int lineForVertical = 0;
        int height = this.textRect.height();
        int width = this.textRect.width();
        String text2 = getText();
        if (text2 != null && text2.length() > 0 && height > 0 && width > 0) {
            float f2 = this.maxTextSizePixels;
            if (f2 > 0.0f) {
                int textHeightPixels = getTextHeightPixels(text2, width, f2);
                while (textHeightPixels > height) {
                    float f3 = this.minTextSizePixels;
                    if (f2 <= f3) {
                        break;
                    }
                    f2 = Math.max(f2 - 2.0f, f3);
                    textHeightPixels = getTextHeightPixels(text2, width, f2);
                }
                if (f2 == this.minTextSizePixels && textHeightPixels > height) {
                    TextPaint textPaint2 = new TextPaint(this.textPaint);
                    textPaint2.setTextSize(f2);
                    StaticLayout staticLayout2 = new StaticLayout(text2, textPaint2, width, Layout.Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier, this.lineSpacingExtra, false);
                    if (staticLayout2.getLineCount() > 0 && staticLayout2.getLineForVertical(height) - 1 >= 0) {
                        int lineStart = staticLayout2.getLineStart(lineForVertical);
                        int lineEnd = staticLayout2.getLineEnd(lineForVertical);
                        float lineWidth = staticLayout2.getLineWidth(lineForVertical);
                        float measureText = textPaint2.measureText(mEllipsis);
                        while (((float) width) < lineWidth + measureText) {
                            lineEnd--;
                            lineWidth = textPaint2.measureText(text2.subSequence(lineStart, lineEnd + 1).toString());
                        }
                        setText(text2.subSequence(0, lineEnd) + mEllipsis);
                    }
                }
                this.textPaint.setTextSize((float) i2);
                this.staticLayout = new StaticLayout(this.text, this.textPaint, this.textRect.width(), this.alignment, this.lineSpacingMultiplier, this.lineSpacingExtra, true);
            }
        }
        return this;
    }
}
