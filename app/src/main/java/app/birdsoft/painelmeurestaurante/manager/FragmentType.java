package app.birdsoft.painelmeurestaurante.manager;

import androidx.annotation.StringRes;

import app.birdsoft.painelmeurestaurante.R;

public enum FragmentType {
    novoPedido(R.string.novo_pedido), preparandoPedido(R.string.preparando_pedido), pedidoEmTransido(R.string.pedidos_transito);

    private @StringRes
    final int name;

    FragmentType(@StringRes int desc){
        this.name = desc;
    }

    public @StringRes int getName() {
        return name;
    }
}
