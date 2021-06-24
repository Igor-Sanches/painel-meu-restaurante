package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;
import java.util.UUID;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.dialogo.MainDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.CardapioViewModel;
import app.birdsoft.painelmeurestaurante.widget.ImageUtils;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AdicionarCardapioActivity extends AppCompatActivity {

    private ImageView capa_produto;
    private RadioButton radio_produto_criado, radio_produto_com_adicionais, radio_produto_unico;
    private TextInputLayout input_name, input_receita;
    private Uri image_url;
    private TextView valor_produto;
    private CardView card_layout_valor;
    private double valor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_cardapio);
        capa_produto = findViewById(R.id.capa_produto);
        input_name = findViewById(R.id.input_name);
        input_receita = findViewById(R.id.input_receita);
        radio_produto_criado = findViewById(R.id.radio_produto_criado);
        radio_produto_com_adicionais = findViewById(R.id.radio_produto_com_adicionais);
        radio_produto_unico = findViewById(R.id.radio_produto_unico);
        valor_produto = findViewById(R.id.valor_produto);
        card_layout_valor = findViewById(R.id.card_layout_valor);

        radio_produto_criado.setOnClickListener((view) -> card_layout_valor.setVisibility(radio_produto_criado.isChecked() ? View.GONE : View.VISIBLE));
        radio_produto_unico.setOnClickListener((view) -> card_layout_valor.setVisibility(radio_produto_unico.isChecked() ? View.VISIBLE : View.GONE));
        radio_produto_com_adicionais.setOnClickListener((view) -> card_layout_valor.setVisibility(radio_produto_com_adicionais.isChecked() ? View.VISIBLE : View.GONE));
  }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onBuscarImagem(View view) {
        CropImage.activity(null).setAspectRatio(200, 110).setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    public void onValorProduto(View view) {
        MainDialog dialog = new MainDialog();
        dialog.gerarTeclado(this, valor);
        dialog.setOnConfirmationClick(((valor1, real) -> {
            valor = valor1;
            valor_produto.setText(real);
        }));
    }

    public void onCancelar(View view) {
        finish();
    }

    public void onConfirmar(View view) {
        if(Conexao.isConnected(this)){
            if(isDados()){
                String uid = UUID.randomUUID().toString();
                Cardapio cardapio = new Cardapio();
                cardapio.setName(getText(input_name));
                cardapio.setReceita(getText(input_receita));
                cardapio.setUid(uid);
                cardapio.setTipoLanche(getLancheType());
                cardapio.setValor(valor);
                cardapio.setPosition(0);
                cardapio.setDisponivel(true);
                cardapio.setData(DateTime.getTime());
                if(radio_produto_unico.isChecked()){
                    LoadingDialog dialog = new LoadingDialog(this);
                    CardapioViewModel viewModel = new ViewModelProvider(this).get(CardapioViewModel.class);
                    dialog.show();
                    viewModel.insertImage(cardapio, image_url, this).observe(this, (cardapio1 -> {
                        if(cardapio1 != null){
                            viewModel.insert(cardapio1, this).observe(this, (success->{
                                if(success){
                                    MyToast.makeText(this, R.string.cardapio_adicionado, ModoColor._success).show();
                                    finish();
                                }else{
                                    MySnackbar.makeText(this, R.string.cardapio_erro_adicionar, ModoColor._falha).show();
                                }
                                dialog.dismiss();
                            }));
                        }else{
                            dialog.dismiss();
                            MySnackbar.makeText(this, R.string.cardapio_erro_adicionar, ModoColor._falha).show();
                        }
                    }));

                }else{
                    Intent intent = new Intent(this, CardapioVisualizadorActivity.class);
                    cardapio.setImageUrl(image_url == null ? "" : image_url.toString());
                    intent.putExtra("cardapio", cardapio);
                    Navigate.activity(this).navigateForResult(intent, 10);
                }

            }
        }else{
            MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
        }
    }

    private int getLancheType() {
        if(radio_produto_criado.isChecked()) return 0;
        else if(radio_produto_com_adicionais.isChecked()) return 1;
        else return 2;
    }

    private boolean isDados() {

        String nome = getText(input_name);
        if(nome.equals("")){
            MySnackbar.makeText(this, R.string.enter_with_name, ModoColor._falha).show();
            return false;
        }

        if(!radio_produto_com_adicionais.isChecked() && !radio_produto_criado.isChecked() && !radio_produto_unico.isChecked()){
            MySnackbar.makeText(this, R.string.selected_type_lanche, ModoColor._falha).show();
            return false;
        }
        if(!radio_produto_criado.isChecked()){
            if(valor == 0){
                MySnackbar.makeText(this, R.string.selected_type_lanche_valor_0, ModoColor._falha).show();
                return false;
            }
        }
        return true;
    }

    private String getText(TextInputLayout input){
        return Objects.requireNonNull(input.getEditText()).getText().toString().trim();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    image_url = result != null ? result.getUri() : null;
                    assert result != null;
                    ImageUtils.displayImageFromUrl(this, result.getUri().toString(), capa_produto, getResources().getDrawable(R.drawable.image_add_ft));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(resultCode == 10){
            if(requestCode == 10){
                finish();
            }
        }
    }
}