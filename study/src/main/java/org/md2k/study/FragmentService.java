package org.md2k.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    public void onPause() {
        mHandler.removeCallbacks(runnable);
        super.onPause();

    }

    @Override
    public void onResume() {
        setPreferenceScreen(loadPreferenceScreen());
        mHandler.post(runnable);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        applications = Applications.getInstance(context);
        appList = loadAppList();
    }

    ArrayList<App> appList;

    private PreferenceScreen loadPreferenceScreen() {
        Log.d(TAG, "loadPreferenceScreen()");
        ArrayList<String> types = applications.getTypes(appList);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this.getActivity());
        screen.removeAll();
        listPreference.clear();
        for (int t = 0; t < types.size(); t++) {
            Log.d(TAG, types.get(t));
            PreferenceCategory category = new PreferenceCategory(context);
            category.setTitle(types.get(t));
            screen.addPreference(category);
            for (int i = 0; i < appList.size(); i++) {
                if (!appList.get(i).getType().equals(types.get(t))) continue;
                SwitchPreference preference = new SwitchPreference(context);

                preference.setTitle(appList.get(i).getName() + " Service");
                preference.setKey(appList.get(i).getService());
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
                listPreference.add(preference);

            }
        }
        return screen;
    }

    ArrayList<SwitchPreference> listPreference = new ArrayList<>();

    void updatePreferenceSummary() {
        long time;
        SwitchPreference switchPreference;
        for (int i = 0; i < listPreference.size(); i++) {
            switchPreference = listPreference.get(i);
            String serviceName = switchPreference.getKey();
            time = Apps.serviceRunningTime(getActivity(), serviceName);
            if (time < 0) {
                switchPreference.setChecked(false);
                switchPreference.setSummary("Not Running");

            } else {
                switchPreference.setChecked(true);
                long runtime = time / 1000;
                int second = (int) (runtime % 60);
                runtime /= 60;
                int minute = (int) (runtime % 60);
                runtime /= 60;
                int hour = (int) runtime;
                switchPreference.setSummary("Running Time: " + String.format("%02d:%02d:%02d", hour, minute, second));
            }
        }
    }

    private ArrayList<App> loadAppList() {
        ArrayList<App> appList = applications.getApps();
        return appList;
    }

    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            {
                updatePreferenceSummary();
                mHandler.postDelayed(this, 1000);
            }
        }
    };

}
