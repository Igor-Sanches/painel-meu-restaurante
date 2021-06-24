package app.birdsoft.painelmeurestaurante.repository;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.birdsoft.painelmeurestaurante.manager.Chave;
import app.birdsoft.painelmeurestaurante.manager.FireStoreUtils;
import app.birdsoft.painelmeurestaurante.model.BlocoPublicar;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.model.CardapioElements;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.model.ItemCardapio;
import app.birdsoft.painelmeurestaurante.settings.Settings;

public class CardapioRepository {
    private static MutableLiveData<CardapioElements> elementos;
    public static MutableLiveData<CardapioElements> getInstance(Context context) {
        if(elementos == null){
            elementos = GetElements(context);
        }
        return elementos;
    }

    private synchronized static MutableLiveData<CardapioElements> GetElements(Context context) {
        MutableLiveData<CardapioElements> data = new MutableLiveData<>();
        getFirebaseData(data, context);
        return data;
    }

    private static synchronized void getFirebaseData(MutableLiveData<CardapioElements> data, Context context) {
        CardapioElements cardapioElements = new CardapioElements();

        if(Conexao.isConnected(context)){
            FireStoreUtils.getDatabaseOnline()
                    .collection(Chave.CARDAPIO)
                    .document(Settings.getUID(context))
                    .collection(Chave.CARDAPIO)
                    .get().addOnCompleteListener((result ->{
                if(result.isSuccessful()){
                    if(result.getResult() != null){
                        List<Cardapio> cardapios = new ArrayList<>();
                        for (DocumentSnapshot snapshot : result.getResult() ){
                            Cardapio cardapio = snapshot.toObject(Cardapio.class);
                            cardapios.add(cardapio);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            cardapios.sort(Comparator.comparingInt(Cardapio::getPosition));
                        }
                        if(cardapios.size() > 0){
                            cardapioElements.setProgressVisibility(View.GONE);
                            cardapioElements.setVazioVisibility(View.GONE);
                            cardapioElements.setListaVisibility(View.VISIBLE);
                        }else{
                            cardapioElements.setListaVisibility(View.GONE);
                            cardapioElements.setProgressVisibility(View.GONE);
                            cardapioElements.setVazioVisibility(View.VISIBLE);
                        }
                        cardapioElements.setLayoutWifiOffline(View.GONE);
                        cardapioElements.setCardapios(cardapios);
                    }else{
                        cardapioElements.setListaVisibility(View.GONE);
                        cardapioElements.setProgressVisibility(View.GONE);
                        cardapioElements.setVazioVisibility(View.VISIBLE);
                        cardapioElements.setLayoutWifiOffline(View.GONE);
                        cardapioElements.setCardapios(null);
                    }

                }else{
                    cardapioElements.setListaVisibility(View.GONE);
                    cardapioElements.setProgressVisibility(View.GONE);
                    cardapioElements.setVazioVisibility(View.VISIBLE);
                    cardapioElements.setCardapios(null);
                }
                data.setValue(cardapioElements);
            }));
        }else{
            cardapioElements.setListaVisibility(View.GONE);
            cardapioElements.setProgressVisibility(View.GONE);
            cardapioElements.setVazioVisibility(View.GONE);
            cardapioElements.setLayoutWifiOffline(View.VISIBLE);
            cardapioElements.setCardapios(null);
            data.setValue(cardapioElements);
        }
    }

    public static void update(MutableLiveData<CardapioElements> data, Context context) {
        getFirebaseData(data, context);
    }

    public static void delete(Cardapio item, MutableLiveData<Boolean> data, Context context) {
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.CARDAPIO)
                .document(Settings.getUID(context))
                .collection(Chave.CARDAPIO)
                .document(item.getUid())
                .delete()
                .addOnCompleteListener((resultado -> {
                    if(resultado.isSuccessful()){
                        FirebaseStorage
                                .getInstance()
                                .getReference()
                                .child("Cardapio")
                                //.child(Settings.getUID(context))
                                .child(item.getUid())
                                .delete();
                        data.setValue(true);
                    }else{
                        data.setValue(false);
                    }
                }));
    }

    public static void insert(Cardapio cardapio, MutableLiveData<Boolean> data, Context context) {
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.CARDAPIO)
                .document(Settings.getUID(context))
                .collection(Chave.CARDAPIO)
                .document(cardapio.getUid())
                .set(cardapio)
                .addOnCompleteListener((resultado->data.setValue(resultado.isSuccessful())));
    }

    public static void insertImage(Cardapio cardapio, Uri uri, Context context, MutableLiveData<Cardapio> data) {
        if(uri != null){
            if(!uri.toString().equals("")){
                try {
                    FirebaseStorage
                            .getInstance()
                            .getReference()
                            .child("Cardapio")
                            .child(Settings.getUID(context))
                            .child(cardapio.getUid())
                            .putStream(context.getContentResolver().openInputStream(uri))
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    try{
                                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                                            if(task1.isSuccessful()){
                                                try{
                                                    cardapio.setImageUrl(task1.getResult().toString());
                                                    data.setValue(cardapio);
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    data.setValue(null);
                }
            }else data.setValue(cardapio);
        }else data.setValue(cardapio);


    }

    public static void insertSubDados(Cardapio cardapio, List<ItemCardapio> itemCardapios, MutableLiveData<Boolean> data, Context context) {
        ArrayList<BlocoPublicar> blocoPublicars = new ArrayList<>();
        for (ItemCardapio itemCardapio : itemCardapios){
            BlocoPublicar publicar = new BlocoPublicar();
            publicar.setContents(itemCardapio.getContents());
            publicar.setDispayTitulo(itemCardapio.getDispayTitulo());
            publicar.setItensAdicionais(itemCardapio.isItensAdicionais());
            publicar.setMaxItensAdicionais(itemCardapio.getMaxItensAdicionais());
            publicar.setMultiselect(itemCardapio.isMultiselect());
            publicar.setTextos(itemCardapio.getTextos());
            publicar.setText(itemCardapio.getText());
            publicar.setObgdSelect(itemCardapio.isObgdSelect());
            publicar.setSelectMax(itemCardapio.getSelectMax());
            publicar.setUid(cardapio.getUid());
            publicar.setValores(itemCardapio.getValores());
            publicar.setValorMaior(itemCardapio.isValorMaior());
            blocoPublicars.add(publicar);
            System.out.println(itemCardapio.getMaxItensAdicionais().size());
            System.out.println(publicar.getMaxItensAdicionais().size());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("cardapio", blocoPublicars);
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.CARDAPIO)
                .document(Settings.getUID(context))
                .collection(Chave.CARDAPIO)
                .document(cardapio.getUid())
                .update(map)
                .addOnCompleteListener((resultado->data.setValue(resultado.isSuccessful())));
    }

    public static void updateCardapio(Map<String, Object> map, String uid, MutableLiveData<Boolean> data, Context context) {

        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.CARDAPIO)
                .document(Settings.getUID(context))
                .collection(Chave.CARDAPIO)
                .document(uid)
                .update(map)
                .addOnCompleteListener((resultado->data.setValue(resultado.isSuccessful())));
    }

    public static void clone(Cardapio item, MutableLiveData<Boolean> data, Context context) {
        String uid = UUID.randomUUID().toString();
        item.setUid(uid);
        item.setName(item.getName() + " Clone");
        item.setDisponivel(false);
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.CARDAPIO)
                .document(Settings.getUID(context))
                .collection(Chave.CARDAPIO)
                .document(uid)
                .set(item)
                .addOnCompleteListener((resultado->data.setValue(resultado.isSuccessful())));
    }

    private static  boolean isSucesso = false;
    public static void update(List<Cardapio> lista, Context context, MutableLiveData<Boolean> data) {
        if(Conexao.isConnected(context)){
            for(int i = 0; i < lista.size(); i++){
                Cardapio cardapio = lista.get(i);
                FireStoreUtils
                        .getDatabaseOnline()
                        .collection(Chave.CARDAPIO)
                        .document(Settings.getUID(context))
                        .collection(Chave.CARDAPIO)
                        .document(cardapio.getUid())
                        .update("position", i)
                        .addOnCompleteListener((resultado -> {
                            if(!resultado.isSuccessful()){
                                data.setValue(false);
                            }
                            isSucesso = resultado.isSuccessful();
                        }));
            }
            data.setValue(isSucesso);
        }else data.setValue(false);
    }
}
