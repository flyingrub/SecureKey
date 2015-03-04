package flying.grub.securekey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends ActionBarActivity {

    String pin;
    String wsuri;
    WebSocketClientOD wc;
    DoorState doorState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPreferences.getString("prefChoose", null).equals("Local")){
            wsuri = "wss://"+sharedPreferences.getString("prefLocal", null);
        }else{
            wsuri = "wss://"+sharedPreferences.getString("prefExternal", null);
        }

        Log.d("test", sharedPreferences.getString("prefChoose", null));

        initNumberPad();
        doorState = (DoorState) findViewById(R.id.doorstate);
        try {
            initWebSocket();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void initWebSocket() throws Exception{
        URI myURI = new URI(wsuri);
        wc = new WebSocketClientOD(myURI);

        String STOREPASSWORD = "trustme";
        KeyStore trusted = KeyStore.getInstance("BKS");
        trusted.load( getApplication().getResources().openRawResource(R.raw.truststore), STOREPASSWORD.toCharArray() );

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init( trusted );

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init( null, tmf.getTrustManagers(), null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
        SSLSocketFactory factory = sslContext.getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();


        wc.setSocket( factory.createSocket() );
        wc.connect();
    }

    private void initNumberPad(){
        final TextView eText = (TextView) findViewById(R.id.text);

        final int[] numberIds = {R.id.numbercircle1, R.id.numbercircle2, R.id.numbercircle3, R.id.numbercircle4,
                                R.id.numbercircle5, R.id.numbercircle6, R.id.numbercircle7, R.id.numbercircle8,
                                R.id.numbercircle9};
        for (int i = 0; i< numberIds.length; i++){
            ImageView button = (ImageView) findViewById(numberIds[i]);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int number = finalI +1;
                    eText.append("" + number);
                }
            });
        }
        final ImageView delete = (ImageView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = eText.getText().toString();
                str = str.substring ( 0, str.length() - 1 );
                eText.setText ( str );
            }
        });

        final ImageView launch = (ImageView) findViewById(R.id.numbercircle);
        launch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                pin = eText.getText().toString();
                if (wc.isOpen()) {
                    wc.send("hello");
                } else {
                    try {
                        initWebSocket();
                        wc.send("hello");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        });
        launch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PrefActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    class WebSocketClientOD extends WebSocketClient {

        public WebSocketClientOD( URI serverUri ) {
            super( serverUri );
        }

        @Override
        public void onOpen( ServerHandshake handshakedata ) {
            Log.w("Open", "Connected");

        }

        @Override
        public void onMessage( String message ) {
            Log.w("got", "Message : " + message);

            if (message.contains("alea")) {
                String[] data = message.split(" ");
                this.send(data[1] + " open " + pin);
            }else if (message.contains("door")){
                final String[] data = message.split(" ");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(data[1].equals("1")){
                            doorState.open();
                        }else{
                            doorState.close();
                        }

                    }
                });

            }
        }

        @Override
        public void onClose( int code, String reason, boolean remote ) {
            Log.w("Close", "DISConnected");

        }

        @Override
        public void onError( final Exception ex ) {
            ex.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }


}
