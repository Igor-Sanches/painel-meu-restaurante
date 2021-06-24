package app.birdsoft.painelmeurestaurante.dialogo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;

public class DialogInput extends AlertDialog {
    private final View view;
    private final String titulo;
    public DialogInput(@NonNull Context context, View view, String titulo) {
        super(context);
        this.titulo=titulo;
        this.view=view;
    }

    @Override
    public void show() {
        super.show();
        ((TextView) Objects.requireNonNull((View) findViewById(R.id.titulo))).setText(titulo);
        ((LinearLayout) Objects.requireNonNull((View) findViewById(R.id.containers))).addView(view);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_input);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
