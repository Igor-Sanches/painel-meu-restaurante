package app.birdsoft.painelmeurestaurante.tools;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewAnimation {

    public static void expand(View view) {
        view.startAnimation(expandAction(view));
    }

    private static Animation expandAction(final View view) {
        view.measure(-1, -2);
        final int measuredHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        Animation r1 = new Animation() {
            public boolean willChangeBounds() {
                return true;
            }

            public void applyTransformation(float f, Transformation transformation) {
                view.getLayoutParams().height = f == 1.0f ? -2 : (int) (((float) measuredHeight) * f);
                view.requestLayout();
            }

        };
        r1.setDuration((int) (((float) measuredHeight) / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(r1);
        return r1;
    }

    public static void collapse(final View view) {
        final int measuredHeight = view.getMeasuredHeight();
        Animation r1 = new Animation() {
            public boolean willChangeBounds() {
                return true;
            }

            public void applyTransformation(float f, Transformation transformation) {
                if (f == 1.0f) {
                    view.setVisibility(View.GONE);
                    return;
                }
                LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = measuredHeight - ((int) (((float) measuredHeight) * f));
                view.requestLayout();
            }
        };
        r1.setDuration((int) (((float) measuredHeight) / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(r1);
    }

}
