package org.md2k.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

public class FragmentSettings extends PreferenceFragment {
    private static final String TAG = FragmentSettings.class.getSimpleName();
    Context context;
    Applications applications;

    @Override
    public void onResume() {
        this.setPreferenceScreen(loadPreferenceScreen());
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        applications = Applications.getInstance(context);
    }

    ArrayList<App> appList;

    private PreferenceScreen loadPreferenceScreen() {
        Log.d(TAG, "loadPreferenceScreen()");
        appList = loadAppList();
        ArrayList<String> types = applications.getTypes(appList);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this.getActivity());
        screen.removeAll();
        for (int t = 0; t < types.size(); t++) {
            Log.d(TAG, types.get(t));
            PreferenceCategory category = new PreferenceCategory(context);
            category.setTitle(types.get(t));
            screen.addPreference(category);
            for (int i = 0; i < appList.size(); i++) {
                if (!appList.get(i).getType().equals(types.get(t))) continue;
                Preference preference = new Preference(context);
                preference.setTitle(appList.get(i).getName() + " Settings");
                final int finalI = i;
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        Log.d(TAG, "Settings=" + appList.get(finalI).getSettings());
                        intent.setClassName(appList.get(finalI).getPackagename(), appList.get(finalI).getSettings());
                        startActivity(intent);
                        return false;
                    }
                });
                category.addPreference(preference);
            }
        }
        return screen;
    }

    private ArrayList<App> loadAppList() {
        ArrayList<App> appList = applications.getApps();
        Log.d(TAG, "appList=" + appList.size());
        appList = applications.filterApplication(appList, Applications.PACKAGENAME);
        Log.d(TAG, "appList=" + appList.size());

        appList = applications.filterApplication(appList, Applications.SETTINGS);
        Log.d(TAG, "appList=" + appList.size());
        appList = applications.filterApplication(appList,Applications.INSTALLED);

        return appList;
    }
}
