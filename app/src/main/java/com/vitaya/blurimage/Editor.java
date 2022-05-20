package com.vitaya.blurimage;

import static com.vitaya.blurimage.MainActivity.PICK_IMAGE;
import static com.vitaya.blurimage.MainActivity.REQUEST_IMAGE_CAPTURE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Editor extends Activity {

    private Uri uri;
    private Painter painter;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");
        setContentView(R.layout.editor);
        imageViewSetup();

        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                painter.rollback();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                painter.save();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Saved to /Pictures/BlurImage", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePhoto();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(Editor.this, MainActivity.class);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Deleted!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(home);
            }
        });

        buttonMethodEffect(findViewById(R.id.gaussian_blur));
        buttonMethodEffect(findViewById(R.id.black_and_white));
        buttonMethodEffect(findViewById(R.id.single_color));

        buttonAreaEffect(findViewById(R.id.finger));
        buttonAreaEffect(findViewById(R.id.freeform));

        findViewById(R.id.single_color).getBackground().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.SRC_ATOP);
        findViewById(R.id.finger).getBackground().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.SRC_ATOP);
    }

    private void imageViewSetup() {
        Bundle extras = getIntent().getExtras();
        uri = Uri.parse(extras.getString("fileUri"));
        Bitmap imageBitmap = null;
        if (extras.getInt("requestCode") == PICK_IMAGE) {
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (extras.getInt("requestCode") == REQUEST_IMAGE_CAPTURE) {
            ImageView img = findViewById(R.id.imageView);
            img.setImageURI(uri);
            imageBitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        }
        assert imageBitmap != null;
        painter = new Painter(imageBitmap, findViewById(R.id.imageView));
        setupSeek();
        setupColorboard();
    }

    private void setupColorboard() {
        findViewById(R.id.colors).setVisibility(View.VISIBLE);
        RadioGroup group = (RadioGroup) findViewById(R.id.colors);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.black:
                        painter.setColor(new Scalar(0.0, 0.0, 0.0, 255.0));
                        break;
                    case R.id.red:
                        painter.setColor(new Scalar(255.0, 0.0, 0.0, 255.0));
                        break;
                    case R.id.yellow:
                        painter.setColor(new Scalar(255.0, 255.0, 0.0, 255.0));
                        break;
                    case R.id.green:
                        painter.setColor(new Scalar(0.0, 255.0, 0.0, 255.0));
                        break;
                    case R.id.blue:
                        painter.setColor(new Scalar(0.0, 0.0, 255.0, 255.0));
                        break;
                    case R.id.purple:
                        painter.setColor(new Scalar(128.0, 0.0, 128.0, 255.0));
                        break;
                    case R.id.white:
                        painter.setColor(new Scalar(255.0, 255.0, 255.0, 255.0));
                        break;
                }
            }
        });
    }

    private void setupSeek() {
        findViewById(R.id.seekBar).setVisibility(View.VISIBLE);
        SeekBar seek = (SeekBar) findViewById(R.id.seekBar);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                painter.setSize(seekBar.getProgress());
            }
        });
        seek.setProgress(painter.getSize());
    }

    private void sharePhoto() {
        Uri link = null;
        try {
            File file = createImageFile();
            FileOutputStream stream = new FileOutputStream(file);
            painter.share().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();

            link = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider", //(use your app signature + ".provider" )
                    file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (link != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, link);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, null));
        }
    }

    private void clearBackground(int id) {
        RadioButton button = findViewById(id);
        button.getBackground().clearColorFilter();
        button.invalidate();
    }

    public void buttonMethodEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    switch (v.getId()) {
                        case R.id.gaussian_blur:
                            clearBackground(R.id.black_and_white);
                            clearBackground(R.id.single_color);
                            painter.setSelectedMethod(0);
                            findViewById(R.id.colors).setVisibility(View.GONE);
                            break;
                        case R.id.black_and_white:
                            clearBackground(R.id.gaussian_blur);
                            clearBackground(R.id.single_color);
                            painter.setSelectedMethod(1);
                            findViewById(R.id.colors).setVisibility(View.GONE);
                            break;
                        case R.id.single_color:
                            clearBackground(R.id.gaussian_blur);
                            clearBackground(R.id.black_and_white);
                            painter.setSelectedMethod(2);
                            setupColorboard();
                            break;
                    }
                }
                return true;
            }
        });
    }

    public void buttonAreaEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    switch (v.getId()) {
                        case R.id.finger:
                            clearBackground(R.id.freeform);
                            painter.setSelectedArea(0);
                            setupSeek();
                            break;
                        case R.id.freeform:
                            clearBackground(R.id.finger);
                            findViewById(R.id.seekBar).setVisibility(View.GONE);
                            painter.setSelectedArea(1);
                            break;
                    }
                }
                return true;
            }
        });
    }

}
