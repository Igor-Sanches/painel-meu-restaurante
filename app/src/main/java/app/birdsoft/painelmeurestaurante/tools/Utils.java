package app.birdsoft.painelmeurestaurante.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.graphics.drawable.DrawableCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

    @Deprecated
    public static void fecharTeclado(Context context, View view) {
        if (view != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 2);
        }
    }

    public static void setBackgroundColor(ImageView imageView, int i) {
        if (VERSION.SDK_INT >= 21) {
            imageView.setBackgroundTintList(ColorStateList.valueOf(i));
            return;
        }
        Drawable wrap = DrawableCompat.wrap(new ColorDrawable(i));
        DrawableCompat.setTint(wrap, i);
        imageView.setBackground(wrap);
    }

    public static void setBackgroundColor(ImageView imageView, RelativeLayout relativeLayout, int i, int i2) {
        if (VERSION.SDK_INT >= 21) {
            imageView.setBackgroundTintList(ColorStateList.valueOf(i));
            relativeLayout.setBackgroundTintList(ColorStateList.valueOf(i2));
            return;
        }
        Drawable wrap = DrawableCompat.wrap(new ColorDrawable(i));
        DrawableCompat.setTint(wrap, i);
        imageView.setBackground(wrap);
        Drawable wrap2 = DrawableCompat.wrap(new ColorDrawable(i2));
        DrawableCompat.setTint(wrap2, i2);
        relativeLayout.setBackground(wrap2);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isPoynt() {
        return "POYNT".equals(Build.MANUFACTURER);
    }

    @SuppressLint("WrongConstant")
    public static void setOrientation(Activity activity) {
        if (isPoynt() || !isTablet(activity)) {
            activity.setRequestedOrientation(1);
        }
    }

    public static boolean isRunningLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static int getWidthFromScreenSize(Integer num, Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        double d = displayMetrics.widthPixels;
        double intValue = num;
        Double.isNaN(intValue);
        double d2 = intValue / 100.0d;
        Double.isNaN(d);
        return (int) (d * d2);
    }

    public static double roundHalfUp(double d, int i) {
        if (i >= 0) {
            return new BigDecimal(d).setScale(i, RoundingMode.HALF_UP).doubleValue();
        }
        throw new IllegalArgumentException();
    }

    public static void toggleArrow(boolean z, View view, boolean z2) {
        long j = 200;
        if (z) {
            ViewPropertyAnimator animate = view.animate();
            if (!z2) {
                j = 0;
            }
            animate.setDuration(j).rotation(180.0f);
            return;
        }
        ViewPropertyAnimator animate2 = view.animate();
        if (!z2) {
            j = 0;
        }
        animate2.setDuration(j).rotation(0.0f);
    }

    public static void toggleArrow(boolean z, View view) {
        toggleArrow(z, view, true);
    }

}
