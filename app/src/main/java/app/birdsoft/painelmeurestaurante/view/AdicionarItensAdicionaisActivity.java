package app.birdsoft.painelmeurestaurante.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorItensLista;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorItensListaOrdem;
import app.birdsoft.painelmeurestaurante.dialogo.DialogDeletar;
import app.birdsoft.painelmeurestaurante.dialogo.DialogInput;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.MainDialog;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.BlocoPublicar;
import app.birdsoft.painelmeurestaurante.model.ItemCardapio;
import app.birdsoft.painelmeurestaurante.model.ItensBlocoCardapio;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AdicionarItensAdicionaisActivity extends AppCompatActivity {

    private ItemCardapio bloco;
    private int position;
    private static final int MAS = 1, MENOS = 2;
    private Button btn_confirnar;
    private int limite = 1;
    private RecyclerView mRecyclerView;
    private AdaptadorItensLista adaptador;
    private ArrayList<ItensBlocoCardapio> itemCardapios;
    private AdaptadorItensListaOrdem adaptadorOrdem;
    private LinearLayout vazio, listaLayout;
    private ItemTouchHelper mItemTouchHelper;
    private double valor = 0;
    private BottomSheetDialog dialog_selecionador;
    private BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_adicionar_itens_adicionais);
            View sheetBottom = findViewById(R.id.bottom_sheet);
            sheetBehavior = BottomSheetBehavior.from(sheetBottom);
            adaptadorOrdem = new AdaptadorItensListaOrdem((itemCardapios = new ArrayList<>()), this);
            adaptador = new AdaptadorItensLista((itemCardapios = new ArrayList<>()), this);
            dialog_selecionador = new BottomSheetDialog(this);
            btn_confirnar = findViewById(R.id.btn_confirnar);
            position = getIntent().getIntExtra("position", -1);
            bloco = HelperManager.convert((BlocoPublicar)getIntent().getSerializableExtra("data"));
            ((TextView)findViewById(R.id.displayName)).setText(bloco.getDispayTitulo());
            if(position != -1) {
                 itemCardapios = new ArrayList<>();
                ArrayList<Double> valores = bloco.getValores();
                ArrayList<String> contents = bloco.getContents();
                ArrayList<Boolean> texts = bloco.getText();
                ArrayList<String> textos = bloco.getTextos();
                ArrayList<Integer> quantidade = bloco.getMaxItensAdicionais();
                for (int i = 0; i < contents.size(); i++) {
                    Double valor = valores.get(i);
                    Boolean isText = null;
                    String texto = null;
                    if(texts != null){
                        isText = texts.get(i);
                        texto = textos.get(i);
                    }
                    String content = contents.get(i);
                    ItensBlocoCardapio _itens = new ItensBlocoCardapio();
                    if(quantidade != null){
                        int quantidadeCount = quantidade.get(i);
                        _itens.setMaxItensQuantidate(quantidadeCount);
                    }
                   if(texts != null){
                       _itens.setTexto(texto);
                       _itens.setText(isText);
                   }
                    _itens.setValor(valor);
                    _itens.setContent(content);
                    itemCardapios.add(_itens);
                }
                adaptador.insert(itemCardapios);
            }
            vazio = findViewById(R.id.vazio);
            listaLayout = findViewById(R.id.listaLayout);
            mRecyclerView = findViewById(R.id.mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(adaptador);
            onLista();
            adaptador.setmOnItemSelectionClickListener((v, item, position) -> {
                PopupMenu menu = new PopupMenu(this, v);
                menu.inflate(R.menu.menu_itens);
                menu.setOnMenuItemClickListener(item1 -> {
                    if(item1.getItemId() == R.id.delete){
                        DialogDeletar deletar = new DialogDeletar(this, item.getContent());
                        deletar.show();
                        deletar.setOnDeleteConfirmation((nome -> {
                            adaptador.removeItem(position);
                            onLista();
                        }));
                    }else{
                        if(item.isText()){
                            onEditorTexto(item, position);
                        }else{
                            if(bloco.isItensAdicionais()){
                            onDialogoEditorQuant(item, position);
                        }else{
                            onDialogoEditor(item, position);
                        }
                        }
                    }
                    return false;
                });
                menu.show();
            });
        }catch (Exception x){
            System.out.println(x.getMessage());
        }
    }

    private void onEditorTexto(ItensBlocoCardapio item, int position) {
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.card_nome, null);
        DialogInput alert = new DialogInput(this, root, getString(R.string.nome_title));
        alert.show();
        TextInputLayout nomeInput = root.findViewById(R.id.userName);
        Objects.requireNonNull(nomeInput.getEditText()).setText(item.getTexto());
        nomeInput.getEditText().setSelection(item.getTexto().length());
        root.findViewById(R.id.confirmar).setOnClickListener((v -> {
            String nome = nomeInput.getEditText().getText().toString().trim();
            if(!nome.equals("")){
                ItensBlocoCardapio pedido1 = itemCardapios.get(position);
                pedido1.setTexto(nome);
                adaptador.insert(itemCardapios);
                onLista();
                MyToast.makeText(this, R.string.item_atualizado, ModoColor._success).show();
                alert.dismiss();
            }else{
                MySnackbar.makeText(this, R.string.digite_nome, ModoColor._falha).show();
            }
        }));
        root.findViewById(R.id.cancel).setOnClickListener((v -> alert.dismiss()));

    }

    public void onDialogoEditorQuant(ItensBlocoCardapio item, int position){
        valor = item.getValor();
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.card_add_item_cardapio_quant, null);
        DialogInput sheetDialog = new DialogInput(this, root, getString(R.string.adicione_bloco));
        TextInputLayout itemnome = root.findViewById(R.id.itemnome);
        FrameLayout valorItem = root.findViewById(R.id.valorItem);
        TextView textValor = root.findViewById(R.id.valorText);
        TextView number = root.findViewById(R.id.number);
        number.setText(String.valueOf(item.getMaxItensQuantidate()));
        Objects.requireNonNull(itemnome.getEditText()).setText(item.getContent());
        itemnome.getEditText().setSelection(itemnome.getEditText().getText().length());
        textValor.setText(Mask.formatarValor(valor));
        ImageButton btnRemove = root.findViewById(R.id.btnRemove);
        ImageButton btnAdd = root.findViewById(R.id.btnAdd);
        btnRemove.setEnabled(Integer.parseInt(number.getText().toString()) > 0);
        btnRemove.setOnClickListener(view14 -> {
            int index = Integer.parseInt(number.getText().toString());
            index -= 1;
            number.setText(String.format("%s", index));
            btnRemove.setEnabled(index > 0);
        });
        btnAdd.setOnClickListener(view12 -> {
            int index = Integer.parseInt(number.getText().toString());
            index += 1;
            number.setText(String.format("%s", index));
            btnRemove.setEnabled(index > 0);
        });
        valorItem.setOnClickListener(view1 -> {
            MainDialog dialog = new MainDialog();
            dialog.gerarTeclado(this, valor);
            dialog.setOnConfirmationClick(((valor2, real) -> {
                valor = valor2;
                textValor.setText(real);
            }));
        });
        root.findViewById(R.id.confirmar).setOnClickListener(view15 -> {
            String item_ = itemnome.getEditText().getText().toString().trim();
            if(!item_.equals("")){
                if(valor == 0){
                    DialogMessage message
                            = new DialogMessage(this, getString(R.string.salvar_sem_preco), true, getString(R.string.confirmar));
                    message.show();
                    message.setOnPossiveButtonClicked(() -> {
                        sheetDialog.dismiss();
                        int index = Integer.parseInt(number.getText().toString());
                        atalizarItemAlista(item_, valor, position, index);
                    });
                }else{
                    sheetDialog.dismiss();
                    int index = Integer.parseInt(number.getText().toString());
                    atalizarItemAlista(item_, valor, position, index);
                }
            }else{
                itemnome.setErrorEnabled(true);
                itemnome.setError(getString(R.string.digite_nome_item));
            }
        });
        itemnome.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemnome.setError("");
                itemnome.setErrorEnabled(false);
            }
        });
        root.findViewById(R.id.cancel).setOnClickListener(view13 -> sheetDialog.dismiss());

        sheetDialog.show();
    }

    public void onDialogoAdicionarQuant(){
        valor = 0;
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.card_add_item_cardapio_quant, null);
        DialogInput sheetDialog = new DialogInput(this, root, getString(R.string.adicione_bloco));
        TextInputLayout itemnome = root.findViewById(R.id.itemnome);
        FrameLayout valorItem = root.findViewById(R.id.valorItem);
        TextView textValor = root.findViewById(R.id.valorText);
        TextView number = root.findViewById(R.id.number);
        Objects.requireNonNull(itemnome.getEditText()).setSelection(itemnome.getEditText().getText().length());
        textValor.setText(Mask.formatarValor(valor));
        ImageButton btnRemove = root.findViewById(R.id.btnRemove);
        ImageButton btnAdd = root.findViewById(R.id.btnAdd);
        number.setText("1");
        btnRemove.setEnabled(Integer.parseInt(number.getText().toString()) > 0);
        btnRemove.setOnClickListener(view14 -> {
            int index = Integer.parseInt(number.getText().toString());
            index -= 1;
            number.setText(String.format("%s", index));
            btnRemove.setEnabled(index > 0);
        });
        btnAdd.setOnClickListener(view12 -> {
            int index = Integer.parseInt(number.getText().toString());
            index += 1;
            number.setText(String.format("%s", index));
            btnRemove.setEnabled(index > 0);
        });
        valorItem.setOnClickListener(view1 -> {
            MainDialog dialog = new MainDialog();
            dialog.gerarTeclado(this, valor);
            dialog.setOnConfirmationClick(((valor2, real) -> {
                valor = valor2;
                textValor.setText(real);
            }));
        });
        root.findViewById(R.id.confirmar).setOnClickListener(view15 -> {
            String item_ = itemnome.getEditText().getText().toString().trim();
            if(!item_.equals("")){
                if(valor == 0){
                    DialogMessage message
                            = new DialogMessage(this, getString(R.string.salvar_sem_preco), true, getString(R.string.confirmar));
                    message.show();
                    message.setOnPossiveButtonClicked(() -> {
                        sheetDialog.dismiss();
                        int index = Integer.parseInt(number.getText().toString());
                        AdicionarItemAlista(item_, valor, index);
                    });
                }else{
                    sheetDialog.dismiss();
                    int index = Integer.parseInt(number.getText().toString());
                    AdicionarItemAlista(item_, valor, index);
                }
            }else{
                itemnome.setErrorEnabled(true);
                itemnome.setError(getString(R.string.digite_nome_item));
            }
        });
        itemnome.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemnome.setError("");
                itemnome.setErrorEnabled(false);
            }
        });
        root.findViewById(R.id.cancel).setOnClickListener(view13 -> sheetDialog.dismiss());

        sheetDialog.show();
    }

    public void onDialogoEditor(ItensBlocoCardapio item, int position){
        valor = item.getValor();
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.card_add_item_cardapio, null);
        DialogInput sheetDialog = new DialogInput(this, root, getString(R.string.adicione_bloco));
        TextInputLayout itemnome = root.findViewById(R.id.itemnome);
        FrameLayout valorItem = root.findViewById(R.id.valorItem);
        TextView textValor = root.findViewById(R.id.valorText);
        Objects.requireNonNull(itemnome.getEditText()).setText(item.getContent());
        itemnome.getEditText().setSelection(itemnome.getEditText().getText().length());
        textValor.setText(Mask.formatarValor(valor));

        valorItem.setOnClickListener(view1 -> {
            MainDialog dialog = new MainDialog();
            dialog.gerarTeclado(this, valor);
            dialog.setOnConfirmationClick(((valor2, real) -> {
                valor = valor2;
                textValor.setText(real);
            }));
        });
        root.findViewById(R.id.confirmar).setOnClickListener(view15 -> {
            String item_ = itemnome.getEditText().getText().toString().trim();
            if(!item_.equals("")){
                if(valor == 0){
                    DialogMessage message
                            = new DialogMessage(this, getString(R.string.salvar_sem_preco), true, getString(R.string.confirmar));
                    message.show();
                    message.setOnPossiveButtonClicked(() -> {
                        sheetDialog.dismiss();
                        int index = Integer.parseInt("1");
                        atalizarItemAlista(item_, valor, position, index);
                    });
                }else{
                    sheetDialog.dismiss();
                    int index = Integer.parseInt("1");
                    atalizarItemAlista(item_, valor, position, index);
                }
            }else{
                itemnome.setErrorEnabled(true);
                itemnome.setError(getString(R.string.digite_nome_item));
            }
        });
        itemnome.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemnome.setError("");
                itemnome.setErrorEnabled(false);
            }
        });
        root.findViewById(R.id.cancel).setOnClickListener(view13 -> sheetDialog.dismiss());

        sheetDialog.show();
    }

    public void onDialogoAdicionar(){
        valor = 0;
        @SuppressLint("InflateParams") View root = LayoutInflater.from(this).inflate(R.layout.card_add_item_cardapio, null);
        DialogInput sheetDialog = new DialogInput(this, root, getString(R.string.adicione_bloco));
        TextInputLayout itemnome = root.findViewById(R.id.itemnome);
        FrameLayout valorItem = root.findViewById(R.id.valorItem);
        TextView textValor = root.findViewById(R.id.valorText);
        valorItem.setOnClickListener(view1 -> {
            MainDialog dialog = new MainDialog();
            dialog.gerarTeclado(this, valor);
            dialog.setOnConfirmationClick(((valor2, real) -> {
                valor = valor2;
                textValor.setText(real);
            }));
        });
        root.findViewById(R.id.confirmar).setOnClickListener(view15 -> {
            String item_ = Objects.requireNonNull(itemnome.getEditText()).getText().toString().trim();
            if(!item_.equals("")){
                if(valor == 0){
                    DialogMessage message
                            = new DialogMessage(this, getString(R.string.salvar_sem_preco), true, getString(R.string.confirmar));
                    message.show();
                    message.setOnPossiveButtonClicked(() -> {

                        sheetDialog.dismiss();
                        int index = Integer.parseInt("1");
                        AdicionarItemAlista(item_, valor, index);
                    });
                }else{
                    sheetDialog.dismiss();
                    int index = Integer.parseInt("1");
                    AdicionarItemAlista(item_, valor, index);
                }
            }else{
                itemnome.setErrorEnabled(true);
                itemnome.setError(getString(R.string.digite_nome_item));
            }
        });
        Objects.requireNonNull(itemnome.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemnome.setError("");
                itemnome.setErrorEnabled(false);
            }
        });
        root.findViewById(R.id.cancel).setOnClickListener(view13 -> sheetDialog.dismiss());

        sheetDialog.show();
    }

    private void onLista() {
        findViewById(R.id.ordenar_blocos).setVisibility(adaptador.getItemCount() > 1 ? View.VISIBLE : View.GONE);
        if(adaptador.getItemCount() > 0){
            listaLayout.setVisibility(View.VISIBLE);
            vazio.setVisibility(View.GONE);
        }else{
            vazio.setVisibility(View.VISIBLE);
            listaLayout.setVisibility(View.GONE);
        }
        btn_confirnar.setVisibility(adaptador.getItemCount() > 0 ? View.VISIBLE : View.GONE);
    }

    private void atalizarItemAlista(String content, Double valor, int position, int maxItens) {
        ItensBlocoCardapio pedido1 = itemCardapios.get(position);
        pedido1.setContent(content);
        pedido1.setMaxItensQuantidate(maxItens);
        pedido1.setValor(valor);
        adaptador.insert(itemCardapios);
        onLista();
        MyToast.makeText(this, R.string.item_atualizado, ModoColor._success).show();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void AdicionarItemAlista(String content, double valor, int maxItens) {
        ItensBlocoCardapio itensBloco = new ItensBlocoCardapio();
        itensBloco.setContent(content);
        itensBloco.setValor(valor);
        itensBloco.setText(false);
        itensBloco.setTexto("");
        itensBloco.setMaxItensQuantidate(maxItens);
        itemCardapios.add(itensBloco);
        adaptador.insert(itemCardapios);
        onLista();
        MyToast.makeText(this, R.string.item_adicionado, ModoColor._success).show();
    }

    public void onCancelarItens(View view) {
        finish();
    }

    public void onConfirmarItens(View view) {
        List<ItensBlocoCardapio> itemCardapios_ = adaptador.getLista();
        ArrayList<String> textos = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        ArrayList<Double> precos = new ArrayList<>();
        ArrayList<Integer> quantidate = new ArrayList<>();
        ArrayList<Boolean> texts = new ArrayList<>();
        for (int i = 0; i < itemCardapios_.size(); i++) {
            contents.add(itemCardapios_.get(i).getContent());
            precos.add(itemCardapios_.get(i).getValor());
            quantidate.add(itemCardapios_.get(i).getMaxItensQuantidate());
            texts.add(itemCardapios_.get(i).isText());
            textos.add(itemCardapios_.get(i).getTexto());
        }
        bloco.setTextos(textos);
        bloco.setText(texts);
        bloco.setMaxItensAdicionais(quantidate);
        bloco.setContents(contents);
        bloco.setValores(precos);
        Intent intent = new Intent();
        intent.putExtra("data", bloco);
        intent.putExtra("position", position);
        setResult(1010, intent);
        finish();
    }

    public void onAdicionarItemCardapio(View view) {
        @SuppressLint("InflateParams") View root = getLayoutInflater().inflate(R.layout.layout_dialog_selector_type_block, null);
        DialogInput sheetDialog = new DialogInput(this, root, getString(R.string.tipo_bloco));
        root.findViewById(R.id.selected_bloco_texto).setOnClickListener((v -> createBlocoTexto(sheetDialog)));
        root.findViewById(R.id.selected_bloco_item).setOnClickListener((v -> createBlocoItem(sheetDialog)));
        root.findViewById(R.id.cancel).setOnClickListener((v -> sheetDialog.dismiss()));
        sheetDialog.show();
    }

    private void createBlocoItem(DialogInput input) {
        input.dismiss();
         if(bloco.isItensAdicionais()){
                    onDialogoAdicionarQuant();
                }else onDialogoAdicionar();
    }

    private void createBlocoTexto(DialogInput input) {
        input.dismiss();
        @SuppressLint("InflateParams") View root = getLayoutInflater().inflate(R.layout.card_add_item_bloco_texto, null);
        DialogInput sheetDialog = new DialogInput(this, root, getString(R.string.adicione_bloco));
        TextInputLayout nomeInput = root.findViewById(R.id.itemnome);
        root.findViewById(R.id.confirmar).setOnClickListener((v -> {
            String nome = Objects.requireNonNull(nomeInput.getEditText()).getText().toString().trim();
            if(!nome.equals("")){
                ItensBlocoCardapio itensBloco = new ItensBlocoCardapio();
                itensBloco.setContent("");
                itensBloco.setValor(0.00);
                itensBloco.setText(true);
                itensBloco.setTexto(nome);
                itensBloco.setMaxItensQuantidate(0);
                itemCardapios.add(itensBloco);
                adaptador.insert(itemCardapios);
                onLista();
                MyToast.makeText(this, R.string.item_adicionado, ModoColor._success).show();
                sheetDialog.dismiss();
            }else{
                MySnackbar.makeText(this, R.string.digite_nome, ModoColor._falha).show();
            }
        }));
        root.findViewById(R.id.cancel).setOnClickListener((v -> sheetDialog.dismiss()));

        sheetDialog.show();
    }

    public void onConfig(View view) {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_selecionador_tipo_item_cardapio, null);
        TextInputLayout blockName;
        SwitchCompat obgdSelected, valorMaior, multiSelect, quantidade2;
        LinearLayout layoutMultiSelect;
        ImageButton selectRemove, selectAdd;
        TextView numeroLimite;
        limite = 1;
        quantidade2 = _view.findViewById(R.id.quantidade);
        quantidade2.setVisibility(View.GONE);
        blockName = _view.findViewById(R.id.blockName);
        obgdSelected = _view.findViewById(R.id.obgdSelected);
        valorMaior = _view.findViewById(R.id.valorMaior);
        layoutMultiSelect = _view.findViewById(R.id.layoutMultiSelect);
        multiSelect = _view.findViewById(R.id.multiSelect);
        selectRemove = _view.findViewById(R.id.selectRemove);
        selectAdd = _view.findViewById(R.id.selectAdd);
        numeroLimite = _view.findViewById(R.id.numeroLimite);
        Objects.requireNonNull(blockName.getEditText()).setText(bloco.getDispayTitulo());
        blockName.getEditText().setSelection(bloco.getDispayTitulo().length());
        obgdSelected.setChecked(bloco.isObgdSelect());
        valorMaior.setChecked(bloco.isValorMaior());
        multiSelect.setChecked(bloco.isMultiselect());
        if(!bloco.isMultiselect()){
            limite = bloco.getSelectMax();
            layoutMultiSelect.setVisibility(View.VISIBLE);
        }else{
            limite = 1;
            layoutMultiSelect.setVisibility(View.GONE);
        }
        numeroLimite.setText(String.valueOf(limite));
        selectRemove.setEnabled(limite > 1);
        selectAdd.setOnClickListener(view12 -> limiteView(MAS, numeroLimite, selectRemove));
        selectRemove.setOnClickListener(view1 -> limiteView(MENOS, numeroLimite, selectRemove));

        multiSelect.setOnCheckedChangeListener((compoundButton, b) -> layoutMultiSelect.setVisibility(b ? View.GONE : View.VISIBLE));

        blockName.getEditText().addTextChangedListener(new TextWatcher() {
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
        _view.findViewById(R.id.cancel).setOnClickListener((v -> dialog_selecionador.dismiss()));
        _view.findViewById(R.id.confirmar).setOnClickListener((v -> {
            String displayName = blockName.getEditText().getText().toString().trim();
            if(!displayName.equals("")) {
                bloco.setDispayTitulo(displayName);
                ((TextView)findViewById(R.id.displayName)).setText(bloco.getDispayTitulo());
                bloco.setValorMaior(valorMaior.isChecked());
                bloco.setMultiselect(multiSelect.isChecked());
                bloco.setObgdSelect(obgdSelected.isChecked());
                bloco.setSelectMax(!multiSelect.isChecked() ? limite : -1);
                bloco.setPositionSelect(obgdSelected.isChecked() ? 0 : -1);
                dialog_selecionador.dismiss();
            }else{
                MySnackbar.makeText(this, R.string.nome_digite, ModoColor._falha).show();
            }
        }));
        dialog_selecionador.setContentView(_view);
        if(Build.VERSION.SDK_INT >= 21){
            dialog_selecionador.getWindow().addFlags(67108864);
        }
        ((View)_view.getParent()).setBackgroundColor(getResources().getColor(R.color.transparent));
        dialog_selecionador.show();
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

    public void onOrdenarBlocos(View view) {
        if(((Button)view).getText().equals(getString(R.string.confirmar))){
            mItemTouchHelper = null;
            adaptador.insert(adaptadorOrdem.getLista());
            mRecyclerView.setAdapter(adaptador);
            findViewById(R.id.adicionar_item).setVisibility(View.VISIBLE);
            ((Button)view).setText(R.string.ordenar);
        }else{
            if(mRecyclerView.getAdapter() == adaptador){
                adaptadorOrdem.insert(itemCardapios);
                mRecyclerView.setAdapter(adaptadorOrdem);
                mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(adaptadorOrdem));
                mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                findViewById(R.id.adicionar_item).setVisibility(View.GONE);
                ((Button)view).setText(R.string.confirmar);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mRecyclerView.getAdapter() == adaptadorOrdem){
            mItemTouchHelper = null;
            mRecyclerView.setAdapter(adaptador);
            findViewById(R.id.adicionar_item).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.ordenar_blocos)).setText(R.string.ordenar);
            return;
        }
        super.onBackPressed();
    }
}