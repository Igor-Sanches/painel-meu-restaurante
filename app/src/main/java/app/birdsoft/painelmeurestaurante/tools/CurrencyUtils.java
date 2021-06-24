package app.birdsoft.painelmeurestaurante.tools;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    public static String formatCurrencyAmount(Object obj) {
        return NumberFormat.getCurrencyInstance().format(Double.valueOf(Utils.roundHalfUp((Double) obj, 2)));
    }

    public static String formatCurrencyAmountBR(Object obj) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(Double.valueOf(Utils.roundHalfUp((Double) obj, 2)));
    }
}
