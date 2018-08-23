package com.htetznaing.vivomyanmarfonts.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextToImage {
    public Bitmap textAsBitmap(String text, float textSize, float stroke,
                               int color, Typeface typeface) {

        TextPaint paint = new TextPaint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(stroke);
        paint.setTypeface(typeface);

        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);

        StaticLayout staticLayout = new StaticLayout(text, 0, text.length(),
                paint, 435, Layout.Alignment.ALIGN_CENTER, 1.0f,
                1.0f, false);

        int height = 70;

        Bitmap image = Bitmap.createBitmap(435, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);

        staticLayout.draw(canvas);

        return image;

    }


    public static Bitmap textAsBitmap(String text, int fontSize,Typeface typeface){
        int paddingRight = 25;
        int paddingLeft = 10;

        //Paint config
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setTypeface(typeface);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        paint.setAntiAlias(true);
        Bitmap largeWhiteBitmap = Bitmap.createBitmap((int) paint.measureText(text) + paddingRight, fontSize + paddingRight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(largeWhiteBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawText(text, paddingLeft, fontSize+3, paint);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        largeWhiteBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return largeWhiteBitmap;
    }

        public boolean saveImage(Bitmap imageData, String filename) {
            File sdIconStorageDir = new File(filename);
            try {
                String filePath = sdIconStorageDir.toString();
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                BufferedOutputStream bos = new BufferedOutputStream(
                        fileOutputStream);
                imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bos.flush();
                bos.close();

            } catch (FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }

    public Bitmap thumb(String text, float textSize, float stroke, int color, Typeface typeface) {
        TextPaint paint = new TextPaint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(stroke);
        paint.setTypeface(typeface);

        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);

        StaticLayout staticLayout = new StaticLayout(text, 0, text.length(),
                paint, 435, Layout.Alignment.ALIGN_CENTER, 1.0f,
                1.0f, false);

        int height = 500;

        Bitmap image = Bitmap.createBitmap(435, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);

        staticLayout.draw(canvas);
        return image;
    }
}