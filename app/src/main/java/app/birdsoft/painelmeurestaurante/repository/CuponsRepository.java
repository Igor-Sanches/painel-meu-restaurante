package app.birdsoft.painelmeurestaurante.repository;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.Chave;
import app.birdsoft.painelmeurestaurante.manager.FireStoreUtils;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Cliente;
import app.birdsoft.painelmeurestaurante.model.Cupom;
import app.birdsoft.painelmeurestaurante.model.CuponsElements;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.service.NotificationSendData;
import app.birdsoft.painelmeurestaurante.service.NotificationType;
import app.birdsoft.painelmeurestaurante.service.SendNotification;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.Status;

public class CuponsRepository {
    private static MutableLiveData<CuponsElements> elementos;
    public static MutableLiveData<CuponsElements> getInstance(Context context) {
        if(elementos == null){
            elementos = GetElements(context);
        }
        return elementos;
    }

    private synchronized static MutableLiveData<CuponsElements> GetElements(Context context) {
        MutableLiveData<CuponsElements> data = new MutableLiveData<>();
        getFirebaseData(data, context);
        return data;
    }

    private static synchronized void getFirebaseData(MutableLiveData<CuponsElements> data, Context context) {
        CuponsElements cuponsElements = new CuponsElements();

        if(Conexao.isConnected(context)){
            FireStoreUtils.getDatabaseOnline()
                    .collection(Chave.CUPOM)
                    .document(Settings.getUID(context))
                    .collection(Chave.CUPOM)
                    .get()
                    .addOnCompleteListener((result ->{
                        if(result.isSuccessful()){
                            if(result.getResult() != null){
                                List<Cupom> cupoms = new ArrayList<>();
                                for(DocumentSnapshot snapshot : result.getResult()){
                                    Cupom cupom = snapshot.toObject(Cupom.class);
                                    cupoms.add(cupom);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    cupoms.sort(Comparator.comparingLong(Cupom::getDataValidade).reversed());
                                }
                                if(cupoms.size() > 0){
                                    cuponsElements.setProgressVisibility(View.GONE);
                                    cuponsElements.setVazioVisibility(View.GONE);
                                    cuponsElements.setListaVisibility(View.VISIBLE);
                                }else{
                                    cuponsElements.setListaVisibility(View.GONE);
                                    cuponsElements.setProgressVisibility(View.GONE);
                                    cuponsElements.setVazioVisibility(View.VISIBLE);
                                }
                                cuponsElements.setLayoutWifiOffline(View.GONE);
                                cuponsElements.setCupoms(cupoms);
                            }else{
                                cuponsElements.setListaVisibility(View.GONE);
                                cuponsElements.setProgressVisibility(View.GONE);
                                cuponsElements.setVazioVisibility(View.VISIBLE);
                                cuponsElements.setLayoutWifiOffline(View.GONE);
                                cuponsElements.setCupoms(null);
                            }
                        }else{
                            cuponsElements.setListaVisibility(View.GONE);
                            cuponsElements.setProgressVisibility(View.GONE);
                            cuponsElements.setVazioVisibility(View.VISIBLE);
                            cuponsElements.setLayoutWifiOffline(View.GONE);
                            cuponsElements.setCupoms(null);
                        }
                        data.setValue(cuponsElements);
                    }));
        }else{
            cuponsElements.setListaVisibility(View.GONE);
            cuponsElements.setProgressVisibility(View.GONE);
            cuponsElements.setVazioVisibility(View.GONE);
            cuponsElements.setLayoutWifiOffline(View.VISIBLE);
            cuponsElements.setCupoms(null);
            data.setValue(cuponsElements);
        }
    }

    public static void update(MutableLiveData<CuponsElements> data, Context context) {
        getFirebaseData(data, context);
    }

    public static void delete(Cupom cupom, MutableLiveData<Boolean> data, Context context) {
        FireStoreUtils.getDatabaseOnline()
                .collection(Chave.CUPOM)
                .document(Settings.getUID(context))
                .collection(Chave.CUPOM)
                .document(cupom.getCodigo())
                .delete()
                .addOnCompleteListener((resultado -> data.setValue(resultado.isSuccessful())));
    }

    public static void insert(Cupom cupom, List<EditText> emails, MutableLiveData<Boolean> data, Context context) {
        Map<String, Object> map = new HashMap<>();
        FireStoreUtils
                .getDatabase()
                .collection(Chave.Usuario)
                .document(Settings.getUID(context))
                .collection(Chave.Usuario)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(!cupom.isAlluser()){
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        if(cliente != null){
                            if(!cupom.isAlluser()){
                                for (EditText email : emails) {
                                    if (email.getText().toString().trim().equals(cliente.getEmail())) {
                                        map.put(cliente.getUuid(), -1);
                                        onPushNotificationNewCupom(cliente.getUuid(), cupom, context);
                                    }
                                }
                            }else{
                                onPushNotificationNewCupom(cliente.getUuid(), cupom, context);
                            }
                        }
                    }
                }
            }
        });

        map.put("dataValidade", cupom.getDataValidade());
        map.put("numerouso", cupom.getNumerouso());
        map.put("codigo", cupom.getCodigo());
        map.put("ativo", cupom.isAtivo());
        map.put("vencimento", cupom.isVencimento());
        map.put("alluser", cupom.isAlluser());
        map.put("expirado", false);
        map.put("mininum", cupom.isMininum());
        map.put("descontoType", cupom.getDescontoType());
        map.put("valorMinimo", cupom.getValorMinimo());
        map.put("valorDesconto", cupom.getValorDesconto());
        FireStoreUtils.getDatabaseOnline()
                .collection(Chave.CUPOM)
                .document(Settings.getUID(context))
                .collection(Chave.CUPOM)
                .document(cupom.getCodigo())
                .set(map).addOnCompleteListener((resultado -> data.setValue(resultado.isSuccessful())));
    }

    private static void onPushNotificationNewCupom(String uuid, Cupom cupom, Context context) {
        NotificationSendData data = new NotificationSendData();
        data.setMessage(getDesconto(cupom, context));
        data.setTitle(context.getString(R.string.novo_cupom));
        data.setNotificationType(NotificationType.cupon.toString());
        data.setState_pedido(Status.novoPedido.toString());
        data.setUid_cliente(uuid);
        data.setUid_pedido("");
        new SendNotification().onPush(data, context);
    }

    private static String getDesconto(Cupom cupom, Context context) {
        if(cupom.getDescontoType() != 0){
            if(cupom.getDescontoType() == 2){
                return context.getString(R.string.cupom_descont) + " " + Mask.formatarValor(cupom.getValorDesconto()) + " Off";
            }else return context.getString(R.string.cupom_descont) + " " + ((int)cupom.getValorDesconto()) + "% Off";
        }else return context.getString(R.string.cupom_descont) + " " + context.getString(R.string.cupom_frete_grates);
    }

    public static void changed(Cupom cupom, MutableLiveData<Boolean> data, Context context) {
        Map<String, Object> map = new HashMap<>();
        if(cupom.isAtivo()){
            map.put("ativo", false);
        }else{
            map.put("ativo", true);
            if(!HelperManager.isVencido(cupom.getDataValidade())){
                map.put("expirado", false);
                map.put("dataValidade", DateTime.getValidadeNova());
            }
        }

        FireStoreUtils.getDatabaseOnline()
                .collection(Chave.CUPOM)
                .document(Settings.getUID(context))
                .collection(Chave.CUPOM)
                .document(cupom.getCodigo())
                .update(map).addOnCompleteListener((resultado -> data.setValue(resultado.isSuccessful())));
    }

    public static void updateValidade(Cupom cupom, Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("expirado", true);
        FireStoreUtils.getDatabaseOnline()
                .collection(Chave.CUPOM)
                .document(Settings.getUID(context))
                .collection(Chave.CUPOM)
                .document(cupom.getCodigo())
                .update(map);

    }
}