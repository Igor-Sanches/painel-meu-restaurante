package app.birdsoft.painelmeurestaurante.tools;

import java.util.regex.Pattern;

public class Email {
    public static boolean validar(String email) {
        if (email.startsWith("mailto:") || email.startsWith("MAILTO:") && isEmailValido(email)) {
            if (email.contains("@") && !email.contains("/") || email.contains("@") && !email.contains(" ")) {
                String[] aar = email.split("@");
                String number = aar[0];
                if (email.substring(number.length() + 1).contains("@") || email.substring(number.length() + 1).contains(",")) {
                    return false;
                }

                if (aar[1].contains(".")) {
                    return !aar[1].endsWith(".") && !aar[1].startsWith(".") && !aar[1].contains(",");
                } else return false;
            }
            return false;
        } else if (email.contains("@") && !email.contains(":") && isEmailValido(email)) {
            String[] aar = email.split("@");
            String number = aar[0];
            if (email.substring(number.length() + 1).contains("@") || email.substring(number.length() + 1).contains(",")) {
                return false;
            }

            if (aar[1].contains(".")) {
                return !aar[1].endsWith(".") && !aar[1].startsWith(".") && !aar[1].contains(",");
            } else return false;
        } else return false;
    }


    private static final Pattern EMAIL_VALIDO = Pattern.compile("[a-zA-Z0-9@.!#$&%'*+\\-/?^_`{|}~]+");
    private static boolean isEmailValido(String codigo) {
        return EMAIL_VALIDO.matcher(codigo).matches();
    }
}