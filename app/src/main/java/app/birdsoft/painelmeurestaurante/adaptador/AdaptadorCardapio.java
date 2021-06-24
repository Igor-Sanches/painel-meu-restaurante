package app.birdsoft.painelmeurestaurante.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.model.Cardapio;
import app.birdsoft.painelmeurestaurante.tools.ItemAnimation;
import app.birdsoft.painelmeurestaurante.widget.ImageUtils;

public class AdaptadorCardapio extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public boolean on_attach = true;
    private int lastPosition = -1;
    private final List<Cardapio> cardapios;
    private final Context context;
    public boolean loading;
    private OnDeleteItemListener onDeleteItemListener;
    private OnClonarItemListener onClonarItemListener;
    private OnEditarItemListener onEditarItemListener;

    public void setOnClonarItemListener(OnClonarItemListener onClonarItemListener) {
        this.onClonarItemListener = onClonarItemListener;
    }

    public void setOnEditarItemListener(OnEditarItemListener onEditarItemListener) {
        this.onEditarItemListener = onEditarItemListener;
    }

    public List<Cardapio> getLista() {
        return cardapios;
    }

    public interface OnClonarItemListener{
        void onClone(View v, Cardapio item, int position);
    }

    public interface OnEditarItemListener{
        void onEditar(View v, Cardapio item, int position);
    }

    public void setOnDeleteItemListener(OnDeleteItemListener onDeleteItemListener) {
        this.onDeleteItemListener = onDeleteItemListener;
    }

    public interface OnDeleteItemListener{
        void onDelete(View v, Cardapio item, int position);
    }

    public AdaptadorCardapio(List<Cardapio> str, Context context) {
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

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView displayName, summary;
        private final Button apagar;
        private final Button editar;
        private final Button clonar;
        private final FrameLayout item_indisponivel;
        private final ImageView iconeProduto;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            iconeProduto = root.findViewById(R.id.iconeProduto);
            displayName = root.findViewById(R.id.displayName);
            summary = root.findViewById(R.id.summary);
            apagar = root.findViewById(R.id.apagar);
            item_indisponivel = root.findViewById(R.id.item_indisponivel);
            editar = root.findViewById(R.id.editar);
            clonar = root.findViewById(R.id.clonar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OriginalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cardapio, parent, false));
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
                viewHolder.apagar.setOnClickListener((v->{
                    if(onDeleteItemListener != null) onDeleteItemListener.onDelete(v, string, position);
                }));
                viewHolder.editar.setOnClickListener((v->{
                    if(onEditarItemListener != null) onEditarItemListener.onEditar(v, string, position);
                }));
                viewHolder.clonar.setOnClickListener((v->{
                    if(onClonarItemListener != null) onClonarItemListener.onClone(v, string, position);
                }));
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

    @Override
    public int getItemCount() {
        return cardapios.size();
    }
}



