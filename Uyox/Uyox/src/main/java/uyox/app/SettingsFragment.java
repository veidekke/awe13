package uyox.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.teleal.cling.model.meta.Device;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPref = (ListPreference) findPreference("content_directory");
        ArrayList<Device> properDevices = ContentDirectoryBrowser.getProperDevices("ContentDirectory");
        CharSequence[] entries = new CharSequence[properDevices.size()];
        for(int i = 0; i < properDevices.size(); i++){
            entries[i] = properDevices.get(i).getDisplayString();
        }
        listPref.setEntries(entries);
        listPref.setEntryValues(entries);
        listPref.setDefaultValue(entries[0]);
        listPref.setSummary(listPref.getEntry());
        listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary((String) o);
                return true;
            }
        });
    }
}