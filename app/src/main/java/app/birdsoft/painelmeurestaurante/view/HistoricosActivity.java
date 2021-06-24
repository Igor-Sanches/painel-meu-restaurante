package app.birdsoft.painelmeurestaurante.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorHistoricos;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.HistoricosViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class HistoricosActivity extends AppCompatActivity {

    private BottomSheetDialog sheetDialog;
    private BottomSheetBehavior sheetBehavior;
    private LoadingDialog dialog;
    private AdaptadorHistoricos adaptador;
    private LinearLayout lyt_progress, layout_wifi_error, listaLayout, vazio;
    private HistoricosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historicos);
        dialog = new LoadingDialog(this);
        View sheetBottom = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheetBottom);
        sheetDialog = new BottomSheetDialog(this);
        adaptador = new AdaptadorHistoricos(this, new ArrayList<>());
        lyt_progress = findViewById(R.id.lyt_progress);
        listaLayout = findViewById(R.id.listaLayout);
        vazio = findViewById(R.id.vazio);
        layout_wifi_error = findViewById(R.id.layout_wifi_error);
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adaptador);
        viewModel = new ViewModelProvider(this).get(HistoricosViewModel.class);
        runOnUiThread(()-> viewModel.init(this));
        viewModel.getMutableLiveData().observe(this, historicoElements -> {
            adaptador.setInsert(historicoElements.getPedidos());
            listaLayout.setVisibility(historicoElements.getListaVisibility());
            lyt_progress.setVisibility(historicoElements.getProgressVisibility());
            vazio.setVisibility(historicoElements.getVazioVisibility());
            layout_wifi_error.setVisibility(historicoElements.getLayoutWifiOffline());
        });
        adaptador.setOnClickItem((view, pedido, position) -> {
            if(!sheetDialog.isShowing()) {
                onDialog(pedido);
            }
        });
    }

    private void onDialog(Pedido pedido) {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_historico_item_opcao, null);
        _view.findViewById(R.id.cancel).setOnClickListener((v -> sheetDialog.dismiss()));
        String name;
        if(pedido.getItensPedido().size() > 1){
            name = pedido.getItensPedido().get(0).getDisplayName() + " "+getString(R.string.e_mais) + " "+ (pedido.getItensPedido().size() - 1);
        }else name = pedido.getItensPedido().get(0).getDisplayName();
        String msg="";
        msg += "Pedido: " + name + "\n" + "Cliente: " + pedido.getClienteNome() + "\nValor: " + Mask.formatarValor(pedido.getValorTotal());
        ((TextView)_view.findViewById(R.id.sobre_cliente)).setText(msg);

        sheetDialog.setCancelable(false);
        sheetDialog.setContentView(_view);
        _view.findViewById(R.id.deletar_do_historico).setOnClickListener((v -> {
            sheetDialog.dismiss();
            DialogMessage message = new DialogMessage(this, getString(R.string.delete_historico_msg),  true, getString(R.string.confirmar), getString(R.string.deletar_historico));
            message.show();
            message.setOnPossiveButtonClicked(() -> {
                if(Conexao.isConnected(this)){
                    viewModel.delete(pedido, this).observe(this, (success ->{
                        if(success){
                            viewModel.update(this);
                            MySnackbar.makeText(this, R.string.historico_deletado, ModoColor._success).show();
                        }else{
                            MySnackbar.makeText(this, R.string.historico_deletado_erro, ModoColor._success).show();
                        }
                        dialog.dismiss();
                    }));
                }else{
                    MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._success).show();
                }
            });
        }));
        if(Build.VERSION.SDK_INT >= 21){
            sheetDialog.getWindow().addFlags(67108864);
        }
        ((View)_view.getParent()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.show();
    }

    @Override
    protected void onResume() {
        onUpdateData();
        super.onResume();
    }

    private void onUpdateData() {
        viewModel.update(this);
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        viewModel.update(this);
    }

    public void onBack(View view) {
        onBackPressed();
    }
}