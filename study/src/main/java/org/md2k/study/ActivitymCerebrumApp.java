package org.md2k.study;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.study.groups.GroupManager;
import org.md2k.study.groups.device.GroupDeviceSettings;
import org.md2k.study.groups.sensorquality.GroupSensorQuality;
import org.md2k.study.groups.service.GroupService;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.datakit.DataKitHandler;

public class ActivitymCerebrumApp extends Activity {
    public static final String TAG = ActivitymCerebrumApp.class.getSimpleName();
    GroupManager groupManager;
    MyExpandableListAdapter adapter = null;
    DataKitHandler dataKitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_systemhealth);
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        groupManager = new GroupManager(this, onDataUpdated);
        Log.d(TAG, "size=" + groupManager.groups.size());
        adapter = new MyExpandableListAdapter(this, groupManager.groups);
        listView.setAdapter(adapter);
    }

    void setupDataKitHandler() {
        dataKitHandler = DataKitHandler.getInstance(this);
        dataKitHandler.connect(new OnConnectionListener() {
            @Override
            public void onConnected() {
                GroupDeviceSettings groupDeviceSettings = (GroupDeviceSettings) groupManager.groups.get(GroupManager.GROUP_DEVICE_SETTINGS);
                groupDeviceSettings.setDataKitHandler(dataKitHandler);
//                GroupSensorQuality groupSensorQuality = (GroupSensorQuality) groupManager.groups.get(GroupManager.GROUP_SENSOR_QUALITY);
//                groupSensorQuality.setDataKitHandler(dataKitHandler);
            }
        });
    }

    void clearDataKitHandler() {
        GroupDeviceSettings groupDeviceSettings = (GroupDeviceSettings) groupManager.groups.get(GroupManager.GROUP_DEVICE_SETTINGS);
        groupDeviceSettings.setDataKitHandler(null);
        GroupSensorQuality groupSensorQuality = (GroupSensorQuality) groupManager.groups.get(GroupManager.GROUP_SENSOR_QUALITY);
        groupSensorQuality.setDataKitHandler(null);

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

    @Override
    public void onResume() {
        setupDataKitHandler();
        for (int i = 0; i < groupManager.groups.size(); i++) {
            groupManager.groups.get(groupManager.groups.keyAt(i)).refresh();
        }
        adapter.notifyDataSetChanged();
        mHandler.post(runnable);
        super.onResume();
    }


    void createSettingsList(final int position) {
/*        final DataKitHandler dataKitHandler = DataKitHandler.getInstance(this);
        dataKitHandler.connect(new OnConnectionListener() {
            @Override
            public void onConnected() {
                ArrayList<DeviceInfo> deviceInfos = DeviceInfos.getInstance(ActivitymCerebrumApp.this).getDeviceInfos();
                Group group = new Group(position);
                boolean red=false;

                for (int i = 0; i < deviceInfos.size(); i++) {
                    int required = 0, available = 0;
                    String name;
                    if (deviceInfos.get(i).platformtype.equals(PlatformType.AUTOSENSE_CHEST))
                        name = "AutoSense (C)";
                    else if (deviceInfos.get(i).platformtype.equals(PlatformType.MICROSOFT_BAND) && deviceInfos.get(i).location.equals("LEFT_WRIST"))
                        name = "MSBand (L)";
                    else if (deviceInfos.get(i).platformtype.equals(PlatformType.MICROSOFT_BAND) && deviceInfos.get(i).location.equals("RIGHT_WRIST"))
                        name = "MSBand (R)";
                    else if (deviceInfos.get(i).platformtype.equals(PlatformType.PHONE))
                        name = "Phone";
                    else continue;
                    required = deviceInfos.get(i).datasourcetype.size();
                    if (dataKitHandler != null)
                        available = deviceInfos.get(i).findDataSources(dataKitHandler);
                    if (required == available)
                        group.children.add(new Children(Children.TYPE_SETTINGS, name, Group.GREEN));
                    else {
                        group.children.add(new Children(Children.TYPE_SETTINGS, name, Group.RED));
                        red=true;
                    }
                    if(red==true) group.setStatus(Group.RED);
                    groups.append(position, group);
                    adapter.notifyDataSetChanged();
                }
                dataKitHandler.disconnect();
            }
        });
*/
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        super.onDestroy();
    }
    int count=0;

    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            {
                Log.d(TAG, "runnable");
                count++;
                if(count==3){
                    groupManager.groups.get(GroupManager.GROUP_DEVICE_SETTINGS).refresh();
                    groupManager.groups.get(GroupManager.GROUP_SENSOR_QUALITY).refresh();
                    count=0;
                }
                groupManager.groups.get(GroupManager.GROUP_SERVICE).refresh();
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onPause() {
        if (dataKitHandler != null)
            dataKitHandler.disconnect();
        clearDataKitHandler();
        mHandler.removeCallbacks(runnable);
        super.onPause();

    }

    OnDataUpdated onDataUpdated = new OnDataUpdated() {
        @Override
        public void onChange() {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    };
}
