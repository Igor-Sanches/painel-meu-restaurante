package app.birdsoft.painelmeurestaurante.repository;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import app.birdsoft.painelmeurestaurante.manager.Chave;
import app.birdsoft.painelmeurestaurante.manager.FirebaseUtils;
import app.birdsoft.painelmeurestaurante.model.Anuncio;
import app.birdsoft.painelmeurestaurante.model.AnunciosElements;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.settings.Settings;

public class AnunciosRepository {
    private static MutableLiveData<AnunciosElements> elementos;
    public static MutableLiveData<AnunciosElements> getInstance(Context context) {
        if(elementos == null){
            elementos = GetElements(context);
        }
        return elementos;
    }

    private synchronized static MutableLiveData<AnunciosElements> GetElements(Context context) {
        MutableLiveData<AnunciosElements> data = new MutableLiveData<>();
        getFirebaseData(data, context);
        return data;
    }

    private static synchronized void getFirebaseData(MutableLiveData<AnunciosElements> data, Context context) {
        AnunciosElements anunciosElements = new AnunciosElements();
        if(Conexao.isConnected(context)){
            FirebaseUtils
                    .getDatabaseRef()
                    .child(Chave.ANUNCIOS)
                    .child(Settings.getUID(context))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try{
                                if(snapshot.exists()){
                                    GenericTypeIndicator<List<Anuncio>> list = new GenericTypeIndicator<List<Anuncio>>() {};
                                    List<Anuncio> anuncios = snapshot.getValue(list);
                                    if(anuncios!=null){
                                        if(anuncios.size() > 0){
                                            anunciosElements.setProgressVisibility(View.GONE);
                                            anunciosElements.setVazioVisibility(View.GONE);
                                            anunciosElements.setListaVisibility(View.VISIBLE);
                                            anunciosElements.setLayoutWifiOffline(View.GONE);
                                        }else{
                                            anunciosElements.setListaVisibility(View.GONE);
                                            anunciosElements.setProgressVisibility(View.GONE);
                                            anunciosElements.setLayoutWifiOffline(View.GONE);
                                            anunciosElements.setVazioVisibility(View.VISIBLE);
                                        }
                                        anunciosElements.setAnuncios(anuncios);
                                    }else{
                                        anunciosElements.setListaVisibility(View.GONE);
                                        anunciosElements.setLayoutWifiOffline(View.GONE);
                                        anunciosElements.setProgressVisibility(View.GONE);
                                        anunciosElements.setVazioVisibility(View.VISIBLE);
                                        anunciosElements.setAnuncios(new ArrayList<>());
                                    }
                                }else{
                                    anunciosElements.setListaVisibility(View.GONE);
                                    anunciosElements.setLayoutWifiOffline(View.GONE);
                                    anunciosElements.setProgressVisibility(View.GONE);
                                    anunciosElements.setVazioVisibility(View.VISIBLE);
                                    anunciosElements.setAnuncios(new ArrayList<>());
                                }

                                data.setValue(anunciosElements);

                            }catch (Exception x){
                                anunciosElements.setListaVisibility(View.GONE);
                                anunciosElements.setProgressVisibility(View.GONE);
                                anunciosElements.setVazioVisibility(View.VISIBLE);
                                anunciosElements.setLayoutWifiOffline(View.GONE);
                                anunciosElements.setAnuncios(new ArrayList<>());
                                data.setValue(anunciosElements);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            anunciosElements.setListaVisibility(View.GONE);
                            anunciosElements.setProgressVisibility(View.GONE);
                            anunciosElements.setVazioVisibility(View.VISIBLE);
                            anunciosElements.setAnuncios(new ArrayList<>());
                            data.setValue(anunciosElements);
                        }
                    });
        }else{
            anunciosElements.setListaVisibility(View.GONE);
            anunciosElements.setLayoutWifiOffline(View.VISIBLE);
            anunciosElements.setProgressVisibility(View.GONE);
            anunciosElements.setVazioVisibility(View.GONE);
            anunciosElements.setAnuncios(new ArrayList<>());
            data.setValue(anunciosElements);
        }


    }

    public static void update(MutableLiveData<AnunciosElements> data, Context context) {
        getFirebaseData(data, context);
    }

    public static void insertImage(InputStream inputStream, MutableLiveData<Anuncio> data, Context context) {
        if(Conexao.isConnected(context)){
            String uid = UUID.randomUUID().toString();
            Anuncio anuncio = new Anuncio();
            anuncio.setUid(uid);
            FirebaseStorage.getInstance()
                    .getReference()
                    .child(Chave.ANUNCIOS)
                    .child(Settings.getUID(context))
                    .child(anuncio.getUid()).putStream(inputStream)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            try{
                                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        try{
                                            anuncio.setUrlImage(task1.getResult().toString());
                                            data.setValue(anuncio);
                                        }catch (Exception x){
                                            data.setValue(null);
                                        }
                                    }else{
                                        data.setValue(null);
                                    }
                                });

                            }catch (Exception x){
                                data.setValue(null);
                            }
                        }else{
                            data.setValue(null);
                        }
                    });
        }else data.setValue(null);

    }

    public static void insert(List<Anuncio> anuncios, MutableLiveData<Boolean> data, Context context) {
        FirebaseUtils
                .getDatabaseRef()
                .child(Chave.ANUNCIOS)
                .child(Settings.getUID(context))
                .setValue(anuncios)
        .addOnCompleteListener((result->data.setValue(result.isSuccessful())));
    }

    public static void insertDeletede(Anuncio anuncio, List<Anuncio> anuncios, MutableLiveData<Boolean> data, Context context) {
        FirebaseStorage.getInstance().getReference().child("Anuncios").child(anuncio.getUid()).delete();
        FirebaseUtils
                .getDatabaseRef()
                .child(Chave.ANUNCIOS)
                .child(Settings.getUID(context))
                .setValue(anuncios)
                .addOnCompleteListener((result -> data.setValue(result.isSuccessful())));
    }
}
