package com.vitaya.blurimage;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.vitaya.blurimage.areas.Finger;
import com.vitaya.blurimage.areas.Freeform;
import com.vitaya.blurimage.areas.SelectedArea;

import org.opencv.core.Scalar;


public class Painter {

    private SelectedArea area;
    private int size;

    Painter(@NonNull Bitmap imageBitmap, ImageView img) {
        size = 20;
        area = new Finger(imageBitmap, img,size);
        area.setSelectedMethod(2);
        area.setTouchEvent();
    }

    public void setSelectedMethod(int value) {
        area.setSelectedMethod(value);
    }

    public void setSelectedArea(int value) {
        switch (value) {
            case 0:
                area = new Finger(area, size);
                break;
            case 1:
                area = new Freeform(area);
                break;
        }
        area.setTouchEvent();
    }

    public void rollback()
    {
        area.rollback();
    }

    public void save()
    {
        area.saveFile();
    }

    public Bitmap share() {
        return area.share();
    }

    public void setSize(int val)
    {
        size = val;
        area = new Finger(area, size);
        area.setTouchEvent();
    }
    public int getSize()
    {
        return size;
    }

    public void setColor(Scalar scalar) {
        area.setColor(scalar);
    }
}