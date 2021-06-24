package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.SwipeItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.ItemCardapioLista;
import app.birdsoft.painelmeurestaurante.tools.Utils;
import app.birdsoft.painelmeurestaurante.tools.ViewAnimation;

public class AdaptadorItensPedidos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    public List<ItemCardapioLista> items;

    public void remove(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }

    public List<ItemCardapioLista> getLista() {
        return items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public View layout;
        private final TextView categoria;
        private final LinearLayout container;
        public View lyt_expand;
        private final ImageButton btn_delete;
        private final ImageButton btn_editar;
        private final ImageButton bt_expand;
        private final ImageView btn_move;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            bt_expand = root.findViewById(R.id.bt_expand);
            categoria = root.findViewById(R.id.titleCategory);
            container= root.findViewById(R.id.containeContents);
            lyt_expand = root.findViewById(R.id.lyt_expand);
            btn_delete = root.findViewById(R.id.btn_delete);
            btn_editar = root.findViewById(R.id.btn_editar);
            btn_move = root.findViewById(R.id.btn_move);
        }

        public void onItemSelected() {
            this.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_grey_100));
        }

        public void onItemClear() {
            this.itemView.setBackgroundColor(0);
        }
    }


    public AdaptadorItensPedidos(Context context, List<ItemCardapioLista> list) {
        this.items = list;
        this.context = context;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new OriginalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardapio_adicionais, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder_, final int position) {
        if (viewHolder_ instanceof OriginalViewHolder) {
            final OriginalViewHolder viewHolder = (OriginalViewHolder)viewHolder_;
            final ItemCardapioLista itemCardapio = items.get(position);
            viewHolder.categoria.setText(itemCardapio.getDisplayName());
            final ArrayList<String> contents = itemCardapio.getContents();
            //final ArrayList<Double> precos = itemCardapio.getValores();
            final ArrayList<Integer> maxItensAdicionais = itemCardapio.getQuantidate();
            for(int i = 0; i < contents.size(); i++){
                LinearLayout.LayoutParams layoutP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                layoutP.gravity = Gravity.CENTER_VERTICAL;
                LinearLayout layout = new LinearLayout(context);
                layout.setLayoutParams(layoutP);
                layout.setGravity(Gravity.CENTER_VERTICAL);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView btn = new TextView(context);
                btn.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                btn.setPadding(20, 0, 0, 0);
                btn.setText(contents.get(i));
                btn.setTag(i);
                LinearLayout layoutBtn = new LinearLayout(context);
                layoutBtn.setGravity(Gravity.CENTER_VERTICAL);
                layoutBtn.setVisibility(View.VISIBLE);
                layoutBtn.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,5,0);
                layoutBtn.setLayoutParams(layoutParams);
                layoutBtn.setGravity(Gravity.CENTER_VERTICAL);
                TypedValue value = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true);

                TextView number = new TextView(context);
                TableLayout.LayoutParams paramsNumber = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsNumber.setMargins(10,0,5,0);
                number.setGravity(Gravity.CENTER_HORIZONTAL);
                number.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                number.setLayoutParams(paramsNumber);
                Integer num = maxItensAdicionais.get(i);
                if(num == null) num = 1;
                number.setText(String.format("Quant: %s", num));
                layoutBtn.addView(number);
                layout.addView(btn);
                layout.addView(layoutBtn);

                viewHolder.container.addView(layout);
            }
            viewHolder.btn_delete.setVisibility(View.GONE);
            viewHolder.btn_editar.setVisibility(View.GONE);
            viewHolder.btn_move.setVisibility(View.GONE);

            viewHolder.bt_expand.setOnClickListener(view -> itemCardapio.expanded = toggleLayoutExpand(!itemCardapio.expanded, view, viewHolder.lyt_expand));
            if (itemCardapio.expanded) {
                viewHolder.lyt_expand.setVisibility(View.VISIBLE);
            } else {
                viewHolder.lyt_expand.setVisibility(View.GONE);
            }
            Utils.toggleArrow(itemCardapio.expanded, viewHolder.bt_expand, false);
        }
    }

    public boolean toggleLayoutExpand(boolean z, View view, View view2) {
        Utils.toggleArrow(z, view);
        if (z) {
            ViewAnimation.expand(view2);
        } else {
            ViewAnimation.collapse(view2);
        }
        return z;
    }

    public int getItemCount() {
        return this.items.size();
    }


}
