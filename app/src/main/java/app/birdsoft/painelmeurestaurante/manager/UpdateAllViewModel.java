package app.birdsoft.painelmeurestaurante.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import app.birdsoft.painelmeurestaurante.viewModel.AnunciosViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.CardapioViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.CuponsViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.EstabelecimentoViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.HistoricosViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.PedidosViewModel;

public class UpdateAllViewModel extends AsyncTask<ViewModelStoreOwner, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final LifecycleOwner owne;
    public UpdateAllViewModel(Context context, LifecycleOwner owne){
        this.context = context;
        this.owne = owne;
    }

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(ViewModelStoreOwner... owner) {
        new ViewModelProvider(owner[0]).get(AnunciosViewModel.class).init(context);
        new ViewModelProvider(owner[0]).get(HistoricosViewModel.class).init(context);
        new ViewModelProvider(owner[0]).get(CardapioViewModel.class).init(context);
        new ViewModelProvider(owner[0]).get(CuponsViewModel.class).init(context);
        new ViewModelProvider(owner[0]).get(EstabelecimentoViewModel.class).init(context);
        new ViewModelProvider(owner[0]).get(HistoricosViewModel.class).init(context);
        new ViewModelProvider(owner[0]).get(PedidosViewModel.class).init(context, FragmentType.novoPedido);
        new ViewModelProvider(owner[0]).get(PedidosViewModel.class).init(context, FragmentType.pedidoEmTransido);
        new ViewModelProvider(owner[0]).get(PedidosViewModel.class).init(context, FragmentType.preparandoPedido);
        new ViewModelProvider(owner[0]).get(HistoricosViewModel.class).autoDelete(owne, context);
        new ViewModelProvider(owner[0]).get(CuponsViewModel.class).CuponsRevisao(owne, context);
        return null;
    }
}
