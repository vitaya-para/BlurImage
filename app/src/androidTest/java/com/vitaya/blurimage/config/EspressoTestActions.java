package com.vitaya.blurimage.config;


import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.test.espresso.InjectEventSecurityException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.MotionEvents;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;

import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

// code from https://github.com/dbottillo/Blog
public class EspressoTestActions {
    public static ViewAction clickSeekBar(final int pos) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        SeekBar seekBar = (SeekBar) view;
                        final int[] screenPos = new int[2];
                        seekBar.getLocationOnScreen(screenPos);

                        int trueWidth = seekBar.getWidth()
                                - seekBar.getPaddingLeft() - seekBar.getPaddingRight();

                        float relativePos = (0.3f + pos) / (float) seekBar.getMax();
                        if (relativePos > 1.0f)
                            relativePos = 1.0f;

                        final float screenX = trueWidth * relativePos + screenPos[0]
                                + seekBar.getPaddingLeft();
                        final float screenY = seekBar.getHeight() / 2f + screenPos[1];
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    @NonNull
    public static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    public static ViewAction touchDownAndUp(int expectedId, Collection<Point> list) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Send touch events.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                // Get view absolute position
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                view.getWidth();
                ;
                double k = (double) view.getHeight() /
                ((BitmapDrawable) view.getContext().getResources().getDrawable(expectedId)).getBitmap().getHeight();
                // Offset coordinates by view position
                Point begin = list.iterator().next();
                float[] coordinates= new float[]{(int)(begin.x* k) + location[0], (int)(begin.y* k) + location[1]};
                float[] precision = new float[]{1f, 1f};

                // Send down event, pause, and send up
                MotionEvent down = MotionEvents.sendDown(uiController, coordinates, precision).down;
                Point last = null;
                for(Point point : list)
                {
                    down.setAction(MotionEvent.ACTION_MOVE);
                    down.setLocation((int)(point.x * k) + location[0], (int)(point.y* k) + location[1]);
                    try {
                        uiController.injectMotionEvent(down);
                    } catch (InjectEventSecurityException e) {
                        e.printStackTrace();
                    }
                    uiController.loopMainThreadForAtLeast(200);
                    last = point;
                }
                coordinates = new float[]{(int)(last.x* k) + location[0], (int)(last.y* k) + location[1]};
                MotionEvents.sendUp(uiController, down, coordinates);
            }
        };
    }
}
