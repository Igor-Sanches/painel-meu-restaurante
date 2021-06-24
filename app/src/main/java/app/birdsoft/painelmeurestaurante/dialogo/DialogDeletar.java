package app.birdsoft.painelmeurestaurante.dialogo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;

public class DialogDeletar extends AlertDialog {
    private final Activity activity;
    private OnDeleteConfirmation onDeleteConfirmation;
    private final String nome;
    public interface OnDeleteConfirmation{
        void onDelete(String nome);
    }

    public void setOnDeleteConfirmation(OnDeleteConfirmation onDeleteConfirmation) {
        this.onDeleteConfirmation = onDeleteConfirmation;
    }

    public DialogDeletar(@NonNull Activity context, String nome) {
        super(context);
        this.nome=nome;
        this.activity=context;
    }

    @Override
    public void show() {
        super.show();
        ((TextView) Objects.requireNonNull((View) findViewById(R.id.msg_delete))).setText(String.format("%s: %s", activity.getString(R.string.delete_msg), nome));
        Objects.requireNonNull((View) findViewById(R.id.cancel)).setOnClickListener((v -> dismiss()));
        Objects.requireNonNull((View) findViewById(R.id.confirmar)).setOnClickListener((v -> {
            dismiss();
            onDeleteConfirmation.onDelete(nome);
        }));

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_deletar);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
