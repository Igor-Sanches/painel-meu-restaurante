package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.SwipeItemTouchHelper;
import app.birdsoft.painelmeurestaurante.model.ItensBlocoCardapio;
import app.birdsoft.painelmeurestaurante.tools.Mask;

public class AdaptadorItensLista extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItensBlocoCardapio> str;
    private final Context context;
    public OnItemSelectionClickListener mOnItemSelectionClickListener;
    public boolean loading;
    public boolean on_atach = true;

    public AdaptadorItensLista(List<ItensBlocoCardapio> str, Context context) {
        this.str =str;
        this.context =context;
    }

    public void insert(List<ItensBlocoCardapio> itemCardapios) {
        this.str = itemCardapios;
        notifyDataSetChanged();
    }

    public interface OnItemSelectionClickListener{
        void onItemClick(View v, ItensBlocoCardapio item, int position);
    }

    public void setmOnItemSelectionClickListener(OnItemSelectionClickListener mOnItemSelectionClickListener) {
        this.mOnItemSelectionClickListener = mOnItemSelectionClickListener;
    }

    public List<ItensBlocoCardapio> getLista(){
        return str;
    }

    public void removeItem(int i) {
       try{
           if(str.size() > 0){
               str.remove(i);
               notifyDataSetChanged();
           }
       }catch (Exception x){
           Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public TextView text, preco;
        public View layout;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            text = root.findViewById(R.id.text);
            preco = root.findViewById(R.id.preco);
            layout = root.findViewById(R.id.layout);
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
        return new OriginalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cardapio_lista, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof OriginalViewHolder){
            try{
                OriginalViewHolder viewHolder = (OriginalViewHolder)holder;
                ItensBlocoCardapio string = str.get(position);
                if(string.isText()){
                    viewHolder.text.setText(string.getTexto());
                    viewHolder.preco.setText(context.getString(R.string.text));
                }else{
                    viewHolder.text.setText(string.getContent());
                    viewHolder.preco.setText(Mask.formatarValor(string.getValor()));
                }

                viewHolder.layout.setOnClickListener(view -> {
                    if(mOnItemSelectionClickListener != null){
                        mOnItemSelectionClickListener.onItemClick(view, string, position);
                    }
                });
            }catch (Exception x){
                new AlertDialog.Builder(context).setMessage(x.getMessage()).show();
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
        return str.size();
    }
}



