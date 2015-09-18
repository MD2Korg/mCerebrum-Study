package org.md2k.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

public class FragmentService extends PreferenceFragment {
    private static final String TAG = FragmentService.class.getSimpleName();
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
    ArrayList<Applications.Application> appList;

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
                SwitchPreference preference = new SwitchPreference(context);
                if(Apps.isServiceRunning(context, appList.get(i).getService()))
                    preference.setChecked(true);
                else preference.setChecked(false);

                preference.setTitle(appList.get(i).getName() + " Service");
                final int finalI = i;
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean switched = !((SwitchPreference) preference)
                                .isChecked();
                        Intent intent = new Intent();
                        intent.setClassName(appList.get(finalI).getPackagename(), appList.get(finalI).getService());
                        if (switched) {
                            context.startService(intent);
                        } else {
                            context.stopService(intent);
                        }
                        return true;
                    }
                });
                category.addPreference(preference);
            }
        }
        return screen;
    }

    private ArrayList<Applications.Application> loadAppList() {
        ArrayList<Applications.Application> appList = applications.getApplications();
        appList = applications.filterApplication(appList, Applications.PACKAGENAME);
        appList = applications.filterApplication(appList, Applications.SERVICE);
        appList = applications.filterApplication(appList, Applications.INSTALLED);
        return appList;
    }

/*    void setPreferences(){
        SwitchPreference switchPreference;
        for(int i=0;i<apps.applications.size();i++) {
            switchPreference = (SwitchPreference) findPreference(apps.applications.get(i).name);
            final int finalI = i;
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean switched = !((SwitchPreference) preference)
                            .isChecked();
                    Intent intent = new Intent();
                    intent.setClassName(apps.applications.get(finalI).name, apps.applications.get(finalI).service);

                    if (switched) {
                        getActivity().getApplicationContext().startService(intent);
                    } else {
                        getActivity().getApplicationContext().stopService(intent);
                    }
                    return true;
                }
            });
        }
    }
*/
}
