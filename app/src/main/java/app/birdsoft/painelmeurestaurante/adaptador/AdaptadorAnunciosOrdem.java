package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.manager.SwipeItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.Anuncio;

public class AdaptadorAnunciosOrdem extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DragItemTouchHelper.MoveHelperAdapter {
    public Context ctx;
    public List<Anuncio> items;
    public boolean loading;

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
            bt_delete = root.findViewById(R.id.bt_delete);
            bt_move = root.findViewById(R.id.bt_move);
        }

        public void onItemSelected() {
            this.itemView.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_100));
        }

        public void onItemClear() {
            this.itemView.setBackgroundColor(0);
        }
    }

    public AdaptadorAnunciosOrdem(Context context, List<Anuncio> list) {
        this.items = list;
        this.ctx = context;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new OriginalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_anuncios, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof OriginalViewHolder) {
           try{
               OriginalViewHolder originalViewHolder = (OriginalViewHolder) viewHolder;
               Anuncio anuncio = this.items.get(position);
               Glide.with(ctx).load(anuncio.getUrlImage()).into(originalViewHolder.banner);
               originalViewHolder.bt_delete.setVisibility(View.GONE);

           }catch (Exception x){
               new AlertDialog.Builder(ctx).setMessage(x.getMessage()).show();
           }
        }
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void onItemMove(int i, int i2) {
        Collections.swap(this.items, i, i2);
        notifyItemMoved(i, i2);
    }
}
