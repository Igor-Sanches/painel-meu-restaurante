package app.birdsoft.painelmeurestaurante.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogInput;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.viewModel.LoginViewModel;
import app.birdsoft.painelmeurestaurante.widget.MySnackbar;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AppSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        private LoadingDialog loading;
        private LoginViewModel viewModel;
        private Preference email;
        private Preference senha;
        private String emailInput;

        public SettingsFragment() {
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            viewModel.init(getActivity());
            loading = new LoadingDialog(getActivity());
            email = findPreference("PROFILE_EMAIL");
            senha = findPreference("PROFILE_SENHA");
            Preference notificationSettings = findPreference("CONFIGURATION_PUSH");
            Preference btn_exit = findPreference("PROFILE_EXIT");
            assert notificationSettings != null;
            notificationSettings.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", requireActivity().getPackageName());
                intent.putExtra("app_uid", requireActivity().getApplicationInfo().uid);
                intent.putExtra("android.provider.extra.APP_PACKAGE", requireActivity().getPackageName());
                startActivity(intent);
                return false;
            });
            email.setOnPreferenceClickListener(preference -> {
                if(checkInfo(email.getSummary().toString())){
                    trocaEmail();
                }
                return false;
            });
            senha.setOnPreferenceClickListener(preference -> {
                if(checkInfo(senha.getSummary().toString())){
                    trocarSenha();
                }
                return false;
            });
            assert btn_exit != null;
            btn_exit.setOnPreferenceClickListener(preference -> {
                HelperManager.exitApp(getActivity());
                return false;
            });
            onDate();
            viewModel.getMutableLiveData().observe(this, (settingsElements -> {
                if(settingsElements.getEmail() != null){
                    if(!settingsElements.getEmail().equals("")){
                        emailInput = settingsElements.getEmail();
                        email.setSummary(settingsElements.getEmail());
                        senha.setSummary(getString(R.string.clique_senha));
                    }else{
                        email.setSummary(R.string.falha_settings_info);
                        senha.setSummary(R.string.falha_settings_info);
                    }
                }else{
                    email.setSummary(R.string.falha_settings_info);
                    senha.setSummary(R.string.falha_settings_info);
                }
            }));
        }

        private void trocaEmail() {

            @SuppressLint("InflateParams") View root = LayoutInflater.from(getActivity()).inflate(R.layout.card_email, null);
            DialogInput dialog = new DialogInput(requireActivity(), root, getString(R.string.email));
            TextInputLayout mTextInputLayout = root.findViewById(R.id.user_editor);
            EditText editor = mTextInputLayout.getEditText();
            assert editor != null;
            editor.setText(emailInput);
            editor.setSelection(emailInput.length());
            Button cancel = root.findViewById(R.id.cancel);
            Button enter = root.findViewById(R.id.confirmar);
            cancel.setOnClickListener(v -> dialog.dismiss());
            enter.setOnClickListener((v -> {
                if(isConnected()){
                    String email = editor.getText().toString().trim();
                    if(!email.equals(emailInput)){
                    if(!email.equals("")){
                        loading.show();
                        viewModel.trocaEmail(String.format("delivery%s", email), getActivity()).observe(this, (success ->{
                            if(success){
                                this.email.setSummary(email);
                                emailInput = email;
                                dialog.dismiss();
                                update();
                                MyToast.makeText(getActivity(), R.string.troca_email, ModoColor._success).show();
                            }else{
                                MyToast.makeText(getActivity(), R.string.error_troca_email, ModoColor._falha).show();
                            }
                            loading.dismiss();
                        }));
                    }else{
                        MyToast.makeText(getActivity(), R.string.digite_o_seu_email, ModoColor._falha).show();
                    }
                    }else{
                        dialog.dismiss();
                    }
                }
            }));

            dialog.show();
        }

        private void trocarSenha() {

            @SuppressLint("InflateParams") View root = LayoutInflater.from(getActivity()).inflate(R.layout.card_senha, null);
            DialogInput dialog = new DialogInput(requireActivity(), root, getString(R.string.senha));
            TextInputLayout mTextInputLayout = root.findViewById(R.id.user_editor);
            EditText editor = mTextInputLayout.getEditText();
            Button cancel = root.findViewById(R.id.cancel);
            Button enter = root.findViewById(R.id.confirmar);
            cancel.setOnClickListener(v -> dialog.dismiss());
            enter.setOnClickListener((v -> {
                if(isConnected()){
                    assert editor != null;
                    String senha = editor.getText().toString().trim();
                    if(!senha.equals("")){
                        loading.show();
                        viewModel.trocaSenha(senha).observe(this, (success ->{
                            if(success){
                                this.senha.setSummary(getString(R.string.clique_senha));
                                dialog.dismiss();

                                update();
                                MyToast.makeText(getActivity(), R.string.troca_senha, ModoColor._success).show();
                            }else{
                                MyToast.makeText(getActivity(), R.string.error_troca_senha, ModoColor._falha).show();
                            }
                            loading.dismiss();
                        }));
                    }else{
                        MyToast.makeText(getActivity(), R.string.digite_o_seu_senha, ModoColor._falha).show();
                    }
                }
            }));

            dialog.show();
        }

        private void update() {
            viewModel.update(getActivity());
        }

        private void onDate() {
            //formato.setSummary(DateTime.toDateString());
        }

        public void onPause() {
            super.onPause();
            this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private boolean checkInfo(String summary) {
            if(summary.equals(getString(R.string.falha_settings_info)) || summary.equals("...")){
                MySnackbar.makeText(getActivity(), R.string.tente_atualizar_info).show();
                viewModel.update(getActivity());
                return false;
            }
            return true;
        }

        private boolean isConnected() {
            if(!Conexao.isConnected(getActivity())){
                MySnackbar.makeText(getActivity(), R.string.sem_conexao, ModoColor._falha).show();
                return false;
            }
            return true;
        }

        public void onResume() {
            super.onResume();
            this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("FORMATO_DATA")){
                Settings.setFormateDate(PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(key, "dd/MM/yyyy"), getActivity());
                onDate();
            }else{
                Settings.put(key, (PreferenceManager.getDefaultSharedPreferences(requireActivity()).getBoolean(key, true)), getActivity());
            }
        }

    }
}