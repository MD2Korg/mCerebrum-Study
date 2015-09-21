package org.md2k.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

public class ActivitymCerebrumApp extends Activity {
    public static final String TAG = ActivitymCerebrumApp.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_mcerebrumapp);
        if(getActionBar()!=null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

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
        Intent intent;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                break;
            case R.id.action_applications:
                intent = new Intent(this, ActivityAppList.class);
                startActivity(intent);
                break;
            case R.id.action_services:
                intent = new Intent(this, ActivityServiceList.class);
                startActivity(intent);
                break;

            case R.id.action_about:
                intent = new Intent(this, ActivityAbout.class);
                startActivity(intent);
                break;
            case R.id.action_copyright:
                intent = new Intent(this, ActivityCopyright.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        UI.createOverviewUI(this);
        UI.updateAppCount(this);
        UI.updateServiceCount(this);
        UI.createDeviceUI(this);
        UI.updateDevice(this);
        super.onResume();
    }



    void updateServiceCount() {
        int count = 0;
        ArrayList<Application> applications = Applications.getInstance(this).applications;
        applications = Applications.getInstance(this).filterApplication(applications, Applications.SERVICE);
        for (int i = 0; i < applications.size(); i++) {
            if (Apps.isServiceRunning(this, applications.get(i).getService()))
                count++;
        }
        ((TextView) findViewById(R.id.textView_service_running)).setText(String.valueOf(count)+" (out of "+String.valueOf(applications.size()+")"));
        if (count == applications.size()) {
            ((ImageView) findViewById(R.id.imageView_service_status)).setImageDrawable(getResources().getDrawable(R.drawable.ok));
            findViewById(R.id.button_service_fix).setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) findViewById(R.id.imageView_service_status)).setImageDrawable(getResources().getDrawable(R.drawable.error));
            findViewById(R.id.button_service_fix).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
