package app.birdsoft.painelmeurestaurante.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.manager.IOnSwipe;
import app.birdsoft.painelmeurestaurante.manager.SwipeRunnable;
import app.birdsoft.painelmeurestaurante.manager.Tools;
import app.birdsoft.painelmeurestaurante.model.Cupom;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.Pagamento;
import app.birdsoft.painelmeurestaurante.tools.Status;
import app.birdsoft.painelmeurestaurante.view.VisualizadorDePedidoActivity;

public class AdaptadorPainelPedidos extends RecyclerView.Adapter<AdaptadorPainelPedidos.myViewHolder> {
    private Context context;
    private List<Pedido> itemList = new ArrayList<>();
    private IOnSwipe iOnSwipe;
    private myViewHolder viewHolder;
    private final Handler mHandler = new Handler();
    public static final int SWIPE_LEFT = -1;
    public static final int SWIPE_RIGHT = 1;
    public static final int TIME_POST_DELAYED = 5000; // in ms
    private final ArrayList<SwipeRunnable> mRunnables = new ArrayList<>();
    private int adapterPositionClicked = -1;
    private OnClickMenuPopupListener onClickMenuPopupListener;
    private OnTimerDialogSelected onTimerDialogSelected;
    private OnAvancarPedido onAvancarPedido;
    private OnCancelarPedido onCancelarPedido;
    private ONMotivoShowListener onMotivoShowListener;
    private boolean isSelectedInView = false;

    public void insert(List<Pedido> pedidos) {
        this.itemList = pedidos;
        notifyDataSetChanged();
    }

    public void setOnMotivoShowListener(ONMotivoShowListener onMotivoShowListener) {
        this.onMotivoShowListener = onMotivoShowListener;
    }

    public interface ONMotivoShowListener{
        void onShow(Pedido pedido);
    }

    public boolean isModoSelected() {
        return isSelectedInView;
    }

    public interface OnAvancarPedido{
        void onAvanca(Pedido pedido, int index);
    }

    public interface OnCancelarPedido{
        void onCancel(Pedido pedido);
    }

    public void setOnCancelarPedido(OnCancelarPedido onCancelarPedido) {
        this.onCancelarPedido = onCancelarPedido;
    }

    public void setOnAvancarPedido(OnAvancarPedido onAvancarPedido) {
        this.onAvancarPedido = onAvancarPedido;
    }

    public interface OnTimerDialogSelected{
        void onDialog(View view, Pedido pedido, int position);
    }

    public void setOnTimerDialogSelected(OnTimerDialogSelected onTimerDialogSelected) {
        this.onTimerDialogSelected = onTimerDialogSelected;
    }

    public void setOnClickMenuPopupListener(OnClickMenuPopupListener onClickMenuPopupListener) {
        this.onClickMenuPopupListener = onClickMenuPopupListener;
    }

    public interface OnClickMenuPopupListener{
        void onMenuOptions(View view, Pedido pedido, int position);
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pedido_principal, parent, false);

            viewHolder= new myViewHolder(itemView);
        }catch (Exception x){
            message(x.getMessage());
        }
        return viewHolder;

    }

    private void message(String message) {
        new AlertDialog.Builder(context).setMessage(message).show();
    }

    public AdaptadorPainelPedidos(Context context, List<Pedido> itemList, IOnSwipe iOnSwipe) {
       try{
           this.context = context;
           this.itemList = itemList;
           this.iOnSwipe = iOnSwipe;

       }catch (Exception x){
           message(x.getMessage());
       }
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        public ImageButton esconderMenuOptions, btnZap, btnMap, menuOptionsBtn, btnTimerSelector;
        //public Button esconderStatusOptions;
        public Button menuOptions, btn_confirm_cancel;
        public CardView itemLayout;
        public RelativeLayout layoutMenuOptions;
        public RelativeLayout layoutStatusOptions;
        public Button statusOptions, motivo_cancelamento_btn;
        public TextView
                valorfrete,
                valorPedido,
                clienteNome,
                descontoCupom,
                clienteNumero,
                distancia,
                clientePagamento,
                clienteTroco,
                valorTotal,
                prazo,
                dataRecebimento,
                endereco,
                numeroCupom,
                pedidoItens;
        public LinearLayout layoutTroco, cancelMotivoLayout, cupomArea;
        public FrameLayout btnPedidoView, btnPedidoAvancarView;
        private View colorBar;
        private ImageView canceladoLogo, imageColorCirculo, atrasadoLogo;

        public myViewHolder(View itemView) {
            super(itemView);
            try{
                descontoCupom = itemView.findViewById(R.id.descontoCupom);
                numeroCupom = itemView.findViewById(R.id.numeroCupom);
                cupomArea = itemView.findViewById(R.id.cupomArea);
                atrasadoLogo = itemView.findViewById(R.id.atrasadoLogo);
                btnTimerSelector = itemView.findViewById(R.id.btnTimerSelector);
                cancelMotivoLayout = itemView.findViewById(R.id.cancelMotivoLayout);
                canceladoLogo = itemView.findViewById(R.id.canceladoLogo);
                menuOptionsBtn = itemView.findViewById(R.id.menuOptionsBtn);
                btnMap = itemView.findViewById(R.id.btnMap);
                motivo_cancelamento_btn = itemView.findViewById(R.id.motivo_cancelamento_btn);
                btnPedidoAvancarView = itemView.findViewById(R.id.btnPedidoAvancarView);
                btnZap = itemView.findViewById(R.id.btnZap);
                valorfrete = itemView.findViewById(R.id.valorfrete);
                valorPedido = itemView.findViewById(R.id.valorPedido);
                btn_confirm_cancel = itemView.findViewById(R.id.btn_confirm_cancel);
                btnPedidoView = itemView.findViewById(R.id.btnPedidoView);
                esconderMenuOptions = itemView.findViewById(R.id.esconderMenuOptions);
                //esconderStatusOptions = itemView.findViewById(R.id.esconderStatusOptions);
                menuOptions = itemView.findViewById(R.id.menuOptions);
                imageColorCirculo = itemView.findViewById(R.id.imageColorCirculo);
                layoutMenuOptions = itemView.findViewById(R.id.layoutMenuOptions);
                distancia = itemView.findViewById(R.id.distancia);
                itemLayout = itemView.findViewById(R.id.itemlayout);
                layoutStatusOptions = itemView.findViewById(R.id.layoutStatusOptions);
                statusOptions = itemView.findViewById(R.id.statusOptions);
                colorBar = itemView.findViewById(R.id.colorBar);
                clienteNome = itemView.findViewById(R.id.clienteNome);
                clienteNumero = itemView.findViewById(R.id.clienteNumero);
                layoutTroco = itemView.findViewById(R.id.layoutTroco);
                clientePagamento = itemView.findViewById(R.id.clientePagamento);
                clienteTroco = itemView.findViewById(R.id.clienteTroco);
                valorTotal = itemView.findViewById(R.id.valorTotal);
                dataRecebimento = itemView.findViewById(R.id.dataRecebimento);
                endereco = itemView.findViewById(R.id.endereco);
                prazo = itemView.findViewById(R.id.prazo);
                pedidoItens = itemView.findViewById(R.id.pedidoItens);
            }catch (Exception x){
                message(x.getMessage());
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        try{
            Pedido mPedido = itemList.get(position);
            if(mPedido.getCupom() != null){
                viewHolder.numeroCupom.setText(mPedido.getCupom().getCodigo());
                viewHolder.descontoCupom.setText(getCupom(mPedido.getCupom()));
                viewHolder.cupomArea.setVisibility(View.VISIBLE);
            }
            holder.clienteNome.setText(mPedido.getClienteNome());
            holder.clienteNumero.setText(Mask.formatarTelefoneCelular(mPedido.getTelefone()));
            holder.clientePagamento.setText(Tools.getPagamento(mPedido.getPagamento(), context));
            if(mPedido.getPagamento().equals(Pagamento.dinheiro.toString())){
                double valorTroco = mPedido.getValorTroco();
                if(valorTroco != (double) 0){
                    holder.layoutTroco.setVisibility(View.VISIBLE);
                    holder.clienteTroco.setText(Mask.formatarValor(valorTroco));
                }
            }
            holder.distancia.setText(mPedido.getDistancia());
            holder.dataRecebimento.setText(DateTime.toDateString(mPedido.getDataPedido()));
            holder.endereco.setText(mPedido.getEndereco().replace("\n", " "));
            holder.valorPedido.setText(Mask.formatarValor(mPedido.getValorTotal()));
            holder.valorfrete.setText(Mask.formatarValor(mPedido.getFrete()));
            holder.valorTotal.setText(Mask.formatarValor(mPedido.getValorComFrete()));
            String x;
            int count = mPedido.getItensPedido().size();
            if(count == 1){
                x =  "1 pedido";
            }else{
                x = count + " pedidos";
            }
            holder.pedidoItens.setText(x);
            holder.prazo.setText(String.format("%s %s", mPedido.getPrazo(), context.getString(R.string.minutos)));

            if(mPedido.isCancelado()){
                holder.canceladoLogo.setImageResource(R.drawable.cancelado);
                holder.canceladoLogo.setVisibility(View.VISIBLE);
                holder.cancelMotivoLayout.setVisibility(View.VISIBLE);
                holder.atrasadoLogo.setVisibility(View.GONE);
                holder.atrasadoLogo.setImageResource(R.color.branco);
                holder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.colorCancelBanner));
                holder.imageColorCirculo.setBackgroundTintList(holder.btn_confirm_cancel.getTextColors());
                holder.btnPedidoAvancarView.setVisibility(View.GONE);
                holder.btnTimerSelector.setEnabled(false);
            }else{
                holder.canceladoLogo.setImageResource(R.color.branco);
                holder.canceladoLogo.setVisibility(View.GONE);
                holder.cancelMotivoLayout.setVisibility(View.GONE);
                if(mPedido.isAtrasado()){
                    holder.atrasadoLogo.setImageResource(R.drawable.atrasado);
                    holder.atrasadoLogo.setVisibility(View.VISIBLE);
                }else{
                    holder.atrasadoLogo.setVisibility(View.GONE);
                    holder.atrasadoLogo.setImageResource(R.color.branco);
                }
            }
            holder.btnPedidoAvancarView.setOnClickListener(v -> onAvancarPedido.onAvanca(mPedido, 0));
            holder.btn_confirm_cancel.setOnClickListener(v -> onCancelarPedido.onCancel(mPedido));
            holder.motivo_cancelamento_btn.setOnClickListener(v -> onMotivoShowListener.onShow(mPedido));
            switch (Status.valueOf(mPedido.getStatusPedido())){
                case novoPedido: holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.novoPedido)); break;
                case preparandoPedido: holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.preparo)); break;
                case pedidoEmTransido: holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.emTransito)); break;
                case pedidoEntregue: holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.finalizado)); break;
                case pedidoCancelado: holder.colorBar.setBackgroundColor(context.getResources().getColor(R.color.cancelado)); break;
            }
           if (mPedido.isItem) {
                holder.itemLayout.setVisibility(View.VISIBLE);
                holder.layoutMenuOptions.setVisibility(View.GONE);
                holder.layoutStatusOptions.setVisibility(View.GONE);
            }
            if (mPedido.isMenuOptions) {
                holder.itemLayout.setVisibility(View.GONE);
                holder.layoutMenuOptions.setVisibility(View.VISIBLE);
                holder.layoutStatusOptions.setVisibility(View.GONE);
            }
            if (mPedido.isStatusOptions) {
                holder.itemLayout.setVisibility(View.GONE);
                holder.layoutMenuOptions.setVisibility(View.GONE);
                holder.layoutStatusOptions.setVisibility(View.VISIBLE);
            }

            if(adapterPositionClicked != -1){
                try {
                    itemList.get(adapterPositionClicked).isMenuOptions = false;
                    itemList.get(adapterPositionClicked).isItem = true;
                    itemList.get(adapterPositionClicked).isStatusOptions = false;
                } catch (Exception ignored) {
                }
                try {
                    holder.itemLayout.setVisibility(View.VISIBLE);
                    holder.layoutStatusOptions.setVisibility(View.GONE);
                    holder.layoutMenuOptions.setVisibility(View.GONE);
                    //holder.itemLayout.startAnimation(inFromRightAnimation());
                    itemList.get(adapterPositionClicked).isMenuOptions = false;
                    itemList.get(adapterPositionClicked).isItem = true;
                    itemList.get(adapterPositionClicked).isStatusOptions = false;
                } catch (Exception ignored) {

                }
            }

            holder.btnTimerSelector.setOnClickListener(view -> {
                if(onTimerDialogSelected != null){
                    onTimerDialogSelected.onDialog(view, mPedido, position);
                }
            });

            holder.menuOptionsBtn.setOnClickListener(view -> {
                if(onClickMenuPopupListener!=null){
                    onClickMenuPopupListener.onMenuOptions(view, mPedido, position);
                }
            });

            holder.btnMap.setOnClickListener(view -> HelperManager.startGPS(mPedido, context));

            holder.btnZap.setOnClickListener(view -> HelperManager.startZap(mPedido, context));

            holder.menuOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(adapterPositionClicked != -1 && adapterPositionClicked != position){
                        try {
                            itemList.get(adapterPositionClicked).isMenuOptions = false;
                            itemList.get(adapterPositionClicked).isItem = true;
                            itemList.get(adapterPositionClicked).isStatusOptions = false;
                            isSelectedInView = true;
                        } catch (Exception ignored) {

                        }
                        notifyItemChanged(adapterPositionClicked);
                    }
                    adapterPositionClicked = position;
                    int adapterPosition = holder.getAdapterPosition();
                    try {
                        mPedido.isMenuOptions = true;
                        mPedido.isItem = false;
                        mPedido.isStatusOptions = false;
                    } catch (Exception ignored) {
                    }
                    try {
                        holder.itemLayout.setVisibility(View.GONE);
                        holder.layoutStatusOptions.setVisibility(View.GONE);
                        holder.layoutMenuOptions.setVisibility(View.VISIBLE);
                        holder.layoutMenuOptions.startAnimation(inFromRightAnimation());
                        mPedido.isMenuOptions = true;
                        mPedido.isItem = false;
                        mPedido.isStatusOptions = false;
                    } catch (Exception ignored) {

                    }
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        try {
                            final SwipeRunnable runnable = new SwipeRunnable() {
                                @Override
                                public void run() {
                                    synchronized (mRunnables) {
                                        iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_RIGHT);
                                    }
                                }
                            };
                            synchronized (mRunnables) {
                                mRunnables.set(adapterPosition, runnable);
                                mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            holder.esconderMenuOptions.setOnClickListener(v -> {
               adapterPositionClicked = position;
                int adapterPosition = holder.getAdapterPosition();
                try {
                    mPedido.isMenuOptions = false;
                    mPedido.isItem = true;
                    mPedido.isStatusOptions = false;
                    isSelectedInView = false;
                } catch (Exception ignored) {
                }

                try {
                    holder.itemLayout.setVisibility(View.VISIBLE);
                    holder.layoutStatusOptions.setVisibility(View.GONE);
                    holder.layoutMenuOptions.setVisibility(View.GONE);
                    holder.itemLayout.startAnimation(inFromRightAnimation());
                    mPedido.isMenuOptions = false;
                    mPedido.isItem = true;
                    mPedido.isStatusOptions = false;
                } catch (Exception ignored) {
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        synchronized (mRunnables) {

                            Runnable runnable = mRunnables.set(adapterPosition, null);
                            mHandler.removeCallbacks(runnable);
                        }
                    } catch (Exception ignored) {
                    }
                }
            });

            /*
            holder.statusOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(adapterPositionClicked != -1){
                        try {
                            itemList.get(adapterPositionClicked).isMenuOptions = false;
                            itemList.get(adapterPositionClicked).isItem = true;
                            itemList.get(adapterPositionClicked).isStatusOptions = false;
                        } catch (Exception e) {
                        }
                        try {
                            holder.itemLayout.setVisibility(View.VISIBLE);
                            holder.layoutStatusOptions.setVisibility(View.GONE);
                            holder.layoutMenuOptions.setVisibility(View.GONE);
                            holder.itemLayout.startAnimation(inFromRightAnimation());
                            itemList.get(adapterPositionClicked).isMenuOptions = false;
                            itemList.get(adapterPositionClicked).isItem = true;
                            itemList.get(adapterPositionClicked).isStatusOptions = false;
                        } catch (Exception e) {

                        }
                    }
                    adapterPositionClicked = position;
                    int adapterPosition = holder.getAdapterPosition();
                    try {
                        mPedido.isMenuOptions = false;
                        mPedido.isItem = false;
                        mPedido.isStatusOptions = true;
                    } catch (Exception e) {
                    }
                    try {
                        holder.itemLayout.setVisibility(View.GONE);
                        holder.layoutMenuOptions.setVisibility(View.GONE);
                        holder.layoutStatusOptions.setVisibility(View.VISIBLE);
                        holder.layoutStatusOptions.startAnimation(inFromLeftAnimation());
                        mPedido.isMenuOptions = false;
                        mPedido.isItem = false;
                        mPedido.isStatusOptions = true;
                    } catch (Exception e) {

                    }
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        try {
                            final SwipeRunnable runnable = new SwipeRunnable() {
                                @Override
                                public void run() {
                                    synchronized (mRunnables) {
                                        iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_LEFT);
                                    }
                                }
                            };
                            synchronized (mRunnables) {
                                mRunnables.set(adapterPosition, runnable);
                                mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            holder.esconderStatusOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(adapterPositionClicked != -1){
                        try {
                            itemList.get(adapterPositionClicked).isMenuOptions = false;
                            itemList.get(adapterPositionClicked).isItem = true;
                            itemList.get(adapterPositionClicked).isStatusOptions = false;
                        } catch (Exception e) {
                        }
                        try {
                            holder.itemLayout.setVisibility(View.VISIBLE);
                            holder.layoutStatusOptions.setVisibility(View.GONE);
                            holder.layoutMenuOptions.setVisibility(View.GONE);
                            holder.itemLayout.startAnimation(inFromRightAnimation());
                            itemList.get(adapterPositionClicked).isMenuOptions = false;
                            itemList.get(adapterPositionClicked).isItem = true;
                            itemList.get(adapterPositionClicked).isStatusOptions = false;
                        } catch (Exception e) {

                        }
                    }
                    adapterPositionClicked = position;
                    int adapterPosition = holder.getAdapterPosition();
                    try {
                        itemList.get(adapterPosition).isMenuOptions = false;
                        itemList.get(adapterPosition).isItem = true;
                        itemList.get(adapterPosition).isStatusOptions = false;
                     } catch (Exception e) {
                    }

                    try {
                        holder.itemLayout.setVisibility(View.VISIBLE);
                        holder.layoutMenuOptions.setVisibility(View.GONE);
                        holder.layoutStatusOptions.setVisibility(View.GONE);
                        holder.layoutStatusOptions.startAnimation(outToRightAnimation());
                        itemList.get(adapterPosition).isMenuOptions = false;
                        itemList.get(adapterPosition).isItem = true;
                        itemList.get(adapterPosition).isStatusOptions = false;
                    } catch (Exception e) {
                    }
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        try {
                            synchronized (mRunnables) {

                                Runnable runnable = mRunnables.set(adapterPosition, null);
                                mHandler.removeCallbacks(runnable);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });

             */
            holder.btnPedidoView.setOnClickListener(v -> {
                Intent intent = new Intent(context, VisualizadorDePedidoActivity.class);
                intent.putExtra("pedido", mPedido);
                context.startActivity(intent);
            });

            //Note:for swipe action,if swipe action is not required u can remove this touch listener
            holder.itemLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
                @Override
                public boolean onSwipeRight() {
                    Toast.makeText(context, " item left to right swipe", Toast.LENGTH_SHORT).show();
                    int adapterPosition = holder.getAdapterPosition();
                    try {

                        itemList.get(adapterPosition).isMenuOptions = false;
                        itemList.get(adapterPosition).isItem = false;
                        itemList.get(adapterPosition).isStatusOptions = true;
                    } catch (Exception ignored) {
                    }
                    try {
                        holder.itemLayout.setVisibility(View.GONE);
                        holder.layoutMenuOptions.setVisibility(View.GONE);
                        holder.layoutStatusOptions.setVisibility(View.VISIBLE);
                        holder.layoutStatusOptions.startAnimation(inFromLeftAnimation());
                        itemList.get(adapterPosition).isMenuOptions = false;
                        itemList.get(adapterPosition).isItem = false;
                        itemList.get(adapterPosition).isStatusOptions = true;
                    } catch (Exception ignored) {

                    }
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        try {
                            final SwipeRunnable runnable = new SwipeRunnable() {
                                @Override
                                public void run() {
                                    synchronized (mRunnables) {
                                        iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_LEFT);
                                    }
                                }
                            };
                            synchronized (mRunnables) {
                                mRunnables.set(adapterPosition, runnable);
                                mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }

                @Override
                public boolean onSwipeLeft() {
                    int adapterPosition = holder.getAdapterPosition();
                    Toast.makeText(context, " item right to left swipe", Toast.LENGTH_SHORT).show();
                    try {

                        itemList.get(adapterPosition).isMenuOptions = true;
                        itemList.get(adapterPosition).isItem = false;
                        itemList.get(adapterPosition).isStatusOptions = false;
                    } catch (Exception ignored) {
                    }
                    try {
                        holder.itemLayout.setVisibility(View.GONE);
                        holder.layoutStatusOptions.setVisibility(View.GONE);
                        holder.layoutMenuOptions.setVisibility(View.VISIBLE);
                        holder.layoutMenuOptions.startAnimation(inFromRightAnimation());
                        itemList.get(adapterPosition).isMenuOptions = true;
                        itemList.get(adapterPosition).isItem = false;
                        itemList.get(adapterPosition).isStatusOptions = false;
                    } catch (Exception ignored) {

                    }
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        try {
                            final SwipeRunnable runnable = new SwipeRunnable() {
                                @Override
                                public void run() {
                                    synchronized (mRunnables) {
                                        iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_RIGHT);
                                    }
                                }
                            };
                            synchronized (mRunnables) {
                                mRunnables.set(adapterPosition, runnable);
                                mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }

                @Override
                public boolean onSwipeTop() {
                    return true;
                }

                @Override
                public boolean onSwipeBottom() {
                    return true;
                }
            });
        /*/Note:for swipe action,if swipe action is not required u can remove this touch listener
        holder.itemLayout.setOnTouchListener(new RelativeLayoutTouchListener(context,holder.getAdapterPosition()) {
            int adapterPosition=holder.getAdapterPosition();
            @Override
            public void onRightToLeftSwipe() {
                super.onRightToLeftSwipe();
                Toast.makeText(context, " item right to left swipe", Toast.LENGTH_SHORT).show();
                try {
                    itemList.get(adapterPosition).setUndo(true);
                    itemList.get(adapterPosition).setItem(false);
                    itemList.get(adapterPosition).setUndoIgnore(false);
                } catch (Exception e) {
                }
                try {
                    holder.itemLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.setVisibility(View.GONE);
                    holder.undoLayout.setVisibility(View.VISIBLE);
                    holder.undoLayout.startAnimation(inFromRightAnimation());
                    itemList.get(adapterPosition).setUndo(true);
                    itemList.get(adapterPosition).setItem(false);
                    itemList.get(adapterPosition).setUndoIgnore(false);
                } catch (Exception e) {
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        final SwipeRunnable runnable = new SwipeRunnable() {
                            @Override
                            public void run() {
                                synchronized (mRunnables) {
                                    iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_RIGHT);
                                }
                            }
                        };
                        synchronized (mRunnables) {
                            mRunnables.set(adapterPosition, runnable);
                            mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onLeftToRightSwipe() {
                super.onLeftToRightSwipe();
                Toast.makeText(context, " item left to right swipe", Toast.LENGTH_SHORT).show();
                int adapterPosition = holder.getAdapterPosition();
                try {
                    itemList.get(adapterPosition).setUndo(false);
                    itemList.get(adapterPosition).setItem(false);
                    itemList.get(adapterPosition).setUndoIgnore(true);
                } catch (Exception e) {
                }
                try {
                    holder.itemLayout.setVisibility(View.GONE);
                    holder.undoLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.setVisibility(View.VISIBLE);
                    holder.undoignoreLayout.startAnimation(inFromLeftAnimation());
                    itemList.get(adapterPosition).setUndo(false);
                    itemList.get(adapterPosition).setItem(false);
                    itemList.get(adapterPosition).setUndoIgnore(true);
                } catch (Exception e) {
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        final SwipeRunnable runnable = new SwipeRunnable() {
                            @Override
                            public void run() {
                                synchronized (mRunnables) {
                                    iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_LEFT);
                                }
                            }
                        };
                        synchronized (mRunnables) {
                            mRunnables.set(adapterPosition, runnable);
                            mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void itemClicked() {
                super.itemClicked();
                Toast.makeText(context, " item Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        */
        }catch (Exception x){
            message(x.getMessage());
        }
    }

    private String getCupom(Cupom cupom) {
        if(cupom.getDescontoType() != 0){
            if(cupom.getDescontoType() == 2){
                return Mask.formatarValor(cupom.getValorDesconto()) + " Off";
            }else return ((int)cupom.getValorDesconto()) + "% Off";
        }else return context.getString(R.string.cupom_frete_grates);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private Animation inFromRightAnimation() {
try{
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
}catch (Exception x){
    message(x.getMessage());
    return null;
}
    }

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (mRunnables.size() == 0) {
            int count = getItemCount();
            for (int i = 0; i < count; i++) {
                mRunnables.add(null);
            }
        }
        registerAdapterDataObserver(new SwipeAdapterDataObserver());
    }

    private class SwipeAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        public void onChanged() {
            synchronized (mRunnables) {
                int size = mRunnables.size();
                int itemCount = getItemCount();
                if (itemCount > size) {
                    onItemRangeChanged(0, size);
                    onItemRangeInserted(size, itemCount - size);
                } else {
                    onItemRangeChanged(0, itemCount);
                    onItemRangeRemoved(itemCount, size - itemCount);
                }
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    Runnable r = mRunnables.set(positionStart + i, null);
                    if (r != null) {
                        mHandler.removeCallbacks(r);
                    }
                }
            }
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    mRunnables.add(positionStart, null);
                }
            }
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    int c = fromPosition > toPosition ? i : 0;
                    mRunnables.set(toPosition + c, mRunnables.remove(fromPosition + c));
                }
            }
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    Runnable r = mRunnables.remove(positionStart);
                    if (r != null) {
                        mHandler.removeCallbacks(r);
                    }
                }
            }
        }
    }

    public void remove(int position) {
        try {
            Toast.makeText(context, itemList.get(position).getClienteNome() + " removido", Toast.LENGTH_SHORT).show();
            itemList.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*//class required for swipe action,Touch
    public abstract class RelativeLayoutTouchListener implements View.OnTouchListener {
        static final String logTag = "ActivitySwipeDetector";
        private Context activity;
        private int position;
        static final int MIN_DISTANCE = 2;
        private float downX, downY, upX, upY;
        // private MainActivity mMainActivity;
        public RelativeLayoutTouchListener(Context mainActivity,int position) {
            activity = mainActivity;
            this.position=position;
        }
        public void onRightToLeftSwipe() {
            Log.i(logTag, "RightToLeftSwipe!");
        }
        public void onLeftToRightSwipe() {
            Log.i(logTag, "LeftToRightSwipe!");
        }
        public void onTopToBottomSwipe() {
            Log.i(logTag, "onTopToBottomSwipe!");
        }
        public void onBottomToTopSwipe() {
            Log.i(logTag, "onBottomToTopSwipe!");
        }
        public void itemClicked() {
            Log.i(logTag, "Clicked!");
        }
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();
                    float deltaX = downX - upX;
                    float deltaY = downY - upY;
                    // swipe horizontal?
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {
                           this.itemClicked();
                        return true;
                    }
                    // swipe vertical?
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            return true;
                        }
                    } else {
                        Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);
                        // return false; // We don't consume the event
                    }
                    return false; // no swipe horizontally and no swipe vertically
                }// case MotionEvent.ACTION_UP:
            }
            return false;
        }
    }*/

    public abstract static class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        public boolean onTouch(final View v, final MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 5;
            private static final int SWIPE_VELOCITY_THRESHOLD = 5;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                result = onSwipeRight();
                            } else {
                                result = onSwipeLeft();
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                result = onSwipeBottom();
                            } else {
                                result = onSwipeTop();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        public boolean onSwipeRight() {
            return false;
        }

        public boolean onSwipeLeft() {
            return false;
        }

        public boolean onSwipeTop() {
            return false;
        }

        public boolean onSwipeBottom() {
            return false;
        }

    }
}