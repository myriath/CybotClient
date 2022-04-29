package com.example.cybotclient.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.cybotclient.R;
import com.example.cybotclient.activities.MainActivity;
import com.example.cybotclient.data.field.Field;
import com.example.cybotclient.data.field.FieldObject;
import com.example.cybotclient.data.field.Position;

import java.util.LinkedList;

public class FieldFragment extends Fragment {
    FieldView fieldView;

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
        fieldView = new FieldView(getActivity());
        layout.addView(fieldView);

        return rootView;
    }

    public void redraw() {
        fieldView.invalidate();
    }

    public void clear() {
        MainActivity.field.clear();
    }

    private static class FieldView extends CanvasView {
        public FieldView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.canvas = canvas;
            ready = true;

            float midX = getWidth() / 2.0f;
            float midY = getHeight() / 2.0f;
            drawCircle(midX, midY, 18.75f, Color.RED, true);
            drawCircle(midX, midY, 28.75f, Color.GRAY, false);
            drawCircle(midX, midY, 68.75f, Color.GRAY, false);

            Field field = MainActivity.field;
            drawCircle(
                    (float) (midX + Math.cos(Math.toRadians(field.getBot().getAngle())) * 18.75),
                    (float) (midY - Math.sin(Math.toRadians(field.getBot().getAngle())) * 18.75),
                    4, Color.BLUE, true
            );
            if (!field.getObjects().isEmpty()) {
                FieldObject smallest = field.getObjects().get(0);
                for (FieldObject obj : field.getObjects()) {
                    Log.i("obj", obj.toString());
                    drawCircle(
                            (float) (obj.getX() - field.getBot().getX()) + midX,
                            (float) -(obj.getY() - field.getBot().getY()) + midY,
                            (float) obj.getRadius(),
                            Color.GREEN,
                            true
                    );
                    if (obj.getRadius() < smallest.getRadius()) {
                        smallest = obj;
                    }
                }


                drawCircle(
                        (float) (smallest.getX() - field.getBot().getX()) + midX,
                        (float) -(smallest.getY() - field.getBot().getY()) + midY,
                        (float) smallest.getRadius(),
                        Color.WHITE,
                        true
                );
            }
            LinkedList<Position> positions = field.getBot().getPositions();
            Position prev = positions.get(0);
            double botX = field.getBot().getX() - midX;
            double botY = field.getBot().getY() + midY;
            for (int i = 1; i < positions.size(); i++) {
                drawLine((float)(prev.x - botX),
                        (float)(-prev.y + botY),
                        (float)(positions.get(i).x - botX),
                        (float)(-positions.get(i).y + botY),
                        Color.WHITE, 2);
                prev = positions.get(i);
            }
        }
    }
}