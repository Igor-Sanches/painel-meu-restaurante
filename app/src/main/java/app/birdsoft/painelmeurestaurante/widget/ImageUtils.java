package app.birdsoft.painelmeurestaurante.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

public class ImageUtils {

    public static void displayImageFromUrl(Context context, String str, ImageView imageView, Drawable drawable) {
        displayImageFromUrl(context, str, imageView, drawable, null);
    }

    public static void displayImageFromUrl(Context context, String str, ImageView imageView, Drawable drawable, RequestListener<Drawable> requestListener) {
        RequestOptions placeholder = new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(drawable);
        Glide.with(context).load(str).apply(placeholder).listener(requestListener).into(imageView);
    }

}
