package app.birdsoft.painelmeurestaurante.dialogo;

import android.app.Dialog;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import com.facebook.appevents.AppEventsConstants;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.tools.Mask;

public class CurrencyAmountKeyboard {
    private final LinearLayout bt0;
    private final LinearLayout bt1;
    private final LinearLayout bt2;
    private final LinearLayout bt3;
    private final LinearLayout bt4;
    private final LinearLayout bt5;
    private final LinearLayout bt6;
    private final LinearLayout bt7;
    private final LinearLayout bt8;
    private final LinearLayout bt9;
    private final ImageButton btBackspace;
    private final ImageButton btClear;
    private boolean firstType = false;
    private Double receivedAmount;
    private final TextView tvReceivedAmount;

    public CurrencyAmountKeyboard(Dialog dialog) {
        bt0 = dialog.findViewById(R.id.bt0);
        bt1 = dialog.findViewById(R.id.bt1);
        bt2 = dialog.findViewById(R.id.bt2);
        bt3 = dialog.findViewById(R.id.bt3);
        bt4 = dialog.findViewById(R.id.bt4);
        bt5 = dialog.findViewById(R.id.bt5);
        bt6 = dialog.findViewById(R.id.bt6);
        bt7 = dialog.findViewById(R.id.bt7);
        bt8 = dialog.findViewById(R.id.bt8);
        bt9 = dialog.findViewById(R.id.bt9);
        tvReceivedAmount = dialog.findViewById(R.id.tvReceivedAmount);
        btClear = dialog.findViewById(R.id.btClear);
        btBackspace = dialog.findViewById(R.id.btBackspace);
        initializeKeyboardEvents();
    }

    private void initializeKeyboardEvents() {
        bt0.setOnClickListener(view -> setRecievedAmount(AppEventsConstants.EVENT_PARAM_VALUE_NO));
        bt1.setOnClickListener(view -> setRecievedAmount(AppEventsConstants.EVENT_PARAM_VALUE_YES));
        bt2.setOnClickListener(view -> setRecievedAmount(ExifInterface.GPS_MEASUREMENT_2D));
        bt3.setOnClickListener(view -> setRecievedAmount(ExifInterface.GPS_MEASUREMENT_3D));
        bt4.setOnClickListener(view -> setRecievedAmount("4"));
        bt5.setOnClickListener(view -> setRecievedAmount("5"));
        bt6.setOnClickListener(view -> setRecievedAmount("6"));
        bt7.setOnClickListener(view -> setRecievedAmount("7"));
        bt8.setOnClickListener(view -> setRecievedAmount("8"));
        bt9.setOnClickListener(view -> setRecievedAmount("9"));
        btClear.setOnClickListener(view -> clearRecievedValue());
        btBackspace.setOnClickListener(view -> backspaceReceivedValue());
        btBackspace.setOnLongClickListener(view -> {
            CurrencyAmountKeyboard.this.clearRecievedValue();
            return true;
        });
    }

    public void setTvReceivedAmount(double d) {
        tvReceivedAmount.setText(Mask.formatarValor(d));
        receivedAmount = d;
    }

    public void setRecievedAmount(String str) {
        String str2;
        if (this.firstType) {
            this.tvReceivedAmount.setText(Mask.formatarValor(0.0d));
            this.receivedAmount = 0.0d;
            this.firstType = false;
        }
        Long valueOf = Long.valueOf(Mask.unmaskKeyboard(this.tvReceivedAmount.getText().toString()));
        if (String.valueOf(valueOf).length() >= 8) {
            Long valueOf2 = 99999999L;
            str2 = valueOf2 +
                    "";
        } else {
            str2 = valueOf +
                    str;
        }
        this.receivedAmount = Double.parseDouble(str2) / 100.0d;
        this.tvReceivedAmount.setText(Mask.formatarValor(this.receivedAmount));
    }

    /* access modifiers changed from: private */
    public void clearRecievedValue() {
        if (this.firstType) {
            this.firstType = false;
        }
        this.receivedAmount = 0.0d;
        this.tvReceivedAmount.setText(Mask.formatarValor(this.receivedAmount));
    }

    public void backspaceReceivedValue() {
        boolean z = this.firstType;
        Double valueOf = 0.0d;
        if (z) {
            this.firstType = false;
            this.tvReceivedAmount.setText(Mask.formatarValor(valueOf));
            this.receivedAmount = valueOf;
        }
        Long valueOf2 = Long.valueOf(Mask.unmaskKeyboard(this.tvReceivedAmount.getText().toString()));
        if (valueOf2 != 0) {
            String sb = new StringBuilder(String.valueOf(valueOf2)).deleteCharAt(String.valueOf(valueOf2).length() - 1).toString();
            if (sb.equals("")) {
                this.receivedAmount = valueOf;
            } else {
                this.receivedAmount = Double.parseDouble(sb) / 100.0d;
            }
            this.tvReceivedAmount.setText(Mask.formatarValor(this.receivedAmount));
        }
    }

    public void show() {
        Double valueOf = 0.0d;
        this.tvReceivedAmount.setText(Mask.formatarValor(valueOf));
        this.firstType = true;
        this.receivedAmount = valueOf;
    }

    public String getValorString(){
        return Mask.formatarValor(receivedAmount);
    }

    public Double getReceivedAmount() {
        return this.receivedAmount;
    }
}
