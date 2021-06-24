package app.birdsoft.painelmeurestaurante.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorViewPager;
import app.birdsoft.painelmeurestaurante.manager.Loader;

public class PainelPedidosActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, Loader {

    private AdaptadorViewPager adaptador;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel_pedidos);
        initLayout();
    }

    private void initLayout() {
        pager = findViewById(R.id.viewpager);
        adaptador = new AdaptadorViewPager(getSupportFragmentManager(), this);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adaptador);
        TabLayout tabLayout = findViewById(R.id.tabs);
        pager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    public void load() {
        FragmentManager manager = getSupportFragmentManager();
        manager.getFragments();
        if(manager.getFragments().size() > 0){
            for (Fragment fragment : manager.getFragments()){
                ((Loader)fragment).load();
            }
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        FragmentManager manager = getSupportFragmentManager();
        manager.getFragments();
        if(manager.getFragments().size() > 0){
            for (Fragment fragment : manager.getFragments()){
                if(fragment instanceof Loader){
                    ((Loader)fragment).setRefreshing(refreshing);
                }
            }
        }
    }

    @Override
    public void finishMode() {
        FragmentManager manager = getSupportFragmentManager();
        manager.getFragments();
        if(manager.getFragments().size() > 0){
            for (Fragment fragment : manager.getFragments()){
                if(fragment instanceof Loader){
                    ((Loader)fragment).finishMode();
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adaptador.notifyDataSetChanged();
        pager.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}