package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.model.Pedido;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.ItemAnimation;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.tools.Status;

public class AdaptadorHistoricos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Pedido> mPedidos;
    private final Context context;
    private int lastPosition = -1;
    public boolean on_attach = true;
    public boolean on_atach = true;
    private onClickItem onClickItem;

    public void setOnClickItem(onClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public void remove(int position) {
        mPedidos.remove(position);
        notifyDataSetChanged();
    }

    public void setInsert(List<Pedido> pedidos) {
        mPedidos = pedidos;
        notifyDataSetChanged();
    }

    public interface onClickItem{
        void onClick(View view, Pedido pedido, int position);
    }

    public AdaptadorHistoricos(Context context, List<Pedido> mPedidos) {
        this.context = context;
        this.mPedidos = mPedidos;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView displaySistuacao, displayValorTotal, displayName, data;
        private final ImageView icon_checked;
        public CardView layout;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            icon_checked = root.findViewById(R.id.icon_checked);
            displaySistuacao = root.findViewById(R.id.displaySistuacao);
            displayValorTotal = root.findViewById(R.id.displayValorTotal);
            displayName = root.findViewById(R.id.displayName);
            data = root.findViewById(R.id.data);
            layout = root.findViewById(R.id.layout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OriginalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_historico, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof OriginalViewHolder){
            try{
                OriginalViewHolder viewHolder = (OriginalViewHolder)holder;
                Pedido pedido = mPedidos.get(position);
                viewHolder.data.setText(DateTime.toDateString("dd/MM/yyyy HH:mm", pedido.getDataRecebimento()));

                viewHolder.displayName.setText(pedido.getClienteNome());
                viewHolder.displayValorTotal.setText(Mask.formatarValor(pedido.getValorComFrete()));
                viewHolder.icon_checked.setColorFilter(pedido.getStatusPedido().equals(Status.pedidoEntregue.toString()) ?
                        context.getResources().getColor(R.color.verdeProgress)
                        : context.getResources().getColor(R.color.vermelhoProgress));
                viewHolder.displaySistuacao.setText(pedido.getStatusPedido().equals(Status.pedidoEntregue.toString()) ? context.getString(R.string.pedido_entregue) : context.getString(R.string.pedido_cancelado));
                viewHolder.layout.setOnClickListener(view -> {
                    if(onClickItem != null){
                        onClickItem.onClick(view, pedido, position);
                    }
                });
                setAnimation(viewHolder.itemView, position);
            }catch (Exception x){
                Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_atach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int i) {
        if (i > this.lastPosition) {
            int animation_type = -1;
            ItemAnimation.animate(view, this.on_attach ? i : -1, animation_type);
            this.lastPosition = i;
        }
    }

    @Override
    public int getItemCount() {
        return mPedidos.size();
    }

}




