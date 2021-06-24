package app.birdsoft.painelmeurestaurante.widget;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;

public class MySnackbar {

    private final View view;
    private ModoColor color;
    private final String msg;

    private MySnackbar(View view, @StringRes int msg, ModoColor color){
        this.view = view;
        this.color = color;
        this.msg = view.getContext().getString(msg);
    }

    public static MySnackbar makeText(View view, @StringRes int msg, ModoColor color){
        return new MySnackbar(view, msg, color);
    }

    private MySnackbar(View view, @StringRes int msg){
        this.view = view;
        this.msg = view.getContext().getString(msg);
    }

    public static MySnackbar makeText(View view, @StringRes int msg){
        return new MySnackbar(view, msg);
    }

    private MySnackbar(View view, String msg){
        this.view = view;
        this.msg = msg;
    }

    public static MySnackbar makeText(View view, String msg){
        return new MySnackbar(view, msg);
    }

    private MySnackbar(View view, String msg, ModoColor color){
        this.view = view;
        this.color = color;
        this.msg = msg;
    }

    public static MySnackbar makeText(View view, String msg, ModoColor color){
        return new MySnackbar(view, msg, color);
    }

    private MySnackbar(Activity activity, @StringRes int msg, ModoColor color){
        this.view = view(activity);
        this.color = color;
        this.msg = activity.getString(msg);
    }

    private MySnackbar(Activity activity, String msg, ModoColor color){
        this.view = view(activity);
        this.color = color;
        this.msg = msg;
    }

    public static MySnackbar makeText(Activity activity, @StringRes int msg, ModoColor color){
        return new MySnackbar(activity, msg, color);
    }

    public static MySnackbar makeText(Activity activity, String msg, ModoColor color){
        return new MySnackbar(activity, msg, color);
    }

    private MySnackbar(Activity activity, @StringRes int msg){
        this.view = view(activity);
        this.msg = activity.getString(msg);
    }

    private View view(Activity activity) {
        return activity.findViewById(R.id.layout);//.getWindow().getDecorView();
    }

    public static MySnackbar makeText(Activity activity, @StringRes int msg){
        return new MySnackbar(activity, msg);
    }

    private MySnackbar(Activity activity, String msg){
        this.view = view(activity);
        this.msg = msg;
    }

    public static MySnackbar makeText(Activity activity, String msg){
        return new MySnackbar(activity, msg);
    }

    public void show(){
        int colorTextRes = 0;
        int colorCardRes = 0;
        if(color != null){
            switch (color){
                case _falha:
                    colorTextRes = view.getContext().getResources().getColor(R.color.branco);
                    colorCardRes = view.getContext().getResources().getColor(R.color.vermelhoProgress);
                    break;
                case _success:
                    colorTextRes = view.getContext().getResources().getColor(R.color.branco);
                    colorCardRes = view.getContext().getResources().getColor(R.color.verdeProgress);
                    break;
                case _default:
                    colorTextRes = view.getContext().getResources().getColor(R.color.branco);
                    colorCardRes = view.getContext().getResources().getColor(R.color.colorPrimaryDark);
                    break;
            }
        }else{
            colorTextRes = view.getContext().getResources().getColor(R.color.branco);
            colorCardRes = view.getContext().getResources().getColor(R.color.colorPrimaryDark);
        }

        View root = View.inflate(view.getContext(), R.layout.my_snackbar_layout, null);
        if(root != null){
            CardView card = root.findViewById(R.id.toastCard);
            TextView text = root.findViewById(R.id.toastTextColor);
            card.setCardBackgroundColor(colorCardRes);
            text.setTextColor(colorTextRes);
            text.setText(msg);

            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(colorCardRes);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout)snackbar.getView();
            layout.setPadding(0,0,0,0);
            layout.addView(root, 0);
            snackbar.show();
        }
    }
}
