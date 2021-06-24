package app.birdsoft.painelmeurestaurante.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent; 
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class MyViewPager extends ViewPager {
    public MyViewPager(@NonNull Context context) {
        super(context);
        setMyScroller();
    }

    private void setMyScroller() {
        try{
            Class<?> viewPegar = ViewPager.class;
            Field scroller = viewPegar.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        }catch (Exception x){
            x.printStackTrace();
        }
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public static class MyScroller extends Scroller{

        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350);
        }
    }
}
