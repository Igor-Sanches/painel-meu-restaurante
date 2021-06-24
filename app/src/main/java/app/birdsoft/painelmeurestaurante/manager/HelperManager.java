package app.birdsoft.painelmeurestaurante.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogSair;
import app.birdsoft.painelmeurestaurante.model.BlocoPublicar;
import app.birdsoft.painelmeurestaurante.model.EstabelecimentoHorario;
import app.birdsoft.painelmeurestaurante.model.ItemCardapio;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.PedidosViewModel;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class HelperManager {

    public static LiveData<Boolean> getContrato(Context context){
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        FireStoreUtils
                .getDatabaseOnline()
                .collection("Contato")
                .document("contrato")
                .get()
                .addOnCompleteListener((result -> {
                    if(result.isSuccessful()){
                        if(result.getResult().exists()){
                            Map<String, Object> map = result.getResult().getData();
                            if(map != null){
                                if(map.get("numero") != null){
                                    data.setValue(true);
                                    startZap((String) Objects.requireNonNull(map.get("numero")), context);
                                }else data.setValue(false);
                            }else data.setValue(false);
                        }else data.setValue(false);
                    }else data.setValue(false);
                }));
        return data;
    }

    public static void startZap(String zap2, Context context) {
        StringBuilder numero = new StringBuilder();
        for(char c : zap2.toCharArray()){
            if("0123456789".contains(String.valueOf(c))){
                numero.append(c);
            }
        }

        String zap = PhoneNumberUtils.formatNumber(numero.toString());
        if("###########".length() == zap.length() || "##########".length() == zap.length()){
            zap = "+55" + zap;
        }
        String link = "https://api.whatsapp.com/send?phone=" + zap;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    public static void startCall(Pedido pedido, Context context) {
        StringBuilder numero = new StringBuilder();
        for(char c : pedido.getTelefone().toCharArray()){
            if("0123456789".contains(String.valueOf(c))){
                numero.append(c);
            }
        }

        String zap = PhoneNumberUtils.formatNumber(numero.toString());
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + zap)));
    }

    public static void startZap(Pedido pedido, Context context) {
        StringBuilder numero = new StringBuilder();
        for(char c : pedido.getTelefone().toCharArray()){
            if("0123456789".contains(String.valueOf(c))){
                numero.append(c);
            }
        }

        String zap = PhoneNumberUtils.formatNumber(numero.toString());
        if("###########".length() == zap.length() || "##########".length() == zap.length()){
            zap = "+55" + zap;
        }
        String link = "https://api.whatsapp.com/send?phone=" + zap;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static void startGPS(Pedido pedido, Context context) {
        String coordenadas = pedido.getCoordenadas();
        Uri gmmIntentUri = Uri.parse("geo:" + coordenadas + "?q=" + coordenadas);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }else{
            MyToast.makeText(context, R.string.erro_gps_rota).show();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static void startGPSRota(Pedido pedido, Context context) {
        String coordenadas = pedido.getCoordenadas();
        String link = "https://www.google.com.br/maps/dir//" + coordenadas + "/@" + coordenadas + ",16z/data=!4m4!4m3!1m1!4e2!1m0";
        Uri gmmIntentUri = Uri.parse(link);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }else{
            MyToast.makeText(context, R.string.erro_gps_rota).show();
        }
    }

    public static void exitApp(Activity activity) {
        DialogSair dialogSair = new DialogSair(activity);
        dialogSair.show();
    }
/*
    public static LiveData<Boolean> endereco(Cliente _cliente, String _endereco, Context context, ViewModelStoreOwner owner, LifecycleOwner _owner){
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        View root = LayoutInflater.from(context).inflate(R.layout.card_endereco, null);
        TextInputLayout mTextInputLayout = root.findViewById(R.id.pinUser);
        EditText editor = mTextInputLayout.getEditText();
        editor.setText(_cliente == null ? _endereco : _cliente.getEndereco());
        editor.setSelection(_cliente == null ? _endereco.length() : _cliente.getEndereco().length());
        Button cancel = root.findViewById(R.id.cancel);
        Button enter = root.findViewById(R.id.enter);
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
            data.setValue(false);
        });
        LoadingDialog dialog1 = new LoadingDialog(context);
        enter.setOnClickListener((v -> {
            if(Conexao.isConnected(context)){
                String endereco = editor.getText().toString().trim();
                if(!endereco.equals("")){
                    if(!(_cliente == null ? _endereco : _cliente.getEndereco()).equals(endereco)){
                        dialog1.show();
                        new ViewModelProvider(owner).get(SettingsViewModel.class).trocaEndereco(endereco, context).observe(_owner, (success ->{
                            if(success){
                                Usuario.setEndereco(endereco, context);
                                new ViewModelProvider(owner).get(SettingsViewModel.class).update(context);
                                data.setValue(true);
                                dialog.dismiss();
                                MyToast.makeText(context, R.string.troca_endereco, ModoColor._success).show();
                            }else{
                                MyToast.makeText(context, R.string.error_troca_endereco, ModoColor._falha).show();
                            }
                            dialog1.dismiss();
                        }));
                    }else{
                        dialog.dismiss();
                    }
                }else{
                    MyToast.makeText(context, R.string.digite_o_seu_endereco, ModoColor._falha).show();
                }
            }else{
                MyToast.makeText(context, R.string.sem_conexao, ModoColor._falha).show();
            }

        }));

        dialog.setView(root);
        dialog.show();
        return data;
    }
*/
    public static boolean isVencido(long dataValidade) {
        return DateTime.isValido(dataValidade);
    }

    public static long clieckTime = 0;
    public static boolean isClicked(){
        if(System.currentTimeMillis() - clieckTime > 4000){
            clieckTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static List<EstabelecimentoHorario> drive(List<EstabelecimentoHorario> horarios) {
        if(horarios == null){
            List<EstabelecimentoHorario> _horarios = new ArrayList<>();
            _horarios.add(new EstabelecimentoHorario("dom", false, "--:--", "--:--"));
            _horarios.add(new EstabelecimentoHorario("seg", false, "--:--", "--:--"));
            _horarios.add(new EstabelecimentoHorario("ter", false, "--:--", "--:--"));
            _horarios.add(new EstabelecimentoHorario("qua", false, "--:--", "--:--"));
            _horarios.add(new EstabelecimentoHorario("qui", false, "--:--", "--:--"));
            _horarios.add(new EstabelecimentoHorario("set", false, "--:--", "--:--"));
            _horarios.add(new EstabelecimentoHorario("sab", false, "--:--", "--:--"));
            return _horarios;
        }
        return horarios;
    }

    public static BlocoPublicar convert(ItemCardapio itemCardapio) {
        BlocoPublicar publicar = new BlocoPublicar();
        publicar.setContents(itemCardapio.getContents());
        publicar.setDispayTitulo(itemCardapio.getDispayTitulo());
        publicar.setItensAdicionais(itemCardapio.isItensAdicionais());
        publicar.setMaxItensAdicionais(itemCardapio.getMaxItensAdicionais());
        publicar.setMultiselect(itemCardapio.isMultiselect());
        publicar.setObgdSelect(itemCardapio.isObgdSelect());
        publicar.setSelectMax(itemCardapio.getSelectMax());
        publicar.setValores(itemCardapio.getValores());
        publicar.setText(itemCardapio.getText());
        publicar.setTextos(itemCardapio.getTextos());
        publicar.setValorMaior(itemCardapio.isValorMaior());
        return publicar;
    }

    public static ItemCardapio convert(BlocoPublicar publicar) {
        ItemCardapio itemCardapio = new ItemCardapio();
        itemCardapio.setContents(publicar.getContents());
        itemCardapio.setDispayTitulo(publicar.getDispayTitulo());
        itemCardapio.setText(publicar.getText());
        itemCardapio.setTextos(publicar.getTextos());
        itemCardapio.setItensAdicionais(publicar.isItensAdicionais());
        itemCardapio.setMaxItensAdicionais(publicar.getMaxItensAdicionais());
        itemCardapio.setMultiselect(publicar.isMultiselect());
        itemCardapio.setObgdSelect(publicar.isObgdSelect());
        itemCardapio.setSelectMax(publicar.getSelectMax());
        itemCardapio.setValores(publicar.getValores());
        itemCardapio.setValorMaior(publicar.isValorMaior());
        return itemCardapio;
    }

    public static void startShareGPS(Pedido pedido, Context context) {
       try{
           String coordenadas = pedido.getCoordenadas().replace(" ", "");
           String link = "https://www.google.com.br/maps/dir//null@" + coordenadas + ",16z/data=!4m4!4m3!1m1!4e2!1m0".replace(" ", "");
           Intent intent = new Intent("android.intent.action.SEND");
           System.out.println(Uri.parse(link));
           intent.setType("text/plain");
           intent.putExtra("android.intent.extra.SUBJECT", "");
           intent.putExtra("android.intent.extra.TEXT", link);
           context.startActivity(Intent.createChooser(intent, ""));
       }catch (Exception x){
           MyToast.makeText(context, R.string.erro_share, ModoColor._falha).show();
       }
    }

    public static void startShareZap(Pedido pedido, Context context) {
        try{
            StringBuilder numero = new StringBuilder();
            for(char c : pedido.getTelefone().toCharArray()){
                if("0123456789".contains(String.valueOf(c))){
                    numero.append(c);
                }
            }

            String zap = PhoneNumberUtils.formatNumber(numero.toString());
            if("###########".length() == zap.length() || "##########".length() == zap.length()){
                zap = "+55" + zap;
            }
            String link = "https://api.whatsapp.com/send?phone=" + zap;
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "");
            intent.putExtra("android.intent.extra.TEXT", link);
            context.startActivity(Intent.createChooser(intent, ""));
        }catch (Exception x){
            MyToast.makeText(context, R.string.erro_share, ModoColor._falha).show();
        }
    }

    public static LiveData<Boolean> onBanirCliente(PedidosViewModel viewModel, Pedido pedido, LifecycleOwner owner, Context context) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        FireStoreUtils
                .getDatabaseOnline()
                .collection(Chave.Usuario)
                .document(pedido.getUid_client())
                .update("block", true).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        viewModel.remove(pedido, context).observe(owner, (data::setValue));
                    }else data.setValue(false);
        });

        return data;
    }

    public static List<ItemCardapio> convert(ArrayList<BlocoPublicar> cardapio) {
        List<ItemCardapio> cardapioList = new ArrayList<>();
        for(BlocoPublicar blocoPublicar : cardapio){
            cardapioList.add(convert(blocoPublicar));
        }
        return cardapioList;
    }
}
