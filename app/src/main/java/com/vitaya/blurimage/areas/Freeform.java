package com.vitaya.blurimage.areas;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Freeform extends SelectedArea {

    private float startX, startY;
    private float x, y;
    private final Map<Integer, List<Integer>> figure;
    private Mat backupMat;
    private float maxX, maxY, minX, minY;

    public Freeform(@NonNull Bitmap imageBitmap, ImageView img) {
        super(imageBitmap, img);
        figure = new HashMap<>();
    }

    public Freeform(SelectedArea a) {
        super(a);
        figure = new HashMap<>();
    }

    private float maxOf(float num1, double num2, double num3) {
        if (num1 >= num2 && num1 >= num3)
            return num1;
        else if (num2 >= num1 && num2 >= num3)
            return (float) num2;
        else
            return (float) num3;
    }

    private float minOf(float num1, double num2, double num3) {
        if (num1 <= num2 && num1 <= num3)
            return num1;
        else if (num2 <= num1 && num2 <= num3)
            return (float) num2;
        else
            return (float) num3;
    }


    private void painteventFreeform() {
        history.push(backupMat.clone());
        matImage = backupMat;
        figure.entrySet().parallelStream().forEach(
                entry -> {
                    if (entry.getValue().size() == 1)
                        return;
                    if (entry.getKey() < 1 || entry.getKey() > matImage.rows() - 2)
                        return;
                    List<Integer> arr = new ArrayList<>(entry.getValue());
                    Collections.sort(arr);
                    int size = entry.getValue().size();
                    if (size % 2 != 0)
                        size--;
                    for (int j = 0; j < size; j += 2) {
                        Rect m = new Rect(arr.get(j), entry.getKey(), arr.get(j + 1) - arr.get(j), 1);
                        paintEvent(m, matImage);
                    }
                }
        );
        figure.clear();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setTouchEvent() {
        img.setOnTouchListener(new View.OnTouchListener() {
            private float coefH;

            private Point check(float x, float y) {
                int preX = (int) (x * coefH);
                int preY = (int) (y * coefH);
                if (preX - (int) (8 * coefH) >= matImage.cols())
                    preX = matImage.cols() - 1;
                if (preX <= 0)
                    preX = 0;
                if (preY - (int) (8 * coefH) >= matImage.rows())
                    preY = matImage.rows() - 1;
                if (preY <= 0)
                    preY = 0;
                return new Point(preX, preY);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int h = v.getHeight();
                int w = v.getWidth();
                coefH = (float) matImage.rows() / h;
                float coefW = (float) matImage.cols() / w;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = maxX = minX = x = event.getX() * coefW;
                        startY = maxY = minY = y = event.getY() * coefH;
                        backupMat = matImage.clone();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Point p1 = new Point((int) x, (int) y);
                        Point p2 = check(event.getX(), event.getY());

                        Imgproc.line(matImage, p1, p2, new Scalar(255.0, 0.0, 0.0), (int) (8 * coefW), 8);
                        addLine(p1, p2);

                        maxX = maxOf(maxX, p1.x, p2.x);
                        maxY = maxOf(maxY, p1.y, p2.y);

                        minX = minOf(minX, p1.x, p2.x);
                        minY = minOf(minY, p1.y, p2.y);

                        x = (int) p2.x;
                        y = (int) p2.y;

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Imgproc.line(matImage, new Point((int) startX, (int) startY),
                                check(event.getX(), event.getY()), new Scalar(255, 0, 0, 235), (int) (8 * coefW), 8);
                        addLine(new Point((int) startX, (int) startY), check(event.getX(), event.getY()));
                        update();
                        painteventFreeform();
                        figure.clear();
                        break;
                }
                update();
                return true;
            }
        });
    }

    private void addLine(Point begin, Point end) {
        double A = begin.y - end.y;
        double B = end.x - begin.x;
        double C = begin.x * end.y - end.x * begin.y;

        if(begin.y > end.y)
        {
            Point tmp = begin;
            begin = end;
            end = tmp;
        }

        for(int i = (int)begin.y; i < (int)end.y; i++)
        {
            double x = -(C + B * i) / A;
            addPoint(new Point(x, i));
        }
    }

    private void addPoint(Point point) {
        List<Integer> set = figure.get((int) point.y);
        if (set == null)
            set = new ArrayList<>();
        set.add((int) point.x);
        figure.put((int) point.y, set);
    }
}