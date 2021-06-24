package app.birdsoft.painelmeurestaurante.login.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import app.birdsoft.painelmeurestaurante.MainActivity;
import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogMessage;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.login.LoginActivity;
import app.birdsoft.painelmeurestaurante.login.WelcomeActivity;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.navigation.Navigate;
import app.birdsoft.painelmeurestaurante.service.SendNotification;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.Email;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.view.EstabelecimentoActivity;
import app.birdsoft.painelmeurestaurante.viewModel.EstabelecimentoViewModel;
import app.birdsoft.painelmeurestaurante.viewModel.LoginViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;

public class LoginFragment extends Fragment {

    private View root;
    private FirebaseAuth auth;
    private TextInputLayout emailUser, senhaUser;
    private LoadingDialog dialog;
    //private BottomSheetDialog sheetDialog;
    //private BottomSheetBehavior sheetBehavior;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try{
            root = inflater.inflate(R.layout.fragment_login, container, false);
            auth = Conexao.getFirebaseAuth();
            dialog = new LoadingDialog(getActivity());
            senhaUser = root.findViewById(R.id.senhaUser);
            //View sheetBottom = root.findViewById(R.id.bottom_sheet);
            //sheetBehavior = BottomSheetBehavior.from(sheetBottom);
            Objects.requireNonNull(senhaUser.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    senhaUser.setError("");
                    senhaUser.setErrorEnabled(false);
                }
            });
            emailUser = root.findViewById(R.id.emailUser);
            root.findViewById(R.id.btnLogin).setOnClickListener(view -> onLogin());
            root.findViewById(R.id.btnRegister).setOnClickListener(view -> goRegister());
        }catch (Exception ignored){

        }
        return root;
    }

    private void goRegister() {
        LoginActivity.getInstance().onPage(1);
    }

    private void onLogin() {
        String email = Objects.requireNonNull(emailUser.getEditText()).getText().toString().trim();
        final String senha = Objects.requireNonNull(senhaUser.getEditText()).getText().toString().trim();
        if(Conexao.isConnected(getActivity())) {
            if (!email.equals("")) {
                if (Email.validar(email)) {
                    if (!senha.equals("")) {
                        dialog.show();
                        auth.signInWithEmailAndPassword(String.format("delivery%s", email), senha).addOnCompleteListener(task -> {
                            try{
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();
                                    if (user != null) {
                                        salvarDados(user);
                                    }else{
                                        MySnackbar.makeText(requireActivity(), getString(R.string.falha_user), ModoColor._falha).show();
                                        dialog.dismiss();
                                    }
                                } else {
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthInvalidUserException user) {
                                        MySnackbar.makeText(requireActivity(), getString(R.string.firebaseAuthInvalidUserException), ModoColor._falha).show();

                                    } catch (FirebaseAuthInvalidCredentialsException user) {
                                        senhaUser.getEditText().requestFocus();
                                        senhaUser.setError(getString(R.string.firebaseAuthInvalidCredentialsException));
                                        senhaUser.setErrorEnabled(true);
                                    } catch (Exception e) {
                                        MySnackbar.makeText(requireActivity(), getString(R.string.authException), ModoColor._falha).show();
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                }
                            }catch (Exception ignored){

                            }
                        });
                    } else {
                        senhaUser.getEditText().requestFocus();
                        senhaUser.setErrorEnabled(true);
                        senhaUser.setError(getString(R.string.digite_sua_senha));
                    }
                } else {
                    emailUser.getEditText().requestFocus();
                    emailUser.setErrorEnabled(true);
                    emailUser.setError(getString(R.string.digite_o_seu_email_valido));
                }
            } else {
                emailUser.getEditText().requestFocus();
                emailUser.setErrorEnabled(true);
                emailUser.setError(getString(R.string.digite_o_seu_email));
            }
        }
    }

    private void salvarDados(FirebaseUser user) {
        Settings.setUID(user.getUid(), requireActivity());
        new ViewModelProvider(requireActivity()).get(LoginViewModel.class)
                .isContaExist(getActivity())
                .observe(requireActivity(), (existe -> {
                    if(existe){
                        EstabelecimentoViewModel viewModel = new ViewModelProvider(requireActivity()).get(EstabelecimentoViewModel.class);
                        viewModel.init(requireActivity());
                        viewModel.getMutableLiveDataNoUpdate(getActivity()).observe(requireActivity(), (isConfig -> {
                            Settings.setConfig(isConfig, requireActivity());
                            if(isConfig){
                                Navigate.activity(getActivity()).navigate(MainActivity.class);
                            }else{
                                Intent intent = new Intent(getActivity(), EstabelecimentoActivity.class);
                                intent.putExtra("config", true);
                                Navigate.activity(getActivity()).navigate(intent);
                            }
                            SendNotification.UpdateToken(getActivity());
                            WelcomeActivity.getInstance().finish();
                            requireActivity().finish();
                            dialog.dismiss();
                        }));
                    }else{
                        auth.signOut();
                        goRegister();
                        new DialogMessage(requireActivity(), getString(R.string.conta_n_exist), false, "").show();
                    }
                }));

    }

    /*
    private void onRedefinir() {
        try{
            if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            @SuppressLint("InflateParams") View _view = getLayoutInflater().inflate(R.layout.dialog_redefinir_senha, null);

            final TextInputLayout emailInput = _view.findViewById(R.id.emailUser);
            Objects.requireNonNull(emailInput.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    emailInput.setError("");
                    emailInput.setErrorEnabled(false);
                }
            });
            FrameLayout btnRec= _view.findViewById(R.id.buttonGo);
            btnRec.setOnClickListener(v -> {
                try {
                    final String _emailB2 = emailInput.getEditText().getText().toString();
                    if (Conexao.isConnected(getActivity())) {
                        if (!_emailB2.equals("")) {

                            if (Email.validar(_emailB2)) {
                                onRecuperar(emailInput);
                            } else {
                                emailInput.setErrorEnabled(true);
                                emailInput.setError(getString(R.string.digite_o_seu_email_valido));
                                emailInput.getEditText().requestFocus();
                            }

                        } else {
                            emailInput.setErrorEnabled(true);
                            emailInput.setError(getString(R.string.digite_o_seu_email));
                            emailInput.getEditText().requestFocus();
                        }


                    } else {
                        MyToast.makeText(getActivity(), R.string.sem_conexao, ModoColor._falha).show();

                    }
                }catch (Exception x){
                    MyToast.makeText(getActivity(), x.getMessage(), ModoColor._falha).show();
                }
            });

            sheetDialog = new BottomSheetDialog(requireActivity());
            sheetDialog.setContentView(_view);
            if(Build.VERSION.SDK_INT >= 21){
                sheetDialog.getWindow().addFlags(67108864);
            }
            ((View)_view.getParent()).setBackgroundColor(getResources().getColor(R.color.transparent));
            sheetDialog.show();
        }catch (Exception x){
            MyToast.makeText(getActivity(), x.getMessage(), ModoColor._falha).show();
        }
    }

    private void onRecuperar(final TextInputLayout emailInput){
        sheetDialog.dismiss();
        MyToast.makeText(getActivity(), R.string.verificando_email, ModoColor._default).show();
        dialog.show();
        String email = Objects.requireNonNull(emailInput.getEditText()).getText().toString().trim();
        auth.sendPasswordResetEmail(String.format("delivery%s", email))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        MyToast.makeText(getActivity(), R.string.verifique_sua_caixa_email, ModoColor._success).show();
                    }else{
                        try{
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthInvalidUserException user) {
                            emailInput.getEditText().requestFocus();
                            emailInput.setErrorEnabled(true);
                            emailInput.setError(getString(R.string.firebaseAuthInvalidUserException));
                        }catch (Exception e) {
                            MyToast.makeText(getActivity(), R.string.authException, ModoColor._falha).show();
                            e.printStackTrace();
                        }

                    }
                    dialog.dismiss();
                    sheetDialog.show();
                });
    }
    */
}