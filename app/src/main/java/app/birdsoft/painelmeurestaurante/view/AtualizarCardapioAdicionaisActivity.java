package app.birdsoft.painelmeurestaurante.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorItemAdicional;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorItemAdicionalOrdem;
import app.birdsoft.painelmeurestaurante.dialogo.DialogDeletar;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.model.ItemCardapio;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.CardapioViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AtualizarCardapioAdicionaisActivity extends AppCompatActivity {

    private boolean isUpdate = false;
    private RecyclerView mRecyclerView;
    private List<ItemCardapio> itemCardapios;
    private AdaptadorItemAdicional adaptador;
    private AdaptadorItemAdicionalOrdem adaptadorOrdem;
    private static final int MAS = 1, MENOS = 2;
    private BottomSheetDialog sheetDialog;
    private int limite = 1;
    private BottomSheetBehavior sheetBehavior;
    private ItemTouchHelper mItemTouchHelper;
    private ImageButton adicionar_item, on_config_cardapio;
    private Button ordenar_blocos, btn_confirnar_publicar, btn_cancelar_publicar;
    private Cardapio cardapio;
    private LinearLayout layout_principal;
    private LinearLayout vazio;
    private TextView displayName, summary, valorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_cardapio_adicionais);
        cardapio = (Cardapio)getIntent().getSerializableExtra("cardapio");
        View sheetBottom = findViewById(R.id.bottom_sheet);
        adicionar_item = findViewById(R.id.adicionar_item);
        on_config_cardapio = findViewById(R.id.on_config_cardapio);
        sheetBehavior = BottomSheetBehavior.from(sheetBottom);
        sheetDialog = new BottomSheetDialog(this);
        btn_confirnar_publicar  =findViewById(R.id.btn_confirnar_publicar);
        btn_cancelar_publicar = findViewById(R.id.btn_cancelar_publicar);
        ordenar_blocos = findViewById(R.id.ordenar_blocos);
        itemCardapios = new ArrayList<>();
        layout_principal = findViewById(R.id.layout_principal);
        LinearLayout lyt_progress = findViewById(R.id.lyt_progress);
        valorText = findViewById(R.id.valorText);
        LinearLayout layout_wifi_error = findViewById(R.id.layout_wifi_error);
        summary = findViewById(R.id.summary);
        displayName = findViewById(R.id.displayName);
        vazio = findViewById(R.id.vazio);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorItemAdicional(this, new ArrayList<>());
        adaptadorOrdem = new AdaptadorItemAdicionalOrdem(this, new ArrayList<>());
        if(cardapio != null) {
            itemCardapios = HelperManager.convert(cardapio.getCardapio());
            if (cardapio != null) {
                displayName.setText(cardapio.getName());
                if (cardapio.getReceita() != null) {
                    summary.setText(cardapio.getReceita());
                }
                valorText.setText(Mask.formatarValor(cardapio.getValor()));
            }
            if(itemCardapios.size() > 0){
                layout_principal.setVisibility(View.VISIBLE);
                vazio.setVisibility(View.GONE);
            }else{
                layout_principal.setVisibility(View.VISIBLE);
                vazio.setVisibility(View.VISIBLE);
            }
            layout_wifi_error.setVisibility(View.GONE);
            lyt_progress.setVisibility(View.GONE);
            adicionar_item.setVisibility(View.VISIBLE);
            insertList();
        }
    }

    private boolean isConnection() {
        if(!Conexao.isConnected(this)){
            MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mRecyclerView.getAdapter() == adaptadorOrdem){
            mItemTouchHelper = null;
            mRecyclerView.setAdapter(adaptador);
            adicionar_item.setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.ordenar_blocos)).setText(R.string.ordenar);
            return;
        }
        super.onBackPressed();
    }

    private void insertList() {
        adaptador = new AdaptadorItemAdicional(this, itemCardapios);
        mRecyclerView.setAdapter(adaptador);
        mItemTouchHelper = null;
        adaptador.setOnDeleteItemListener((v, nome, position) ->{
            DialogDeletar deletar = new DialogDeletar(this, nome);
            deletar.show();
            deletar.setOnDeleteConfirmation((nome1 ->{
                itemCardapios.remove(position);
                insertList();
                isUpdate = true;
            }));

        });
        adaptador.setOnEditItemListener(((v, bloco, posicao) -> {
            try{
                Intent intent = new Intent(this, AdicionarItensAdicionaisActivity.class);
                intent.putExtra("data", HelperManager.convert(bloco));
                intent.putExtra("position", posicao);
                Navigate.activity(this).navigateForResult(intent, 1010);
            }catch (Exception x){
                System.out.println(x.getMessage());
            }
        }));
        adaptador.setOnItemCheckedChanged((v, title, preco, itemCardapio, position, checkBox, layout, btnRemove, btnAdd, number, maxItensAdicionais) -> {
            try{
                boolean isAdd = false;

                if(layout != null){
                    isAdd = true;
                    btnAdd.setOnClickListener(view -> {
                        if(checkBox.isChecked()){
                            try{
                                int itensAdicionaisCount = Integer.parseInt(number.getText().toString());
                                itensAdicionaisCount += 1;
                                number.setText(String.format("%s", itensAdicionaisCount));
                                btnRemove.setEnabled(itensAdicionaisCount > 1);
                                if(maxItensAdicionais > 0){
                                    btnAdd.setEnabled(itensAdicionaisCount < maxItensAdicionais);
                                }
                                itemCardapio.valor += preco;
                                for (int i = 0; i < itemCardapio.lista.getContents().size(); i++) {
                                    if(itemCardapio.lista.getContents().get(i).equals(title) && itemCardapio.lista.getValores().get(i).equals(preco)){
                                        itemCardapio.lista.getQuantidate().set(i, itensAdicionaisCount);
                                    }
                                }
                                valorView();
                            }catch (Exception x){
                                // message(x.getMessage());
                            }
                        }
                    });
                    btnRemove.setOnClickListener(view -> {
                        if(checkBox.isChecked()){
                            int itensAdicionaisCount = Integer.parseInt(number.getText().toString());
                            itensAdicionaisCount -= 1;
                            number.setText(String.format("%s", itensAdicionaisCount));
                            itemCardapio.valor -= preco;
                            for (int i = 0; i < itemCardapio.lista.getContents().size(); i++) {
                                if(itemCardapio.lista.getContents().get(i).equals(title) && itemCardapio.lista.getValores().get(i).equals(preco)){
                                    itemCardapio.lista.getQuantidate().set(i, itensAdicionaisCount);
                                }
                            }
                            btnRemove.setEnabled(itensAdicionaisCount >= 2);
                            if(maxItensAdicionais != 0){
                                if(itensAdicionaisCount < maxItensAdicionais){
                                    btnAdd.setEnabled(true);
                                }
                            }
                            valorView();
                        }
                    });

                    if(checkBox.isChecked()){
                        int itensAdicionaisCount = Integer.parseInt(number.getText().toString());
                        Double y = itemCardapio.valor;
                        for (int i = 0; i < itensAdicionaisCount; i++) {
                            y += preco;
                        }
                        itemCardapio.valor = y;
                        if(maxItensAdicionais > 1){
                            layout.setVisibility(View.VISIBLE);
                        }
                        itemCardapio.lista.setDisplayName(itemCardapio.getDispayTitulo() + "");
                        itemCardapio.lista.getContents().add(title);
                        itemCardapio.lista.getValores().add(preco);
                        itemCardapio.lista.getQuantidate().add(itensAdicionaisCount);
                        valorView();
                        layout.setVisibility(maxItensAdicionais > 1 ? View.VISIBLE : View.GONE);
                    }else{

                        int itensAdicionaisCount = Integer.parseInt(number.getText().toString());
                        Double y = itemCardapio.valor;
                        for (int i = 0; i < itensAdicionaisCount; i++) {
                            y -= preco;
                        }
                        itemCardapio.valor = y;
                        if(maxItensAdicionais > 1){
                            layout.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < itemCardapio.lista.getContents().size(); i++) {
                            if(itemCardapio.lista.getContents().get(i).equals(title) && itemCardapio.lista.getValores().get(i).equals(preco)){
                                itemCardapio.lista.getValores().remove(i);
                                itemCardapio.lista.getContents().remove(i);
                                itemCardapio.lista.getQuantidate().remove(i);
                                if(itemCardapio.lista.getContents().size() == 0){
                                    itemCardapio.lista.setDisplayName("");
                                }
                            }
                        }
                        valorView();
                        layout.setVisibility(View.GONE);
                    }
                }

                if (!itemCardapio.isMultiselect()) {
                    if (checkBox.isChecked()) {
                        itemCardapio.max.add(checkBox);
                        if (itemCardapio.max.size() == itemCardapio.getSelectMax()) {
                            for (int i = 0; i < itemCardapio.allCheckBox.size(); i++){
                                CheckBox box = itemCardapio.allCheckBox.get(i);
                                for (int j = 0; j < itemCardapio.max.size(); j++) {
                                    CheckBox check = itemCardapio.max.get(j);
                                    if (check != box) {
                                        box.setEnabled(false);
                                    }
                                }
                            }

                            for (CheckBox x : itemCardapio.max) {
                                x.setEnabled(true);
                            }

                            //bloco.max.get(0).setChecked(false);
                            //bloco.max.remove(0)
                        }
                    } else {
                        for (int i = 0; i < itemCardapio.max.size(); i++) {
                            if (itemCardapio.max.get(i) == checkBox) {
                                itemCardapio.max.remove(i);
                                for (CheckBox check : itemCardapio.allCheckBox) {
                                    check.setEnabled(true);
                                }
                            }
                        }
                    }

                }

                if(!isAdd){
                    if(checkBox.isChecked()){
                        itemCardapio.lista.setDisplayName(itemCardapio.getDispayTitulo() + "");
                        itemCardapio.lista.getContents().add(title);
                        itemCardapio.lista.getValores().add(preco);
                        itemCardapio.lista.getQuantidate().add(1);
                    }else{
                        for (int i = 0; i < itemCardapio.lista.getContents().size(); i++) {
                            if(itemCardapio.lista.getContents().get(i).equals(title) && itemCardapio.lista.getValores().get(i).equals(preco)){
                                itemCardapio.lista.getValores().remove(i);
                                itemCardapio.lista.getContents().remove(i);
                                itemCardapio.lista.getQuantidate().remove(i);
                                if(itemCardapio.lista.getContents().size() == 0){
                                    itemCardapio.lista.setDisplayName("");
                                }
                            }
                        }
                    }
                    if(checkBox.isChecked()){
                        if(itemCardapio.isValorMaior()){
                            if(itemCardapio.valor < preco) {
                                itemCardapio.valor = preco;
                            }
                        }else{
                            itemCardapio.valor += preco;
                        }
                    }else{
                        if(itemCardapio.isValorMaior()){
                            double newValor = 0;
                            for (int i = 0; i < itemCardapio.lista.getValores().size(); i++) {
                                Double x = itemCardapio.lista.getValores().get(i);
                                if(newValor < x){
                                    newValor = x;
                                }
                            }

                            itemCardapio.valor = newValor;
                        }else{
                            itemCardapio.valor -= preco;
                        }
                    }
                }



                valorView();
                itemCardapio.modificate = itemCardapio.lista.getContents().size() > 0;
                itemCardapio.selected_obg = itemCardapio.modificate;
            }catch (Exception x){
                //message(x.getMessage());
            }
        });
        onLista();
        valorView();
    }

    private void valorView() {
        List<ItemCardapio> itemCardapioList = adaptador.getLista();
        double x = 0;
        for (int i = 0; i < itemCardapioList.size(); i++) {
            x += itemCardapioList.get(i).valor;
        }

        if(cardapio.getTipoLanche() == 0 || cardapio.getTipoLanche() == 1){
            x += cardapio.getValor();
        }
        double valor = x;
        valorText.setText(Mask.formatarValor(valor));
    }

    private void onLista() {
        if(adaptador.getItemCount() > 0){
            layout_principal.setVisibility(View.VISIBLE);
            vazio.setVisibility(View.GONE);
        }else{
            vazio.setVisibility(View.VISIBLE);
            layout_principal.setVisibility(View.GONE);
        }
        btn_confirnar_publicar.setVisibility(adaptador.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        ordenar_blocos.setVisibility(adaptador.getItemCount() >= 2 ? View.VISIBLE : View.GONE);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onConfigCardapio(View view) {
        Intent intent = new Intent(this, AtualizarCardapioItensActivity.class);
        intent.putExtra("data", cardapio);
        Navigate.activity(this).navigateForResult(intent, 10);
    }

    public void onOrdenarBlocos(View view) {
        if(((Button)view).getText().equals(getString(R.string.confirmar))){
            isUpdate = true;
            itemCardapios = adaptadorOrdem.getLista();
             btn_confirnar_publicar.setVisibility(itemCardapios.size() > 0 ? View.VISIBLE : View.GONE);
            btn_cancelar_publicar.setVisibility(View.VISIBLE);
            adicionar_item.setVisibility(View.VISIBLE);
            on_config_cardapio.setVisibility(View.VISIBLE);
            ((Button)view).setText(R.string.ordenar);
            insertList();
        }else{
            adaptadorOrdem = new AdaptadorItemAdicionalOrdem(this, itemCardapios);
            mRecyclerView.setAdapter(adaptadorOrdem);
            mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(adaptadorOrdem));
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
            btn_confirnar_publicar.setVisibility(View.GONE);
            btn_cancelar_publicar.setVisibility(View.GONE);
            on_config_cardapio.setVisibility(View.GONE);
            adicionar_item.setVisibility(View.GONE);
            ((Button)view).setText(R.string.confirmar);
        }
    }

    public void onCancelarItens(View view) {
        finish();
    }

    public void onAdicionarItemCardapio(View view) {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_selecionador_tipo_item_cardapio, null);
        TextInputLayout blockName;
        SwitchCompat obgdSelected, valorMaior, multiSelect, quantidade;
        LinearLayout layoutMultiSelect;
        ImageButton selectRemove, selectAdd;
        TextView numeroLimite;
        limite = 1;
        quantidade = _view.findViewById(R.id.quantidade);
        blockName = _view.findViewById(R.id.blockName);
        obgdSelected = _view.findViewById(R.id.obgdSelected);
        valorMaior = _view.findViewById(R.id.valorMaior);
        layoutMultiSelect = _view.findViewById(R.id.layoutMultiSelect);
        multiSelect = _view.findViewById(R.id.multiSelect);
        selectRemove = _view.findViewById(R.id.selectRemove);
        selectAdd = _view.findViewById(R.id.selectAdd);
        numeroLimite = _view.findViewById(R.id.numeroLimite);
        selectRemove.setEnabled(limite > 1);
        selectAdd.setOnClickListener(view12 -> limiteView(MAS, numeroLimite, selectRemove));
        selectRemove.setOnClickListener(view1 -> limiteView(MENOS, numeroLimite, selectRemove));
        quantidade.setOnCheckedChangeListener((compoundButton, b) -> {
            valorMaior.setChecked(false);
            valorMaior.setEnabled(!b);
        });
        multiSelect.setOnCheckedChangeListener((compoundButton, b) -> layoutMultiSelect.setVisibility(b ? View.GONE : View.VISIBLE));

        Objects.requireNonNull(blockName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(blockName.isErrorEnabled()){
                    blockName.setError("");
                    blockName.setErrorEnabled(false);
                }
            }
        });
        sheetDialog.setCancelable(false);
        sheetDialog.setContentView(_view);
        _view.findViewById(R.id.cancel).setOnClickListener((v -> sheetDialog.dismiss()));
        _view.findViewById(R.id.confirmar).setOnClickListener((v -> {
            String displayName = blockName.getEditText().getText().toString().trim();
            if(!displayName.equals("")) {
                ItemCardapio bloco = new ItemCardapio();
                bloco.setDispayTitulo(displayName);
                bloco.setValorMaior(valorMaior.isChecked());
                bloco.setMultiselect(multiSelect.isChecked());
                bloco.setObgdSelect(obgdSelected.isChecked());
                bloco.setSelectMax(!multiSelect.isChecked() ? limite : -1);
                bloco.setPositionSelect(obgdSelected.isChecked() ? 0 : -1);
                bloco.setItensAdicionais(quantidade.isChecked());
                Intent intent = new Intent(this, AdicionarItensAdicionaisActivity.class);
                intent.putExtra("data", HelperManager.convert(bloco));
                intent.putExtra("position", -1);
                Navigate.activity(this).navigateForResult(intent, 1010);
                sheetDialog.dismiss();
            }else{
                MyToast.makeText(this, R.string.digite_nome_bloco, ModoColor._falha).show();
            }
        }));
        if(Build.VERSION.SDK_INT >= 21){
            sheetDialog.getWindow().addFlags(67108864);
        }
        ((View)_view.getParent()).setBackgroundColor(getResources().getColor(R.color.transparent));
        sheetDialog.show();
    }

    private void limiteView(int index, TextView numeroLimite, ImageButton selectRemove) {
        if(index == MAS){
            limite += 1;
        }else{
            limite -= 1;
        }
        numeroLimite.setText(String.valueOf(limite));
        viewEnabledBtn(selectRemove);
    }

    private void viewEnabledBtn(ImageButton selectRemove) {
        selectRemove.setEnabled(limite > 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1010){
            if(requestCode == 1010){
                assert data != null;
                int position = data.getIntExtra("position", -1);
                ItemCardapio bloco = (ItemCardapio) data.getSerializableExtra("data");
                if(position == -1){
                    itemCardapios.add(bloco);
                }else{
                    itemCardapios.set(position, bloco);
                }
                insertList();
                onLista();
                isUpdate = true;
            }
        }else if(resultCode == 10){
           if(requestCode == 10){
               assert data != null;
               this.cardapio = (Cardapio)data.getSerializableExtra("data");
               if(cardapio != null){
                   displayName.setText(cardapio.getName());
                   if(cardapio.getReceita() != null){
                       summary.setText(cardapio.getReceita());
                   }
                   valorText.setText(Mask.formatarValor(cardapio.getValor()));
               }
           }
        }
    }

    public void onConfirmarItens(View view) {
      if(isConnection()){
          if(isUpdate){
              LoadingDialog dialog = new LoadingDialog(this);
              dialog.show();
              new ViewModelProvider(this).get(CardapioViewModel.class).insertSubDados(cardapio, itemCardapios, this).observe(this, (sucesso->{
                  if(sucesso){
                      MyToast.makeText(this, R.string.cardapio_atualizado_sucesso, ModoColor._success).show();
                      finish();
                  }else{
                      MySnackbar.makeText(this, R.string.cardapio_atualizado_erro, ModoColor._falha).show();
                  }
                  dialog.dismiss();
              }));
          }else{
              MyToast.makeText(this, R.string.cardapio_atualizado_sucesso, ModoColor._success).show();
              finish();

          }
      }
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        new ViewModelProvider(this).get(CardapioViewModel.class).update(this);
    }
}