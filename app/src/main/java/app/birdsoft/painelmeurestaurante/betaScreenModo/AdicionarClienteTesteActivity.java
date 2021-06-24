package app.birdsoft.painelmeurestaurante.betaScreenModo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.dialogo.DialogCodigo;
import app.birdsoft.painelmeurestaurante.dialogo.LoadingDialog;
import app.birdsoft.painelmeurestaurante.manager.HelperManager;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import app.birdsoft.painelmeurestaurante.tools.ModoColor;
import app.birdsoft.painelmeurestaurante.widget.MyToast;

public class AdicionarClienteTesteActivity extends AppCompatActivity {
    //Adicionar um novo cliente para testar o app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_cliente_teste);
        ((TextView)findViewById(R.id.token_view)).setText(Settings.getUID(this));
        findViewById(R.id.token_view).setSelected(true);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void gerarQr() throws WriterException {
        BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix;
        bitMatrix = multiFormatWriter.encode(Settings.getUID(this), barcodeFormat, 200, 200);
        BarcodeEncoder encoder = new BarcodeEncoder();
        DialogCodigo codigo = new DialogCodigo(this, encoder.createBitmap(bitMatrix));
        codigo.show();
    }

    public void onGerarCodigo(View view) {
        try{
            gerarQr();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void onCopiaCodigo(View view) {
        try{
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(Settings.getUID(this));
            MyToast.makeText(this, R.string.copiado_sucesso).show();
        }catch (Exception x){
            MyToast.makeText(this, R.string.copiado_sucesso).show();
        }
    }

    public void onContrato(View view) {
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        HelperManager.getContrato(this).observe(this, (result -> {
            if(!result)
                MyToast.makeText(this, R.string.erro_contatar, ModoColor._falha).show();

            dialog.dismiss();
        }));
    }
}