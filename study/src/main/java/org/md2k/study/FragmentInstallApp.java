package org.md2k.study;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

public class FragmentInstallApp extends PreferenceFragment {
    private static final String TAG = FragmentInstallApp.class.getSimpleName();
    Context context;
    Applications applications;

    @Override
    public void onResume(){
        this.setPreferenceScreen(loadPreferenceScreen());
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        applications= Applications.getInstance(context);
    }
    ArrayList<App> appList;
    private PreferenceScreen loadPreferenceScreen(){
        Log.d(TAG, "loadPreferenceScreen()");
        appList=loadAppList();
        ArrayList<String> types=applications.getTypes(appList);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this.getActivity());
        screen.removeAll();
        for (int t = 0; t < types.size(); t++) {
            Log.d(TAG, types.get(t));
            PreferenceCategory category = new PreferenceCategory(context);
            category.setTitle(types.get(t));

            screen.addPreference(category);
            for (int i = 0; i < appList.size(); i++) {
                if(!appList.get(i).getType().equals(types.get(t))) continue;
                Preference preference = new Preference(context);
                preference.setTitle(appList.get(i).getName()+" App");
                if (Apps.isPackageInstalled(context, appList.get(i).getPackagename())) {
                    preference.setSummary("Installed");
                    preference.setIcon(R.drawable.icon_tick_dark);
                } else {
                    preference.setSummary("(click to install)");
                    preference.setIcon(R.drawable.icon_cancel_dark);
                    final int finalI = i;
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            downloadAndInstallApp(appList.get(finalI).getPackagename(), appList.get(finalI).getDownloadlink());
                            return false;
                        }
                    });
                }
                category.addPreference(preference);
            }
        }
        return screen;
    }
    private ArrayList<App> loadAppList(){
        ArrayList<App> appList= applications.getApps();
        Log.d(TAG,"appList="+appList.size());
        appList=applications.filterApplication(appList,Applications.PACKAGENAME);
        Log.d(TAG,"appList="+appList.size());

        appList=applications.filterApplication(appList,Applications.DOWNLOADLINK);
        Log.d(TAG,"appList="+appList.size());
        return appList;
    }

    void downloadAndInstallApp(String packagename,String downloadlink) {
        final DownloadTask downloadTask = new DownloadTask(context);
        downloadTask.execute(downloadlink);
    }
}
