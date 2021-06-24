package app.birdsoft.painelmeurestaurante.login;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.login.fragments.InicioIntroFragment;
import app.birdsoft.painelmeurestaurante.login.fragments.IntroFragment;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class WelcomeActivity extends AppCompatActivity {
    public WelcomeContent contentFragment;
    private static WelcomeActivity instance;

    public static WelcomeActivity getInstance() {
        return instance;
    }

    private long countClick = 0;

    private void toast(String msg){
        MyToast.makeText(this, msg).show();
    }

    @Override
    public void onBackPressed() {
       exit();
    }

    private void exit(){
        if(System.currentTimeMillis() - countClick > 2000){
            countClick = System.currentTimeMillis();
            toast(getString(R.string.clique_para_sair));
            return;
        }
        finishAffinity();
    }

    public interface WelcomeContent {
        boolean shouldDisplay(Context context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_welcome);
            instance = this;
            this.contentFragment = getCurrentFragment(this);
            if (this.contentFragment == null) {
                finish();
            }
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(R.id.welcome_content, (Fragment) contentFragment);
            beginTransaction.commit();


        }catch(Exception ignored){
         }
    }

    private static WelcomeContent getCurrentFragment(Context context) {
        for (WelcomeContent welcomeContent : getWelcomeContent()) {
            if (welcomeContent.shouldDisplay(context)) {
                return welcomeContent;
            }
        }
        return null;
    }

    private static List<WelcomeContent> getWelcomeContent() {
        return new ArrayList<>(Arrays.asList(new InicioIntroFragment(), new IntroFragment()));
    }

    public static boolean shouldDisplay(Context context) {
        return getCurrentFragment(context) != null;
    }
}

