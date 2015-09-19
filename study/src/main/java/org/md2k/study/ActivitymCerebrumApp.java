package org.md2k.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;


public class ActivitymCerebrumApp extends ActionBarActivity {
    public static final String TAG = ActivitymCerebrumApp.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_mcerebrumapp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ActivitymCerebrumAppSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        updateAppCount();
        updateServiceCount();
        super.onResume();
    }
    void updateAppCount(){
        int count = 0;
        ArrayList<App> applications = Applications.getInstance(this).apps;
        for (int i = 0; i < applications.size(); i++) {
            if (Apps.isPackageInstalled(this, applications.get(i).getPackagename()))
                count++;
        }
        ((TextView) findViewById(R.id.textView_application_required)).setText(String.valueOf(applications.size()));
        ((TextView) findViewById(R.id.textView_application_installed)).setText(String.valueOf(count));
        if (count == applications.size())
            ((TextView) findViewById(R.id.textView_application_installed)).setText(String.valueOf(count));
        else
            ((TextView) findViewById(R.id.textView_application_installed)).setText(String.valueOf(count));
    }
    void updateServiceCount(){
        int count = 0;
        ArrayList<App> applications = Applications.getInstance(this).apps;
        for (int i = 0; i < applications.size(); i++) {
            if (Apps.isServiceRunning(this, applications.get(i).getService()))
                count++;
        }
        ((TextView) findViewById(R.id.textView_application_required)).setText(String.valueOf(applications.size()));
        ((TextView) findViewById(R.id.textView_application_installed)).setText(String.valueOf(count));
        if (count == applications.size())
            ((TextView) findViewById(R.id.textView_application_installed)).setText(String.valueOf(count));
        else
            ((TextView) findViewById(R.id.textView_application_installed)).setText(String.valueOf(count));
    }

}
