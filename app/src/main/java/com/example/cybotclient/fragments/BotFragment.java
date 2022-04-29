package com.example.cybotclient.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.cybotclient.R;
import com.example.cybotclient.data.Scan;

import java.util.LinkedList;

public class BotFragment extends Fragment {
    BotView botView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_canvas, container, false);
        RelativeLayout layout = rootView.findViewById(R.id.fragment_canvas_view);
        botView = new BotView(getActivity());
        layout.addView(botView);

        return rootView;
    }

    public void addScan(Scan scan) {
        botView.addScan(scan);
    }

    public boolean drawReady() {
        return botView.isReady();
    }

    public void redraw() {
        botView.invalidate();
    }

    public void clear() {
        botView.clearLines();
    }

    private static class BotView extends CanvasView {
        private final LinkedList<Scan> scanLines;

        public BotView(Context context) {
            super(context);
            scanLines = new LinkedList<>();
        }

        public void addScan(Scan scan) {
            scanLines.add(scan);
        }

        public void clearLines() {
            scanLines.clear();
        }

        public void drawScan(Scan scan, int color, int thickness, float scalar) {
            if (scan.distance > 80 || scan.distance < 10) return;

            double distPixels = (scan.distance / 10.0) * scalar + 10;
            double rad = Math.toRadians(scan.angle);
            double rad2 = Math.toRadians(scan.angle + 2);
            float midX = canvas.getWidth() / 2.0f;
            float midY = canvas.getHeight() / 2.0f;
            float x1 = (float) (Math.cos(rad) * distPixels) + midX;
            float y1 = (float) -(Math.sin(rad) * distPixels) + midY;
            float x2 = (float) (Math.cos(rad2) * distPixels) + midX;
            float y2 = (float) -(Math.sin(rad2) * distPixels) + midY;

            drawLine(x1, y1, x2, y2, color, thickness);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.canvas = canvas;
            ready = true;

            int sideLength;
            if (getWidth() < getHeight()) {
                sideLength = getWidth() - 10;
            } else {
                sideLength = getHeight() - 10;
            }

            float midX = getWidth() / 2.0f;
            float midY = getHeight() / 2.0f;
            drawCircle(midX, midY, sideLength / 2.0f + 5, Color.WHITE, true);
            drawCircle(midX, midY, 10, Color.RED, true);

            paint.setStrokeWidth(2);
            drawCircle(midX, midY, sideLength / 2.0f, Color.BLACK, false);
            float pixelsPerRing = (sideLength / 2.0f - 10) / 8.0f;
            drawCircle(midX, midY, pixelsPerRing + 10, Color.BLACK, false);

            paint.setStrokeWidth(1);
            for (int i = 2; i < 8; i++) {
                drawCircle(midX, midY, pixelsPerRing * i + 10, Color.BLACK, false);
            }

            for (Scan scan : scanLines) {
                drawScan(scan, Color.RED, 3, pixelsPerRing);
            }
        }
    }
}