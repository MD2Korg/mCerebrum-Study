package org.md2k.study.user.application.interventionapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.md2k.study.user.application.UserApp;
import org.md2k.study.R;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;

import java.util.ArrayList;
import java.util.List;

public class ActivityInterventionApp extends AppCompatActivity {
    public static final String TAG = ActivityInterventionApp.class.getSimpleName();
    List<UserApp> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention_app);
        GridView gridview = (GridView) findViewById(R.id.gridview);

        List<UserApp> allItems = getAllItemObject();
        AdapterIntervention adapterIntervention = new AdapterIntervention(ActivityInterventionApp.this, allItems);
        gridview.setAdapter(adapterIntervention);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(items.get(position).getPackage_name());
                startActivity(launchIntent);
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
    private List<UserApp> getAllItemObject() {
        items.add(new UserApp("mood_surfing","Mood Surfing",null, "org.md2k.moodsurfing"));
        items.add(new UserApp("thought_shakeup","Thought Shakeup", null,"org.md2k.thoughtshakeup"));
        items.add(new UserApp("head_space","Head Space",null, "com.getsomeheadspace.android"));
        return items;
    }

}
