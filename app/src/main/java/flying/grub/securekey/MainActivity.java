package flying.grub.securekey;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends ActionBarActivity {

    String pin;
    final String wsuri = "wss://192.168.1.99:9000";
    WebSocketClientOD wc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initWebSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Button button = (Button) findViewById(R.id.button);
        final EditText eText = (EditText) findViewById(R.id.text);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                pin = eText.getText().toString();
                if (wc.isOpen()){
                    wc.send("hello");
                }else{
                    wc.connect();
                    wc.send("hello");
                }


            }
        });
    }

    private void initWebSocket() throws Exception{
        URI myURI = null;
        myURI = new URI(wsuri);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            }
        }

        @Override
        public void onClose( int code, String reason, boolean remote ) {
            Log.w("Close", "DISConnected");

        }

        @Override
        public void onError( Exception ex ) {
            ex.printStackTrace();

        }

    }


}
