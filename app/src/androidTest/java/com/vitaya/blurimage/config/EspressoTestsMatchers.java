package com.vitaya.blurimage.config;

import android.view.View;

import org.hamcrest.Matcher;
// code from https://github.com/dbottillo/Blog
public class EspressoTestsMatchers {

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(DrawableMatcher.EMPTY);
    }

    public static Matcher<View> hasDrawable() {
        return new DrawableMatcher(DrawableMatcher.ANY);
    }

    public static Matcher<View> withProgress(final int value) {
        return new SeekBarMatcher(value);
    }
}
