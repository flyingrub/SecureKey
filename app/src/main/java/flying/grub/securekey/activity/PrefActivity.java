package flying.grub.securekey.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import flying.grub.securekey.BuildConfig;
import flying.grub.securekey.R;

/**
 * Created by fly on 04/03/15.
 */
public class PrefActivity extends PreferenceActivity {

    private final static String TAG = PrefActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference version = findPreference("version");
        version.setSummary(String.format("v%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

        Preference wifi = findPreference("pref_local_wifi");
        wifi.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setWifiLocal();
                return true;
            }
        });

        Preference qrcode = findPreference("qrcode");
        qrcode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getApplicationContext(), QrCodeActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private void setWifiLocal() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        Log.d(TAG, wifiInfo.toString());

        if (wifiInfo.getNetworkId() != -1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("local_wifi_value", wifiInfo.getMacAddress());
            editor.apply();

            Toast.makeText(getApplicationContext(), String.format(getString(R.string.wifi_added), wifiInfo.getSSID()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.not_connected_wifi), Toast.LENGTH_SHORT).show();
        }

    }


}