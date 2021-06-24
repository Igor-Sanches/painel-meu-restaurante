package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.InputStream;
import java.util.List;

import app.birdsoft.painelmeurestaurante.model.Anuncio;
import app.birdsoft.painelmeurestaurante.model.AnunciosElements;
import app.birdsoft.painelmeurestaurante.repository.AnunciosRepository;

public class AnunciosViewModel extends ViewModel {
    private MutableLiveData<AnunciosElements> mutableLiveData;

    public void init(Context context){
        if(mutableLiveData != null){
            return;
        }
        mutableLiveData = AnunciosRepository.getInstance(context);
    }

    public MutableLiveData<AnunciosElements> getMutableLiveData(){
        return mutableLiveData;
    }

    public void update(Context context) {
        if(mutableLiveData != null){
            AnunciosRepository.update(mutableLiveData, context);
        }

    }

    public LiveData<Anuncio> insertImage(InputStream inputStream, Context context) {
        MutableLiveData<Anuncio> data = new MutableLiveData<>();
        AnunciosRepository.insertImage(inputStream, data, context);
        return data;
    }

    public LiveData<Boolean> insert(List<Anuncio> anuncios, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        AnunciosRepository.insert(anuncios, data, context);
        return data;
    }

    public LiveData<Boolean> insertDeletede(Anuncio anuncio, List<Anuncio> anuncios, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        AnunciosRepository.insertDeletede(anuncio, anuncios, data, context);
        return data;
    }
}
