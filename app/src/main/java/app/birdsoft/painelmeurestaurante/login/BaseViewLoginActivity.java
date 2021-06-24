package app.birdsoft.painelmeurestaurante.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseViewLoginActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (WelcomeActivity.shouldDisplay(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }
}
