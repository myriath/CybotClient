package com.example.cybotclient.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public abstract class CanvasView extends View {
    protected final Paint paint;
    protected Canvas canvas;
    protected boolean ready;

    public CanvasView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public void drawLine(float x1, float y1, float x2, float y2, int color, int thickness) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);

        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void drawCircle(float x, float y, float radius, int color, boolean fill) {
        paint.setColor(color);
        paint.setStyle(fill ? Paint.Style.FILL : Paint.Style.STROKE);

        canvas.drawCircle(x, y, radius, paint);
    }
}