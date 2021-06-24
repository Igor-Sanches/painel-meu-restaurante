package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.birdsoft.painelmeurestaurante.model.HistoricoElements;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.repository.HistoricosRepository;

public class HistoricosViewModel extends ViewModel {
    private MutableLiveData<HistoricoElements> mutableLiveData;

    public void init(Context contextx){
        if(mutableLiveData != null){
            return;
        }
        mutableLiveData = HistoricosRepository.getInstance(contextx);
    }

    public MutableLiveData<HistoricoElements> getMutableLiveData(){
        return mutableLiveData;
    }

    public void update(Context context) {
        if(mutableLiveData != null){
            HistoricosRepository.update(mutableLiveData, context);
        }

    }

    public LiveData<Boolean> delete(Pedido pedido, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        HistoricosRepository.delete(pedido, data, context);
        return data;
    }

    public void autoDelete(LifecycleOwner owner, Context context) {
        try{
            if(mutableLiveData != null){
                getMutableLiveData().observe(owner, (historicoElements -> {
                    if(historicoElements.getPedidos() != null){
                        if(historicoElements.getPedidos().size() > 100){
                            for(int i = 0; i < 100; i++){
                                Pedido pedido = historicoElements.getPedidos().get(i);
                                HistoricosRepository.delete(pedido, context);
                            }
                            update(context);
                        }
                    }
                }));
            }
        }catch (Exception ignored){


        }
    }
}
