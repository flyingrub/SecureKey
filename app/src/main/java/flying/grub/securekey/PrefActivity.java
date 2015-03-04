package flying.grub.securekey;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * Created by fly on 04/03/15.
 */
public class PrefActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference localornot = (ListPreference) findPreference ("prefChoose");
        localornot.setValueIndex(0);
    }
}