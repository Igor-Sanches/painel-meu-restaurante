package app.birdsoft.painelmeurestaurante.repository;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.manager.Chave;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.FireStoreUtils;
import app.birdsoft.painelmeurestaurante.manager.FirebaseUtils;
import app.birdsoft.painelmeurestaurante.model.Anuncio;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.model.LoginElements;
import app.birdsoft.painelmeurestaurante.settings.Settings;

public class LoginRepository {
    private static MutableLiveData<LoginElements> elementos;

    public static MutableLiveData<LoginElements> getInstance(Context context) {
        if (elementos == null)
            elementos = GetElements(context);

        return elementos;
    }

    private synchronized static MutableLiveData<LoginElements> GetElements(Context context) {
        MutableLiveData<LoginElements> data = new MutableLiveData<>();
        getFirebaseData(data, context);
        return data;
    }

    private static synchronized void getFirebaseData(MutableLiveData<LoginElements> data, Context context) {
        LoginElements loginElements = new LoginElements();
        if (Conexao.isConnected(context)) {
            FireStoreUtils
                    .getDatabaseOnline()
                    .collection(Chave.DADOS)
                    .document(Settings.getUID(context))
                    .collection(Chave.DADOS)
                    .document("autenticacao")
                    .get().addOnCompleteListener((documento -> {
                if (documento.isSuccessful()) {
                    if (documento.getResult() != null) {
                        Map<String, Object> map = documento.getResult().getData();
                        if(map != null){
                            String email = (String) map.get("email");
                            if (email != null) {
                                loginElements.setEmail(email);
                                loginElements.setInicioVisibility(View.VISIBLE);
                                loginElements.setLayoutWifiOffline(View.GONE);
                                loginElements.setProgressVisibility(View.GONE);
                                loginElements.setVazioVisibility(View.GONE);
                            } else {
                                loginElements.setEmail("");
                                loginElements.setInicioVisibility(View.GONE);
                                loginElements.setLayoutWifiOffline(View.GONE);
                                loginElements.setProgressVisibility(View.GONE);
                                loginElements.setVazioVisibility(View.VISIBLE);
                            }
                        }else {
                            loginElements.setEmail("");
                            loginElements.setInicioVisibility(View.GONE);
                            loginElements.setLayoutWifiOffline(View.GONE);
                            loginElements.setProgressVisibility(View.GONE);
                            loginElements.setVazioVisibility(View.VISIBLE);
                        }
                    } else {
                        loginElements.setEmail("");
                        loginElements.setInicioVisibility(View.GONE);
                        loginElements.setLayoutWifiOffline(View.GONE);
                        loginElements.setProgressVisibility(View.GONE);
                        loginElements.setVazioVisibility(View.VISIBLE);
                    }

                } else {
                    loginElements.setEmail("");
                    loginElements.setInicioVisibility(View.GONE);
                    loginElements.setLayoutWifiOffline(View.GONE);
                    loginElements.setProgressVisibility(View.GONE);
                    loginElements.setVazioVisibility(View.VISIBLE);
                }
                data.setValue(loginElements);

            }));
        } else {
            loginElements.setEmail("");
            loginElements.setInicioVisibility(View.GONE);
            loginElements.setLayoutWifiOffline(View.VISIBLE);
            loginElements.setProgressVisibility(View.GONE);
            loginElements.setVazioVisibility(View.GONE);
            data.setValue(loginElements);
        }

    }

    public static void update(MutableLiveData<LoginElements> data, Context context) {
        getFirebaseData(data, context);
    }

    public static void trocaEmail(String email, MutableLiveData<Boolean> data, Context context) {
        Objects.requireNonNull(Conexao.getFirebaseAuth()
                .getCurrentUser())
                .updateEmail(email)
                .addOnCompleteListener((command -> {
                    if (command.isSuccessful()) {
                        FireStoreUtils
                                .getDatabaseOnline()
                                .collection(Chave.DADOS)
                                .document(Settings.getUID(context))
                                .collection(Chave.DADOS)
                                .document("autenticacao")
                                .update("email", email)
                                .addOnCompleteListener((command1 -> data.setValue(command1.isSuccessful())));
                    } else {
                        data.setValue(false);
                    }
                }));
    }

    public static void trocaSenha(String senha, MutableLiveData<Boolean> data) {
        Objects.requireNonNull(Conexao.getFirebaseAuth().getCurrentUser()).updatePassword(senha)
                .addOnCompleteListener((command -> data.setValue(command.isSuccessful())));
    }

    public static void isContaExist(MutableLiveData<Boolean> data, Context context) {
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.DADOS)
                .document(Settings.getUID(context))
                .collection(Chave.DADOS)
                .document("autenticacao")
                .get().addOnCompleteListener((result -> {
            if (result.isSuccessful()) {
                data.setValue(result.getResult().exists());
            } else data.setValue(false);
        }));
    }

    public static void createAccont(String email, MutableLiveData<Boolean> data, Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.DADOS)
                .document(Settings.getUID(context))
                .collection(Chave.DADOS)
                .document("autenticacao")
                .set(map).addOnCompleteListener((result -> {
                    iniciarPostagem(context);
            data.setValue(result.isSuccessful());
        }));
    }

    private static void iniciarPostagem(Context context) {
        FireStoreUtils.getDatabaseOnline()
                .collection("Cardapio")
                .get().addOnCompleteListener((result -> {
                    if(result.isSuccessful()){
                        if(Conexao.isConnected(context)) {
                            if (result.getResult() != null) {
                                for (DocumentSnapshot snapshot : result.getResult()) {
                                    Cardapio cardapio = snapshot.toObject(Cardapio.class);
                                    if(cardapio!=null)
                                        CardapioRepository
                                                .insert(cardapio, new MutableLiveData<>(), context);
                                }
                            }
                        }
                    }
        }));
        FirebaseUtils
                .getDatabaseRef()
                .child("Anuncios")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            GenericTypeIndicator<List<Anuncio>> list = new GenericTypeIndicator<List<Anuncio>>() {};
                            List<Anuncio> anuncios = snapshot.getValue(list);
                            AnunciosRepository.insert(anuncios, new MutableLiveData<>(), context);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
