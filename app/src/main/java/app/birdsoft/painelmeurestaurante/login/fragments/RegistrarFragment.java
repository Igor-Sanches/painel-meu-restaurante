package app.birdsoft.painelmeurestaurante.login.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.login.LoginActivity;
import app.birdsoft.painelmeurestaurante.login.SplashActivity;
import app.birdsoft.painelmeurestaurante.login.WelcomeActivity;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.service.SendNotification;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.Email;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.LoginViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class RegistrarFragment extends Fragment {

    private TextInputLayout emailUser, senhaUser, senhaDouUser;
    private LoadingDialog dialog;
    private FirebaseAuth auth;

    public RegistrarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registrar, container, false);
        dialog = new LoadingDialog(getActivity());
        auth = Conexao.getFirebaseAuth();
        root.findViewById(R.id.btnLogin).setOnClickListener(view -> onLogin());
        emailUser = root.findViewById(R.id.emailUser);
        senhaUser = root.findViewById(R.id.senhaUser);
        senhaDouUser = root.findViewById(R.id.senhaDouUser);
        root.findViewById(R.id.buttonRegister).setOnClickListener(view -> onRegister());

        onInput();
        return root;
    }

    private void onRegister() {
        String email = Objects.requireNonNull(emailUser.getEditText()).getText().toString().trim();
        String senha = Objects.requireNonNull(senhaUser.getEditText()).getText().toString().trim();
        String senhaDou = Objects.requireNonNull(senhaDouUser.getEditText()).getText().toString().trim();
            if (!email.equals("")) {
                if (Email.validar(email)) {
                    if (!senha.equals("")) {
                        if (!senhaDou.equals("")) {
                            if (senha.equals(senhaDou)) {
                                onContinue(new String[]{email, senha});
                            } else {
                                onError(senhaDouUser, getString(R.string.senha_n_igual));
                            }
                        } else {
                            onError(senhaDouUser, getString(R.string.senha_novamente));
                        }
                    } else {
                        onError(senhaUser, getString(R.string.digite_sua_senha));
                    }
                } else {
                    onError(emailUser, getString(R.string.digite_o_seu_email_valido));
                }
            } else {
                onError(emailUser, getString(R.string.digite_o_seu_email));
            }

    }

    private void onContinue(final String[] dados) {
        dialog.show();
        String email = dados[0];
        String senha = dados[1];
        auth.createUserWithEmailAndPassword(String.format("delivery%s", email), senha)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        try{
                            FirebaseUser user = task.getResult().getUser();
                            if(user != null){
                                salvarDadosCriarDb(user, dados);
                            }
                        }catch (Exception ignored){

                        }


                    }else{
                        try{
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthWeakPasswordException user){
                            onError(senhaUser, getString(R.string.senha_fraca));
                        }catch (FirebaseAuthInvalidCredentialsException user){
                            onError(senhaUser, getString(R.string.firebaseAuthInvalidCredentialsException));
                        }catch (FirebaseAuthUserCollisionException user){
                            onError(emailUser, getString(R.string.firebaseAuthUserCollisionException));
                        }catch (Exception e) {
                            MySnackbar.makeText(getActivity(), getString(R.string.authException), ModoColor._falha).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void salvarDadosCriarDb(FirebaseUser user, String[] dados) {
        try {
            String email = dados[0];
            String uuid = user.getUid();
            Settings.setUID(uuid, getActivity());
            new ViewModelProvider(requireActivity())
                    .get(LoginViewModel.class)
                    .createAccont(email, getActivity())
                    .observe(requireActivity(), (sucesso -> {
                        if(sucesso){
                            onFinish();
                        }else{
                            MyToast.makeText(getActivity(), R.string.falha_criar_conta, ModoColor._falha).show();
                            auth.signOut();
                        }
                    }));
        }catch (Exception x){
            MySnackbar.makeText(getActivity(), x.getMessage(), ModoColor._falha).show();
        }
    }

    private void onFinish(){
        dialog.dismiss();
        requireActivity().startActivity(new Intent(getActivity(), SplashActivity.class));
        SendNotification.UpdateToken(getActivity());
        requireActivity().finish();
        WelcomeActivity.getInstance().finish();
    }

    private void onLogin() {
        LoginActivity.getInstance().onPage(0);
    }

    private void onError(TextInputLayout input, String error){
        Objects.requireNonNull(input.getEditText()).requestFocus();
        input.setErrorEnabled(true);
        input.setError(error);
    }

    private void onError(TextInputLayout input){
        Objects.requireNonNull(input.getEditText()).requestFocus();
        input.setErrorEnabled(false);
        input.setError("");
    }

    private void onInput(){
        Objects.requireNonNull(emailUser.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onError(emailUser);
            }
        });
        Objects.requireNonNull(senhaUser.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onError(senhaUser);
            }
        });
        Objects.requireNonNull(senhaDouUser.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onError(senhaDouUser);
            }
        });

    }

}