package com.read.scriptures.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class PicassoBlurTransformation implements Transformation {
    RenderScript renderScript;

    public PicassoBlurTransformation(Context context) {
        super();
        renderScript = RenderScript.create(context);
    }

//    @Override
//    public Bitmap transform(Bitmap bitmap) {
//        Bitmap newBitmap = StackBlur.blurNativelyPixels(bitmap, 20, false);
//        bitmap.recycle();
//        return newBitmap;
//    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap newBitmap = rsBlur(bitmap, 25);
        return newBitmap;
    }


    private Bitmap rsBlur(Bitmap source, int radius) {
        Bitmap inputBmp = source;
        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.forEach(output);
        output.copyTo(inputBmp);
        renderScript.destroy();
        return inputBmp;
    }

    @Override
    public String key() {
        return "blur";
    }
}