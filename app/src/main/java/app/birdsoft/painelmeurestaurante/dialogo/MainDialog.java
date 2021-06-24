package app.birdsoft.painelmeurestaurante.dialogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.tools.DialogComplement;
import app.birdsoft.painelmeurestaurante.tools.Utils;

public class MainDialog {

    private OnConfirmationClick onConfirmationClick;

    public interface OnConfirmationClick{
        void onValor(Double valor, String real);
    }

    public void setOnConfirmationClick(OnConfirmationClick onConfirmationClick) {
        this.onConfirmationClick = onConfirmationClick;
    }

    public void gerarTeclado(final Context context, Double valor) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        @SuppressLint("InflateParams") final View _view = LayoutInflater.from(context).inflate(R.layout.dialog_teclado_dinheiro, null);
        dialog.setContentView(_view);
        if (Build.VERSION.SDK_INT >= 21) {
            dialog.getWindow().addFlags(67108864);
        }
        ((View) _view.getParent()).setBackgroundColor(context.getResources().getColor(R.color.light_grey_100));
        dialog.show();
        dialog.setOnDismissListener(dialog1 -> {

        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());
        }
        if (Utils.isRunningLandscape(context)) {
            layoutParams.width = Utils.getWidthFromScreenSize(50, context);
        } else {
            layoutParams.width = -1;
        }
        layoutParams.height = -2;
        if (window != null) {
            window.setAttributes(layoutParams);
        }
        final CurrencyAmountKeyboard currencyAmountKeyboard = new CurrencyAmountKeyboard(dialog);
        Button button = dialog.findViewById(R.id.btOk);
        Objects.requireNonNull((View)dialog.findViewById(R.id.btCancel)).setOnClickListener(v -> dialog.dismiss());

        assert button != null;
        button.setOnClickListener(v -> {
            dialog.dismiss();
            onConfirmationClick.onValor(currencyAmountKeyboard.getReceivedAmount(), currencyAmountKeyboard.getValorString());
        });
        dialog.setOnDismissListener(DialogComplement.INSTANCE);
        try {
            dialog.show();
            dialog.getWindow().clearFlags(131080);
        } catch (Exception ignored) {
        }
        currencyAmountKeyboard.show();
        currencyAmountKeyboard.setTvReceivedAmount(valor);
    }

}

