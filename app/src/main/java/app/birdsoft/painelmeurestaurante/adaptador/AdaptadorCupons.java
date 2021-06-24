package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.model.Cupom;
import app.birdsoft.painelmeurestaurante.tools.DateTime;
import app.birdsoft.painelmeurestaurante.tools.Mask;

public class AdaptadorCupons extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Cupom> mCupons;
    private final Context context;
    public boolean on_atach = true;
    private onClickItem onClickItem;

    public void setOnClickItem(onClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public void remove(int position) {
        mCupons.remove(position);
        notifyDataSetChanged();
    }

    public void setInsert(List<Cupom> cupon) {
        this.mCupons = cupon;
        notifyDataSetChanged();
    }

    public interface onClickItem{
        void onClick(View view, Cupom cupon, int position);
    }

    public AdaptadorCupons(Context context, List<Cupom> mCupons) {
        this.context = context;
        this.mCupons = mCupons;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView codigo, numero_usados, displayName, data, desconto_valor;
        public CardView layout;
        private final FrameLayout cupom_desativado;

        public OriginalViewHolder(@NonNull View root) {
            super(root);
            cupom_desativado = root.findViewById(R.id.cupom_desativado);
            displayName = root.findViewById(R.id.displayName);
            data = root.findViewById(R.id.data);
            numero_usados = root.findViewById(R.id.numero_usados);
            codigo = root.findViewById(R.id.codigo);
            desconto_valor = root.findViewById(R.id.desconto_valor);
            layout = root.findViewById(R.id.layout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OriginalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cupom, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof OriginalViewHolder){
            try{
                OriginalViewHolder viewHolder = (OriginalViewHolder)holder;
                Cupom cupom = mCupons.get(position);
                viewHolder.displayName.setText(getName(cupom));
                viewHolder.codigo.setText(cupom.getCodigo());
                viewHolder.cupom_desativado.setVisibility(cupom.isAtivo() ? View.GONE : View.VISIBLE);
                viewHolder.desconto_valor.setText(getDesconto(cupom));
                viewHolder.numero_usados.setText(getUser(cupom));
                viewHolder.data.setText(getValido(cupom));

                viewHolder.layout.setOnClickListener((view -> {
                    if(onClickItem!=null){
                        onClickItem.onClick(view, cupom, position);
                    }
                }));
            }catch (Exception x){
                Toast.makeText(context, x.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getUser(Cupom cupom) {
        if(cupom.getNumerouso() == 0){
            return context.getString(R.string.sem_uso_do_cupom);
        }else{
            if(cupom.getNumerouso() == 1){
                return "1 " +context.getString(R.string.um_uso_cupom);
            }else return cupom.getNumerouso() + " " + context.getString(R.string.usados_cupom);
        }
    }

    private String getValido(Cupom cupom) {
        if(cupom.isExpirado()){
            if(cupom.isVencimento()){
                if(HelperManager.isVencido(cupom.getDataValidade())){
                    return context.getString(R.string.vencimento_expirado);
                }else  return context.getString(R.string.vencimento) + " " + DateTime.toDateString("dd/MM/yyyy", cupom.getDataValidade());
            }else{
                return context.getString(R.string.cupom_valido);
            }
        }else return context.getString(R.string.cupom_valido);
    }

    private String getDesconto(Cupom cupom) {
        if(cupom.getDescontoType() != 0){
            if(cupom.getDescontoType() == 2){
                return Mask.formatarValor(cupom.getValorDesconto()) + " Off";
            }else return ((int)cupom.getValorDesconto()) + "% Off";
        }else return "";
    }

    private String getName(Cupom cupon) {
        if(cupon.getDescontoType() == 0){
            return context.getString(R.string.cupom_frete_grates);
        }else{
            return context.getString(R.string.cupom_desconto);
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
        return mCupons.size();
    }

}
