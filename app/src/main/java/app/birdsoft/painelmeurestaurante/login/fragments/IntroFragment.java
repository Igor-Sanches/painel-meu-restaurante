package app.birdsoft.painelmeurestaurante.login.fragments;

import android.content.Context;

import app.birdsoft.painelmeurestaurante.login.WelcomeActivity;

public class IntroFragment extends WelcomeFragment implements WelcomeActivity.WelcomeContent {
    public boolean shouldDisplay(Context context) {
        return false;
    }

}
