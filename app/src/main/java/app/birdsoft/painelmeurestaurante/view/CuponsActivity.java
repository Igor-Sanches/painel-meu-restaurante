package app.birdsoft.painelmeurestaurante.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorCupons;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Cupom;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.CuponsViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class CuponsActivity extends AppCompatActivity {

    private BottomSheetDialog sheetDialog;
    private BottomSheetBehavior sheetBehavior;
    private LoadingDialog dialog;
    private AdaptadorCupons adaptador;
    private CoordinatorLayout listaLayout;
    private LinearLayout lyt_progress, layout_wifi_error, vazio;
    private CuponsViewModel viewModel;
    private ImageButton adicionar_cupom;

    private boolean isConnection() {
        if(!Conexao.isConnected(this)){
            MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupons);
        dialog = new LoadingDialog(this);
        View sheetBottom = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheetBottom);
        adicionar_cupom = findViewById(R.id.adicionar_cupom);
        sheetDialog = new BottomSheetDialog(this);
        viewModel = new ViewModelProvider(this).get(CuponsViewModel.class);
        runOnUiThread(()-> viewModel.init(this));
        adaptador = new AdaptadorCupons(CuponsActivity.this, new ArrayList<>());
        lyt_progress = findViewById(R.id.lyt_progress);
        listaLayout = findViewById(R.id.listaLayout);
        vazio = findViewById(R.id.vazio);
        layout_wifi_error = findViewById(R.id.layout_wifi_error);
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adaptador);
        viewModel.getMutableLiveData().observe(this, cuponsElements -> {
            adaptador.setInsert(cuponsElements.getCupoms());
            listaLayout.setVisibility(cuponsElements.getListaVisibility());
            lyt_progress.setVisibility(cuponsElements.getProgressVisibility());
            vazio.setVisibility(cuponsElements.getVazioVisibility());
            layout_wifi_error.setVisibility(cuponsElements.getLayoutWifiOffline());
            adicionar_cupom.setVisibility(cuponsElements.getLayoutWifiOffline() != View.VISIBLE && cuponsElements.getProgressVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
        });
        adaptador.setOnClickItem((v, cupom, position)->{
           if(!sheetDialog.isShowing()){
               showDialog(cupom);
           }
        });
    }

    private void showDialog(Cupom cupom) {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_cupom_item_opcao, null);
        _view.findViewById(R.id.cancel).setOnClickListener(v1 -> sheetDialog.dismiss());
        String msg = getDst(cupom);
        ((TextView)_view.findViewById(R.id.sobre_cliente)).setText(msg);

        sheetDialog.setCancelable(false);
        sheetDialog.setContentView(_view);
        Button btn_action = _view.findViewById(R.id.desativar_ativar_cupom);
        if(cupom.isAtivo()){
            btn_action.setText(getString(R.string.desativar_cupom));
            btn_action.setBackgroundColor(getResources().getColor(R.color.vermelhoCancel));
        }else{
            btn_action.setText(R.string.ativar_cupom);
            btn_action.setBackgroundColor(getResources().getColor(R.color.colorBtnVerde));
        }
        btn_action.setOnClickListener((v->{
            sheetDialog.dismiss();
            DialogMessage message = new DialogMessage(this, getString(cupom.isAtivo() ? R.string.confirmar_desativar_cupom : R.string.confirmar_ativar_cupom),  true, getString(R.string.confirmar), null);
            message.show();
            message.setOnPossiveButtonClicked(() -> {
                if(Conexao.isConnected(this)){
                    onChanged(cupom);
                }else{
                    MyToast.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
                }
            });
        }));
        _view.findViewById(R.id.deletar_do_cupons).setOnClickListener(v2v -> {
            sheetDialog.dismiss();
            DialogMessage message = new DialogMessage(this, getString(R.string.confirmar_delete),  true, getString(R.string.confirmar), null);
            message.show();
            message.setOnPossiveButtonClicked(() -> {
                if(Conexao.isConnected(this)){
                    ondelete(cupom);
                }else{
                    MyToast.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
                }
            });
        });
        if(Build.VERSION.SDK_INT >= 21){
            sheetDialog.getWindow().addFlags(67108864);
        }
        ((View)_view.getParent()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.show();
    }

    private void onChanged(Cupom cupom) {
       if(isConnection()){
           dialog.show();
           viewModel.changed(cupom, this).observe(this, (success -> {
               if(success){
                   MyToast.makeText(this, cupom.isAtivo() ? R.string.cupom_desativado_sucesso : R.string.cupom_ativado_sucesso, ModoColor._success).show();
                   viewModel.update(this);
               }else{
                   MyToast.makeText(this, cupom.isAtivo() ? R.string.cupom_desativado_falha : R.string.cupom_ativado_falha, ModoColor._falha).show();
               }

               dialog.dismiss();
           }));
       }
    }

    private void ondelete(Cupom cupom) {
       if(isConnection()){
           dialog.show();
           viewModel.delete(cupom, this).observe(this, (success -> {
               if(success){
                   MyToast.makeText(this, R.string.cupom_deletado_sucesso, ModoColor._success).show();
                   viewModel.update(this);
               }else{
                   MyToast.makeText(this, R.string.cupom_deletado_falha, ModoColor._falha).show();
               }

               dialog.dismiss();
           }));
       }
    }

    private String getDst(Cupom cupom) {
       String msg =  getString(R.string.codigo) + " " + cupom.getCodigo() + "\n" +
                getString(R.string.tipo) + " " + getType(cupom.getDescontoType());

       if(cupom.isAtivo()){
           msg += "\n" + getString(R.string.cupom_ativo);
       }else msg += "\n" + getString(R.string.cupom_desativo);

       if(cupom.isAlluser()){
           msg += "\n" + getString(R.string.cupom_alluser);
       }else msg += "\n" + getString(R.string.cupom_not_alluser);

        if(cupom.isVencimento()){
            msg += "\n" + getValido(cupom);
        }else msg += "\n" + getString(R.string.cupom_valido);

        msg += "\n" + getDesconto(cupom);

        return msg;
    }

    private String getDesconto(Cupom cupom) {
        if(cupom.getDescontoType() != 0){
            if(cupom.getDescontoType() == 2){
                return Mask.formatarValor(cupom.getValorDesconto()) + " Off";
            }else return ((int)cupom.getValorDesconto()) + "% Off";
        }else return "";
    }

    private String getValido(Cupom cupom) {
        if(cupom.isExpirado()){
            if(cupom.isVencimento()){
                if(HelperManager.isVencido(cupom.getDataValidade())){
                    return getString(R.string.vencimento_expirado);
                }else  return getString(R.string.vencimento) + " " + DateTime.toDateString("dd/MM/yyyy", cupom.getDataValidade());
            }else{
                return getString(R.string.cupom_valido);
            }
        }else return getString(R.string.cupom_valido);
    }

    private String getType(int descontoType) {
        if(descontoType == 0){
            return getString(R.string.cupom_frete_grates);
        }else{
            return getString(R.string.cupom_desconto);
        }
    }

    @Override
    protected void onResume() {
        HelperManager.clieckTime = 0;
        viewModel.update(this);
        super.onResume();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onAdicionarCupom(View view) {
        if(HelperManager.isClicked()){
            Navigate.activity(this).navigate(AdicionarCupomActivity.class);
        }
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        viewModel.update(this);
    }
}