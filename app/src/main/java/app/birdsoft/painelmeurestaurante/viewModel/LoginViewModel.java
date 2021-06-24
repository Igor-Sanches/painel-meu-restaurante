package app.birdsoft.painelmeurestaurante.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.birdsoft.painelmeurestaurante.model.LoginElements;
import app.birdsoft.painelmeurestaurante.repository.LoginRepository;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginElements> mutableLiveData;

    public void init(Context context){
        if(mutableLiveData != null){
            return;
        }
        mutableLiveData = LoginRepository.getInstance(context);
    }

    public MutableLiveData<LoginElements> getMutableLiveData(){
        return mutableLiveData;
    }

    public void update(Context context) {
        if(mutableLiveData != null){
            LoginRepository.update(mutableLiveData, context);
        }

    }

    public LiveData<Boolean> createAccont(String email, Context context){
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        LoginRepository.createAccont(email, data, context);
        return data;
    }

    public LiveData<Boolean> isContaExist(Context context){
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        LoginRepository.isContaExist(data, context);
        return data;
    }

    public LiveData<Boolean> trocaEmail(String email, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        LoginRepository.trocaEmail(email, data, context);
        return data;
    }


    public LiveData<Boolean> trocaSenha(String senha) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        LoginRepository.trocaSenha(senha, data);
        return data;
    }
}
