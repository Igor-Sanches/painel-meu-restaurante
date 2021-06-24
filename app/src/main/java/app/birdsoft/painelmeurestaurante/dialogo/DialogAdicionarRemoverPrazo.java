package app.birdsoft.painelmeurestaurante.dialogo;

import android.app.Activity;
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

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.service.NotificationSendData;
import app.birdsoft.painelmeurestaurante.service.NotificationType;
import app.birdsoft.painelmeurestaurante.service.SendNotification;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.tools.Status;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class DialogAdicionarRemoverPrazo extends AlertDialog {

    private int prazo;
    private Activity activity;
    private OnSetPrazoListener onSetPrazoListener;
    public interface OnSetPrazoListener{
        void onSetPrazo(int min, int atualMin);
    }

    public void setOnSetPrazoListener(OnSetPrazoListener onSetPrazoListener) {
        this.onSetPrazoListener = onSetPrazoListener;
    }

    public DialogAdicionarRemoverPrazo(@NonNull Activity activity, int prazo) {
        super(activity);
        this.prazo = prazo;
        this.activity = activity;
    }

    @Override
    public void show() {
        super.show();
        TextInputLayout inputLayout = findViewById(R.id.input_prazo);
        Objects.requireNonNull(inputLayout.getEditText()).setText(String.valueOf(prazo));
        int atualPrazo = prazo;
        Objects.requireNonNull((View) findViewById(R.id.cancel)).setOnClickListener((v -> dismiss()));
        Objects.requireNonNull((View) findViewById(R.id.confirmar)).setOnClickListener((v -> {
            String _prazoMinFixo = inputLayout.getEditText().getText().toString().trim();
            if(!_prazoMinFixo.equals("")) {
                int min = Integer.parseInt(_prazoMinFixo);
                if (min > 0) {
                    if(min != atualPrazo){
                        dismiss();
                        onSetPrazoListener.onSetPrazo(min, atualPrazo);
                    }else{
                        dismiss();
                        MySnackbar.makeText(activity, R.string.prazo_atualizado, ModoColor._success).show();
                    }
                }else{
                    MyToast.makeText(activity, R.string.error_digite_prazo_min_minimo, ModoColor._falha).show();
                }
            }else{
                MyToast.makeText(activity, R.string.error_digite_prazo_minuto, ModoColor._falha).show();
            }
        }));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prazo);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
