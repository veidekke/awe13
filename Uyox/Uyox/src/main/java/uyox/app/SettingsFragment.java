package uyox.app;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import org.teleal.cling.model.meta.Device;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        initContentDirectoryPref();
        initPlayDevicesPref();
    }

    private CharSequence[] getEntries(String serviceType){
        ArrayList<Device> properDevices = ContentDirectoryBrowser.getProperDevices(serviceType);
        CharSequence[] entries = new CharSequence[properDevices.size()];
        for(int i = 0; i < properDevices.size(); i++){
            entries[i] = properDevices.get(i).getDisplayString();
        }
        return entries;
    }

    private void initPlayDevicesPref(){
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("play_devices");
        CharSequence[] entries = getEntries("AVTransport");
        for(CharSequence entry : entries){
            CheckBoxPreference pref = new CheckBoxPreference(getActivity());
            pref.setTitle(entry);
            pref.setKey(entry.toString());
            pref.setDefaultValue(false);
            preferenceCategory.addPreference(pref);
        }
    }

    private void initContentDirectoryPref(){
        ListPreference listPref = (ListPreference) findPreference("content_directory");
        CharSequence[] entries = getEntries("ContentDirectory");
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