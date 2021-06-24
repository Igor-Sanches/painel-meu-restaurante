package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogInput;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.dialogo.MainDialog;
import app.birdsoft.painelmeurestaurante.login.SplashActivity;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.manager.Tools;
import app.birdsoft.painelmeurestaurante.model.EstabelecimentoHorario;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.Email;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.EstabelecimentoViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class EstabelecimentoActivity extends AppCompatActivity {

    private List<EstabelecimentoHorario> horarios;
    private LinearLayout lyt_progress, layout_wifi_error, vazio, listaLayout;
    private EstabelecimentoViewModel viewModel;
    private int km = 1, prazo = 0, prazoMinFixo = 0;
    private LoadingDialog loading;
    private GeoPoint local;
    private boolean isShowing = false;
    private boolean isConfig = false;
    private long coount = 0;
    private int index = 1, taxa = -1, subtaxa = -1, subPrazo = -1;
    private double valorTaxaFixa = 0, valorTaxaKm = 0;
    private String numeroTelefone = "",numeroZap = "", email = "", endereco = "";
    private Button btn_semana_dom, btn_geopoint, btn_semana_qua, btn_semana_sab, btn_semana_sex, btn_semana_qui, btn_semana_seg, btn_semana_ter, config_distancia_max, btn_taxa, config_prazo_entregas, btn_prazo_default, btn_zap, btn_endereco, btn_email, config_taxas_entregas, config_taxa_quilometros, btn_telefone;
    private SwitchCompat bool_semana_dom, bool_semana_sex, bool_semana_sab, bool_semana_seg, bool_semana_qui, bool_semana_qua, bool_semana_ter;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private LinearLayout layout_taxa_fixa, layout_taxa_km;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onResume() {
        if(isShowing){
            return;
        }
        viewModel.update(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estabelecimento);
        isConfig = getIntent().getBooleanExtra("config", false);
        findViewById(R.id.button_config).setVisibility(isConfig ? View.VISIBLE : View.GONE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        loading = new LoadingDialog(this);
        viewModel = new ViewModelProvider(this).get(EstabelecimentoViewModel.class);
        viewModel.init(this);
        horarios = new ArrayList<>();
        bool_semana_sab = findViewById(R.id.bool_semana_sab);
        btn_semana_sab = findViewById(R.id.btn_semana_sab);
        bool_semana_sex = findViewById(R.id.bool_semana_sex);
        btn_semana_sex = findViewById(R.id.btn_semana_sex);
        bool_semana_qui = findViewById(R.id.bool_semana_qui);
        btn_semana_qui = findViewById(R.id.btn_semana_qui);
        bool_semana_qua = findViewById(R.id.bool_semana_qua);
        btn_semana_qua = findViewById(R.id.btn_semana_qua);
        bool_semana_ter = findViewById(R.id.bool_semana_ter);
        btn_semana_ter = findViewById(R.id.btn_semana_ter);
        bool_semana_seg = findViewById(R.id.bool_semana_seg);
        btn_semana_seg = findViewById(R.id.btn_semana_seg);
        bool_semana_dom = findViewById(R.id.bool_semana_dom);
        btn_semana_dom = findViewById(R.id.btn_semana_dom);
        btn_email = findViewById(R.id.btn_email);
        btn_prazo_default = findViewById(R.id.btn_prazo_default);
        btn_endereco = findViewById(R.id.btn_endereco);
        btn_zap  =findViewById(R.id.btn_zap);
        config_prazo_entregas = findViewById(R.id.config_prazo_entregas);
        config_taxa_quilometros = findViewById(R.id.config_taxa_quilometros);
        config_taxas_entregas = findViewById(R.id.config_taxas_entregas);
        btn_taxa = findViewById(R.id.btn_taxa);
        layout_taxa_fixa = findViewById(R.id.layout_taxa_fixa);
        btn_telefone = findViewById(R.id.btn_telefone);
        vazio = findViewById(R.id.vazio);
        layout_taxa_km = findViewById(R.id.layout_taxa_km);
        config_distancia_max = findViewById(R.id.config_distancia_max);
        listaLayout = findViewById(R.id.listaLayout);
        lyt_progress = findViewById(R.id.lyt_progress);
        btn_geopoint = findViewById(R.id.btn_geopoint);
        layout_wifi_error = findViewById(R.id.layout_wifi_error);
        viewModel.getMutableLiveData().observe(this, (estabelecimentoElements -> {
            km = estabelecimentoElements.getKm();
            valorTaxaFixa = estabelecimentoElements.getValorFixo();
            valorTaxaKm = estabelecimentoElements.getValorPerKm();
            email = estabelecimentoElements.getEmail();
            endereco = estabelecimentoElements.getEndereco();
            numeroZap = estabelecimentoElements.getWhatsapp();
            prazoMinFixo = estabelecimentoElements.getPrazoMinutoFixo();
            taxa = estabelecimentoElements.getTaxa();
            prazo = estabelecimentoElements.getPrazo();
            local = estabelecimentoElements.getGeoPoint();
            numeroTelefone = estabelecimentoElements.getLigacao();
            horarios = estabelecimentoElements.getHorarios();
            horarios = HelperManager.drive(horarios);
            iniciarDados();
            lyt_progress.setVisibility(estabelecimentoElements.getLayoutProgress());
            vazio.setVisibility(estabelecimentoElements.getLayoutVazio());
            listaLayout.setVisibility(estabelecimentoElements.getLayoutInicial());
            layout_wifi_error.setVisibility(estabelecimentoElements.getLayoutWifiOffline());
        }));
        bool_semana_sab.setOnClickListener((v -> selectedTime(6, bool_semana_sab, btn_semana_sab)));
        bool_semana_sex.setOnClickListener((v -> selectedTime(5, bool_semana_sex, btn_semana_sex)));
        bool_semana_qui.setOnClickListener((v -> selectedTime(4, bool_semana_qui, btn_semana_qui)));
        bool_semana_qua.setOnClickListener((v -> selectedTime(3, bool_semana_qua, btn_semana_qua)));
        bool_semana_ter.setOnClickListener((v -> selectedTime(2, bool_semana_ter, btn_semana_ter)));
        bool_semana_seg.setOnClickListener((v -> selectedTime(1, bool_semana_seg, btn_semana_seg)));
        bool_semana_dom.setOnClickListener((v -> selectedTime(0, bool_semana_dom, btn_semana_dom)));
    }

    private void iniciarDados() {
        config_distancia_max.setText(km == 0 ? getString(R.string.adicionar) : (km + " Km"));
        btn_taxa.setText(taxa == -1 ? getString(R.string.adicionar) : getString(getTituloTaxa()));
        if(taxa != -1) layoutTaxaTipo();
        config_taxas_entregas.setText(valorTaxaFixa == 0 ? getString(R.string.adicionar) : Mask.formatarValor(valorTaxaFixa));
        config_taxa_quilometros.setText(valorTaxaKm == 0 ? getString(R.string.adicionar) : (Mask.formatarValor(valorTaxaKm) + "/Km"));
        btn_telefone.setText(numeroTelefone.equals("") ? getString(R.string.adicionar) : numeroTelefone);
        btn_zap.setText(numeroZap.equals("") ? getString(R.string.adicionar) : numeroZap);
        btn_email.setText(email.equals("") ? getString(R.string.adicionar) : email);
        btn_endereco.setText(endereco.equals("") ? getString(R.string.adicionar) : getString(R.string.editar));
        btn_prazo_default.setText(prazo == 0 ? getString(R.string.config_prazo_time) : getString(R.string.config_prazo_fixo));
        config_prazo_entregas.setText(prazoMinFixo == 0 ? "0 Mim" : prazoMinFixo + " Min");
        btn_geopoint.setText(local == null ? getString(R.string.adicionar) : getString(R.string.atualizar));
        layoutPrazoTipo();
        horariosLayout();
    }

    private void horariosLayout() {
        EstabelecimentoHorario dom = horarios.get(0);
        EstabelecimentoHorario seg = horarios.get(1);
        EstabelecimentoHorario ter = horarios.get(2);
        EstabelecimentoHorario qua = horarios.get(3);
        EstabelecimentoHorario qui = horarios.get(4);
        EstabelecimentoHorario sex = horarios.get(5);
        EstabelecimentoHorario sab = horarios.get(6);
        btn_semana_dom.setVisibility(dom.isAberto() ? View.VISIBLE : View.GONE);
        btn_semana_seg.setVisibility(seg.isAberto() ? View.VISIBLE : View.GONE);
        btn_semana_ter.setVisibility(ter.isAberto() ? View.VISIBLE : View.GONE);
        btn_semana_qua.setVisibility(qua.isAberto() ? View.VISIBLE : View.GONE);
        btn_semana_qui.setVisibility(qui.isAberto() ? View.VISIBLE : View.GONE);
        btn_semana_sex.setVisibility(sex.isAberto() ? View.VISIBLE : View.GONE);
        btn_semana_sab.setVisibility(sab.isAberto() ? View.VISIBLE : View.GONE);
        bool_semana_dom.setChecked(dom.isAberto());
        bool_semana_seg.setChecked(seg.isAberto());
        bool_semana_ter.setChecked(ter.isAberto());
        bool_semana_qua.setChecked(qua.isAberto());
        bool_semana_qui.setChecked(qui.isAberto());
        bool_semana_sex.setChecked(sex.isAberto());
        bool_semana_sab.setChecked(sab.isAberto());
        btn_semana_dom.setText(String.format("%s - %s", dom.getHorarioAbrir(), dom.getHorarioFechar()));
        btn_semana_seg.setText(String.format("%s - %s", seg.getHorarioAbrir(), seg.getHorarioFechar()));
        btn_semana_ter.setText(String.format("%s - %s", ter.getHorarioAbrir(), ter.getHorarioFechar()));
        btn_semana_qua.setText(String.format("%s - %s", qua.getHorarioAbrir(), qua.getHorarioFechar()));
        btn_semana_qui.setText(String.format("%s - %s", qui.getHorarioAbrir(), qui.getHorarioFechar()));
        btn_semana_sex.setText(String.format("%s - %s", sex.getHorarioAbrir(), sex.getHorarioFechar()));
        btn_semana_sab.setText(String.format("%s - %s", sab.getHorarioAbrir(), sab.getHorarioFechar()));
    }

    private int getTituloTaxa() {
        if(taxa == 0) return R.string.config_sem_taxa;
        else if(taxa == 1) return R.string.config_taxa_fixa;
        else return R.string.config_texa_km;
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onConfigDistanciaMax(View view) {
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_km, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_distancia_max));

        TextView km_progress = root.findViewById(R.id.km_progress);
        SeekBar progress_km = root.findViewById(R.id.progress_km);
        index = km == 0 ? 1 : km;
        progress_km.setProgress(index);
        km_progress.setText(String.format("%s Km", index));

        progress_km.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                index = i;
                km_progress.setText(String.format("%s Km", index));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        root.findViewById(R.id.confirmar)
                .setOnClickListener(view12 -> {
                    if(isConnection()){
                        if(index > 0){
                            loading.show();
                            viewModel.updateOrInsert(index, "km", this).observe(this, (success ->{
                                if(success){
                                    km = index;
                                    config_distancia_max.setText(String.format("%sKm", km));
                                    dialog.dismiss();
                                }else{
                                    MySnackbar.makeText(this, R.string.error_adicionar_km_config, ModoColor._falha).show();
                                }
                                loading.dismiss();
                            }));

                        }else{
                            MyToast.makeText(this, R.string.erro_min_km, ModoColor._falha).show();
                        }
                    }
                });


        root.findViewById(R.id.cancel)
                .setOnClickListener(view1 -> dialog.dismiss());

        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    private boolean isConnection() {
        if(!Conexao.isConnected(this)){
            MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
            return false;
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public void onTaxaSelector(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.menu_taxa_options);
        menu.setOnMenuItemClickListener(item -> {
            if(isConnection()){
                loading.show();
                switch (item.getItemId()){
                    case R.id.menuSemTaxa:
                        subtaxa = 0;
                        break;
                    case R.id.menuTaxaFixa:
                        subtaxa = 1;
                        break;
                    case R.id.menuTaxaKm:
                        subtaxa = 2;
                        break;
                }
                if(taxa != subtaxa){
                    viewModel.updateOrInsert(subtaxa, "taxa", this).observe(this, (succees->{
                        if(succees){
                            MySnackbar.makeText(this, R.string.taxa_adicionada, ModoColor._success).show();
                            taxa = subtaxa;
                            btn_taxa.setText(item.getTitle());
                            layoutTaxaTipo();
                        }else{
                            MySnackbar.makeText(this, R.string.taxa_falha_ao_adicionar, ModoColor._falha).show();
                        }
                        loading.dismiss();
                    }));
                }else{
                    loading.dismiss();
                }
            }
            return false;
        });
        menu.show();
    }

    private void layoutTaxaTipo() {
        switch (taxa){
            case 0:
                layout_taxa_km.setVisibility(View.GONE);
                layout_taxa_fixa.setVisibility(View.GONE);
                break;
            case 1:
                layout_taxa_km.setVisibility(View.GONE);
                layout_taxa_fixa.setVisibility(View.VISIBLE);
                break;
            case 2:
                layout_taxa_km.setVisibility(View.VISIBLE);
                layout_taxa_fixa.setVisibility(View.GONE);
                break;
        }
    }

    public void onTaxaFixa(View view) {

        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_taxa_fixa, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_taxa_fixa));
        TextView fixo_valor = root.findViewById(R.id.km_valor);
        Button btn_editar = root.findViewById(R.id.btn_editar);
        fixo_valor.setText(Mask.formatarValor(valorTaxaFixa));

        btn_editar.setOnClickListener(view1 -> {
            MainDialog mainDialog = new MainDialog();
            mainDialog.gerarTeclado(this, valorTaxaFixa);
            mainDialog.setOnConfirmationClick((_valor, real) -> {
                valorTaxaFixa = _valor;
                fixo_valor.setText(real);
            });
        });

        root.findViewById(R.id.confirmar)
                .setOnClickListener(view12 -> {
                    if(isConnection()){
                        if(valorTaxaFixa > 0){
                            loading.dismiss();
                            viewModel.updateOrInsert(valorTaxaFixa, "valorFixo", this).observe(this, (success->{
                                if(success){
                                    MySnackbar.makeText(this, R.string.valor_fixo_add, ModoColor._success).show();
                                    config_taxas_entregas.setText(Mask.formatarValor(valorTaxaFixa));
                                }else{
                                    MySnackbar.makeText(this, R.string.valor_fixo_erro, ModoColor._falha).show();
                                }
                                dialog.dismiss();
                                loading.dismiss();
                            }));
                        }else{
                            MySnackbar.makeText(this, R.string.valor_erro_minimo, ModoColor._falha).show();
                        }
                    }
                });

        root.findViewById(R.id.cancel)
                .setOnClickListener(view13 -> dialog.dismiss());


        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    public void onTaxaKm(View view) {

        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_taxa_km, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_taxa_km));

        TextView km_valor = root.findViewById(R.id.km_valor);
        Button btn_editar = root.findViewById(R.id.btn_editar);
        TextView ex_valor_km = root.findViewById(R.id.ex_valor_km);
        double valor = valorTaxaKm * 7;
        km_valor.setText(String.format("%s /Km", Mask.formatarValor(valorTaxaKm)));
        ex_valor_km.setText(String.format("%s: %s", getString(R.string.valor_total), Mask.formatarValor(valor)));

        btn_editar.setOnClickListener(view1 -> {
            MainDialog mainDialog = new MainDialog();
            mainDialog.gerarTeclado(this, valorTaxaKm);
            mainDialog.setOnConfirmationClick((_valor, real) -> {
                valorTaxaKm = _valor;
                double valor1 = _valor * 7;
                km_valor.setText(String.format("%s /Km", real));
                ex_valor_km.setText(String.format("%s: %s", getString(R.string.valor_total), Mask.formatarValor(valor1)));
            });
        });

        root.findViewById(R.id.confirmar)
                .setOnClickListener(view12 -> {
                    if(isConnection()){
                        if(valorTaxaKm > 0){
                            loading.dismiss();
                            viewModel.updateOrInsert(valorTaxaKm, "valorPerKm", this).observe(this, (success->{
                                if(success){
                                    MySnackbar.makeText(this, R.string.valor_km_add, ModoColor._success).show();
                                    config_taxa_quilometros.setText(String.format("%s/Km", Mask.formatarValor(valorTaxaKm)));
                                }else{
                                    MySnackbar.makeText(this, R.string.valor_km_erro, ModoColor._falha).show();
                                }
                                dialog.dismiss();
                                loading.dismiss();
                            }));
                        }else{
                            MySnackbar.makeText(this, R.string.valor_erro_minimo, ModoColor._falha).show();
                        }
                    }
                });

        root.findViewById(R.id.cancel)
                .setOnClickListener(view13 -> dialog.dismiss());


        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    public void onTelefone(View view) {

        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_telefone, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_telefone));

        TextInputLayout input = root.findViewById(R.id.telefone_config);
        if(!numeroTelefone.equals("")){
            Objects.requireNonNull(input.getEditText()).setText(numeroTelefone);
            input.getEditText().setSelection(numeroTelefone.length());
        }
        EditText phone = input.getEditText();
        assert phone != null;
        phone.addTextChangedListener(Mask.insertTelefoneCelular(phone));
        input.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(input.isErrorEnabled()){
                    input.setError("");
                    input.setErrorEnabled(false);
                }
            }
        });
        root.findViewById(R.id.confirmar)
                .setOnClickListener(view12 -> {
                    if(isConnection()){
                        String numero = input.getEditText().getText().toString().trim();
                        if(!numero.equals("")){
                            if(numero.length() == "(##) ####-####".length() || numero.length() == "(##) #####-####".length()){
                                if(!numero.equals(numeroTelefone)){
                                    loading.show();
                                    viewModel.updateOrInsert(numero, "telefone", this).observe(this, (success->{
                                        if(success){
                                            numeroTelefone = numero;
                                            btn_telefone.setText(numeroTelefone);
                                            MySnackbar.makeText(this, R.string.numero_telefone_add, ModoColor._success).show();
                                        }else{
                                            MySnackbar.makeText(this, R.string.numero_telefone_erro, ModoColor._falha).show();
                                        }
                                        dialog.dismiss();
                                        loading.dismiss();
                                    }));
                                }else dialog.dismiss();
                            }else{
                                MySnackbar.makeText(this, R.string.numero_telefone_erro_incorreto, ModoColor._falha).show();
                            }
                        }else{
                            MySnackbar.makeText(this, R.string.numero_telefone_erro_clear, ModoColor._falha).show();
                        }
                    }
                });
        root.findViewById(R.id.cancel)
                .setOnClickListener(view1 -> dialog.dismiss());

        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    public void onWhatsApp(View view) {

        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_telefone, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_whasapp));

        TextInputLayout input = root.findViewById(R.id.telefone_config);
        if(!numeroZap.equals("")){
            Objects.requireNonNull(input.getEditText()).setText(numeroZap);
            input.getEditText().setSelection(numeroZap.length());
        }
        EditText phone = input.getEditText();
        assert phone != null;
        phone.addTextChangedListener(Mask.insertTelefoneCelular(phone));
        input.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(input.isErrorEnabled()){
                    input.setError("");
                    input.setErrorEnabled(false);
                }
            }
        });
        root.findViewById(R.id.confirmar)
                .setOnClickListener(view12 -> {
                    if(isConnection()){
                        String numero = input.getEditText().getText().toString().trim();
                        if(!numero.equals("")){
                            if(numero.length() == "(##) ####-####".length() || numero.length() == "(##) #####-####".length()){
                                if(!numero.equals(numeroZap)){
                                    loading.show();
                                    viewModel.updateOrInsert(numero, "whatsapp", this).observe(this, (success->{
                                        if(success){
                                            numeroZap = numero;
                                            btn_zap.setText(numeroZap);
                                            MySnackbar.makeText(this, R.string.numero_whatsapp_add, ModoColor._success).show();
                                        }else{
                                            MySnackbar.makeText(this, R.string.numero_whatsapp_erro, ModoColor._falha).show();
                                        }
                                        dialog.dismiss();
                                        loading.dismiss();
                                    }));
                                }else dialog.dismiss();
                            }else{
                                MySnackbar.makeText(this, R.string.numero_telefone_erro_incorreto, ModoColor._falha).show();
                            }
                        }else{
                            MySnackbar.makeText(this, R.string.numero_telefone_erro_clear, ModoColor._falha).show();
                        }
                    }
                });
        root.findViewById(R.id.cancel)
                .setOnClickListener(view1 -> dialog.dismiss());

        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    public void onEmail(View view) {
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_email, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_email));

        TextInputLayout input = root.findViewById(R.id.email_config);
        if(email != null){
            if(!email.equals("")){
                Objects.requireNonNull(input.getEditText()).setText(email);
                input.getEditText().setSelection(email.length());
            }
        }
        Objects.requireNonNull(input.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(input.isErrorEnabled()){
                    input.setError("");
                    input.setErrorEnabled(false);
                }
            }
        });
        root.findViewById(R.id.confirmar)
                .setOnClickListener(view1 -> {
                    if(isConnection()){
                        String _email = input.getEditText().getText().toString().trim();
                        if(!_email.equals("")){
                            if(Email.validar(_email)){
                                if(!_email.equals(email)){
                                    loading.show();
                                    viewModel.updateOrInsert(_email, "email", this).observe(this, (success->{
                                        if(success){
                                            email = _email;
                                            btn_email.setText(email);
                                            MySnackbar.makeText(this, R.string.numero_email_add, ModoColor._success).show();
                                        }else{
                                            MySnackbar.makeText(this, R.string.numero_email_erro, ModoColor._falha).show();
                                        }
                                        dialog.dismiss();
                                        loading.dismiss();
                                    }));
                                }else dialog.dismiss();
                            }else{
                                MySnackbar.makeText(this, R.string.numero_email_erro_incorreto, ModoColor._falha).show();
                            }
                        }else{
                            MySnackbar.makeText(this, R.string.numero_email_erro_clear, ModoColor._falha).show();
                        }
                    }
                });
        root.findViewById(R.id.cancel)
                .setOnClickListener(view12 -> dialog.dismiss());

        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    public void onEndereco(View view) {
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.card_endereco, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_endereco));

        TextInputLayout input = root.findViewById(R.id.endereco_config);
        if(!endereco.equals("")){
            Objects.requireNonNull(input.getEditText()).setText(endereco);
            input.getEditText().setSelection(endereco.length());
        }
        Objects.requireNonNull(input.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(input.isErrorEnabled()){
                    input.setError("");
                    input.setErrorEnabled(false);
                }
            }
        });
        root.findViewById(R.id.confirmar)
                .setOnClickListener(view1 -> {
                    if(isConnection()){
                        String _endereco = input.getEditText().getText().toString().trim();
                        if(!_endereco.equals("")){
                            if(!_endereco.equals(endereco)){
                                loading.show();
                                viewModel.updateOrInsert(_endereco, "endereco", this).observe(this, (success->{
                                    if(success){
                                        endereco = _endereco;
                                        btn_endereco.setText(R.string.editar);
                                        MySnackbar.makeText(this, R.string.numero_endereco_add, ModoColor._success).show();
                                    }else{
                                        MySnackbar.makeText(this, R.string.numero_endereco_erro, ModoColor._falha).show();
                                    }
                                    dialog.dismiss();
                                    loading.dismiss();
                                }));
                            }else dialog.dismiss();
                        }else{
                            MySnackbar.makeText(this, R.string.numero_endereco_erro_clear, ModoColor._falha).show();
                        }
                    }
                });
        root.findViewById(R.id.cancel)
                .setOnClickListener(view12 -> dialog.dismiss());

        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    @SuppressLint("NonConstantResourceId")
    public void onPrazo(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.menu_prazo_options);
        menu.setOnMenuItemClickListener(item -> {
            if(isConnection()){
                loading.show();
                switch (item.getItemId()) {
                    case R.id.prazo_time:
                        subPrazo = 0;
                        break;
                    case R.id.prazo_fixo:
                        subPrazo = 1;
                        break;
                }
                if(prazo != subPrazo){
                    viewModel.updateOrInsert(subPrazo, "prazo", this).observe(this, (succees->{
                        if(succees){
                            MySnackbar.makeText(this, R.string.prazo_adicionada, ModoColor._success).show();
                            prazo = subPrazo;
                            btn_prazo_default.setText(item.getTitle());
                            layoutPrazoTipo();
                        }else{
                            MySnackbar.makeText(this, R.string.prazo_falha_ao_adicionar, ModoColor._falha).show();
                        }
                        loading.dismiss();
                    }));
                }else{
                    loading.dismiss();
                }
            }
            return false;
        });
        menu.show();
    }

    private void layoutPrazoTipo() {
        findViewById(R.id.layout_prazo).setVisibility(prazo == 1 ? View.VISIBLE : View.GONE);
    }

    public void onPrazoFixo(View view) {

        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_prazo, null);
        DialogInput dialog = new DialogInput(this, root, getString(R.string.config_prazo_entrega));

        TextInputLayout inputLayout = root.findViewById(R.id.input_prazo);
        Objects.requireNonNull(inputLayout.getEditText()).setText(String.valueOf(prazoMinFixo));
        root.findViewById(R.id.cancel).setOnClickListener((v -> dialog.dismiss()));
        root.findViewById(R.id.confirmar).setOnClickListener((v -> {
            if(isConnection()){
                String _prazoMinFixo = inputLayout.getEditText().getText().toString().trim();
                if(!_prazoMinFixo.equals("")){
                    int min = Integer.parseInt(_prazoMinFixo);
                    if(min > 0){
                        loading.show();
                        viewModel.updateOrInsert(min, "prazoMinutoFixo", this).observe(this, (success->{
                            if(success){
                                prazoMinFixo = min;
                                config_prazo_entregas.setText(String.format("%s Min", min));
                                MySnackbar.makeText(this, R.string.add_minuto_prazo, ModoColor._success).show();
                                dialog.dismiss();
                            }else{
                                MyToast.makeText(this, R.string.error_digite_prazo_falha, ModoColor._falha).show();
                            }
                            dialog.dismiss();
                            loading.dismiss();
                        }));

                    }else{
                        MyToast.makeText(this, R.string.error_digite_prazo_min_minimo, ModoColor._falha).show();
                    }
                }else{
                    MyToast.makeText(this, R.string.error_digite_prazo_minuto, ModoColor._falha).show();
                }
            }
        }));

        dialog.setOnDismissListener((dialog1 -> isShowing = false));
        dialog.show();
        isShowing = true;
    }

    private void selectedTime(int position, SwitchCompat checked, Button btn_semana){
        EstabelecimentoHorario horario = horarios.get(position);
        if(checked.isChecked()){
            @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.dialog_config_editor_semana, null);
            DialogInput dialog = new DialogInput(this, root, getString(R.string.config_horario));

            Button btnAbrir = root.findViewById(R.id.btnHoraAbrir);
            Button btnFechar = root.findViewById(R.id.btnHoraFechar);
            if(horario.getHorarioAbrir() != null){
                if(!horario.getHorarioAbrir().equals("--:--") && !horario.getHorarioFechar().equals("--:--")){
                    btnAbrir.setText(horario.getHorarioAbrir());
                    btnFechar.setText(horario.getHorarioFechar());
                }
            }
            btnAbrir.setOnClickListener(view -> {
                int hora = 0;
                int munuto = 0;
                if(!btnAbrir.getText().toString().equals("--:--")){
                    String[] hrs = btnAbrir.getText().toString().split(":");
                    hora = Integer.parseInt(hrs[0]);
                    munuto = Integer.parseInt(hrs[1]);
                }
                TimePickerDialog newInstance = TimePickerDialog.newInstance((view1, hourOfDay, minute, second) -> {
                    String _hourOfDay = ""+hourOfDay;
                    if(_hourOfDay.length() == 1){
                        _hourOfDay = "0" + hourOfDay;
                    }
                    String _minute = ""+minute;
                    if(_minute.length() == 1){
                        _minute = "0" + minute;
                    }
                    String tempo = _hourOfDay + ":" + _minute;
                    horario.setHorarioAbrir(tempo);
                    btnAbrir.setText(tempo);
                }, hora, munuto, true);
                newInstance.setThemeDark(false);
                newInstance.setAccentColor(getResources().getColor(R.color.colorBtnAzul));
                newInstance.show(getSupportFragmentManager(), "Timepickerdialog");
            });
            btnFechar.setOnClickListener(view -> {
                int hora = 0;
                int munuto = 0;
                if(!btnFechar.getText().toString().equals("--:--")){
                    String[] hrs = btnFechar.getText().toString().split(":");
                    hora = Integer.parseInt(hrs[0]);
                    munuto = Integer.parseInt(hrs[1]);
                }
                TimePickerDialog newInstance = TimePickerDialog.newInstance((view12, hourOfDay, minute, second) -> {
                    String _hourOfDay = ""+hourOfDay;
                    if(_hourOfDay.length() == 1){
                        _hourOfDay = "0" + hourOfDay;
                    }
                    String _minute = ""+minute;
                    if(_minute.length() == 1){
                        _minute = "0" + minute;
                    }
                    String tempo = _hourOfDay + ":" + _minute;
                    horario.setHorarioFechar(tempo);
                    btnFechar.setText(tempo);
                }, hora, munuto, true);
                newInstance.setThemeDark(false);
                newInstance.setAccentColor(getResources().getColor(R.color.colorBtnAzul));
                newInstance.show(getSupportFragmentManager(), "Timepickerdialog");
            });

            root.findViewById(R.id.confirmar)
                    .setOnClickListener(view -> {
                        if(isConnection()){
                            if(!horario.getHorarioAbrir().equals("--:--")){
                                if(!horario.getHorarioFechar().equals("--:--")){
                                    if(Tools.isCheckDate(horario.getHorarioAbrir(), horario.getHorarioFechar())){
                                        horario.setAberto(true);
                                        horarios.set(position, horario);
                                        loading.show();
                                        viewModel.updateOrInsert(horarios, "horarios", this).observe(this, (success->{
                                            if(success){
                                                btn_semana.setText(String.format("%s - %s", horario.getHorarioAbrir(), horario.getHorarioFechar()));
                                                btn_semana.setVisibility(View.VISIBLE);
                                                MySnackbar.makeText(this, R.string.salvo_horario, ModoColor._success).show();
                                            }else{
                                                MySnackbar.makeText(this, R.string.error_horario, ModoColor._falha).show();
                                            }
                                            dialog.dismiss();
                                            loading.dismiss();
                                        }));
                                    }else{
                                        MySnackbar.makeText(this, R.string.error_horario_tree, ModoColor._falha).show();
                                    }

                                }else{
                                    MySnackbar.makeText(this, R.string.error_horario_two, ModoColor._falha).show();
                                }


                            }else {
                                MySnackbar.makeText(this, R.string.error_horario_one, ModoColor._falha).show();
                            }
                        }else checked.setChecked(!checked.isChecked());

                    });
            root.findViewById(R.id.cancel)
                    .setOnClickListener(view -> {
                        dialog.dismiss();
                        checked.setChecked(!checked.isChecked());
                    });

            dialog.setOnDismissListener((dialog1 -> isShowing = false));
            dialog.show();
            isShowing = true;
        }else{
            if(isConnection()){
                horario.setAberto(false);
                loading.show();
                horarios.set(position, horario);
                viewModel.updateOrInsert(horarios, "horarios", this).observe(this, (success -> {
                    if(success){
                        btn_semana.setVisibility(View.GONE);
                        MySnackbar.makeText(this, R.string.horario_desativado_sucesso, ModoColor._success).show();
                    }else{
                        MySnackbar.makeText(this, R.string.horario_desativado_error, ModoColor._falha).show();
                    }
                    loading.dismiss();
                }));
            }else checked.setChecked(!checked.isChecked());
        }

    }

    public void onGeolocalizacao(View view) {
        if(isConnection()){
            loading.show();
            if(Conexao.isConnected(this)){
                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationSettingsRequest.Builder builder =
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest);

                SettingsClient client = LocationServices.getSettingsClient(this);

                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                task.addOnSuccessListener(this,
                        locationSettingsResponse -> buscarMinhaLocalizacao());

                task.addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(this,
                                        REQUEST_CHECK_SETTINGS);
                                loading.dismiss();
                                MySnackbar.makeText(this, R.string.erro_gps_buscar, ModoColor._falha).show();

                            } catch (IntentSender.SendIntentException sendEx) {
                                loading.dismiss();
                                new AlertDialog.Builder(this)
                                        .setMessage(getString(R.string.gps_nescessario))
                                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).setOnDismissListener(dialog -> finish()).show();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            buscarMinhaLocalizacao();
                            break;
                    }
                });
            }else{
                MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
                loading.dismiss();
            }
        }
    }

    private void buscarMinhaLocalizacao() {
        if (Build.VERSION.SDK_INT < 23) {
            iniciarBuscar();
            return;
        }
        try {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } catch (Exception x) {
            iniciarBuscar();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loading.show();
                iniciarBuscar();
            }
        } catch (Exception x) {
            //message(x.getMessage());
        }
    }

    private void iniciarBuscar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        this.mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            System.out.println("onComplete");
            try {
                if (task.isSuccessful()) {
                    Location result = task.getResult();
                    LatLng minhaLocalizacao = new LatLng(result.getLatitude(), result.getLongitude());
                    new CalcularDistanciaAsyncTask(this).execute(minhaLocalizacao);
                }
            } catch (Exception x) {
                MySnackbar.makeText(this, R.string.erro_gps, ModoColor._falha).show();
            }
        });
    }

    public void onConfig(View view) {
        if(isNameAdicionar(config_distancia_max)){
            insertErro(R.string.adicione_config_distancia_max);
            return;
        }
        if(isNameSelecionar(btn_taxa)){
            insertErro(R.string.adicione_config_btn_taxa);
            return;
        }
        if(isNameAdicionar(btn_telefone)){
            insertErro(R.string.adicione_config_btn_telefone);
            return;
        }
        if(isNameAdicionar(btn_zap)){
            insertErro(R.string.adicione_config_btn_zap);
            return;
        }
        if(isNameAdicionar(btn_endereco)){
            insertErro(R.string.adicione_config_btn_endereco);
            return;
        }
        if(isNameAdicionar(btn_geopoint)){
            insertErro(R.string.adicione_config_btn_geopoint);
            return;
        }
        if(isNameSelecionar(btn_prazo_default)){
            insertErro(R.string.adicione_config_btn_prazo_default);
            return;
        }
        if(!bool_semana_dom.isChecked() &&
                !bool_semana_qua.isChecked() &&
                !bool_semana_qui.isChecked() &&
                !bool_semana_sab.isChecked() &&
                !bool_semana_seg.isChecked() &&
                !bool_semana_sex.isChecked() &&
                !bool_semana_ter.isChecked()){
            insertErro(R.string.adicione_config_horarios);
            return;
        }

        viewModel.iniciarDados("aberto", "config", this).observe(this, (sucesso->{
            if(sucesso){
                Settings.setConfig(true, this);
                viewModel.update(this);
                Navigate.activity(this).navigate(SplashActivity.class);
                finish();
            }else{
                MySnackbar.makeText(this, R.string.erro_config, ModoColor._falha).show();
            }
        }));

    }

    private void insertErro(@StringRes int res) {
        MySnackbar.makeText(this, res, ModoColor._falha).show();
    }

    private boolean isNameAdicionar(Button btn) {
        return btn.getText().equals(getString(R.string.adicionar));
    }

    private boolean isNameSelecionar(Button btn) {
        return btn.getText().equals(getString(R.string.selecionar));
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        viewModel.update(this);
    }

    @SuppressLint("StaticFieldLeak")
    private class CalcularDistanciaAsyncTask extends AsyncTask<LatLng, Void, Address> {
        private final Context context;
        public CalcularDistanciaAsyncTask(Context context) {
            this.context = context;
        }

        public Address doInBackground(LatLng... latLngArr) {
            LatLng latLng = latLngArr[0];
            try {
                List fromLocation = new Geocoder(context, Locale.getDefault()).getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (fromLocation != null && fromLocation.size() > 0) {
                    return (Address) fromLocation.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Address address) {
            if (address != null) {
                LatLng minhaLocalizacao = new LatLng(address.getLatitude(), address.getLongitude());
                GeoPoint geoPoint = new GeoPoint(minhaLocalizacao.latitude, minhaLocalizacao.longitude);
                viewModel.updateOrInsert(geoPoint, "local", EstabelecimentoActivity.this).observe(EstabelecimentoActivity.this, (success->{
                    if(success){
                        MySnackbar.makeText(EstabelecimentoActivity.this, R.string.geolocalizacao_salvo, ModoColor._success).show();
                        btn_geopoint.setText(getString(R.string.atualizar));
                    }else{
                        MySnackbar.makeText(EstabelecimentoActivity.this, R.string.geolocalizacao_erro_salvar, ModoColor._falha).show();

                    }

                    loading.dismiss();
                }));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(isConfig){
            if(System.currentTimeMillis() - coount > 2000){
                coount = System.currentTimeMillis();
                MySnackbar.makeText(this, R.string.clique_para_sair).show();
                return;
            }
            finishAffinity();
            return;
        }
        super.onBackPressed();
    }
}