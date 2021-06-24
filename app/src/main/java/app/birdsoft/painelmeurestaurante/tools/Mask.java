package app.birdsoft.painelmeurestaurante.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class Mask {

    public static String unmask(String str) {
        return str.replaceAll("[^\\d,]", "").replaceAll("[,]", ".");
    }

    public static String unmaskKeyboard(String str) {
        return str.replaceAll("[^\\d]", "");
    }

    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String unmask = Mask.unmask(charSequence.toString());
                if (this.isUpdating) {
                    this.old = unmask;
                    this.isUpdating = false;
                    return;
                }
                String str = "";
                int i4 = 0;
                for (char c : str.toCharArray()) {
                    if (c == '#' || unmask.length() <= this.old.length()) {
                        try {
                            str = str +
                                    unmask.charAt(i4);
                            i4++;
                        } catch (Exception ignored) {
                        }
                    } else {
                        str = str +
                                c;
                    }
                }
                this.isUpdating = true;
                editText.setText(str);
                editText.setSelection(str.length());
            }
        };
    }

    public static String formatarTelefoneCelular(String str) {
        String unmask = unmask(str);
        StringBuilder str2 = new StringBuilder();
        int i = 0;
        for (char c : (unmask.length() > 10 ? "(##) #####-####" : "(##) ####-####").toCharArray()) {
            if (c == '#' || unmask.length() <= 0) {
                try {
                    str2.append(unmask.charAt(i));
                    i++;
                } catch (Exception ignored) {
                }
            } else {
                str2.append(c);
            }
        }
        return str2.toString();
    }

    public static TextWatcher insertTelefoneCelular(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String mask;
            String old;

            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            {
                String str = "";
                this.mask = str;
                this.old = str;
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String unmask = Mask.unmask(charSequence.toString());
                if (unmask.length() <= 10) {
                    this.mask = "(##) ####-####";
                } else {
                    this.mask = "(##) #####-####";
                }
                if (this.isUpdating) {
                    this.old = unmask;
                    this.isUpdating = false;
                    return;
                }
                String str = "";
                int i4 = 0;
                for (char c : this.mask.toCharArray()) {
                    if (c == '#' || unmask.length() <= this.old.length()) {
                        try {
                            str = str +
                                    unmask.charAt(i4);
                            i4++;
                        } catch (Exception ignored) {
                        }
                    } else {
                        str = str +
                                c;
                    }
                }
                this.isUpdating = true;
                editText.setText(str);
                editText.setSelection(str.length());
            }
        };
    }

    public static String formatarValor(Object valor) {
        try {
            return CurrencyUtils.formatCurrencyAmount(valor);
        } catch (Exception unused) {
            return "R$ 0,00";
        }
    }
}
