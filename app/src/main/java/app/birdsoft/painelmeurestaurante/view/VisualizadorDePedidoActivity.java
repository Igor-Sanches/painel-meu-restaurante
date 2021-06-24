package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadoPedidos;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.tools.Status;
import app.birdsoft.painelmeurestaurante.viewModel.PedidosViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;

public class VisualizadorDePedidoActivity extends AppCompatActivity {

    private Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizador_de_pedido);

        pedido = (Pedido)getIntent().getSerializableExtra("pedido");
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdaptadoPedidos adaptador = new AdaptadoPedidos(this, pedido.getItensPedido());
        mRecyclerView.setAdapter(adaptador);
        if(pedido.getObservacao() != null){
            if(!pedido.getObservacao().equals("")){
                findViewById(R.id.observacao_layout).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.mensagem)).setText(pedido.getObservacao());
            }
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onAvancarPedido(View view) {
        LoadingDialog loading = new LoadingDialog(this);
        loading.show();
        Status status = getNovoStatus(Status.valueOf(pedido.getStatusPedido()));
        pedido.isItem = null;
        pedido.isMenuOptions = null;
        pedido.isStatusOptions = null;
        pedido.setCancelado(status != Status.pedidoEntregue && pedido.isCancelado());
        pedido.setStatusPedido(status.toString());
        new ViewModelProvider(this).get(PedidosViewModel.class).update(pedido, this).observe(this, (success->{
            if(success){
                MySnackbar.makeText(this, getMensagemStatus(status), ModoColor._success).show();
                finish();
            }else{
                MySnackbar.makeText(this, R.string.error_avanca, ModoColor._falha).show();
            }
            loading.dismiss();
        }));

    }

    private @StringRes
    int getMensagemStatus(Status status) {
        @StringRes int res = R.string.aguarde;
        switch (status){
            case novoPedido:
                res = R.string.novo_pedido_chegou;
                break;
            case pedidoEntregue:
                res = R.string.pedido_entregue_msg;
                break;
            case pedidoEmTransido:
                res = R.string.pedido_em_transito;
                break;
            case preparandoPedido:
                res = R.string.pedido_em_preparo;
                break;
        }
        return res;
    }

    private Status getNovoStatus(Status status) {
        if(status == Status.novoPedido)
            status = Status.preparandoPedido;
        else if(status == Status.preparandoPedido)
            status = Status.pedidoEmTransido;
        else if(status == Status.pedidoEmTransido)
            status = Status.pedidoEntregue;

        return status;
    }

}