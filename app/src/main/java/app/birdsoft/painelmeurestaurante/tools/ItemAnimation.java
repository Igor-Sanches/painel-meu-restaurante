package app.birdsoft.painelmeurestaurante.tools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class ItemAnimation {
    private static final long DURATION_IN_FADE_ID = 500;

    public static void animate(View view, int i, int i2) {
        switch (i2) {
            case 1:
                animateBottomUp(view, i);
                return;
            case 2:
                animateFadeIn(view, i);
                return;
            case 3:
                animateLeftRight(view, i);
                return;
            case 4:
                animateRightLeft(view, i);
                return;
            default:
        }
    }

    private static void animateBottomUp(View view, int i) {
        boolean z = i == -1;
        int i2 = i + 1;
        float f = 800.0f;
        view.setTranslationY(z ? 800.0f : 500.0f);
        view.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        String str = "translationY";
        float[] fArr = new float[2];
        if (!z) {
            f = 500.0f;
        }
        fArr[0] = f;
        fArr[1] = 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, str, fArr);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "alpha", 1.0f);
        ofFloat.setStartDelay(z ? 0 : ((long) i2) * 150);
        ofFloat.setDuration(((long) (z ? 3 : 1)) * 150);
        animatorSet.playTogether(ofFloat, ofFloat2);
        animatorSet.start();
    }

    private static void animateFadeIn(View view, int i) {
        long j;
        boolean z = i == -1;
        int i2 = i + 1;
        view.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.5f, 1.0f);
        ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f}).start();
        if (z) {
            j = 250;
        } else {
            j = (((long) i2) * DURATION_IN_FADE_ID) / 3;
        }
        ofFloat.setStartDelay(j);
        ofFloat.setDuration(DURATION_IN_FADE_ID);
        animatorSet.play(ofFloat);
        animatorSet.start();
    }

    private static void animateLeftRight(View view, int i) {
        boolean z = i == -1;
        int i2 = i + 1;
        view.setTranslationX(-400.0f);
        view.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "translationX", -400.0f, 0.0f);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "alpha", 1.0f);
        ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f}).start();
        ofFloat.setStartDelay(z ? 150 : ((long) i2) * 150);
        ofFloat.setDuration(((long) (z ? 2 : 1)) * 150);
        animatorSet.playTogether(ofFloat, ofFloat2);
        animatorSet.start();
    }

    private static void animateRightLeft(View view, int i) {
        boolean z = i == -1;
        int i2 = i + 1;
        view.setTranslationX(view.getX() + 400.0f);
        view.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "translationX", view.getX() + 400.0f, 0.0f);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "alpha", 1.0f);
        ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f}).start();
        ofFloat.setStartDelay(z ? 150 : ((long) i2) * 150);
        ofFloat.setDuration(((long) (z ? 2 : 1)) * 150);
        animatorSet.playTogether(ofFloat, ofFloat2);
        animatorSet.start();
    }
}
