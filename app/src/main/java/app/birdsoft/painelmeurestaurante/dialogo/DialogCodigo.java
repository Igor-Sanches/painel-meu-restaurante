package app.birdsoft.painelmeurestaurante.dialogo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;

public class DialogCodigo extends AlertDialog {
    private final Bitmap bitmap;
    public DialogCodigo(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.bitmap=bitmap;
    }

    @Override
    public void show() {
        super.show();
        ((ImageView) Objects.requireNonNull((View) findViewById(R.id.image_codigo))).setImageBitmap(bitmap);
        Objects.requireNonNull((View) findViewById(R.id.cancel)).setOnClickListener((v -> dismiss()));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_qr_code);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
