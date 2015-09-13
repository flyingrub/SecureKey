package flying.grub.securekey.activity;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import flying.grub.securekey.R;
import flying.grub.securekey.network.PairingData;

/**
 * Created by fly on 8/18/15.
 */
public class QrCodeActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = QrCodeActivity.class.getSimpleName();

    private QRCodeReaderView mydecoderview;
    private TextView title;
    private TextView summary;

    private static PairingData currentPairingData;

    public static PairingData getCurrentPairingData() {
        PairingData p = currentPairingData;
        currentPairingData = null;
        return p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.settingstoolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.summary);
    }


    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        currentPairingData = new PairingData(text);
        mydecoderview.getCameraManager().stopPreview();
        title.setText(getString(R.string.qrcode_title_trusted));
        summary.setText(getString(R.string.qrcode_summary_trusted));
    }

    // Called when your device have no camera
    @Override
    public void cameraNotFound() {

    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }
}