package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorAnuncios;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorAnunciosOrdem;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.Anuncio;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.AnunciosViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AnunciosActivity extends AppCompatActivity {

    private BottomSheetDialog sheetDialog;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout lyt_progress, layout_wifi_error, listaLayout, vazio;
    private AdaptadorAnuncios adaptador;
    private AnunciosViewModel viewModel;
    private ImageButton adicionar_anuncio;
    private Button ordenar_anuncio;
    private LoadingDialog loading;
    private List<Anuncio> anuncios;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private AdaptadorAnunciosOrdem adaptadorOrdem;

    @Override
    protected void onResume() {
        if(mItemTouchHelper == null){
            viewModel.update(this);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        View sheetBottom = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheetBottom);
        sheetDialog = new BottomSheetDialog(this);
        loading = new LoadingDialog(this);
        viewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);
        viewModel.init(this);
        adaptador = new AdaptadorAnuncios(this, (anuncios = new ArrayList<>()));
        adaptadorOrdem = new AdaptadorAnunciosOrdem(this, (anuncios = new ArrayList<>()));
        lyt_progress = findViewById(R.id.lyt_progress);
        listaLayout = findViewById(R.id.listaLayout);
        ordenar_anuncio = findViewById(R.id.ordenar_anuncio);
        vazio = findViewById(R.id.vazio);
        adicionar_anuncio = findViewById(R.id.adicionar_anuncio);
        layout_wifi_error = findViewById(R.id.layout_wifi_error);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adaptador);
        viewModel.getMutableLiveData().observe(this, (anunciosElements -> {
            if(sheetDialog.isShowing()){
                sheetDialog.dismiss();
            }
            anuncios = anunciosElements.getAnuncios();
            adaptadorOrdem.insert(anuncios);
            adaptador.insert(anuncios);
            layout_wifi_error.setVisibility(anunciosElements.getLayoutWifiOffline());
            vazio.setVisibility(anunciosElements.getVazioVisibility());
            lyt_progress.setVisibility(anunciosElements.getProgressVisibility());
            listaLayout.setVisibility(anunciosElements.getListaVisibility());
            adicionar_anuncio.setVisibility(anunciosElements.getLayoutWifiOffline() != View.VISIBLE && anunciosElements.getProgressVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
            ordenar_anuncio.setVisibility(anunciosElements.getLayoutWifiOffline() != View.VISIBLE && anunciosElements.getProgressVisibility() != View.VISIBLE && anunciosElements.getAnuncios().size() >= 2 ? View.VISIBLE : View.GONE);
        }));
         adaptador.setOnDeleteItem(((anuncio, position) -> {

            DialogMessage message =
                    new DialogMessage(this, getString(R.string.delete_canuncio_msg), true, getString(R.string.confirmar));
            message.show();
            message.setOnPossiveButtonClicked(() -> {
                anuncios.remove(position);
                viewModel.insertDeletede(anuncio, anuncios, this).observe(this, (success->{
                    if(success){
                        MySnackbar.makeText(this, R.string.anuncio_deletado, ModoColor._success).show();
                        viewModel.update(this);
                    }else{
                        MySnackbar.makeText(this, R.string.error_deletar_anuncio, ModoColor._falha).show();
                    }
                    loading.dismiss();
                }));
            });
 
        }));
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void onAdicionarAnuncio(View view) {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_anuncio_formato_selector, null);

        _view.findViewById(R.id.btn_anuncio_720x405).setOnClickListener((v -> {
            sheetDialog.dismiss();
            CropImage.activity(null).setAspectRatio(720, 405).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }));
        _view.findViewById(R.id.btn_anuncio_720x720).setOnClickListener((v -> {
            sheetDialog.dismiss();
            CropImage.activity(null).setAspectRatio(720, 720).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }));
        _view.findViewById(R.id.btn_anuncio_720x1280).setOnClickListener((v -> {
            sheetDialog.dismiss();
            CropImage.activity(null).setAspectRatio(720, 1280).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }));
        _view.findViewById(R.id.btn_anuncio_personalizado).setOnClickListener((v -> {
            sheetDialog.dismiss();
            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }));

        sheetDialog.setContentView(_view);
        if(Build.VERSION.SDK_INT >= 21){
            sheetDialog.getWindow().addFlags(67108864);
        }
        ((View)_view.getParent()).setBackgroundColor(getResources().getColor(R.color.transparent));
        sheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(mRecyclerView.getAdapter() == adaptadorOrdem){
            mItemTouchHelper = null;
            mRecyclerView.setAdapter(adaptador);
            adicionar_anuncio.setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.ordenar_anuncio)).setText(R.string.ordenar);
            return;
        }
        super.onBackPressed();
    }

    public void onOrdenarAnuncio(View view) {
        if(isConnection()){
            if(((Button)view).getText().equals(getString(R.string.confirmar))){
                loading.show();
                viewModel.insert(adaptadorOrdem.getLista(), this).observe(this, (success->{
                    if(success){
                        mItemTouchHelper = null;
                        mRecyclerView.setAdapter(adaptador);
                        MySnackbar.makeText(this, R.string.ordenado_success, ModoColor._success).show();
                        viewModel.update(this);
                        adicionar_anuncio.setVisibility(View.VISIBLE);
                        ((Button)view).setText(R.string.ordenar);
                    }else{
                        MySnackbar.makeText(this, R.string.ordenado_falha, ModoColor._falha).show();
                    }
                    loading.dismiss();
                }));
            }else{
                if(mRecyclerView.getAdapter() == adaptador){
                    mRecyclerView.setAdapter(adaptadorOrdem);
                    mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(adaptadorOrdem));
                    mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                    adicionar_anuncio.setVisibility(View.GONE);
                    ((Button)view).setText(R.string.confirmar);
                }
            }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if(isConnection()){
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    try {
                        if(result !=null){
                            InputStream inputStream = getContentResolver().openInputStream(result.getUri());
                            DialogMessage message =
                                    new DialogMessage(this, getString(R.string.publicar_anuncio), true, getString(R.string.confirmar));
                            message.show();
                            message.setOnPossiveButtonClicked(() -> {
                                onPublicar(inputStream);
                            }); 
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }

    private void onPublicar(InputStream inputStream) {
        if(isConnection()){
            if(inputStream != null){
                loading.show();
                viewModel.insertImage(inputStream, this).observe(this, (anuncio ->{
                    if(anuncio != null){
                        anuncios.add(anuncio);
                        viewModel.insert(anuncios, this).observe(this, (success->{
                            if(success){
                                MySnackbar.makeText(this, R.string.anuncio_postado, ModoColor._success).show();
                                viewModel.update(this);
                            }else{
                                MySnackbar.makeText(this, R.string.error_publicar_anuncio, ModoColor._falha).show();
                            }
                            loading.dismiss();
                        }));
                    }else{
                        MySnackbar.makeText(this, R.string.falha_enviar_imagem_em_storage, ModoColor._falha).show();
                        loading.dismiss();
                    }
                }));
            }else MySnackbar.makeText(this, R.string.file_mull, ModoColor._falha).show();
        }
    }

    public void onUpdateConnection(View view) {
        MyToast.makeText(this, R.string.atualizando).show();

        viewModel.update(this);
    }
}