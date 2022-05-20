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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FreeformTest {
    @Rule
    public ActivityTestRule<Editor> activityRule =
            new ActivityTestRule<>(Editor.class, false, false);

    @Before
    public void before() throws InterruptedException {
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


    @Test
    public void squareTest() {
        //координаты в системе координа итогового изображения
        Point p1 = new Point(64, 64);
        Point p2 = new Point(64, 192);
        Point p3 = new Point(192, 192);
        Point p4 = new Point(192, 64);

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p1, p2, p3, p4))
                .check(matches(withDrawable(R.raw.square)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p2, p3, p4, p1))
                .check(matches(withDrawable(R.raw.square)));
        ;
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p3, p4, p1, p2))
                .check(matches(withDrawable(R.raw.square)));
        ;
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p4, p1, p2, p3))
                .check(matches(withDrawable(R.raw.square)));
        ;
        onView(withId(R.id.undo)).perform(click());
    }

    @Test
    public void triangleTest() {
        Point p1 = new Point(128, 64);
        Point p2 = new Point(64, 192);
        Point p3 = new Point(192, 192);

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p1, p2, p3))
                .check(matches(withDrawable(R.raw.triangle)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p2, p3, p1))
                .check(matches(withDrawable(R.raw.triangle)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p3, p1, p2))
                .check(matches(withDrawable(R.raw.triangle)));
        onView(withId(R.id.undo)).perform(click());
    }

    @Test
    public void starTest() {
        Point p1 = new Point(64, 64);
        Point p2 = new Point(192, 64);
        Point p3 = new Point(64, 192);
        Point p4 = new Point(128, 32);
        Point p5 = new Point(192, 192);

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p1, p2, p3, p4, p5))
                .check(matches(withDrawable(R.raw.star)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p2, p3, p4, p5, p1))
                .check(matches(withDrawable(R.raw.star)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p3, p4, p5, p1, p2))
                .check(matches(withDrawable(R.raw.star)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p4, p5, p1, p2, p3))
                .check(matches(withDrawable(R.raw.star)));
        onView(withId(R.id.undo)).perform(click());

        onView(withId(R.id.imageView)).perform(touchDownAndUp(R.raw.square, p5, p1, p2, p3, p4))
                .check(matches(withDrawable(R.raw.star)));
        onView(withId(R.id.undo)).perform(click());

    }
}
