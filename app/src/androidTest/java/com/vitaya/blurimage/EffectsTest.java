package com.vitaya.blurimage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.vitaya.blurimage.config.EspressoTestActions.clickSeekBar;
import static com.vitaya.blurimage.config.EspressoTestActions.clickXY;
import static com.vitaya.blurimage.config.EspressoTestsMatchers.hasDrawable;
import static com.vitaya.blurimage.config.EspressoTestsMatchers.withDrawable;

import com.vitaya.blurimage.config.DrawableMatcher;

import org.junit.Test;

public class EffectsTest extends MasterTest {

    @Test
    public void based() {
        onView(withId(R.id.imageView)).check(matches(hasDrawable()));
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.rgb)));
    }

    private static void runToMatrix() {
        DrawableMatcher matcher = new DrawableMatcher(R.raw.rgb);
        onView(withId(R.id.imageView)).check(matches(matcher));
        onView(withId(R.id.seekBar)).perform(clickSeekBar(100));
        int y, x;
        for (y = 100; y <= matcher.getHeight(); y += 196) {
            for (x = 100; x <= matcher.getWeight(); x += 196)
                onView(withId(R.id.imageView)).perform(clickXY(x, y));
            onView(withId(R.id.imageView)).perform(clickXY(x, y));
        }
    }
    public static void blackAndWhite()
    {
        onView(withId(R.id.black_and_white)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.grey)));
    }

    @Test
    public void blackAndWhiteTest() {
        blackAndWhite();
    }

    @Test
    public void blurTest() {
        onView(withId(R.id.gaussian_blur)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.blur)));
    }

    @Test
    public void blackTest() {
        onView(withId(R.id.black)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.black)));
    }

    @Test
    public void redTest() {
        onView(withId(R.id.red)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.red)));
    }

    @Test
    public void greenTest() {
        onView(withId(R.id.green)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.green)));
    }

    @Test
    public void blueTest() {
        onView(withId(R.id.blue)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.blue)));
    }

    @Test
    public void whiteTest() {
        onView(withId(R.id.white)).perform(click());
        runToMatrix();
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.white)));
    }

}