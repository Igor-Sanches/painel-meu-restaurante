package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.SwipeItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.Anuncio;

public class AdaptadorAnuncios extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context ctx;
    public List<Anuncio> items;
    public OnDeleteItem onDeleteItem;
    public boolean loading;

    public interface OnDeleteItem{
        void onDelete(Anuncio anuncio, int position);
    }

    public void setOnDeleteItem(OnDeleteItem onDeleteItem) {
        this.onDeleteItem = onDeleteItem;
    }

    public void insert(List<Anuncio> list) {
        items = list;
        notifyDataSetChanged();
    }

    public List<Anuncio> getLista() {
        return items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public ImageButton bt_delete;
        public ImageView banner;
        public ImageView bt_move;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            banner = root.findViewById(R.id.banner);
            bt_move = root.findViewById(R.id.bt_move);
            bt_delete = root.findViewById(R.id.bt_delete);
        }

        public void onItemSelected() {
            this.itemView.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_100));
        }

        public void onItemClear() {
            this.itemView.setBackgroundColor(0);
        }
    }

    public AdaptadorAnuncios(Context context, List<Anuncio> list) {
        this.items = list;
        this.ctx = context;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new OriginalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_anuncios, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof OriginalViewHolder) {
            OriginalViewHolder originalViewHolder = (OriginalViewHolder) viewHolder;
            Anuncio anuncio = items.get(position);

            Glide.with(ctx).load(anuncio.getUrlImage()).into(originalViewHolder.banner);
            originalViewHolder.bt_delete.setOnClickListener(view -> {
                if (onDeleteItem != null) {
                    onDeleteItem.onDelete(anuncio, position);
                }
            });

            originalViewHolder.bt_move.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return this.items.size();
    }

}