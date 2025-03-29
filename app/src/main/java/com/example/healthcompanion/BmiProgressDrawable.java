package com.example.healthcompanion;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.graphics.Rect;

public class BmiProgressDrawable extends Drawable {
    private final Paint paint = new Paint();
    private int progress = 0;
    private int max = 40;
    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float width = bounds.width();
        float height = bounds.height();

        // Fill the entire background with white
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        // Calculate segment widths based on BMI ranges
        float underweightEnd = (18.5f / max) * width; // 0 to 18.5
        float normalEnd = (25f / max) * width;       // 18.5 to 25
        float overweightEnd = (30f / max) * width;   // 25 to 30
        float obeseEnd = width;                      // 30 to 40

        // If progress is 0, don't draw any progress, just keep the white background
        if (progress == 0) {
            return;
        }

        // Calculate progress position
        float progressWidth = (progress / (float) max) * width;

        // Draw underweight segment (0 to 18.5)
        paint.setColor(Color.parseColor("#FFCDD2"));
        canvas.drawRect(0, 0, Math.min(progressWidth, underweightEnd), height, paint);

        // Draw normal segment (18.5 to 25)
        if (progressWidth > underweightEnd) {
            paint.setColor(Color.parseColor("#C8E6C9"));
            canvas.drawRect(underweightEnd, 0, Math.min(progressWidth, normalEnd), height, paint);
        }

        // Draw overweight segment (25 to 30)
        if (progressWidth > normalEnd) {
            paint.setColor(Color.parseColor("#FFECB3"));
            canvas.drawRect(normalEnd, 0, Math.min(progressWidth, overweightEnd), height, paint);
        }

        // Draw obese segment (30 to 40)
        if (progressWidth > overweightEnd) {
            paint.setColor(Color.parseColor("#FF8A65"));
            canvas.drawRect(overweightEnd, 0, progressWidth, height, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE; // Changed to OPAQUE since we're drawing a solid color initially
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidateSelf();
    }

    public void setMax(int max) {
        this.max = max;
        invalidateSelf();
    }
}