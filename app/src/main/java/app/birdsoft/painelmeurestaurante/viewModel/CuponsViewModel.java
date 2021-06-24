package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;
import android.widget.EditText;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Cupom;
import app.birdsoft.painelmeurestaurante.model.CuponsElements;
import app.birdsoft.painelmeurestaurante.repository.CuponsRepository;

public class CuponsViewModel extends ViewModel {
    private MutableLiveData<CuponsElements> mutableLiveData;

    public void init(Context context){
        if(mutableLiveData != null){
            return;
        }
        mutableLiveData = CuponsRepository.getInstance(context);
    }

    public MutableLiveData<CuponsElements> getMutableLiveData(){
        return mutableLiveData;
    }

    public void update(Context context) {
        if(mutableLiveData != null){
            CuponsRepository.update(mutableLiveData, context);
        }

    }

    public LiveData<Boolean> delete(Cupom cupom, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CuponsRepository.delete(cupom, data, context);
        return data;
    }

    public LiveData<Boolean> insert(Cupom cupom, List<EditText> emails, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CuponsRepository.insert(cupom, emails, data, context);
        return data;
    }

    public LiveData<Boolean> changed(Cupom cupom, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        CuponsRepository.changed(cupom, data, context);
        return data;
    }

    public void CuponsRevisao(LifecycleOwner owne, Context context) {
        try{
            if(mutableLiveData != null){
                getMutableLiveData().observe(owne, (cuponsElements -> {
                    if(cuponsElements.getCupoms() != null){
                        for(Cupom cupom : cuponsElements.getCupoms()){
                            if(!cupom.isExpirado() && cupom.isAtivo() && cupom.isVencimento()){
                                if(HelperManager.isVencido(cupom.getDataValidade())){
                                    CuponsRepository.updateValidade(cupom, context);
                                }
                            }
                        }
                    }
                }));
            }
        }catch (Exception ignored){}
    }
}
