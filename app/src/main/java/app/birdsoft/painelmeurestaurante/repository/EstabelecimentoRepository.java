package app.birdsoft.painelmeurestaurante.repository;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import app.birdsoft.painelmeurestaurante.manager.Chave;
import app.birdsoft.painelmeurestaurante.manager.FireStoreUtils;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Estabelecimento;
import app.birdsoft.painelmeurestaurante.model.EstabelecimentoElements;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.DateTime;

public class EstabelecimentoRepository {
    private static MutableLiveData<EstabelecimentoElements> elementos;
    public static MutableLiveData<EstabelecimentoElements> getInstance(Context context) {
        if(elementos == null)
            elementos = GetElements(context);

        return elementos;
    }

    private synchronized static MutableLiveData<EstabelecimentoElements> GetElements(Context context) {
        MutableLiveData<EstabelecimentoElements> data = new MutableLiveData<>();
        getFirebaseData(data, context);
        return data;
    }

    private static synchronized void getFirebaseData(MutableLiveData<EstabelecimentoElements> data, Context context) {
        EstabelecimentoElements estabelecimentoElements = new EstabelecimentoElements();
        if(Conexao.isConnected(context)){
            FireStoreUtils
                    .getDatabaseOnline()
                    .collection(Chave.ESTABELECIMENTO)
                    .document(Settings.getUID(context))
                    .collection(Chave.ESTABELECIMENTO)
                    .document(Chave.DADOS)
                    .get().addOnCompleteListener((documento ->{
                if(documento.isSuccessful()) {
                    if (documento.getResult() != null) {
                        Estabelecimento estabelecimento = documento.getResult().toObject(Estabelecimento.class);
                        if(estabelecimento != null){
                            estabelecimentoElements.setConfig(estabelecimento.isConfig());
                            estabelecimentoElements.setAberto(estabelecimento.isAberto());
                            estabelecimentoElements.setEndereco(estabelecimento.getEndereco() == null ? "" : estabelecimento.getEndereco());
                            estabelecimentoElements.setGeoPoint(estabelecimento.getLocal());
                            estabelecimentoElements.setHorarios(estabelecimento.getHorarios() == null ? HelperManager.drive(null) : estabelecimento.getHorarios());
                            estabelecimentoElements.setLigacao(estabelecimento.getTelefone() == null ? "" : estabelecimento.getTelefone());
                            estabelecimentoElements.setWhatsapp(estabelecimento.getWhatsapp() == null ? "" : estabelecimento.getWhatsapp());
                            estabelecimentoElements.setKm(estabelecimento.getKm() == null ? 0 : estabelecimento.getKm());
                            estabelecimentoElements.setPrazoMinutoFixo(estabelecimento.getPrazoMinutoFixo() == null ? 0 : estabelecimento.getPrazoMinutoFixo());
                            estabelecimentoElements.setTaxa(estabelecimento.getTaxa() == null ? -1 : estabelecimento.getTaxa());
                            estabelecimentoElements.setValorPerKm(estabelecimento.getValorPerKm() == null ? 0 : estabelecimento.getValorPerKm());
                            estabelecimentoElements.setValorFixo(estabelecimento.getValorFixo() == null ? 0 : estabelecimento.getValorFixo());
                            estabelecimentoElements.setEmail(estabelecimento.getEmail() == null ? "" : estabelecimento.getEmail());
                            estabelecimentoElements.setPrazo(estabelecimento.getPrazo() == null ? 0 : estabelecimento.getPrazo());
                            estabelecimentoElements.setLayoutWifiOffline(View.GONE);
                            estabelecimentoElements.setLayoutProgress(View.GONE);
                        }else{
                            insert(context);
                            estabelecimentoElements.setConfig(false);
                            estabelecimentoElements.setAberto(false);
                            estabelecimentoElements.setEndereco("");
                            estabelecimentoElements.setGeoPoint(null);
                            estabelecimentoElements.setHorarios(HelperManager.drive(null));
                            estabelecimentoElements.setPrazo(0);
                            estabelecimentoElements.setLigacao("");
                            estabelecimentoElements.setWhatsapp("");
                            estabelecimentoElements.setKm(0);
                            estabelecimentoElements.setPrazoMinutoFixo(0);
                            estabelecimentoElements.setTaxa(-1);
                            estabelecimentoElements.setValorPerKm(0);
                            estabelecimentoElements.setValorFixo(0);
                            estabelecimentoElements.setEmail("");
                            estabelecimentoElements.setLayoutProgress(View.GONE);
                            estabelecimentoElements.setLayoutWifiOffline(View.GONE);
                        }
                        estabelecimentoElements.setLayoutInicial(View.VISIBLE);
                        estabelecimentoElements.setLayoutVazio(View.GONE);
                        data.setValue(estabelecimentoElements);
                    }else{
                        erro(data, estabelecimentoElements);
                    }

                }else{
                    erro(data, estabelecimentoElements);

                }

            }));
        }else{
            estabelecimentoElements.setConfig(false);
            estabelecimentoElements.setAberto(false);
            estabelecimentoElements.setEndereco("");
            estabelecimentoElements.setGeoPoint(null);
            estabelecimentoElements.setHorarios(HelperManager.drive(null));
            estabelecimentoElements.setLigacao("");
            estabelecimentoElements.setWhatsapp("");
            estabelecimentoElements.setKm(0);
            estabelecimentoElements.setPrazoMinutoFixo(0);
            estabelecimentoElements.setTaxa(-1);
            estabelecimentoElements.setValorPerKm(0);
            estabelecimentoElements.setValorFixo(0);
            estabelecimentoElements.setPrazo(0);
            estabelecimentoElements.setEmail("");
            estabelecimentoElements.setLayoutProgress(View.GONE);
            estabelecimentoElements.setLayoutWifiOffline(View.VISIBLE);
            estabelecimentoElements.setLayoutVazio(View.GONE);
            estabelecimentoElements.setLayoutInicial(View.GONE);
            data.setValue(estabelecimentoElements);
        }


    }

    private static void erro(MutableLiveData<EstabelecimentoElements> data, EstabelecimentoElements estabelecimentoElements) {
        estabelecimentoElements.setConfig(false);
        estabelecimentoElements.setAberto(false);
        estabelecimentoElements.setEndereco("");
        estabelecimentoElements.setGeoPoint(null);
        estabelecimentoElements.setHorarios(HelperManager.drive(null));
        estabelecimentoElements.setLigacao("");
        estabelecimentoElements.setWhatsapp("");
        estabelecimentoElements.setPrazo(0);
        estabelecimentoElements.setKm(0);
        estabelecimentoElements.setPrazoMinutoFixo(0);
        estabelecimentoElements.setTaxa(-1);
        estabelecimentoElements.setValorPerKm(0);
        estabelecimentoElements.setValorFixo(0);
        estabelecimentoElements.setEmail("");
        estabelecimentoElements.setLayoutWifiOffline(View.GONE);
        estabelecimentoElements.setLayoutInicial(View.VISIBLE);
        estabelecimentoElements.setLayoutProgress(View.GONE);
        estabelecimentoElements.setLayoutVazio(View.GONE);
        data.setValue(estabelecimentoElements);
    }

    public static void update(MutableLiveData<EstabelecimentoElements> data, Context context) {
        getFirebaseData(data, context);
    }

    public static void insert(Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", DateTime.getTime());
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.ESTABELECIMENTO)
                .document(Settings.getUID(context))
                .collection(Chave.ESTABELECIMENTO)
                .document(Chave.DADOS)
                .set(map);
    }

    public static void updateOrInsert(Object value, String key, MutableLiveData<Boolean> data, Context context) {
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.ESTABELECIMENTO)
                .document(Settings.getUID(context))
                .collection(Chave.ESTABELECIMENTO)
                .document(Chave.DADOS)
                .update(key, value)
                .addOnCompleteListener((result -> data.setValue(result.isSuccessful())));
    }

    public static void openOrClose(boolean isOpen, MutableLiveData<Boolean> data, Context context) {
        if(Conexao.isConnected(context)){
            FireStoreUtils
                    .getDatabaseOnline()
                    .collection(Chave.ESTABELECIMENTO)
                    .document(Settings.getUID(context))
                    .collection(Chave.ESTABELECIMENTO)
                    .document(Chave.DADOS)
                    .update("aberto", isOpen)
                    .addOnCompleteListener((result -> data.setValue(result.isSuccessful())));
        }else data.setValue(false);
    }

    public static void returnEstabelecimento(MutableLiveData<Boolean> data, Context context) {
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.ESTABELECIMENTO)
                .document(Settings.getUID(context))
                .collection(Chave.ESTABELECIMENTO)
                .document(Chave.DADOS)
                .get().addOnCompleteListener((documento -> {
            if(Conexao.isConnected(context)) {
                if (documento.getResult() != null) {
                    Estabelecimento estabelecimento = documento.getResult().toObject(Estabelecimento.class);
                    data.setValue(estabelecimento != null);
                }else data.setValue(false);
            }else data.setValue(false);
        }));
    }

    public static void iniciarDados(String aberto, String config, MutableLiveData<Boolean> data, Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put(aberto, true);
        map.put(config, true);
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.ESTABELECIMENTO)
                .document(Settings.getUID(context))
                .collection(Chave.ESTABELECIMENTO)
                .document(Chave.DADOS)
                .update(map)
                .addOnCompleteListener((result -> data.setValue(result.isSuccessful())));
    }
}
