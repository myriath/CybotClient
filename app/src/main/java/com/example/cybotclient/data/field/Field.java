package com.example.cybotclient.data.field;

import com.example.cybotclient.activities.MainActivity;

import java.util.LinkedList;

public class Field {
    private final Bot bot;
    private final LinkedList<FieldObject> objects;
    private MainActivity.PagerAdapter adapter;

    public Field() {
        bot = new Bot();
        objects = new LinkedList<>();
    }

    public void setAdapter(MainActivity.PagerAdapter adapter) {
        this.adapter = adapter;
    }

    public void addObject(FieldObject object) {
        boolean objectOverlapping = false;
        for (FieldObject obj : objects) {
            if (calculateDistance(obj, object) < obj.getRadius() + object.getRadius()) {
                objectOverlapping = true;
                break;
            }
        }
        if (!objectOverlapping) {
            objects.add(object);
        }

        if (adapter.getFieldFragment() != null) {
            adapter.getFieldFragment().redraw();
        }
    }

    public void clear() {
        objects.clear();
    }

    public LinkedList<FieldObject> getObjects() {
        return objects;
    }

    public FieldObject getSmallest() {
        if (objects.size() == 0) return null;
        FieldObject smallest = objects.get(0);
        for (FieldObject object : objects) {
            if (object.getRadius() < smallest.getRadius()) {
                smallest = object;
            }
        }
        return smallest;
    }

    public double calculateDistance(FieldObject o0, FieldObject o1) {
        double x = Math.abs(o0.getX() - o1.getX());
        double y = Math.abs(o0.getY() - o1.getY());

        return Math.sqrt(x * x + y * y);
    }

    public Bot getBot() {
        return bot;
    }
}
