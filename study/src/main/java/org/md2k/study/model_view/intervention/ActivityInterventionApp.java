package org.md2k.study.model_view.intervention;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.md2k.study.R;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.controller.ModelManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;

import java.util.ArrayList;
import java.util.List;

public class ActivityInterventionApp extends AppCompatActivity {
    public static final String TAG = ActivityInterventionApp.class.getSimpleName();
    List<ConfigApp> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention_app);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        readItems();
        AdapterIntervention adapterIntervention = new AdapterIntervention(ActivityInterventionApp.this, items);
        gridview.setAdapter(adapterIntervention);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Log.d(TAG, "item clicked...packageName="+items.get(position).getPackage_name());
                intent.setClassName("org.md2k.ema_scheduler","org.md2k.ema_scheduler.ActivityTest");
                intent.putExtra("package_name", items.get(position).getPackage_name());
                startActivity(intent);

//                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(items.get(position).getPackage_name());
//                startActivity(launchIntent);
//                Toast.makeText(MainActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
            case R.id.action_about:
                intent = new Intent(this, ActivityAbout.class);
                try {
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_CODE, String.valueOf(this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_NAME, this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                break;
            case R.id.action_copyright:
                intent = new Intent(this, ActivityCopyright.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void readItems() {
        ConfigApp app;
        items=new ArrayList<>();
        app=ModelManager.getInstance(this).getConfigManager().getConfig().getApps("moodsurfing");
        if(app!=null)
            items.add(app);
        app=ModelManager.getInstance(this).getConfigManager().getConfig().getApps("thoughtshakeup");
        if(app!=null)
            items.add(app);
        app=ModelManager.getInstance(this).getConfigManager().getConfig().getApps("headspace");
        if(app!=null)
            items.add(app);
    }

}
