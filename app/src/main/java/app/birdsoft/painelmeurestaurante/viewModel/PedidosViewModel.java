package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.birdsoft.painelmeurestaurante.manager.FragmentType;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.model.PedidosElements;
import app.birdsoft.painelmeurestaurante.repository.PedidosRepository;

public class PedidosViewModel extends ViewModel {
    private MutableLiveData<PedidosElements> mutableLiveData;

    public void init(Context context, FragmentType fragmentType){
        //if(mutableLiveData != null){
        //    return;
        //}
        mutableLiveData = PedidosRepository.getInstance(context, fragmentType);
    }

    public MutableLiveData<PedidosElements> getMutableLiveData(){
        return mutableLiveData;
    }


    public LiveData<Boolean> update(Pedido pedido, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        PedidosRepository.update(pedido, data, context);
        return data;
    }

    public LiveData<Boolean> remove(Pedido pedido, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        PedidosRepository.remove(pedido, data, context);
        return data;
    }

    public void update(Context context, FragmentType fragmentType) {
        if(mutableLiveData != null){
            PedidosRepository.update(mutableLiveData, context, fragmentType);
        }
    }
}
