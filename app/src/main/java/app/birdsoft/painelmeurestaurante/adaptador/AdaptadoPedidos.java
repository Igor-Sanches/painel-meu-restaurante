package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.model.Carrinho;
import app.birdsoft.painelmeurestaurante.tools.Mask;
import app.birdsoft.painelmeurestaurante.view.ItensPedidosViewActivity;

public class AdaptadoPedidos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Carrinho> mCarrinho;
    private final Context context;
    public boolean on_atach = true;

    public void remove(int position) {
        mCarrinho.remove(position);
        notifyDataSetChanged();
    }

    public void setInsert(List<Carrinho> mCarrinho) {
        this.mCarrinho = mCarrinho;
        notifyDataSetChanged();
    }

    public AdaptadoPedidos(Context context, List<Carrinho> mCarrinho) {
        this.context = context;
        this.mCarrinho = mCarrinho;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name_pedido, itens_pedido, numero_pedido, valor_pedido;
        public Button btn_ver;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            name_pedido = root.findViewById(R.id.name_pedido);
            itens_pedido = root.findViewById(R.id.itens_pedido);
            valor_pedido = root.findViewById(R.id.valor_pedido);
            btn_ver = root.findViewById(R.id.btn_ver);
            numero_pedido = root.findViewById(R.id.numero_pedido);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OriginalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof OriginalViewHolder){
            try{
                OriginalViewHolder viewHolder = (OriginalViewHolder)holder;
                Carrinho carrinho = mCarrinho.get(position);
                viewHolder.numero_pedido.setText(String.valueOf(position + 1));
                viewHolder.name_pedido.setText(carrinho.getDisplayName());
                viewHolder.itens_pedido.setText(String.valueOf(carrinho.getQuantidade()));
                viewHolder.valor_pedido.setText(Mask.formatarValor(carrinho.getValorTotal()));
                if(carrinho.getListas() != null){
                    viewHolder.btn_ver.setVisibility(View.VISIBLE);
                    viewHolder.btn_ver.setOnClickListener((v -> {
                        Intent intent = new Intent(context, ItensPedidosViewActivity.class);
                        intent.putExtra("itens", carrinho);
                        context.startActivity(intent);
                    }));
                }

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

    @Override
    public int getItemCount() {
        return mCarrinho.size();
    }

}
