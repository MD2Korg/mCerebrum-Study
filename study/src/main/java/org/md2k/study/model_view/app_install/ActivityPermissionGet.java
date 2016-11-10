package org.md2k.study.model_view.app_install;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.md2k.study.Status;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.permission.PermissionInfo;

public class ActivityPermissionGet extends AppCompatActivity {
    private static final String TAG = ActivityPermissionGet.class.getSimpleName();
    private static final int PERMISSION_REQUEST = 5321;
    private String packageName;
    private String permissionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            packageName = getIntent().getStringExtra("package_name");
            permissionName = getIntent().getStringExtra("permission");
//            permissionName = "org.md2k.utilities.permission.ActivityPermission";
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, permissionName));
            startActivityForResult(intent, PERMISSION_REQUEST);
        }catch (Exception e){
            Log.d(TAG,"ActivityPermissionGet()...packagename="+packageName+" permissionName="+permissionName+" ... exception");
            Intent sendIntent=new Intent("permission_data");
            sendIntent.putExtra("result", Status.SUCCESS);
//            sendIntent.putExtra("result", Status.APP_PERMISSION_NOT_APPROVED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
            finish();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result=data.getBooleanExtra(PermissionInfo.INTENT_RESULT, false);
                Intent sendIntent=new Intent("permission_data");
                if(result==false)
                    sendIntent.putExtra("result", Status.APP_PERMISSION_NOT_APPROVED);
                else sendIntent.putExtra("result",Status.SUCCESS);
                Log.d(TAG,"ActivityPermissionGet()...packagename="+packageName+" permissionName="+permissionName+" ... result="+result);
                LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
                finish();
            }
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
