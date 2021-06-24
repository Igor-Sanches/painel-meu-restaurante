package app.birdsoft.painelmeurestaurante.repository;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import app.birdsoft.painelmeurestaurante.manager.Chave;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.FirebaseUtils;
import app.birdsoft.painelmeurestaurante.manager.FragmentType; 
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.model.PedidosElements;
import app.birdsoft.painelmeurestaurante.settings.Settings;

public class PedidosRepository {
    public static MutableLiveData<PedidosElements> getInstance(Context context, FragmentType fragmentType) {
        //if(elementos == null){
        //}
        return GetElements(context, fragmentType);
    }

    private synchronized static MutableLiveData<PedidosElements> GetElements(Context context, FragmentType fragmentType) {
        MutableLiveData<PedidosElements> data = new MutableLiveData<>();
        getFirebaseData(data, fragmentType, context);
        return data;
    }

    private static synchronized void getFirebaseData(MutableLiveData<PedidosElements> data, FragmentType fragmentType, Context context) {
        PedidosElements pedidosElements = new PedidosElements();

        if(Conexao.isConnected(context)){
            FirebaseUtils
                    .getDatabaseRef()
                    .child(Chave.PEDIDOS)
                    .child(Settings.getUID(context))
                    .orderByChild("statusPedido")
                    .equalTo(fragmentType.toString())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try{
                                if(snapshot.exists()){
                                    ArrayList<Pedido> pedidos = new ArrayList<>();
                                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                        Pedido pedido = snapshot1.getValue(Pedido.class);
                                        pedidos.add(pedido);
                                    }
                                    if(pedidos.size() > 0){
                                        pedidosElements.setListaVisibility(View.VISIBLE);
                                        pedidosElements.setProgressVisibility(View.GONE);
                                        pedidosElements.setVazioVisibility(View.GONE);
                                    }else{
                                        pedidosElements.setListaVisibility(View.GONE);
                                        pedidosElements.setProgressVisibility(View.GONE);
                                        pedidosElements.setVazioVisibility(View.VISIBLE);
                                    }
                                    pedidosElements.setLayoutWifiOffline(View.GONE);
                                    pedidosElements.setPedidos(pedidos);

                                }else{
                                    pedidosElements.setListaVisibility(View.GONE);
                                    pedidosElements.setProgressVisibility(View.GONE);
                                    pedidosElements.setVazioVisibility(View.VISIBLE);
                                    pedidosElements.setLayoutWifiOffline(View.GONE);
                                    pedidosElements.setPedidos(new ArrayList<>());
                                }

                                data.setValue(pedidosElements);
                            }catch (Exception x){
                                pedidosElements.setListaVisibility(View.GONE);
                                pedidosElements.setProgressVisibility(View.GONE);
                                pedidosElements.setVazioVisibility(View.VISIBLE);
                                pedidosElements.setLayoutWifiOffline(View.GONE);
                                pedidosElements.setPedidos(new ArrayList<>());
                                data.setValue(pedidosElements);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            pedidosElements.setListaVisibility(View.GONE);
                            pedidosElements.setProgressVisibility(View.GONE);
                            pedidosElements.setVazioVisibility(View.VISIBLE);
                            pedidosElements.setLayoutWifiOffline(View.GONE);
                            pedidosElements.setPedidos(new ArrayList<>());
                            data.setValue(pedidosElements);
                        }
                    });
        }else{
            pedidosElements.setListaVisibility(View.GONE);
            pedidosElements.setLayoutWifiOffline(View.VISIBLE);
            pedidosElements.setProgressVisibility(View.GONE);
            pedidosElements.setVazioVisibility(View.GONE);
            pedidosElements.setPedidos(new ArrayList<>());
            data.setValue(pedidosElements);
        }
    }

    public static void update(Pedido pedido, MutableLiveData<Boolean> data, Context context) {
        FirebaseUtils
                .getDatabaseRef()
                .child(Chave.PEDIDOS)
                .child(Settings.getUID(context))
                .child(pedido.getUid_client())
                .setValue(pedido).addOnCompleteListener((result -> data.setValue(result.isSuccessful())));
    }

    public static void remove(Pedido pedido, MutableLiveData<Boolean> data, Context context) {
        FirebaseUtils
                .getDatabaseRef()
                .child(Chave.PEDIDOS)
                .child(Settings.getUID(context))
                .child(pedido.getUid_client())
                .removeValue().addOnCompleteListener((result -> data.setValue(result.isSuccessful())));
    }

    public static void update(MutableLiveData<PedidosElements> data, Context context, FragmentType fragmentType) {
        getFirebaseData(data, fragmentType, context);
    }
}
