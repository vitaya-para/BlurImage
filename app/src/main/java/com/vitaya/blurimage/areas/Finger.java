package com.vitaya.blurimage.areas;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.opencv.core.Rect;

import java.util.HashMap;

public class Finger extends SelectedArea {
    private final int radius;
    private float coefH;

    public Finger(@NonNull Bitmap imageBitmap, ImageView img, int radius) {
        super(imageBitmap, img);
        this.radius = radius;
    }

    public Finger(SelectedArea a, int radius) {
        super(a);
        this.radius = radius;
    }

    public void paintEventSingle(int x, int y) {
        paintEvent(new Rect((int)(x - radius * coefH), y - (int)(radius * coefH), (int)(2 * radius * coefH), (int)(2 * radius * coefH)), matImage);
        update();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setTouchEvent() {
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    history.push(matImage.clone());
                float x = event.getX();
                float y = event.getY();
                int h = v.getHeight();
                int w = v.getWidth();
                coefH = (float) matImage.rows() / h;
                float coefW = (float) matImage.cols() / w;
                if ((int) (x * coefW) < 0 || (int) (y * coefH) < 0 || x >= w || y >= h)
                    return true;
                paintEventSingle((int) (x * coefW), (int) (y * coefH));
                return true;
            }
        });
    }
}