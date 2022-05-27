package com.vitaya.blurimage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.vitaya.blurimage.MainActivity.PICK_IMAGE;
import static com.vitaya.blurimage.config.EspressoTestActions.touchDownAndUp;
import static com.vitaya.blurimage.config.EspressoTestsMatchers.withDrawable;
import static org.junit.Assert.assertNotNull;

import android.content.Intent;
import android.graphics.Point;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class FreeformTest {
    @Rule
    public ActivityTestRule<Editor> activityRule =
            new ActivityTestRule<>(Editor.class, false, false);

    @Before
    public void before(){
        Intent i = new Intent();
        String path = "android.resource://com.vitaya.blurimage/" + R.raw.white;
        assertNotNull(path);
        i.putExtra("requestCode", PICK_IMAGE);
        i.putExtra("fileUri", path);
        activityRule.launchActivity(i);
        onView(withId(R.id.freeform)).perform(click());
        onView(withId(R.id.single_color)).perform(click());
        onView(withId(R.id.black)).perform(click());
    }

    private void testFigure(int expectedId, List<Point> arr) {
        Deque<Point> deque = new ArrayDeque<>(arr);
        Point tmp;
        for (int i = 0; i < arr.size(); i++) {
            onView(withId(R.id.imageView)).perform(touchDownAndUp(expectedId, deque))
                    .check(matches(withDrawable(expectedId)));
            onView(withId(R.id.undo)).perform(click());
            tmp = deque.getFirst();
            deque.removeFirst();
            deque.addLast(tmp);
        }
    }

    @Test
    public void squareTest() {
        //координаты в системе координа итогового изображения
        List<Point> arr = new ArrayList<>();
        arr.add(new Point(64, 64));
        arr.add(new Point(64, 192));
        arr.add(new Point(192, 192));
        arr.add(new Point(192, 64));
        testFigure(R.raw.square, arr);
    }

    @Test
    public void triangleTest() {
        List<Point> arr = new ArrayList<>();
        arr.add(new Point(128, 64));
        arr.add(new Point(64, 192));
        arr.add(new Point(192, 192));
        testFigure(R.raw.triangle, arr);
    }

    @Test
    public void starTest() {
        List<Point> arr = new ArrayList<>();
        arr.add(new Point(64, 64));
        arr.add(new Point(192, 64));
        arr.add(new Point(64, 192));
        arr.add(new Point(128, 32));
        arr.add(new Point(192, 192));
        testFigure(R.raw.star, arr);
    }
}
