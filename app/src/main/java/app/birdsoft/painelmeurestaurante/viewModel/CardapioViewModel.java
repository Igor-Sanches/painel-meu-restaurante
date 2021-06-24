package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.model.CardapioElements;
import app.birdsoft.painelmeurestaurante.model.ItemCardapio;
import app.birdsoft.painelmeurestaurante.repository.CardapioRepository;

public class CardapioViewModel extends ViewModel {
    private MutableLiveData<CardapioElements> mutableLiveData;

    public void init(Context context){
        if(mutableLiveData != null){
            return;
        }
        mutableLiveData = CardapioRepository.getInstance(context);
    }

    public MutableLiveData<CardapioElements> getMutableLiveData(){
        return mutableLiveData;
    }

    public void update(Context context) {
        if(mutableLiveData != null){
            CardapioRepository.update(mutableLiveData, context);
        }

    }

    public LiveData<Boolean> delete(Cardapio item, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CardapioRepository.delete(item, data, context);
        return data;
    }

    public LiveData<Boolean> insert(Cardapio cardapio, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CardapioRepository.insert(cardapio, data, context);
        return data;
    }

    public LiveData<Cardapio> insertImage(Cardapio cardapio, Uri uri, Context context) {
        MutableLiveData<Cardapio> data = new MutableLiveData<>();
        CardapioRepository.insertImage(cardapio, uri, context, data);
        return data;
    }

    public LiveData<Boolean> insertSubDados(Cardapio cardapio, List<ItemCardapio> itemCardapios, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CardapioRepository.insertSubDados(cardapio, itemCardapios, data, context);
        return data;
    }

    public LiveData<Boolean> updateCardapio(Map<String, Object> map, String uid, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CardapioRepository.updateCardapio(map, uid, data, context);
        return data;
    }

    public LiveData<Boolean> clone(Cardapio item, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CardapioRepository.clone(item, data, context);
        return data;
    }

    public LiveData<Boolean> update(List<Cardapio> lista, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CardapioRepository.update(lista, context, data);
        return data;
    }
}
