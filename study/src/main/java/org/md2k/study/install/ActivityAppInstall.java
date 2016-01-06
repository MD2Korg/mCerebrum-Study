package org.md2k.study.install;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.md2k.study.PrefsFragmentSystemHealth;
import org.md2k.study.R;
import org.md2k.study.systemhealth.SystemHealthManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.datakit.DataKitHandler;

public class ActivityAppInstall extends AppCompatActivity {
    public static final String TAG = ActivityAppInstall.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_install);
        getFragmentManager().beginTransaction().replace(R.id.layout_preference_fragment,
                new PrefsFragmentAppInstall()).commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        super.onDestroy();
    }
}
