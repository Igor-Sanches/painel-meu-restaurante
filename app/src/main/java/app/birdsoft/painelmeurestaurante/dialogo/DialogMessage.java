package app.birdsoft.painelmeurestaurante.dialogo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;

public class DialogMessage extends AlertDialog {
    private final String msg;
    private final boolean isButtonPositive;
    private OnPossiveButtonClicked onPossiveButtonClicked;
    private final String nomeButtomPositive;
    private String titulo = null;

    public interface OnPossiveButtonClicked{
        void onClicked();
    }

    public void setOnPossiveButtonClicked(OnPossiveButtonClicked onPossiveButtonClicked) {
        this.onPossiveButtonClicked = onPossiveButtonClicked;
    }

    public DialogMessage(@NonNull Activity context, String msg, boolean isButtonPositive, String nomeButtomPositive) {
        super(context);
        this.msg=msg;
        this.isButtonPositive=isButtonPositive;
        this.nomeButtomPositive=nomeButtomPositive;
    }

    public DialogMessage(@NonNull Activity context, String msg, boolean isButtonPositive, String nomeButtomPositive, String titulo) {
        super(context);
        this.msg=msg;
        this.titulo = titulo;
        this.isButtonPositive=isButtonPositive;
        this.nomeButtomPositive=nomeButtomPositive;
    }

    @Override
    public void show() {
        if(!isShowing()) {
            super.show();
            if(titulo != null){
                ((TextView) Objects.requireNonNull((View) findViewById(R.id.titulo))).setText(titulo);
            }
            Button positive = findViewById(R.id.confirmar);
            Objects.requireNonNull((View) findViewById(R.id.btn_positivo)).setVisibility(isButtonPositive ? View.VISIBLE : View.GONE);
            assert positive != null;
            positive.setText(nomeButtomPositive);
            ((TextView) Objects.requireNonNull((View) findViewById(R.id.messageDialog))).setText(msg);
            Objects.requireNonNull((View) findViewById(R.id.cancel)).setOnClickListener((v -> dismiss()));
            positive.setOnClickListener((v -> {
                dismiss();
                onPossiveButtonClicked.onClicked();
            }));

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_message);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
