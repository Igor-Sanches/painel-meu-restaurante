package app.birdsoft.painelmeurestaurante.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.tools.Pagamento;

public class Tools {
    private static final String maskHr = "HH:mm";
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat date1 = new SimpleDateFormat(maskHr);
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat date2 = new SimpleDateFormat(maskHr);

    public static boolean isCheckDate(String horarioAbrir, String horarioFechar) {
        boolean check = false;
        try{
            check = Objects.requireNonNull(date1.parse(horarioAbrir)).before(date2.parse(horarioFechar));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return check;
    }


    public static String getPagamento(String pagamento, Context context) {
        String metodo = "";
        switch (Pagamento.valueOf(pagamento)){
            case dinheiro: metodo = context.getString(R.string.dinheiro); break;
            case cartao_credito: metodo = context.getString(R.string.cartao_credito); break;
            case cartao_debido: metodo = context.getString(R.string.cartao_debito); break;
        }
        return metodo;
    }

    public static void imageArrow(boolean expanded, ImageView viewById) {
        viewById.setBackgroundResource(expanded ? R.drawable.admin_mode_selected : R.drawable.admin_mode_noselected);
        viewById.setImageResource(expanded ? R.drawable.admin_image_mode_selected : R.drawable.admin_image_mode_noselected);
    }

}
