package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.birdsoft.painelmeurestaurante.model.EstabelecimentoElements;
import app.birdsoft.painelmeurestaurante.repository.EstabelecimentoRepository;

public class EstabelecimentoViewModel extends ViewModel {
    private MutableLiveData<EstabelecimentoElements> mutableLiveData;

    public void init(Context context){
        if(mutableLiveData != null)
            return;

        mutableLiveData = EstabelecimentoRepository.getInstance(context);
    }

    public MutableLiveData<EstabelecimentoElements> getMutableLiveData(){
        return mutableLiveData;
    }

    public void update(Context context) {
        if(mutableLiveData != null){
            EstabelecimentoRepository.update(mutableLiveData, context);
        }

    }

    public LiveData<Boolean> updateOrInsert(Object value, String key, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        EstabelecimentoRepository.updateOrInsert(value, key, data, context);
        return data;
    }

    public LiveData<Boolean> openOrClose(boolean isOpen, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        EstabelecimentoRepository.openOrClose(isOpen, data, context);
        return data;
    }

    public LiveData<Boolean> getMutableLiveDataNoUpdate(Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        EstabelecimentoRepository.returnEstabelecimento(data, context);
        return data;
    }

    public LiveData<Boolean> iniciarDados(String aberto, String config, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        EstabelecimentoRepository.iniciarDados(aberto, config, data, context);
        return data;
    }
}
