package com.student.dzeus.derro.DataBindingAdapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.CircularProgressDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class AdapterBinding {

    @BindingAdapter("loadImage")
    public static void loadImage(ImageView imageView,String url){
        CircularProgressDrawable drawable = new CircularProgressDrawable(imageView.getContext());
        drawable.setStrokeWidth(5f);
        drawable.setCenterRadius(25);
        drawable.start();

        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(drawable))
                .into(imageView);
    }

}
