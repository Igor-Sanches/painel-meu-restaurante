package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.dialogo.MainDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.CardapioViewModel;
import app.birdsoft.painelmeurestaurante.widget.ImageUtils;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AtualizarCardapioUnicoActivity extends AppCompatActivity {

    private ImageView capa_produto;
    private TextInputLayout input_name, input_receita;
    private TextView valor_produto;
    private Cardapio cardapio;
    private String image_url, image_uri_default;
    private SwitchCompat lanche_disponivel;
    private Double valor;

    private boolean isConnection() {
        if(!Conexao.isConnected(this)){
            MySnackbar.makeText(this, R.string.sem_conexao, ModoColor._falha).show();
            return false;
        }
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_cardapio_unico);
        cardapio = (Cardapio)getIntent().getSerializableExtra("cardapio");
        capa_produto = findViewById(R.id.capa_produto);
        LinearLayout vazio = findViewById(R.id.vazio);
        lanche_disponivel = findViewById(R.id.lanche_disponivel);
        LinearLayout layout_wifi_error = findViewById(R.id.layout_wifi_error);
        LinearLayout lyt_progress = findViewById(R.id.lyt_progress);
        NestedScrollView layout_principal = findViewById(R.id.layout_principal);
        input_name = findViewById(R.id.input_name);
        input_receita = findViewById(R.id.input_receita);
        valor_produto = findViewById(R.id.valor_produto);
        if(cardapio != null){
            valor = cardapio.getValor();
            image_uri_default = cardapio.getImageUrl();
            image_url = cardapio.getImageUrl();
            lanche_disponivel.setChecked(cardapio.isDisponivel());
            ImageUtils.displayImageFromUrl(this, cardapio.getImageUrl(), capa_produto, getResources().getDrawable(R.drawable.image_add_ft));
            Objects.requireNonNull(input_name.getEditText()).setText(cardapio.getName());
            Objects.requireNonNull(input_name.getEditText()).setSelection(cardapio.getName().length());
            if(cardapio.getReceita() != null){
                Objects.requireNonNull(input_receita.getEditText()).setText(cardapio.getReceita());
                Objects.requireNonNull(input_receita.getEditText()).setSelection(cardapio.getReceita().length());
            }
            valor_produto.setText(Mask.formatarValor(cardapio.getValor()));
            lanche_disponivel.setChecked(cardapio.isDisponivel());
        }
        layout_wifi_error.setVisibility(View.GONE);
        layout_principal.setVisibility(View.VISIBLE);
        vazio.setVisibility(View.GONE);
        lyt_progress.setVisibility(View.GONE);

    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onBuscarImagem(View view) {
        CropImage.activity(null).setAspectRatio(200, 110).setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    image_url = result != null ? result.getUri().toString() : null;
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

    public void onValorProduto(View view) {
        MainDialog dialog = new MainDialog();
        dialog.gerarTeclado(this, valor);
        dialog.setOnConfirmationClick((valor, real) -> {
            this.valor = valor;
            valor_produto.setText(real);
        });
    }

    public void onCancelar(View view) {
        finish();
    }

    public void onAtualizar(View view) {
        if(isDados()){
            Map<String, Object> map = new HashMap<>();
            if(!cardapio.getName().equals(getText(input_name)))
                map.put("name", getText(input_name));

            if(!cardapio.getReceita().equals(getText(input_receita)))
                map.put("receita", getText(input_receita));

            if(cardapio.getImageUrl() != null){
                if(image_url != null){
                    if(!cardapio.getImageUrl().equals(image_url))
                        map.put("imageUrl", image_url);
                }
            }else{
                if(image_url != null){
                    map.put("imageUrl", image_url);
                }
            }

            if(valor != cardapio.getValor())
                map.put("valor", valor);

            if(cardapio.isDisponivel() != lanche_disponivel.isChecked())
                map.put("disponivel", lanche_disponivel.isChecked());

           if(map.size() > 0){
              if(isConnection()){
                  LoadingDialog dialog = new LoadingDialog(this);
                  dialog.show();
                  if(map.get("imageUrl") != null){
                      new ViewModelProvider(this).get(CardapioViewModel.class).insertImage(cardapio, Uri.parse(image_url), this).observe(this, (cardapio1 -> {
                          if(cardapio1!=null){
                              map.put("imageUrl", cardapio1.getImageUrl());
                              MySnackbar.makeText(this, R.string.image_uptade, ModoColor._success).show();
                              continuar(map, dialog);
                          }else{
                               dialog.dismiss();
                              DialogMessage message =
                                      new DialogMessage(this, getString(R.string.image_erro_uptade), true, getString(R.string.continuar),getString(R.string.msg_alert));
                              message.show();
                              message.setOnPossiveButtonClicked(() -> {
                                  map.put("imageUrl", image_uri_default == null ? "" : image_uri_default);
                                  dialog.show();
                                  continuar(map, dialog);
                              });
                          }
                      }));
                  }else{
                      continuar(map, dialog);
                  }

              }
           }else{
               MyToast.makeText(this, R.string.cardapio_atualizado_sucesso, ModoColor._success).show();
               finish();
           }
        }
    }

    private void continuar(Map<String, Object> map, LoadingDialog dialog) {
        new ViewModelProvider(this).get(CardapioViewModel.class).updateCardapio(map, cardapio.getUid(), this).observe(this, (sucesso->{
            if(sucesso){
                MyToast.makeText(this, R.string.cardapio_atualizado_sucesso, ModoColor._success).show();
                new ViewModelProvider(this).get(CardapioViewModel.class).update(this);
                finish();
            }else{
                MySnackbar.makeText(this, R.string.cardapio_atualizado_erro, ModoColor._falha).show();
            }
            dialog.dismiss();
        }));
    }

    private boolean isDados() {

        String nome = getText(input_name);
        if(nome.equals("")){
            MySnackbar.makeText(this, R.string.enter_with_name, ModoColor._falha).show();
            return false;
        } 
        if(valor == 0){
            MySnackbar.makeText(this, R.string.selected_type_lanche_valor_0, ModoColor._falha).show();
            return false;
        }

        return true;
    }

    private String getText(TextInputLayout input){
        return Objects.requireNonNull(input.getEditText()).getText().toString().trim();
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        new ViewModelProvider(this).get(CardapioViewModel.class).update(this);
    }
}