package com.read.scriptures.util;

import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class PicassoUtils {

    public static void loadImage(ImageView imageView, String url, int placeholder) {
        if (TextUtils.isEmpty(url)) {
            Picasso.get().load(placeholder).placeholder(placeholder).error(placeholder).noFade().into(imageView);
        } else {
            Picasso.get().load(url).placeholder(placeholder).error(placeholder).noFade().into(imageView);
        }
    }

    public static void loadImage(ImageView imageView, String url, int placeholder, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            Picasso.get().load(placeholder).placeholder(placeholder).error(placeholder).resize(width, height).noFade().into(imageView);
        } else {
            Picasso.get().load(url).placeholder(placeholder).error(placeholder).noFade().resize(width, height).into(imageView);
        }
    }

    public static void loadImage(ImageView imageView, int placeholder, int width, int height) {
        Picasso.get().load(placeholder).placeholder(placeholder).error(placeholder).resize(width, height).noFade().into(imageView);
    }

    public static void loadBannerImage(ImageView imageView, String url, int placeholder, int width, int height) {
        Picasso.get().load(url).error(placeholder).resize(width, height).noFade().into(imageView);
    }


    public static void loadImage(ImageView imageView, String url, int placeholder, Transformation transformation, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            Picasso.get().load(placeholder).placeholder(placeholder).transform(transformation).error(placeholder).noFade().resize(width, height).into(imageView);
        } else {
            Picasso.get().load(url).placeholder(placeholder).transform(transformation).error(placeholder).noFade().resize(width, height).into(imageView);
        }
    }

}
