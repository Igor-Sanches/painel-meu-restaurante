package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.DragItemTouchHelper;
import app.birdsoft.painelmeurestaurante.manager.SwipeItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.ItemCardapio;
import app.birdsoft.painelmeurestaurante.tools.Mask;

public class AdaptadorItemAdicionalOrdem extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DragItemTouchHelper.MoveHelperAdapter {

    public Context context;
    public List<ItemCardapio> items;

    public List<ItemCardapio> getLista() {
        return items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public View layout;
        private final TextView categoria;
        private final LinearLayout container;
        public ImageButton bt_expand;
        public View lyt_expand;
        private final ImageButton btn_delete;
        private final ImageButton btn_editar;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            categoria = root.findViewById(R.id.titleCategory);
            container= root.findViewById(R.id.containeContents);
            bt_expand = root.findViewById(R.id.bt_expand);
            lyt_expand = root.findViewById(R.id.lyt_expand);
            btn_delete = root.findViewById(R.id.btn_delete);
            btn_editar = root.findViewById(R.id.btn_editar);
        }

        public void onItemSelected() {
            this.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_grey_100));
        }

        public void onItemClear() {
            this.itemView.setBackgroundColor(0);
        }
    }


    public AdaptadorItemAdicionalOrdem(Context context, List<ItemCardapio> list) {
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
            final ItemCardapio itemCardapio = items.get(position);
            viewHolder.categoria.setText(itemCardapio.getDispayTitulo());
            final ArrayList<String> contents = itemCardapio.getContents();
            final ArrayList<String> textos = itemCardapio.getTextos();
            final ArrayList<Boolean> texts = itemCardapio.getText();
            final ArrayList<Double> precos = itemCardapio.getValores();
            final ArrayList<Integer> maxItensAdicionais = itemCardapio.getMaxItensAdicionais();
            if(itemCardapio.isItensAdicionais()){
                for(int i = 0; i < contents.size(); i++){
                    LinearLayout.LayoutParams layoutP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setLayoutParams(layoutP);
                    layout.setGravity(Gravity.CENTER_VERTICAL);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView texto = null;
                    CheckBox btn = null;
                    if(texts == null){
                        btn = new CheckBox(context);
                        btn.setLayoutParams(new TableLayout.LayoutParams(0, 90, 1.0f));
                        btn.setButtonDrawable(R.drawable.menu_selector);
                        btn.setPadding(20, 0, 0, 0);
                        btn.setText(contents.get(i));
                        btn.setTag(i);
                        itemCardapio.allCheckBox.add(btn);
                    }else{
                        if(texts.get(i)){
                            texto = new TextView(context);
                            texto.setTextAppearance(context, R.style.text_style);
                            //texto.setTextSize(30);
                            texto.setGravity(Gravity.CENTER_VERTICAL);
                            texto.setLayoutParams(new TableLayout.LayoutParams(0, 90, 1.0f));
                            texto.setPadding(20, 0, 0, 0);
                            texto.setText(textos.get(i));
                        }else{
                            btn = new CheckBox(context);
                            btn.setLayoutParams(new TableLayout.LayoutParams(0, 90, 1.0f));
                            btn.setButtonDrawable(R.drawable.menu_selector);
                            btn.setPadding(20, 0, 0, 0);
                            btn.setText(contents.get(i));
                            btn.setTag(i);
                            itemCardapio.allCheckBox.add(btn);
                        }
                    }

                    LinearLayout layout2 = new LinearLayout(context);
                    layout2.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout2.setGravity(Gravity.CENTER_VERTICAL);
                    layout2.setOrientation(LinearLayout.HORIZONTAL);
                    TextView preco = new TextView(context);
                    FrameLayout.LayoutParams lText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lText.setMargins(0,0,10,0);
                    preco.setLayoutParams(lText);
                    preco.setText(String.format("+ %s", Mask.formatarValor(precos.get(i))));
                    LinearLayout layoutBtn = new LinearLayout(context);
                    layoutBtn.setVisibility(View.GONE);
                    layoutBtn.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,0,40,0);
                    layoutBtn.setLayoutParams(layoutParams);
                    layoutBtn.setGravity(Gravity.CENTER_VERTICAL);
                    TypedValue value = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true);
                    ImageButton btnRemove = new ImageButton(context);
                    TableLayout.LayoutParams btns = new TableLayout.LayoutParams(100, 100);
                    btnRemove.setColorFilter(context.getResources().getColor(R.color.preto));
                    btnRemove.setLayoutParams(btns);
                    btnRemove.setEnabled(false);
                    btnRemove.setBackgroundResource(value.resourceId);
                    btnRemove.setImageResource(R.drawable.ic_action_remover);
                    TextView number = new TextView(context);
                    TableLayout.LayoutParams paramsNumber = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsNumber.setMargins(20,0,20,0);
                    number.setGravity(Gravity.CENTER_HORIZONTAL);
                    number.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    number.setLayoutParams(paramsNumber);
                    number.setText("1");
                    ImageButton btnAdd = new ImageButton(context);
                    if(maxItensAdicionais.get(i) == 1){
                        layoutBtn.setVisibility(View.GONE);
                    }
                    btnAdd.setColorFilter(context.getResources().getColor(R.color.preto));
                    btnAdd.setLayoutParams(btns);
                    btnAdd.setBackgroundResource(value.resourceId);
                    btnAdd.setImageResource(R.drawable.ic_action_adicionar);
                    layoutBtn.addView(btnRemove);
                    layoutBtn.addView(number);
                    layoutBtn.addView(btnAdd);
                    if(texts == null){
                        layout2.addView(preco);
                    }else{
                        if(!texts.get(i)){
                            layout2.addView(preco);
                        }
                    }
                    layout2.addView(layoutBtn);
                    if(texts == null){
                        layout.addView(btn);
                    }else layout.addView(texts.get(i) ? texto : btn);
                    layout.addView(layout2);


                    viewHolder.container.addView(layout);
                }

            }else{
                for(int i = 0; i < contents.size(); i++){
                    LinearLayout.LayoutParams layoutP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setLayoutParams(layoutP);
                    layout.setGravity(Gravity.CENTER_VERTICAL);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView texto = null;
                    CheckBox btn = null;
                    if(texts == null){
                        btn = new CheckBox(context);
                        btn.setLayoutParams(new TableLayout.LayoutParams(0, 90, 1.0f));
                        btn.setButtonDrawable(R.drawable.menu_selector);
                        btn.setPadding(20, 0, 0, 0);
                        btn.setText(contents.get(i));
                        btn.setTag(i);
                        itemCardapio.allCheckBox.add(btn);

                    }else{
                        if(texts.get(i)){
                            texto = new TextView(context);
                            texto.setTextAppearance(context, R.style.text_style);
                            texto.setGravity(Gravity.CENTER_VERTICAL);
                            texto.setLayoutParams(new TableLayout.LayoutParams(0, 90, 1.0f));
                            texto.setPadding(20, 0, 0, 0);
                            texto.setText(textos.get(i));
                        }else{
                            btn = new CheckBox(context);
                            btn.setLayoutParams(new TableLayout.LayoutParams(0, 90, 1.0f));
                            btn.setButtonDrawable(R.drawable.menu_selector);
                            btn.setPadding(20, 0, 0, 0);
                            btn.setText(contents.get(i));
                            btn.setTag(i);
                            itemCardapio.allCheckBox.add(btn);

                        }
                    }

                    TextView preco = new TextView(context);
                    preco.setText(String.format("+ %s", Mask.formatarValor(precos.get(i))));

                    if(texts == null){
                        layout.addView(btn);
                        layout.addView(preco);
                    }else{
                        layout.addView(texts.get(i) ? texto : btn);
                        if(!texts.get(i)){
                            layout.addView(preco);
                        }
                    }
                    viewHolder.container.addView(layout);
                }
            }
            viewHolder.btn_delete.setVisibility(View.GONE);
            viewHolder.btn_editar.setVisibility(View.GONE);
            viewHolder.bt_expand.setVisibility(View.GONE);

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
