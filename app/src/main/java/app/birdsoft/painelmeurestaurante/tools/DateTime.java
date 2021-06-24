package app.birdsoft.painelmeurestaurante.tools;
 
import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateTime {
    @SuppressLint("ConstantLocale")
    private static final Locale locale = Locale.getDefault();

    /**
     *
     * @param format = String onde voc� coloca o formato da data ex: dd/MM/yyyy
    * @param time = Adicione o time que deseja para retorna a String com o formado desejado
     * @return
     */
    public static String toDateString(String format, long time){
       SimpleDateFormat date = new SimpleDateFormat(format, locale);
       return date.format(time);
    }

    /**
     *
    * @param time = Adicione o time que deseja para retorna a String com o formado desejado
     * @return
     */
    public static String toDateString(long time){
       SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
       return date.format(time);
    }
      
     /**
     *
     * Pegue o time da data e hora atual no formato dd/MM/yyyy HH:mm
     * @return
     */
    public static long getTime(){
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
        try {
            return Objects.requireNonNull(date.parse(date.format(new Date()))).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(DateTime.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public static boolean isValido(long dataValidade) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parse = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date horaPedidoChegaR = parse.parse(toDateString("dd/MM/yyyy", dataValidade));
            Date horaAtual = parse.parse(parse.format(new Date()));
            if(toDateString("dd/MM/yyyy", dataValidade).equals(parse.format(new Date()))){
                return true;
            }else{
                assert horaPedidoChegaR != null;
                return horaPedidoChegaR.before(horaAtual);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parse = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date data = parse.parse(date);
            assert data != null;
            return data.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Object getValidadeNova() {
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy", locale);
        try {
            return Objects.requireNonNull(date.parse(getDataNova())).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(DateTime.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    private static String getDataNova() {
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(new Date());
       calendar.add(Calendar.DAY_OF_MONTH, 5);

        return toDateString("dd/MM/yyyy", calendar.getTime().getTime());
    }
}
