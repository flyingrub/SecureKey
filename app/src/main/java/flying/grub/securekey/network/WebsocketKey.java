package flying.grub.securekey.network;

import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import flying.grub.securekey.R;
import flying.grub.securekey.activity.MainActivity;
import flying.grub.securekey.view.DoorState;

/**
 * Created by fly on 9/10/15.
 */
public class WebsocketKey extends WebSocketClient {

    private DoorStateChanger changer;
    private static final String STOREPASSWORD = "trustme";

    public WebsocketKey(URI serverUri, DoorStateChanger changer, Context context) throws Exception {
        super(serverUri);

        KeyStore trusted = KeyStore.getInstance("BKS");
        trusted.load(context.getResources().openRawResource(R.raw.truststore), STOREPASSWORD.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trusted);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init( null, tmf.getTrustManagers(), null );
        SSLSocketFactory factory = sslContext.getSocketFactory();

        this.setSocket(factory.createSocket());
        this.changer = changer;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.w("Open", "Connected");
        changer.connected();
    }

    @Override
    public void onMessage(String message) {
        Log.w("got", "Message : " + message);

        if (message.contains("alea")) {
            String[] data = message.split(" ");
            this.send(data[1] + " open " + MainActivity.getPin());
        } else if (message.contains("door")) {
            final String[] data = message.split(" ");
            if(data[1].equals("1")) {
                changer.opened();
            } else {
                changer.closed();
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.w("Close", "DISConnected");
        changer.disconnected();
    }

    @Override
    public void onError(Exception e) {
        changer.disconnected();
    }

}