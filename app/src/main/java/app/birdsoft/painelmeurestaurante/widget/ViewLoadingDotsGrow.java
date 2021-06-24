package app.birdsoft.painelmeurestaurante.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;

public class ViewLoadingDotsGrow extends LinearLayout {
    private ObjectAnimator[] animator;
    private final GradientDrawable circle = new GradientDrawable();
    private Context context;
    private ImageView[] img;
    boolean onLayoutReach = false;

    public ViewLoadingDotsGrow(Context context2) {
        super(context2);
    }

    public ViewLoadingDotsGrow(Context context2, @Nullable AttributeSet attributeSet) {
        super(context2, attributeSet);
        this.context = context2;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new LayoutParams(-1, -1));
        initView();
    }

    public ViewLoadingDotsGrow(Context context2, @Nullable AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
    }

    private void initView() {
        Drawable background = getBackground();
        int color = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : -7829368;
        setBackgroundColor(0);
        removeAllViews();
        this.img = new ImageView[3];
        this.circle.setShape(GradientDrawable.OVAL);
        this.circle.setColor(color);
        this.circle.setSize(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        LayoutParams layoutParams = new LayoutParams(0, -1);
        layoutParams.setMargins(10,0,10,0);
        layoutParams.weight = 1.0f;
        LinearLayout[] linearLayoutArr = new LinearLayout[3];
        for (int i = 0; i < 3; i++) {
            linearLayoutArr[i] = new LinearLayout(this.context);
            linearLayoutArr[i].setGravity(Gravity.CENTER);
            linearLayoutArr[i].setLayoutParams(layoutParams);
            this.img[i] = new ImageView(this.context);
            this.img[i].setBackgroundDrawable(this.circle);
            linearLayoutArr[i].addView(this.img[i]);
            addView(linearLayoutArr[i]);
        }
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!this.onLayoutReach) {
            this.onLayoutReach = true;
            LayoutParams layoutParams = new LayoutParams(getWidth() / 6, getWidth() / 6);
            for (int i5 = 0; i5 < 3; i5++) {
                this.img[i5].setLayoutParams(layoutParams);
            }
            animateView();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try{
            for (int i = 0; i < 3; i++) {
                if (this.animator[i].isRunning()) {
                    this.animator[i].removeAllListeners();
                    this.animator[i].end();
                    this.animator[i].cancel();
                }
            }
        }catch (Exception ignored){

        }
    }

    public void animateView() {
        this.animator = new ObjectAnimator[3];
        for (int i = 0; i < 3; i++) {
            PropertyValuesHolder ofFloat = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f);
            PropertyValuesHolder ofFloat2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f);
            this.animator[i] = ObjectAnimator.ofPropertyValuesHolder(this.img[i], ofFloat, ofFloat2);
            this.animator[i].setRepeatCount(1);
            this.animator[i].setRepeatMode(ValueAnimator.REVERSE);
            this.animator[i].setDuration(300);
            this.animator[i].setStartDelay(i * 300);
            this.animator[i].start();
        }
        this.animator[2].addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                ViewLoadingDotsGrow.this.animateView();
            }
        });
    }
}
