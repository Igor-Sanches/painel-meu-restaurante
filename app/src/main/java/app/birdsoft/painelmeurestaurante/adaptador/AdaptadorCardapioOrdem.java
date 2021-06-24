package app.birdsoft.painelmeurestaurante.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.manager.SwipeItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.tools.ItemAnimation;
import app.birdsoft.painelmeurestaurante.widget.ImageUtils;

public class AdaptadorCardapioOrdem extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DragItemTouchHelper.MoveHelperAdapter {

    public boolean on_attach = true;
    private int lastPosition = -1;
    private final List<Cardapio> cardapios;
    private final Context context;
    public boolean loading;

    public List<Cardapio> getLista() {
        return cardapios;
    }

    public AdaptadorCardapioOrdem(List<Cardapio> str, Context context) {
        this.cardapios =str;
        this.context =context;
    }

    public void clear() {
        if(cardapios.size() > 0){
            cardapios.clear();
            notifyDataSetChanged();
        }
    }


    public void insert(List<Cardapio> list) {
        if(list != null){
            setLoaded();
            clear();
            this.cardapios.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setLoaded() {
        this.loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (this.cardapios.get(i) == null) {
                this.cardapios.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public TextView displayName, summary;
        private final FrameLayout item_indisponivel;
        private final ImageView iconeProduto;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            iconeProduto = root.findViewById(R.id.iconeProduto);
            displayName = root.findViewById(R.id.displayName);
            summary = root.findViewById(R.id.summary);
            item_indisponivel = root.findViewById(R.id.item_indisponivel);
        }

        public void onItemSelected() {
            this.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_grey_100));
        }

        public void onItemClear() {
            this.itemView.setBackgroundColor(0);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OriginalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cardapio_ordem, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof OriginalViewHolder){
            try{
                OriginalViewHolder viewHolder = (OriginalViewHolder)holder;
                Cardapio string = cardapios.get(position);
                viewHolder.displayName.setText(string.getName());
                ImageUtils.displayImageFromUrl(context, string.getImageUrl(), viewHolder.iconeProduto, context.getResources().getDrawable(R.drawable.image_add_ft));
                if(string.getReceita() != null) {
                        viewHolder.summary.setText(string.getReceita());
                }else{
                    viewHolder.summary.setText("");
                }
                viewHolder.item_indisponivel.setVisibility(string.isDisponivel() ? View.GONE : View.VISIBLE);
                setAnimation(viewHolder.itemView, position);
            }catch (Exception x){
                new AlertDialog.Builder(context).setMessage(x.getMessage()).show();
            }
        }
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, i);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int i) {
        if (i > this.lastPosition) {
            int animation_type = 0;
            ItemAnimation.animate(view, this.on_attach ? i : -1, animation_type);
            this.lastPosition = i;
        }
    }

    public void onItemMove(int i, int i2) {
        Collections.swap(this.cardapios, i, i2);
        notifyItemMoved(i, i2);
    }

    @Override
    public int getItemCount() {
        return cardapios.size();
    }
}



