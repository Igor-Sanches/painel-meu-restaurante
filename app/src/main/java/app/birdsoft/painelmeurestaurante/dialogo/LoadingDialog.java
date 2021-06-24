package app.birdsoft.painelmeurestaurante.dialogo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import app.birdsoft.painelmeurestaurante.R;

public class LoadingDialog extends ProgressDialog {

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        ((TextView)findViewById(R.id.messageDialog)).setText(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
