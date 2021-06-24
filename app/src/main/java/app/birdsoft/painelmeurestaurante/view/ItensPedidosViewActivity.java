package app.birdsoft.painelmeurestaurante.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.adaptador.AdaptadorItensPedidos;
import app.birdsoft.painelmeurestaurante.model.Carrinho;

public class ItensPedidosViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itens_pedidos_view);
        Carrinho carrinho = (Carrinho)getIntent().getSerializableExtra("itens");
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorItensPedidos adaptador = new AdaptadorItensPedidos(this, carrinho.getListas());
        mRecyclerView.setAdapter(adaptador);
    }

    public void onBack(View view) {
        onBackPressed();
    }
}