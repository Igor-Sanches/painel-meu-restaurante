package app.birdsoft.painelmeurestaurante.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import app.birdsoft.painelmeurestaurante.MainActivity;
import app.birdsoft.painelmeurestaurante.manager.UpdateAllViewModel;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.view.EstabelecimentoActivity;

public abstract class BaseViewActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (WelcomeActivity.shouldDisplay(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
       }else if(!Settings.isConfig(this)){
            Intent intent = new Intent(this, EstabelecimentoActivity.class);
            intent.putExtra("config", true);
            Navigate.activity(this).navigate(intent);
        }else{
            new UpdateAllViewModel(this, this).execute(this);
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
