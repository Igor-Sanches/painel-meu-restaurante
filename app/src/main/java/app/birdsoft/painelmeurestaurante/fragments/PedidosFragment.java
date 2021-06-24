package app.birdsoft.painelmeurestaurante.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorPainelPedidos;
import app.birdsoft.painelmeurestaurante.dialogo.DialogAdicionarRemoverPrazo;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.FragmentType;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.manager.IOnSwipe;
import app.birdsoft.painelmeurestaurante.manager.Loader;
import app.birdsoft.painelmeurestaurante.manager.Tools;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.service.NotificationSendData;
import app.birdsoft.painelmeurestaurante.service.NotificationType;
import app.birdsoft.painelmeurestaurante.service.SendNotification;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.tools.Status;
import app.birdsoft.painelmeurestaurante.tools.ViewAnimation;
import app.birdsoft.painelmeurestaurante.viewModel.PedidosViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class PedidosFragment extends Fragment implements IOnSwipe, Loader {

    private PedidosViewModel viewModel;
    private AdaptadorPainelPedidos adaptador;
    private BottomSheetDialog sheetDialog;
    private BottomSheetBehavior sheetBehavior;
    private LoadingDialog loading;
    private CoordinatorLayout listaLayout;
    private LinearLayout lyt_progress, layout_wifi_error, vazio;
    private FragmentType fragmentType;
    private TextView falha_vazio;
    private ImageView image_vazio;
    private List<Pedido> pedidos;
    private boolean notificate = false, isShow = false;

    private boolean isConnection() {
        if(!Conexao.isConnected(requireActivity())){
            MySnackbar.makeText(requireActivity(), R.string.sem_conexao, ModoColor._falha).show();
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pedidos, container, false);
        pedidos = new ArrayList<>();
        assert getArguments() != null;
        fragmentType = FragmentType.values()[getArguments().getInt("fragmentType")];
        viewModel = new ViewModelProvider(this).get(PedidosViewModel.class);
        adaptador = new AdaptadorPainelPedidos(getActivity(), new ArrayList<>(), this);
        loading = new LoadingDialog(getActivity());
        View sheetBottom = root.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheetBottom);
        sheetDialog = new BottomSheetDialog(requireActivity());
        falha_vazio = root.findViewById(R.id.falha_vazio);
        lyt_progress = root.findViewById(R.id.lyt_progress);
        listaLayout = root.findViewById(R.id.listaLayout);
        image_vazio = root.findViewById(R.id.image_vazio);
        vazio = root.findViewById(R.id.vazio);
        layout_wifi_error = root.findViewById(R.id.layout_wifi_error);
        RecyclerView mRecyclerView = root.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adaptador);
        load();
        root.findViewById(R.id.connection_falha)
                .setOnClickListener((v -> {
                    MyToast.makeText(getActivity(), R.string.atualizando).show();
                    viewModel.update(getActivity(), fragmentType);
                }));
        return root;
    }

    @Override
    public void onSwipe(int position, int direction) {

    }

    @Override
    public void load() {
        viewModel.init(getActivity(), fragmentType);
        viewModel.getMutableLiveData().observe(requireActivity(), (pedidosElements -> {
            notificate = true;
            pedidos = pedidosElements.getPedidos();
            if(isShow || adaptador.isModoSelected())
                return;

            adaptador.insert(pedidosElements.getPedidos());
            layout_wifi_error.setVisibility(pedidosElements.getLayoutWifiOffline());
            lyt_progress.setVisibility(pedidosElements.getProgressVisibility());
            listaLayout.setVisibility(pedidosElements.getListaVisibility());
            vazio.setVisibility(pedidosElements.getVazioVisibility());
            onVazioType(fragmentType);
            notificate = false;
        }));
        adaptador.setOnClickMenuPopupListener((view, pedido, position) -> {
            view.setEnabled(false);
            if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            @SuppressLint("InflateParams") View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_painel_pedidos_opcoes, null);
            sheetDialog.setCancelable(false);
            sheetDialog.setContentView(root);
            if(pedido.getStatusPedido().equals(Status.pedidoEmTransido.toString())){
                ((TextView)root.findViewById(R.id.pedidoAvncarText)).setText(R.string.finalizar_pedido);
            }
            if(pedido.isCancelado()){
                root.findViewById(R.id.btnCancelarOption).setVisibility(View.GONE);
                root.findViewById(R.id.optionAvancar).setVisibility(View.GONE);
                root.findViewById(R.id.optionAvancarCancel).setVisibility(View.VISIBLE);
                root.findViewById(R.id.btnAddRemoveTimer).setVisibility(View.GONE);
                root.findViewById(R.id.btnMudarMenu).setVisibility(View.GONE);
            }
            LinearLayout btnMoreOptionsAcion = root.findViewById(R.id.option);
            LinearLayout lyt_expand = root.findViewById(R.id.lyt_expand);
            final boolean[] expanded = {false};
            btnMoreOptionsAcion.setOnClickListener(view1 -> {
                expanded[0] = buttonLayoutExpand(!expanded[0], root.findViewById(R.id.imageOptions), lyt_expand);
                if (expanded[0]) {
                    lyt_expand.setVisibility(View.VISIBLE);
                } else {
                    lyt_expand.setVisibility(View.GONE);
                }
            });

            root.findViewById(R.id.optionAvancarCancel).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                onCancelar(pedido, getString(R.string.c_cliente) + " " + pedido.getMsgCancelamento());
            });

            root.findViewById(R.id.btnBanir).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                onDialogBanir(pedido);
            });

            root.findViewById(R.id.optionAvancar).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                onDialogAvancar(pedido);
            });

            root.findViewById(R.id.btnCancelarOption).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                onDialogCancelamento(pedido);
            });

            root.findViewById(R.id.btnAddRemoveTimer).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                onDialogPrazo(view13, pedido);
            });

            root.findViewById(R.id.onGPSRota).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                HelperManager.startGPSRota(pedido, requireActivity());
            });

            root.findViewById(R.id.onShareZap).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                HelperManager.startShareZap(pedido, getActivity());
            });

            root.findViewById(R.id.onShareGPS).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                HelperManager.startShareGPS(pedido, getActivity());
            });

            root.findViewById(R.id.btnZapLinear).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                HelperManager.startZap(pedido, getActivity());
            });

            root.findViewById(R.id.btnLigarCliente).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                HelperManager.startCall(pedido, getActivity());
            });

            root.findViewById(R.id.btnMudarMenu).setOnClickListener(view13 -> {
                sheetDialog.dismiss();
                showAlterar(pedido);
            });

            root.findViewById(R.id.cancel).setOnClickListener(view12 ->{
                sheetDialog.dismiss();
                if(notificate){
                    adaptador.insert(pedidos);
                    notificate = false;
                }
                isShow = false;
            });
            sheetDialog.setOnDismissListener(dialog -> view.setEnabled(true));

            if(Build.VERSION.SDK_INT >= 21){
                sheetDialog.getWindow().addFlags(67108864);
            }
            ((View)root.getParent()).setBackgroundColor(getResources().getColor(R.color.color_dialog));
            isShow = true;
            sheetDialog.show();
        });
        adaptador.setOnTimerDialogSelected((view, pedido, position) -> onDialogPrazo(view, pedido));
        adaptador.setOnCancelarPedido((pedido -> onCancelar(pedido, getString(R.string.c_cliente) + " " + pedido.getMsgCancelamento())));
        adaptador.setOnAvancarPedido(((pedido, index) -> onDialogAvancar(pedido)));
        adaptador.setOnMotivoShowListener((pedido -> {
            if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.fragment_mensagem_dialog_pedido, null);
            sheetDialog = new BottomSheetDialog(requireActivity());
            sheetDialog.setContentView(_view);
            TextView mensagem = _view.findViewById(R.id.mensagem);
            ((TextView)_view.findViewById(R.id.name_toolbar)).setText(getString(R.string.mensagem));
            mensagem.setText(pedido.getMsgCancelamento());
            _view.findViewById(R.id.bt_close).setOnClickListener(view -> sheetDialog.dismiss());

            if(Build.VERSION.SDK_INT >= 21){
                sheetDialog.getWindow().addFlags(67108864);
            }

            sheetDialog.show();
        }));
    }

    private void onDialogBanir(Pedido pedido) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_banir_cliente, null);
        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).create();
        EditText messageAterar = root.findViewById(R.id.messageBanir);
        root.findViewById(R.id.confirmar).setOnClickListener((v->{
            if(!messageAterar.equals("")){
                if(isConnection()){
                    loading.show();
                    dialog.dismiss();
                HelperManager.onBanirCliente(viewModel, pedido, requireActivity(), getActivity()).observe(requireActivity(), (sucesso->{
                    if(sucesso){
                        MySnackbar.makeText(getActivity(), R.string.cliente_banido, ModoColor._success).show();
                        new SendNotification().onPush(
                                new NotificationSendData(
                                        getString(R.string.title_banido),
                                        getString(R.string.msg_banido),
                                        pedido.getUid(),
                                        pedido.getUid_client(),
                                        Status.pedidoCancelado.toString(),
                                        NotificationType.pedido
                                ), getActivity()
                        );
                        adaptador.insert(pedidos);
                    }else{
                        MySnackbar.makeText(getActivity(), R.string.cliente_banido_erro, ModoColor._success).show();
                    }
                    dialog.dismiss();
                    loading.dismiss();
                }));
                }
            }else{
                MyToast.makeText(getActivity(), R.string.digite_a_msg, ModoColor._falha).show();
            }
        }));
        root.findViewById(R.id.cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(root);
        isShow = true;
        dialog.setOnDismissListener(dialog1 -> {
            if(notificate){
                adaptador.insert(pedidos);
                notificate = false;
            }
            isShow = false;
        });
        dialog.show();
    }

    private void onDialogAvancar(Pedido pedido) {
       if(isConnection()){
            loading.show();
        Status status = getNovoStatus(Status.valueOf(pedido.getStatusPedido()));
        pedido.isItem = null;
        pedido.isMenuOptions = null;
        pedido.isStatusOptions = null;
        pedido.setCancelado(status != Status.pedidoEntregue && pedido.isCancelado());
        pedido.setStatusPedido(status.toString());
        isShow = false;
        viewModel.update(pedido, getActivity()).observe(requireActivity(), (success->{
            if(success){
                MySnackbar.makeText(getActivity(), getMensagemStatus(status), ModoColor._success).show();
                new SendNotification().onPush(
                        new NotificationSendData(
                                getTitle(status),
                                getmsg(status),
                                pedido.getUid(),
                                pedido.getUid_client(),
                                status.toString(),
                                NotificationType.pedido
                        ), getActivity()
                );
                adaptador.insert(pedidos);
            }else{
                MySnackbar.makeText(getActivity(), R.string.error_avanca, ModoColor._falha).show();
            }
            loading.dismiss();
        }));
       }

    }

    private String getmsg(Status status) {
        @StringRes int res = R.string.aguarde;
        switch (status){
            case pedidoEntregue:
                res = R.string.msg_pedido_entregue;
                break;
            case pedidoEmTransido:
                res = R.string.msg_pedido_transito;
                break;
            case preparandoPedido:
                res = R.string.msg_pedido_preparando;
                break;
        }
        return getString(res);
    }

    private String getTitle(Status status) {
        @StringRes int res = R.string.aguarde;
        switch (status){
            case pedidoEntregue:
                res = R.string.title_pedido_entregue;
                break;
            case pedidoEmTransido:
                res = R.string.title_pedido_transito;
                break;
            case preparandoPedido:
                res = R.string.title_pedido_preparando;
                break;
        }
        return getString(res);
    }

    private @StringRes int getMensagemStatus(Status status) {
        @StringRes int res = R.string.aguarde;
        switch (status){
            case novoPedido:
                res = R.string.novo_pedido_chegou;
            break;
            case pedidoEntregue:
                res = R.string.pedido_entregue_msg;
                break;
            case pedidoEmTransido:
                res = R.string.pedido_em_transito;
                break;
            case preparandoPedido:
                res = R.string.pedido_em_preparo;
                break;
        }
        return res;
    }

    private Status getNovoStatus(Status status) {
        if(status == Status.novoPedido)
            status = Status.preparandoPedido;
        else if(status == Status.preparandoPedido)
            status = Status.pedidoEmTransido;
        else if(status == Status.pedidoEmTransido)
            status = Status.pedidoEntregue;

        return status;
    }

    private void onDialogCancelamento(Pedido pedido) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_cancelar_pedido, null);
        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).create();
        dialog.setCancelable(false);
        EditText messageCancel = layout.findViewById(R.id.messageCancel);
        layout.findViewById(R.id.confirmar)
                .setOnClickListener(view -> {
                    if(Conexao.isConnected(getActivity())){
                        String message = messageCancel.getText().toString().trim();
                        if(!message.equals("")){
                            dialog.dismiss();
                            onCancelar(pedido, message);
                        }else{
                            MySnackbar.makeText(getActivity(), R.string.cancelamento_enviado_erro, ModoColor._falha).show();
                        }
                    }else {
                        MySnackbar.makeText(getActivity(), R.string.sem_conexao, ModoColor._falha).show();
                    }
                });

        layout.findViewById(R.id.btn_finish)
                .setOnClickListener(view -> dialog.dismiss());
        layout.findViewById(R.id.cancel)
                .setOnClickListener(view -> dialog.dismiss());
        dialog.setView(layout);
        dialog.setOnDismissListener(dialog1 -> {

            if(notificate){
                adaptador.insert(pedidos);
                notificate = false;
            }
            isShow = false;
        });
        isShow = true;
        dialog.show();
    }

    private void onCancelar(Pedido pedido, String message) {
        if(isConnection()){
            loading.show();
            pedido.isItem = null;
            pedido.isMenuOptions = null;
            pedido.isStatusOptions = null;
            pedido.setStatusPedido(Status.pedidoCancelado.toString());
            pedido.setMsgCancelamento(message);
            pedido.setCancelado(true);
            pedido.setDataRecebimento(System.currentTimeMillis());
            viewModel.update(pedido, getActivity()).observe(requireActivity(), (success->{
                if(success){
                    MySnackbar.makeText(getActivity(), R.string.cancelamento_confirmado, ModoColor._success).show();
                    new SendNotification().onPush(
                            new NotificationSendData(
                                    getString(R.string.title_pedido_cancelado),
                                    getString(R.string.msg_pedido_cancelado),
                                    pedido.getUid(),
                                    pedido.getUid_client(),
                                    Status.pedidoCancelado.toString(),
                                    NotificationType.pedido
                            ), getActivity()
                    );
                }else{
                    MySnackbar.makeText(getActivity(), R.string.cancelamento_confirmado_erro, ModoColor._falha).show();
                }
                loading.dismiss();
            }));
        }
    }

    private void onDialogPrazo(View view, Pedido pedido) {
        if(view != null)
            view.setEnabled(false);
        DialogAdicionarRemoverPrazo prazo = new DialogAdicionarRemoverPrazo(requireActivity(), pedido.getPrazo());
        prazo.show();
        prazo.setOnSetPrazoListener(((min, atualMin) -> {
            if(isConnection()){
                loading.show();
                pedido.isItem = null;
                pedido.isMenuOptions = null;
                pedido.isStatusOptions = null;
                pedido.setPrazo(min);
                viewModel.update(pedido, requireActivity()).observe(requireActivity(), (success ->{
                    if(success){
                        MySnackbar.makeText(getActivity(), R.string.prazo_atualizado, ModoColor._success).show();
                        new SendNotification().onPush(
                                new NotificationSendData(
                                        getString(R.string.title_pedido_prazo),
                                        getPrazo(atualMin, min),
                                        pedido.getUid(),
                                        pedido.getUid_client(),
                                        Status.novoPedido.toString(),
                                        NotificationType.prazo
                                ), getActivity()
                        );
                    }else{
                        MySnackbar.makeText(getActivity(), R.string.prazo_atualizado_erro, ModoColor._falha).show();
                    }
                    prazo.dismiss();
                    loading.dismiss();
                }));
            }
        }));
        prazo.setOnDismissListener(dialog1 -> {
            if(view!=null)
                view.setEnabled(true);

            if(notificate){
                adaptador.insert(pedidos);
                notificate = false;
            }
            isShow = false;
        });
        isShow = true;
    }

    private String getPrazo(int atualPrazo, int min) {
        return min > atualPrazo ? getString(R.string.prazo_mais) : getString(R.string.prazo_menos);
    }

    private void showAlterar(Pedido pedido) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_alterar_cardapio, null);
        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).create();

        EditText messageAterar = root.findViewById(R.id.messageAterar);

        if(pedido.getMsgAlteracao()!=null){
            if(!pedido.getMsgAlteracao().equals("")){
                messageAterar.setText(pedido.getMsgAlteracao());
                messageAterar.setSelection(messageAterar.getText().length());
            }
        }

        root.findViewById(R.id.confirmar).setOnClickListener(view -> {
            if(Conexao.isConnected(getActivity())){
                String message = messageAterar.getText().toString().trim();
                if(!message.equals("")){
                  if(isConnection()){
                      loading.show();
                      pedido.isItem = null;
                      pedido.isMenuOptions = null;
                      pedido.isStatusOptions = null;
                      pedido.setAlteracao(true);
                      pedido.setMsgAlteracao(message);
                      dialog.dismiss();
                      viewModel.update(pedido, getActivity()).observe(requireActivity(), (success->{
                          if(success){
                              ProgressBar progressBar = root.findViewById(R.id.progress_circular);
                              LinearLayout layoutInicio = root.findViewById(R.id.layoutInicio);
                              LinearLayout layoutFinish = root.findViewById(R.id.layoutFinish);
                              layoutInicio.setVisibility(View.GONE);
                              progressBar.setVisibility(View.GONE);
                              layoutFinish.setVisibility(View.VISIBLE);
                              MySnackbar.makeText(getActivity(), R.string.alteracao_atualizado, ModoColor._success).show();
                              new SendNotification().onPush(
                                      new NotificationSendData(
                                              getString(R.string.title_pedido_alterado),
                                              getString(R.string.msg_pedido_alterado),
                                              pedido.getUid(),
                                              pedido.getUid_client(),
                                              Status.alteracao.toString(),
                                              NotificationType.alterar_pedido
                                      ), getActivity()
                              );
                          }else{
                              MySnackbar.makeText(getActivity(), R.string.alteracao_atualizado_erro, ModoColor._falha).show();
                          }
                          loading.dismiss();
                      }));
                  }
                }else{
                    MyToast.makeText(getActivity(), R.string.digite_msg_alteracao, ModoColor._falha).show();
                }
            }else {
                MyToast.makeText(getActivity(), R.string.sem_conexao, ModoColor._falha).show();
            }
        });
        root.findViewById(R.id.cancel).setOnClickListener(view -> dialog.dismiss());
        root.findViewById(R.id.btn_finish).setOnClickListener(view -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(root);
        isShow = true;
        dialog.setOnDismissListener(dialog1 -> {
            if(notificate){
                adaptador.insert(pedidos);
                notificate = false;
            }
            isShow = false;
        });
        dialog.show();
    }

    private boolean buttonLayoutExpand(boolean z, ImageView view, View view2) {
        Tools.imageArrow(z, view);
        if (z) {
            ViewAnimation.expand(view2);
        } else {
            ViewAnimation.collapse(view2);
        }
        return z;
    }

    private void onVazioType(FragmentType fragmentType) {
        switch (fragmentType){
            case novoPedido:
                falha_vazio.setText(getString(R.string.falha_novo_pedido));
                image_vazio.setBackgroundResource(R.drawable.nv);
                break;
            case pedidoEmTransido:
                falha_vazio.setText(getString(R.string.falha_a_caminho_pedido));
                image_vazio.setBackgroundResource(R.drawable.a_caminho);
                break;
            case preparandoPedido:
                falha_vazio.setText(getString(R.string.falha_preparando_pedido));
                image_vazio.setBackgroundResource(R.drawable.cozinhando);
                break;
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void finishMode() {

    }
}