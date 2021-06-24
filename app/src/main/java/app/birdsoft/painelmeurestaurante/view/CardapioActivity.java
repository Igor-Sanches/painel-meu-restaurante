package app.birdsoft.painelmeurestaurante.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorCardapio;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorCardapioOrdem;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.CardapioViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class CardapioActivity extends AppCompatActivity {

    private LinearLayout vazio, listaLayout, layout_wifi_error, lyt_progress;
    private AdaptadorCardapio adaptador;
    private CardapioViewModel viewModel;
    private LoadingDialog loading;
    private ImageButton adicionar_cardapio;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;
    private AdaptadorCardapioOrdem adaptadorOrdem;

    @Override
    public void onBackPressed() {
        if(mRecyclerView.getAdapter() == adaptadorOrdem){
            mItemTouchHelper = null;
            mRecyclerView.setAdapter(adaptador);
            adicionar_cardapio.setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.ordenar_cardapio)).setText(R.string.ordenar);
            return;
        }
        super.onBackPressed();
    }

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
        setContentView(R.layout.activity_cardapio);
        viewModel = new ViewModelProvider(this).get(CardapioViewModel.class);
        viewModel.init(this);
        loading = new LoadingDialog(this);
        adaptador = new AdaptadorCardapio(new ArrayList<>(), this);
        adaptadorOrdem = new AdaptadorCardapioOrdem(new ArrayList<>(), this);
        vazio = findViewById(R.id.vazio);
        adicionar_cardapio = findViewById(R.id.adicionar_cardapio);
        listaLayout = findViewById(R.id.listaLayout);
        layout_wifi_error = findViewById(R.id.layout_wifi_error);
        lyt_progress = findViewById(R.id.lyt_progress);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adaptador);
        viewModel.getMutableLiveData().observe(this, cardapioElements -> {
            if(cardapioElements.getCardapios() != null){
                findViewById(R.id.ordenar_cardapio).setVisibility(cardapioElements.getCardapios().size() > 1 && cardapioElements.getLayoutWifiOffline() != View.VISIBLE && cardapioElements.getProgressVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
                adaptador.insert(cardapioElements.getCardapios());
                adicionar_cardapio.setVisibility(!findViewById(R.id.ordenar_cardapio).equals(getString(R.string.confirmar)) && cardapioElements.getLayoutWifiOffline() != View.VISIBLE && cardapioElements.getProgressVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
            }
            vazio.setVisibility(cardapioElements.getVazioVisibility());
            listaLayout.setVisibility(cardapioElements.getListaVisibility());
            lyt_progress.setVisibility(cardapioElements.getProgressVisibility());
            layout_wifi_error.setVisibility(cardapioElements.getLayoutWifiOffline());
        });
        adaptador.setOnClonarItemListener((((v, item, position) -> {
            DialogMessage message
                    = new DialogMessage(this, getString(R.string.clone_mesg), true, getString(R.string.confirmar));
            message.show();
            message.setOnPossiveButtonClicked(() -> {

                        if(isConnection()){
                            loading.show();
                            viewModel.clone(item, this).observe(this, (success->{
                                if(success){
                                    MySnackbar.makeText(this, R.string.clone_success, ModoColor._success).show();
                                    viewModel.update(this);
                                }else{
                                    MySnackbar.makeText(this, R.string.clone_erro, ModoColor._falha).show();
                                }
                                loading.dismiss();
                            }));
                        }

                    });
        })));
        adaptador.setOnEditarItemListener((((v, item, position) -> {
            Intent intent;
            if(item.getTipoLanche() == 2){
                intent = new Intent(this, AtualizarCardapioUnicoActivity.class);
            }else{
                intent = new Intent(this, AtualizarCardapioAdicionaisActivity.class);
            }
            intent.putExtra("cardapio", item);
            Navigate.activity(this).navigate(intent);
        })));
        adaptador.setOnDeleteItemListener((((v, item, position) -> {

            DialogMessage message
                    = new DialogMessage(this, getString(R.string.msg_delete), true, getString(R.string.confirmar));
            message.show();
            message.setOnPossiveButtonClicked(() -> {
                 if(isConnection()){
                            loading.show();
                            viewModel.delete(item, this).observe(this, (success->{
                                if(success){
                                    MySnackbar.makeText(this, R.string.item_cardapio_apagado, ModoColor._success).show();
                                    viewModel.update(this);
                                }else{
                                    MySnackbar.makeText(this, R.string.item_cardapio_apagado_erro, ModoColor._success).show();
                                }
                                loading.dismiss();
                            }));
                        }
                    });
        })));
    }

    @Override
    public void onResume() {
        HelperManager.clieckTime = 0;
        viewModel.update(this);
        super.onResume();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onAdicionarCardapio(View view) {
        if(HelperManager.isClicked()){
            Navigate.activity(this).navigate(AdicionarCardapioActivity.class);
        }
    }

    public void onOrdenarCardapio(View view) {
        if(((Button)view).getText().equals(getString(R.string.confirmar))){
            viewModel.update(adaptadorOrdem.getLista(), this).observe(this, (success -> {
                mItemTouchHelper = null;
                findViewById(R.id.adicionar_cardapio).setVisibility(View.VISIBLE);
                ((Button)view).setText(R.string.ordenar);
                if(success){
                    adaptador.insert(adaptadorOrdem.getLista());
                    MySnackbar.makeText(this, R.string.lista_atualizada, ModoColor._success).show();
                    viewModel.update(this);
                }else{
                    MySnackbar.makeText(this, R.string.lista_atualizada_erro, ModoColor._falha).show();
                }
                mRecyclerView.setAdapter(adaptador);
            }));
        }else{
            if(mRecyclerView.getAdapter() == adaptador){
                adaptadorOrdem.insert(adaptador.getLista());
                mRecyclerView.setAdapter(adaptadorOrdem);
                mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(adaptadorOrdem));
                mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                findViewById(R.id.adicionar_cardapio).setVisibility(View.GONE);
                ((Button)view).setText(R.string.confirmar);
            }
        }
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        viewModel.update(this);
    }
}