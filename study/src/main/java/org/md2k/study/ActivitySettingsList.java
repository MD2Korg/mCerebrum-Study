package org.md2k.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

public class ActivitySettingsList extends PreferenceActivity {
/*    private static final String TAG = ActivitySettingsList.class.getSimpleName();
    Context context;
    AppInfoList appInfoList;

    @Override
    public void onResume() {
        this.setPreferenceScreen(loadPreferenceScreen());
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        appInfoList = AppInfoList.getInstance(context);
    }

    ArrayList<AppInfo> applicationList;

    private PreferenceScreen loadPreferenceScreen() {
        Log.d(TAG, "loadPreferenceScreen()");
        applicationList = loadAppList();
        ArrayList<String> types = appInfoList.getTypes(applicationList);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        screen.removeAll();
        for (int t = 0; t < types.size(); t++) {
            Log.d(TAG, types.get(t));
            PreferenceCategory category = new PreferenceCategory(context);
            category.setTitle(types.get(t));
            screen.addPreference(category);
            for (int i = 0; i < applicationList.size(); i++) {
                if (!applicationList.get(i).getType().equals(types.get(t))) continue;
                Preference preference = new Preference(context);
                preference.setTitle(applicationList.get(i).getName() + " Settings");
                final int finalI = i;
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        Log.d(TAG, "Settings=" + applicationList.get(finalI).getSettings());
                        intent.setClassName(applicationList.get(finalI).getPackage_name(), applicationList.get(finalI).getSettings());
                        startActivity(intent);
                        return false;
                    }
                });
                category.addPreference(preference);
            }
        }
        return screen;
    }

    private ArrayList<AppInfo> loadAppList() {
        ArrayList<AppInfo> applicationList = appInfoList.getAppInfoList();

        applicationList = appInfoList.filterApplication(applicationList, AppInfo.SETTINGS);
        applicationList = appInfoList.filterApplication(applicationList, AppInfo.INSTALLED);

        return applicationList;
    }
    */
}
