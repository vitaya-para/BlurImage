package com.vitaya.blurimage.config;

import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class SeekBarMatcher extends TypeSafeMatcher<View> {

    private final int value;
    private int actual;

    public SeekBarMatcher(int value) {
        super(View.class);
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof SeekBar)) {
            return false;
        }
        SeekBar seekBar = (SeekBar) target;
        actual = seekBar.getProgress();
        return seekBar.getProgress() == value;

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Values aren't equal:\nexpected:");
        description.appendValue(value);
        description.appendText("Got:");
        description.appendValue(actual);
    }
}
