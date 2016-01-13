package org.md2k.study;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.study.admin.ActivitySettings;
import org.md2k.study.admin.AdminManager;
import org.md2k.study.user.UserManager;
import org.md2k.study.user.application.AppAdapter;
import org.md2k.study.user.service.ActivityService;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.datakit.DataKitHandler;

public class ActivityMain extends AppCompatActivity {
    public static final String TAG = ActivityMain.class.getSimpleName();
    DataKitHandler dataKitHandler;
    Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager=Manager.getInstance(ActivityMain.this);
        setContentView(R.layout.activity_main);
        dataKitHandler = DataKitHandler.getInstance(getBaseContext());
        showApplication();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("system_health"));
        if(manager.getStatusAdmin().getStatusCode()==Status.SUCCESS){
            Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
            startService(intent);
        }
        updateStatus(manager.getStatus());
        super.onResume();
    }
    void updateStatus(Status status) {
        TextView textView_status = (TextView) findViewById(R.id.textView_status);
        Button button = (Button) findViewById(R.id.button_status);
        int imgResource;
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.layout_health);


        switch (status.getStatusCode()) {
            case Status.SUCCESS:
                linearLayout.setBackground(ContextCompat.getDrawable(this, R.color.teal_50));
                textView_status.setText(status.getStatusMessage());
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
                imgResource = R.drawable.ic_ok_teal_50dp;
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_teal));
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0);
                button.setText("OK");
                break;
            case Status.APP_NOT_INSTALLED:
            case Status.SLEEPSTART_NOT_DEFINED:
            case Status.SLEEPEND_NOT_DEFINED:
            case Status.USERID_NOT_DEFINED:
            case Status.APP_CONFIG_ERROR:
                linearLayout.setBackground(ContextCompat.getDrawable(this, R.color.red_200));
                textView_status.setText(status.getStatusMessage());
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.red_900));
                imgResource = R.drawable.ic_error_grey_50dp;
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_red));
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0);
                button.setText("FIX");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPasswordDialog();
                    }
                });
                break;
            case Status.APP_NOT_RUNNING:
                linearLayout.setBackground(ContextCompat.getDrawable(this, R.color.red_200));
                textView_status.setText(status.getStatusMessage());
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.red_900));
                imgResource = R.drawable.ic_error_grey_50dp;
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_red));
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0);
                button.setText("FIX");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ActivityMain.this, ActivityService.class);
                        startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
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
            case R.id.action_settings:
                showPasswordDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showPasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.this);
        alertDialog.setTitle("PASSWORD - 1234 (will be removed)");
        alertDialog.setMessage("Enter Password\n\n (or, contact study coordinator)");

        final EditText input = new EditText(ActivityMain.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_id_teal_48dp);

        alertDialog.setPositiveButton("Go",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if (Constants.PASSWORD.equals(password)) {
                            Toast.makeText(getApplicationContext(),
                                    "Password Matched", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Wrong Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    void showApplication() {
        UserManager userManager=manager.getUserManager();
        GridView gridview = (GridView) findViewById(R.id.gridview);

        AppAdapter appAdapter = new AppAdapter(ActivityMain.this, userManager.getUserApps().getAllItemObject());
        gridview.setAdapter(appAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(manager.getStatusAdmin().getStatusCode()!=Status.SUCCESS){
                    Toast.makeText(ActivityMain.this,"Please configure the study first...",Toast.LENGTH_LONG).show();
                }else {
                    UserManager userManager=manager.getUserManager();
                    if (userManager.getUserApps().getApp(position).getPackage_name() != null) {
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(userManager.getUserApps().getApp(position).getPackage_name());
                        startActivity(LaunchIntent);
                    } else if (userManager.getUserApps().getApp(position).getClass_name() != null) {
                        try {
                            Class<?> c = Class.forName(userManager.getUserApps().getApp(position).getClass_name());
                            Intent intent = new Intent(ActivityMain.this, c);
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Status status = (Status) intent.getSerializableExtra("status");
            Log.d(TAG, "received..." + status.getStatusMessage());
            updateStatus(status);
        }
    };
}
