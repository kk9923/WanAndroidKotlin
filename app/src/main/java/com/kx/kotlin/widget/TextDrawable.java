package com.kx.kotlin.widget;

import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;

/**
 *  自定义文字标签Drawable, 结合 SpannableString  +  ImageSpan  使用
 */
public class TextDrawable extends ShapeDrawable {
    private final Paint textPaint;
    private final Paint borderPaint;
    private final String text;
    private final int fontSize;
    private final float radius;
    private final float strokeWidth;
    private final int height;
    private final int width;

    private TextDrawable(TextDrawable.Builder builder) {
        super(builder.shape);
        this.height = builder.height;
        this.width = builder.width;
        this.radius = builder.radius;
        this.text = builder.text;
        this.fontSize = builder.fontSize;
        this.textPaint = new Paint();
        this.textPaint.setColor(builder.textColor);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.strokeWidth = builder.strokeWidth;
        this.borderPaint = new Paint();
        this.borderPaint.setColor(builder.strokeColor);
        this.borderPaint.setColor(builder.textColor);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(builder.strokeWidth);
        Paint var2 = this.getPaint();
        var2.setColor(builder.solidColor);
        setBounds(0, 0, getIntrinsicWidth() + builder.padding,
                getIntrinsicHeight() + builder.padding);
    }

    public void draw(Canvas var1) {
        super.draw(var1);
        Rect var2 = this.getBounds();
        if (this.strokeWidth > 0) {
            this.drawBorder(var1);
        }
        int var3 = var1.save();
        this.textPaint.setTextSize(this.fontSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (var2.height() / 2 - top / 2 - bottom / 2);


        var1.drawText(this.text, (float) (var2.width() / 2), baseLineY, this.textPaint);
        var1.restoreToCount(var3);
    }

    private void drawBorder(Canvas canvas) {
        RectF rectF = new RectF(this.getBounds());
        rectF.inset((this.strokeWidth / 2), (this.strokeWidth / 2));
        canvas.drawRoundRect(rectF, this.radius, this.radius, this.borderPaint);
    }

    public void setAlpha(int var1) {
        this.textPaint.setAlpha(var1);
    }

    public void setColorFilter(ColorFilter var1) {
        this.textPaint.setColorFilter(var1);
    }
    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return this.width;
    }

    public int getIntrinsicHeight() {
        return this.height;
    }

    public static class Builder {
        private String text;
        private int strokeColor;
        private int solidColor;
        private float strokeWidth;
        private RoundRectShape shape;
        private int textColor;
        private int fontSize;
        private int padding ;
        private int paddingLeft = 0 ;
        private int paddingTop = 0 ;
        private int paddingRight = 0 ;
        private int paddingBottom = 0 ;
        private float radius;
        private int width ;
        private int height  ;

        public Builder() {
            this.text = "";
            this.strokeColor = -1;
            this.solidColor = Color.WHITE;
            this.textColor = -1;
            this.strokeWidth = 0;
            this.shape = null;
            this.fontSize = -1;
            this.width = -1;
            this.height = -1;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }
        public Builder padding(int padding) {
            this.padding = padding;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }


        public Builder strokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }
        public Builder solidColor(int solidColor) {
            this.solidColor = solidColor;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            setBounds(text);
            return this;
        }

        public Builder textColor(int var1) {
            this.textColor = var1;
            return this;
        }

        public Builder strokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Builder fontSize(int var1) {
            this.fontSize = var1;
            return this;
        }

        public TextDrawable buildRoundRect(int radius) {
            this.radius = (float) radius;
            float[] var2 = new float[]{(float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius};
            this.shape = new RoundRectShape(var2, null, null);
            return new TextDrawable(this);
        }

        private void setBounds(String text) {
            if (this.width <= 0 || this.height <= 0) {
                if (!TextUtils.isEmpty(text) && this.fontSize >= 0) {
                    Paint var2 = new Paint();
                    var2.setTextSize((float) this.fontSize);
                    Rect var3 = new Rect();
                    var2.getTextBounds(text, 0, text.length(), var3);
                    if (this.width < 0) {
                        this.width = var3.width() + 20;
                    }

                    if (this.height < 0) {
                        this.height = var3.height() + 12;
                    }

                }
            }
        }
    }
}
