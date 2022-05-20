package com.vitaya.blurimage.config;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

// code from https://github.com/dbottillo/Blog
public class DrawableMatcher extends TypeSafeMatcher<View> {

    private final int expectedId;
    private String resourceName;
    static final int EMPTY = -1;
    static final int ANY = -2;
    private int weight;
    private int height;

    public DrawableMatcher(int expectedId) {
        super(View.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)) {
            return false;
        }
        ImageView imageView = (ImageView) target;
        weight = imageView.getWidth();
        height = imageView.getHeight();
        if (expectedId == EMPTY) {
            return imageView.getDrawable() == null;
        }
        if (expectedId == ANY) {
            return imageView.getDrawable() != null;
        }
        Resources resources = target.getContext().getResources();
        BitmapDrawable bd = (BitmapDrawable) resources.getDrawable(expectedId);
        resourceName = resources.getResourceEntryName(expectedId);
        if (bd == null) {
            return false;
        }
        Bitmap otherBitmap = bd.getBitmap();
        Bitmap bitmap = getBitmap(imageView.getDrawable());
        Color a, b;
        //sameAs не работает т.к. допустима погрешность
        for (int i = 0; i < bitmap.getWidth(); i++)
            for (int j = 0; j < bitmap.getHeight(); j++) {
                a = bitmap.getColor(i, j);
                b = otherBitmap.getColor(i, j);
                if(nonEqualStreamColor(a.alpha(), b.alpha()) ||
                        nonEqualStreamColor(a.red(), b.red())||
                        nonEqualStreamColor(a.green(), b.green())||
                        nonEqualStreamColor(a.blue(), b.blue())
                )
                return false;
            }
        return true;
    }

    private boolean nonEqualStreamColor(double a, double b) {
        return ! (Math.abs(a - b) < 1e-3);
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(expectedId);
        if (resourceName != null) {
            description.appendText("[");
            description.appendText(resourceName);
            description.appendText("]");
        }
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }
}