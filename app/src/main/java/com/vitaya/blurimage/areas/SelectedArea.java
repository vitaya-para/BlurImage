package com.vitaya.blurimage.areas;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public abstract class SelectedArea {

    protected int selectedMethod;
    protected Mat matImage;
    protected ImageView img;
    protected Stack<Mat> history;
    protected Scalar color;

    SelectedArea(@NonNull Bitmap imageBitmap, ImageView img) {
        matImage = new Mat(imageBitmap.getHeight(), imageBitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(imageBitmap, matImage);
        this.img = img;
        this.img.setImageBitmap(imageBitmap);
        selectedMethod = 0;
        color = new Scalar(0.0, 0.0, 255.0, 255.0);
        history = new Stack<>();
    }

    public SelectedArea(SelectedArea a) {
        matImage = a.matImage;
        img = a.img;
        selectedMethod = a.selectedMethod;
        color = a.color;
        history = a.history;
    }

    public abstract void setTouchEvent();

    private void gaussianTouchEvent(Rect r, Mat mat) {
        Mat imgCrop = mat.submat(r);
        Imgproc.GaussianBlur(imgCrop, imgCrop, new Size(125, 125), 0);
        Mat tmp = mat.submat(r);
        imgCrop.copyTo(tmp);
    }

    private void blackAndWhiteTouchEvent(Rect r, Mat mat) {
        Mat imgCrop = mat.submat(r);
        double[] a;
        for (int i = 0; i < imgCrop.rows(); i++)
            for (int j = 0; j < imgCrop.cols(); j++) {
                a = imgCrop.get(i, j);
                //https://en.wikipedia.org/wiki/Grayscale HDTV standart
                a[0] = a[1] = a[2] = 0.2126 * a[0] + 0.7152 * a[1] + 0.0722 * a[2];
                imgCrop.put(i, j, a);
            }
    }

    private void colorTouchEvent(Rect r, Mat mat) {
        Imgproc.rectangle(mat, r, color, -1);
    }

    protected void paintEvent(Rect r, Mat mat) {
        r.width = Math.min(r.x + r.width, mat.cols()) - r.x;
        r.height = Math.min(r.y + r.height, mat.rows()) - r.y;
        r.x = Math.max(r.x, 0);
        r.y = Math.max(r.y, 0);
        if (r.height == 0 || r.width == 0)
            return;
        switch (selectedMethod) {
            case 0:
                gaussianTouchEvent(r, mat);
                break;
            case 1:
                blackAndWhiteTouchEvent(r, mat);
                break;
            case 2:
                colorTouchEvent(r, mat);
                break;
        }
    }

    protected void update() {
        Bitmap imageBitmap = Bitmap.createBitmap(matImage.cols(), matImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matImage, imageBitmap);
        img.setImageBitmap(imageBitmap);
    }

    public void setSelectedMethod(int a) {
        selectedMethod = a;
    }

    public void rollback() {
        if (!history.empty()) {
            matImage = history.pop();
            update();
        }
    }

    public void saveFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".png";

        Bitmap imageBitmap = Bitmap.createBitmap(matImage.cols(), matImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matImage, imageBitmap);
        File file = Environment.getExternalStorageDirectory();

        File dir = new File(file.getAbsolutePath() + "/Pictures/BlurImage");
        dir.mkdirs();

        File outFile = new File(dir, imageFileName);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(outFile);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap share() {
        Bitmap imageBitmap = Bitmap.createBitmap(matImage.cols(), matImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matImage, imageBitmap);
        return imageBitmap;
    }

    public void setColor(Scalar color) {
        this.color = color;
    }
}
