package com.vitaya.blurimage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.vitaya.blurimage.EffectsTest.blackAndWhite;
import static com.vitaya.blurimage.config.EspressoTestActions.clickSeekBar;
import static com.vitaya.blurimage.config.EspressoTestsMatchers.withDrawable;
import static com.vitaya.blurimage.config.EspressoTestsMatchers.withProgress;

import org.junit.Test;

public class ButtonsTest extends MasterTest {

    @Test
    public void colorBoardTest() {
        onView(withId(R.id.black)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.red)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.yellow)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.green)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.blue)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.purple)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.white)).perform(click()).check(matches(isChecked()));
    }

    @Test
    public void seekTest() {
        for (int i = 5; i <= 100; i += 10)
            onView(withId(R.id.seekBar)).perform(clickSeekBar(i)).check(matches(withProgress(i)));
    }

    private void checkHiding(boolean colorBoard, boolean seekBar) {
        if (colorBoard)
            onView(withId(R.id.colors)).check(matches(isDisplayed()));
        if (seekBar)
            onView(withId(R.id.seekBar)).check(matches(isDisplayed()));
    }

    @Test
    public void concealmentTest() {
        onView(withId(R.id.finger)).perform(click());

        onView(withId(R.id.gaussian_blur)).perform(click());
        checkHiding(false, true);

        onView(withId(R.id.black_and_white)).perform(click());
        checkHiding(false, true);

        onView(withId(R.id.single_color)).perform(click());
        checkHiding(true, true);

        onView(withId(R.id.freeform)).perform(click());

        onView(withId(R.id.gaussian_blur)).perform(click());
        checkHiding(false, false);

        onView(withId(R.id.black_and_white)).perform(click());
        checkHiding(false, false);

        onView(withId(R.id.single_color)).perform(click());
        checkHiding(true, false);
    }

    @Test
    public void rollbackButtonTest() {
        blackAndWhite();
        for (int i = 0; i < 25; i++)
            onView(withId(R.id.undo)).perform(click());
        onView(withId(R.id.imageView)).check(matches(withDrawable(R.raw.rgb)));
    }

    @Test
    public void deleteTest() {
        onView(withId(R.id.gallery)).check(doesNotExist());
        onView(withId((R.id.delete))).perform(click());
        onView(withId(R.id.gallery)).check(matches(isEnabled()));
    }
}
