package com.vitaya.blurimage;

import static com.vitaya.blurimage.MainActivity.PICK_IMAGE;
import static org.junit.Assert.assertNotNull;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;

public abstract class MasterTest {
    @Rule
    public ActivityTestRule<Editor> activityRule =
            new ActivityTestRule<>(Editor.class, false, false);

    @Before
    public void before() throws InterruptedException {
        Intent i = new Intent();
        String path = "android.resource://com.vitaya.blurimage/" + R.raw.rgb;
        assertNotNull(path);
        i.putExtra("requestCode", PICK_IMAGE);
        i.putExtra("fileUri", path);
        activityRule.launchActivity(i);
    }
}
