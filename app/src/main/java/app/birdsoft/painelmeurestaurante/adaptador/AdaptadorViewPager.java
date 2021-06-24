package app.birdsoft.painelmeurestaurante.adaptador;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import app.birdsoft.painelmeurestaurante.fragments.PedidosFragment;
import app.birdsoft.painelmeurestaurante.manager.FragmentType;

public class AdaptadorViewPager extends FragmentPagerAdapter {

    private final Context context;
    private final int[] mTitulos = {FragmentType.novoPedido.getName(), FragmentType.preparandoPedido.getName(), FragmentType.pedidoEmTransido.getName()};

    public AdaptadorViewPager(FragmentManager manager, Context context){
        super(manager);
        this.context=context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        FragmentType fragmentType = FragmentType.values()[position];
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentType", fragmentType.ordinal());
        PedidosFragment fragmentPedidos = new PedidosFragment();
        fragmentPedidos.setArguments(bundle);
        return fragmentPedidos;
    }

    @Override
    public int getCount() {
        return mTitulos.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(mTitulos[position]);
    }
}
