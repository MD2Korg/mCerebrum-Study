package org.md2k.study;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

public class ActivityAppList extends PreferenceActivity {
    private static final String TAG = ActivityAppList.class.getSimpleName();
    Applications applications;

    @Override
    public void onResume() {
        this.setPreferenceScreen(loadPreferenceScreen());
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);
        applications = Applications.getInstance(this);
        if(getActionBar()!=null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    ArrayList<Application> applicationList;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    private PreferenceScreen loadPreferenceScreen() {
        Log.d(TAG, "loadPreferenceScreen()");
        applicationList = loadAppList();
        ArrayList<String> types = applications.getTypes(applicationList);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        screen.removeAll();
        for (int t = 0; t < types.size(); t++) {
            Log.d(TAG, types.get(t));
            PreferenceCategory category = new PreferenceCategory(this);
            category.setTitle(types.get(t));

            screen.addPreference(category);
            for (int i = 0; i < applicationList.size(); i++) {
                if (!applicationList.get(i).getType().equals(types.get(t))) continue;
                Preference preference = new Preference(this);
                preference.setTitle(applicationList.get(i).getName() + " Application");
                if (Apps.isPackageInstalled(this, applicationList.get(i).getPackagename())) {
                    preference.setSummary("Installed");
                    preference.setIcon(R.drawable.icon_tick_dark);
                    final int finalI1 = i;
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(applicationList.get(finalI1).getPackagename());
                            startActivity(LaunchIntent);
                            return true;
                        }
                    });
                } else {
                    preference.setSummary("(click to install)");
                    preference.setIcon(R.drawable.icon_cancel_dark);
                    final int finalI = i;
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            if (applicationList.get(finalI).getDownloadlink().startsWith("market")) {
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                                        .setData(Uri.parse(applicationList.get(finalI).getDownloadlink()));
                                startActivity(goToMarket);
                            } else
                                downloadAndInstallApp(applicationList.get(finalI).getPackagename(), applicationList.get(finalI).getDownloadlink());
                            return false;
                        }
                    });
                }
                category.addPreference(preference);
            }
        }
        return screen;
    }

    private ArrayList<Application> loadAppList() {
        return applications.getApplications();
    }

    void downloadAndInstallApp(String packagename, String downloadlink) {
        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(downloadlink);
    }
}
