package flying.grub.securekey.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import flying.grub.securekey.R;
import flying.grub.securekey.network.DoorStateChanger;
import flying.grub.securekey.network.WebsocketKey;
import flying.grub.securekey.view.DoorState;


public class MainActivity extends AppCompatActivity {

    private static String pin;
    private String wsuri;
    private WebsocketKey wc;
    private DoorStateChanger changer;
    private DoorState doorState;

    private final static String TAG = PrefActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNumberPad();

        doorState = (DoorState) findViewById(R.id.doorstate);
        changer = new DoorStateChanger() {
            @Override
            public void closed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doorState.close();
                    }
                });
            }

            @Override
            public void disconnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doorState.setConnected(false);
                    }
                });
            }

            @Override
            public void opened() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doorState.open();
                    }
                });
            }

            @Override
            public void connected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doorState.setConnected(true);
                    }
                });
            }
        };
    }

    public static String getPin() {
        return pin;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String mac_local = sharedPreferences.getString("local_wifi_value", "");

        Log.d(TAG, wifiInfo.toString());
        Log.d(TAG, mac_local);
        if (wifiInfo.getNetworkId() != -1 && mac_local.equals(wifiInfo.getMacAddress())) { // if we are on the trusted wifi.
            Log.d(TAG, "Local IP");
            wsuri = "wss://"+sharedPreferences.getString("pref_local_ip", "");
        } else {
            Log.d(TAG, "External IP");
            wsuri = "wss://"+sharedPreferences.getString("pref_external_ip", "");
        }
        Log.i("TAG", "" + wsuri);

        try {
            initWebSocket();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if (wc != null) {
            wc.close();
        }
    }
    private void initWebSocket() throws Exception {
        URI myURI = new URI(wsuri);
        wc = new WebsocketKey(myURI, changer, getBaseContext());
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
                if (str.length() > 0){
                    str = str.substring ( 0, str.length() - 1 );
                    eText.setText ( str );
                }
            }
        });

        final DoorState launch = (DoorState) findViewById(R.id.doorstate);
        launch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                pin = eText.getText().toString();
                if (wc != null && wc.isOpen()) {
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
}
