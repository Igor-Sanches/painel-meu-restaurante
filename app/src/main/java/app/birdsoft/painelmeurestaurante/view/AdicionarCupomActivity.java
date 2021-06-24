package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogInput;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.dialogo.MainDialog;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Cupom;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.Email;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.CuponsViewModel;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AdicionarCupomActivity extends AppCompatActivity {

    private List<EditText> emails;
    private LinearLayout container;
    private TextView data_view,valor_minimo, valor_desconto_porcentagem, valor_desconto_dinheiro;
    private long dataVencimento;
    private TextInputLayout inputCupom;
    private RadioButton radio_cupom_frete, radio_cupom_dinheiro, radio_cupom_porcetagem;
    private CheckBox check_alluser, check_valor_minimo, check_vencimento;
    private double valorMinimo = 0, valorDescontoPorcentagem = 0, valorDescontoDinheiro = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_cupom);
        valor_desconto_porcentagem = findViewById(R.id.valor_desconto_porcentagem);
        valor_desconto_dinheiro = findViewById(R.id.valor_desconto_dinheiro);
        inputCupom = findViewById(R.id.inputCupom);
        check_alluser = findViewById(R.id.check_alluser);
        check_valor_minimo = findViewById(R.id.check_valor_minimo);
        check_vencimento = findViewById(R.id.check_vencimento);
        radio_cupom_porcetagem = findViewById(R.id.radio_cupom_porcetagem);
        radio_cupom_dinheiro = findViewById(R.id.radio_cupom_dinheiro);
        radio_cupom_frete = findViewById(R.id.radio_cupom_frete);
        container = findViewById(R.id.container);
        valor_minimo = findViewById(R.id.valor_minimo);
        data_view = findViewById(R.id.data_view);
        emails = new ArrayList<>();
        radio_cupom_dinheiro.setOnCheckedChangeListener((buttonView, isChecked)-> findViewById(R.id.card_cupom_dinherioo).setVisibility(isChecked ? View.VISIBLE : View.GONE));
        radio_cupom_porcetagem.setOnCheckedChangeListener((buttonView, isChecked)-> findViewById(R.id.card_cupom_porcentagem).setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onAllUser(View view) {
        findViewById(R.id.card_emails).setVisibility(((CheckBox)view).isChecked() ? View.GONE : View.VISIBLE);
    }

    public void onAdicionarEmail(View view) {
        EditText editText = new EditText(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 10;
        layoutParams.rightMargin = 10;
        editText.setLayoutParams(layoutParams);
        editText.setHint("exemple@gmail.com");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setBackgroundResource(R.drawable.bg_editor);
        emails.add(editText);
        container.addView(editText);
    }

    public void onValorMinino(View view) {
        findViewById(R.id.card_valor_minimo).setVisibility(((CheckBox)view).isChecked() ? View.VISIBLE : View.GONE);
    }

    public void onValidadeCupom(View view) {
        findViewById(R.id.card_validade).setVisibility(((CheckBox)view).isChecked() ? View.VISIBLE : View.GONE);
    }

    public void onCancelar(View view) {
        onBackPressed();
    }

    public void onConfirmar(View view) {
        if(isCupom()){
            Cupom cupom = new Cupom();
            cupom.setAlluser(check_alluser.isChecked());
            cupom.setAtivo(true);
            cupom.setCodigo(Objects.requireNonNull(inputCupom.getEditText()).getText().toString().trim().toUpperCase());
            cupom.setDataValidade(check_vencimento.isChecked() ? dataVencimento : 0);
            cupom.setDescontoType(getCupomType());
            cupom.setMininum(check_valor_minimo.isChecked());
            cupom.setNumerouso(0);
            cupom.setExpirado(false);
            cupom.setValorDesconto(getValorDesconto());
            cupom.setValorMinimo(check_valor_minimo.isChecked() ? valorMinimo : 0);
            cupom.setVencimento(check_vencimento.isChecked());
            LoadingDialog dialog = new LoadingDialog(this);
            dialog.show();

           if(Conexao.isConnected(this)){
               new ViewModelProvider(this).get(CuponsViewModel.class).insert(cupom, emails, this).observe(this, (success -> {
                   if(success){
                       MyToast.makeText(this, R.string.cupom_enviado, ModoColor._success).show();
                       finish();
                   }else{
                       toast(R.string.falha_add_cupom);
                   }
                   dialog.dismiss();
               }));
           }else{
               MyToast.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
           }
        }
    }

    private double getValorDesconto() {
        if(radio_cupom_frete.isChecked()) return 0;
        else if(radio_cupom_porcetagem.isChecked()) return valorDescontoPorcentagem;
        else return valorDescontoDinheiro;
    }

    private int getCupomType() {
        if(radio_cupom_frete.isChecked()) return 0;
        else if(radio_cupom_porcetagem.isChecked()) return 1;
        else return 2;
    }

    private boolean isCupom() {
        if(Objects.requireNonNull(inputCupom.getEditText()).getText().toString().trim().equals("")){
            toast(R.string.digite_codigo_erro);
            return false;
        }
        if(inputCupom.getEditText().getText().toString().trim().length() < 6){
            toast(R.string.digite_codigo_erro_max);
            return false;
        }
        if(!isAlluser()){
            return false;
        }
        if(!isValidade()){
            return false;
        }
        if(!radio_cupom_dinheiro.isChecked() && !radio_cupom_porcetagem.isChecked() && !radio_cupom_frete.isChecked()){
            toast(R.string.selected_cupom_type);
            return false;
        }

        return true;
    }

    private boolean isValidade() {
        if(check_vencimento.isChecked()){
            if(dataVencimento > 0){
                if(HelperManager.isVencido(dataVencimento)){
                    return true;
                }else{
                    toast(R.string.error_data_ja_expirada);
                    return false;
                }
            }else{
                toast(R.string.error_data_no_digitada);
                return false;
            }
        }else return true;
    }

    private boolean isAlluser() {
        boolean success = true;
        if(!check_alluser.isChecked()){
            if(emails.size() > 0){
                for (EditText editText : emails){
                    if(editText.getText().toString().trim().equals("")){
                        toast(R.string.alluser_email_erro);
                        success = false;
                        break;
                    }else{
                        if(!Email.validar(editText.getText().toString().trim())) {
                            toast(R.string.alluser_email_valido);
                            success = false;
                            break;
                        }
                    }
                }
                return success;
            }else {
                toast(R.string.alluser_email_erro);
                return false;
            }
        }else return true;
    }

    private void toast(@StringRes  int msg) {
        MyToast.makeText(this, msg, ModoColor._falha).show();
    }

    public void onDataSelector(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_date_picker, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.data_expiracao));
        dialog.show();
        DatePicker picker = root.findViewById(R.id.date_picker);
        picker.setMinDate(calendar.getTime().getTime());
        root.findViewById(R.id.cancel).setOnClickListener((v -> dialog.dismiss()));
        root.findViewById(R.id.confirmar).setOnClickListener((v -> {
            dialog.dismiss();
            String dia = String.valueOf(picker.getDayOfMonth());
            if(dia.length() == 1) dia = "0" + dia;
            String mes = String.valueOf(picker.getMonth());
            if(mes.length() == 1) mes = "0" + mes;
            data_view.setText(String.format("%s/%s/%s", dia, mes, picker.getYear()));
            dataVencimento = DateTime.getDate(data_view.getText().toString());
        }));
    }

    public void onValorMinimo(View view) {
        MainDialog dialog = new MainDialog();
        dialog.gerarTeclado(this, valorMinimo);
        dialog.setOnConfirmationClick((valor, real) -> {
            valor_minimo.setText(real);
            valorMinimo = valor;
        });
    }

    public void onValorDescontoPorcentagem(View view) {

        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_porcentagem, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.porcentagem));
        dialog.show();
        TextInputLayout inputLayout = root.findViewById(R.id.inputPorcentagem);
        root.findViewById(R.id.cancel).setOnClickListener((v -> dialog.dismiss()));
        root.findViewById(R.id.confirmar).setOnClickListener((v -> {
            String porcentagem = Objects.requireNonNull(inputLayout.getEditText()).getText().toString().trim();
            if(!porcentagem.equals("")){
                int porcent = Integer.parseInt(porcentagem);
                if(porcent <= 100){
                    valorDescontoPorcentagem = porcent;
                    valor_desconto_porcentagem.setText(String.format("%s%%", porcent));
                    dialog.dismiss();
                }else{
                    MyToast.makeText(this, R.string.error_digite_porcentagem_max, ModoColor._falha).show();
                }
            }else{
                MyToast.makeText(this, R.string.error_digite_porcentagem, ModoColor._falha).show();
            }
        }));
    }

    public void onValorDescontoDinheiro(View view) {
        MainDialog dialog = new MainDialog();
        dialog.gerarTeclado(this, valorMinimo);
        dialog.setOnConfirmationClick((valor, real) -> {
            valor_desconto_dinheiro.setText(real);
            valorDescontoDinheiro = valor;
        });
    }
}