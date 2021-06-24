package app.birdsoft.painelmeurestaurante;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import app.birdsoft.painelmeurestaurante.betaScreenModo.AdicionarClienteTesteActivity;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.FragmentType;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.view.AnunciosActivity;
import app.birdsoft.painelmeurestaurante.view.AppSettingsActivity;
import app.birdsoft.painelmeurestaurante.view.CardapioActivity;
import app.birdsoft.painelmeurestaurante.view.CuponsActivity;
import app.birdsoft.painelmeurestaurante.view.EstabelecimentoActivity;
import app.birdsoft.painelmeurestaurante.view.HistoricosActivity;
import app.birdsoft.painelmeurestaurante.view.PainelPedidosActivity;
import app.birdsoft.painelmeurestaurante.viewModel.EstabelecimentoViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.PedidosViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class MainActivity extends AppCompatActivity {

    private long count = 0;
    private BottomSheetDialog sheetDialog;
    private BottomSheetBehavior sheetBehavior;
    private CardView painel;
    private Button btn_abrir, btn_fechar;
    private ImageView icone_success;
    private EstabelecimentoViewModel viewModel;
    private LinearLayout layout_falha, layout_progresso;
    private RelativeLayout layout_iniciar;
    private LinearLayout layout_info_card_painel_success, layout_info_card_painel;

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - count > 2000) {
            count = System.currentTimeMillis();
            MySnackbar.makeText(this, R.string.clique_para_sair).show();
            return;
        }
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View sheetBottom = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheetBottom);
        sheetDialog = new BottomSheetDialog(this);
        painel = findViewById(R.id.painel);
        layout_iniciar = findViewById(R.id.layout_iniciar);
        layout_progresso = findViewById(R.id.layout_progresso);
        layout_falha = findViewById(R.id.layout_falha);
        btn_abrir = findViewById(R.id.btn_abrir);
        btn_fechar = findViewById(R.id.btn_fechar);
        icone_success = findViewById(R.id.icone_success);
        layout_info_card_painel_success = findViewById(R.id.layout_info_card_painel_success);
        layout_info_card_painel = findViewById(R.id.layout_info_card_painel);
        viewModel = new ViewModelProvider(this).get(EstabelecimentoViewModel.class);
        viewModel.init(this);
        viewModel.getMutableLiveData().observe(this, (estabelecimentoElements -> {
            if(!estabelecimentoElements.isAberto()){
                btn_fechar.setVisibility(View.VISIBLE);
                btn_abrir.setVisibility(View.GONE);
            }else{
                btn_fechar.setVisibility(View.GONE);
                btn_abrir.setVisibility(View.VISIBLE);
            }
            layout_info_card_painel_success.setVisibility(View.GONE);
            layout_info_card_painel.setVisibility(View.VISIBLE);
            layout_iniciar.setVisibility(estabelecimentoElements.getLayoutInicial());
            layout_falha.setVisibility(estabelecimentoElements.getLayoutWifiOffline() == View.VISIBLE || estabelecimentoElements.getLayoutVazio() == View.VISIBLE ? View.VISIBLE : View.GONE);
            layout_progresso.setVisibility(estabelecimentoElements.getLayoutProgress());
        }));
        PedidosViewModel vm = new ViewModelProvider(this).get(PedidosViewModel.class);
        vm.init(this, FragmentType.novoPedido);
        vm.getMutableLiveData().observe(this, (pedidosElements -> {
            if(pedidosElements.getPedidos().size() > 0){
                String pedidsoMsg = pedidosElements.getPedidos().size() == 1 ?
                        getString(R.string.um_novo_pedido) :
                        getString(R.string.vc_tem) + " " + pedidosElements.getPedidos().size() + " " + getString(R.string.novos_p);
                ((TextView)findViewById(R.id.painel_pedidos_resumo)).setText(pedidsoMsg);
            }else ((TextView)findViewById(R.id.painel_pedidos_resumo)).setText(R.string.sem_pedidos_no_painel);
        }));
    }

    private int getOriginalWidth() {
        return FrameLayout.LayoutParams.MATCH_PARENT;//(int)getResources().getDimension(R.dimen.get_original_width);
    }

    private int getWidth() {
        return (int)getResources().getDimension(R.dimen.get_width);
    }

    public void onSettings(View view) {
        navigate(AppSettingsActivity.class);
    }

    public void onPainelPedidos(View view) {
        navigate(PainelPedidosActivity.class);
    }

    public void onCardapio(View view) {
        navigate(CardapioActivity.class);
    }

    public void onAnuncios(View view) {
        navigate(AnunciosActivity.class);
    }

    public void onCupons(View view) {
        navigate(CuponsActivity.class);
    }

    public void onHistoricos(View view) {
        navigate(HistoricosActivity.class);
    }

    public void onConfig(View view) {
        navigate(EstabelecimentoActivity.class);
    }

    private void navigate(Class<?> classe){
        if(HelperManager.isClicked()){
            Navigate.activity(this).navigate(classe);
        }
    }

    @Override
    protected void onResume() {
        HelperManager.clieckTime = 0;
        viewModel.update(this);
        super.onResume();
    }

    public void onFechar(View view) {
        animarTamanhoLargura(true);
    }

    public void onAbrir(View view) {
        animarTamanhoLargura(false);
    }

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    private void animarTamanhoLargura(boolean isOpen) {
        ValueAnimator anim = ValueAnimator.ofInt(painel.getMeasuredWidth(), getWidth());
        anim.addUpdateListener(animation -> {
            int value = (Integer)animation.getAnimatedValue();
            ViewGroup.LayoutParams params = painel.getLayoutParams();
            params.width = value;
            layout_info_card_painel.setVisibility(View.GONE);
            if(isOpen){
                layout_info_card_painel_success.setBackgroundDrawable(getDrawable(R.drawable.btn_aberto));
                icone_success.setColorFilter(getResources().getColor(R.color.acriligo_tint_verde));
            }else{
                layout_info_card_painel_success.setBackgroundDrawable(getDrawable(R.drawable.btn_fechado));
                icone_success.setColorFilter(getResources().getColor(R.color.acriligo_tint_vermelho));
            }
            layout_info_card_painel_success.setVisibility(View.VISIBLE);
            painel.requestLayout();
            if(!sheetDialog.isShowing()){
                runOnUiThread(() -> iniciarDialog(isOpen));
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    private void iniciarDialog(boolean isOpen) {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_estabelecimento_item_opcao, null);

        TextView msg = _view.findViewById(R.id.msg);
        Button btnClose = _view.findViewById(R.id.cancel);
        Button action = _view.findViewById(R.id.btn_action);

        if(isOpen) {
            action.setBackgroundResource(R.drawable.btn_acriligo_vermelho);
            action.setTextColor(getResources().getColor(R.color.acriligo_tint_vermelho));
            action.setText(R.string.fechar_estabelecimento);
            msg.setText(R.string.fechar_estabelecimento_msg);
        }else{
            action.setBackgroundResource(R.drawable.btn_acriligo_verde);
            action.setTextColor(getResources().getColor(R.color.acriligo_tint_verde));
            action.setText(R.string.abrir_estabelecimento);
            msg.setText(R.string.abrir_estabelecimento_msg);
        }

        btnClose.setOnClickListener((v) -> {
            sheetDialog.dismiss();
            animarRetornoTamanhoLargura(!isOpen);
        });
        action.setOnClickListener((v) -> {
            sheetDialog.dismiss();
            LoadingDialog dialog = new LoadingDialog(this);
            dialog.show();
            viewModel.openOrClose(!isOpen, this).observe(this, (sucesso -> {
                if(sucesso){
                    animarRetornoTamanhoLargura(isOpen);
                    viewModel.update(this);
                }else{
                    animarRetornoTamanhoLargura(!isOpen);
                    MyToast.makeText(this, R.string.erro_atualizar_open, ModoColor._falha).show();
                }
                dialog.dismiss();
            }));
        });

        sheetDialog.setCancelable(false);
        sheetDialog.setContentView(_view);

        if(Build.VERSION.SDK_INT >= 21){
            sheetDialog.getWindow().addFlags(67108864);
        }
        ((View)_view.getParent()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.show();
    }

    private void animarRetornoTamanhoLargura(boolean isOpen) {
        ValueAnimator anim = ValueAnimator.ofInt(painel.getMeasuredWidth(), getOriginalWidth());
        anim.addUpdateListener(animation -> {
            int value = (Integer)animation.getAnimatedValue();
            ViewGroup.LayoutParams params = painel.getLayoutParams();
            params.width = value;
            if(isOpen){
                btn_fechar.setVisibility(View.VISIBLE);
                btn_abrir.setVisibility(View.GONE);
            }else{
                btn_fechar.setVisibility(View.GONE);
                btn_abrir.setVisibility(View.VISIBLE);
           }
            layout_info_card_painel_success.setVisibility(View.GONE);
            layout_info_card_painel.setVisibility(View.VISIBLE);
            painel.requestLayout();
        });
        anim.setDuration(250);
        anim.start();
    }

    public void onAdicionarCLientes(View view) {
        navigate(AdicionarClienteTesteActivity.class);
    }

    public void onContratarServico(View view) {
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        HelperManager.getContrato(this).observe(this, (result -> {
            if(!result)
                MyToast.makeText(this, R.string.erro_contatar, ModoColor._falha).show();

            dialog.dismiss();
        }));
    }

    public void onUpdateConnection(View view) {
        layout_progresso.setVisibility(View.VISIBLE);
        layout_falha.setVisibility(View.GONE);
        layout_iniciar.setVisibility(View.GONE);
        viewModel.update(this);
    }
}