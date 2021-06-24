package app.birdsoft.painelmeurestaurante.dialogo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class DialogSair extends AlertDialog {
    private final Activity activity;
    public DialogSair(@NonNull Activity context) {
        super(context);
        this.activity=context;
    }

    @Override
    public void show() {
        super.show();
        Objects.requireNonNull((View) findViewById(R.id.cancel)).setOnClickListener((v -> dismiss()));
        Objects.requireNonNull((View) findViewById(R.id.confirmar)).setOnClickListener((v -> {
            dismiss();
            Conexao.getFirebaseAuth().signOut();
            Settings.delete(activity);
            MyToast.makeText(activity, R.string.sair_msg_sucesso).show();
            activity.finishAffinity();
        }));

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_sair);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
