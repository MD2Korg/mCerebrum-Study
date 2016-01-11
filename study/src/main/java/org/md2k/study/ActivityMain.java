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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.study.admin.ActivitySettings;
import org.md2k.study.admin.AdminManager;
import org.md2k.study.user.application.AppAdapter;
import org.md2k.study.user.application.ShowApps;
import org.md2k.study.user.application.interventionapp.ActivityInterventionApp;
import org.md2k.study.user.service.ActivityService;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.datakit.DataKitHandler;

public class ActivityMain extends AppCompatActivity {
    public static final String TAG = ActivityMain.class.getSimpleName();
    DataKitHandler dataKitHandler;
    ShowApps showApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showApps=ShowApps.getInstance(ActivityMain.this);
        dataKitHandler=DataKitHandler.getInstance(getBaseContext());
        Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("system_health"));

        if(!dataKitHandler.isConnected()) {
            dataKitHandler.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    AdminManager.getInstance(getBaseContext()).readFromDB();
                    setupApplications();
                }
            });
        }
        else{
            setupApplications();
        }
        super.onResume();
    }
    void updateSystemHealth(Status status){
        ImageView imageViewOk=(ImageView) findViewById(R.id.imageViewOk);
        ImageView imageViewWarning=(ImageView) findViewById(R.id.imageViewWarning);
        ImageView imageViewError=(ImageView) findViewById(R.id.imageViewError);
        Button buttonFix=(Button)findViewById(R.id.button_fix);
        TextView textViewMessage = (TextView) findViewById(R.id.textView_message);
        switch(status.getStatusCode()) {
            case Status.SUCCESS:
                imageViewOk.setImageResource(R.drawable.ic_ok_teal_50dp);
                imageViewWarning.setImageResource(R.drawable.ic_warning_grey_50dp);
                imageViewError.setImageResource(R.drawable.ic_error_grey_50dp);
                buttonFix.setVisibility(View.INVISIBLE);
                textViewMessage.setText(status.getStatusMessage());
                textViewMessage.setTextColor(ContextCompat.getColor(this,R.color.teal_700));
                break;
            case Status.APP_NOT_INSTALLED:
            case Status.SLEEPSTART_NOT_DEFINED:
            case Status.SLEEPEND_NOT_DEFINED:
            case Status.USERID_NOT_DEFINED:
            case Status.APP_CONFIG_ERROR:
                imageViewOk.setImageResource(R.drawable.ic_ok_grey_50dp);
                imageViewWarning.setImageResource(R.drawable.ic_warning_grey_50dp);
                imageViewError.setImageResource(R.drawable.ic_error_red_50dp);
                buttonFix.setVisibility(View.VISIBLE);
                buttonFix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPasswordDialog();
                    }
                });
                textViewMessage.setText(status.getStatusMessage());
                textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.red_700));
                break;
            case Status.APP_NOT_RUNNING:
                imageViewOk.setImageResource(R.drawable.ic_ok_grey_50dp);
                imageViewWarning.setImageResource(R.drawable.ic_warning_grey_50dp);
                imageViewError.setImageResource(R.drawable.ic_error_red_50dp);
                buttonFix.setVisibility(View.VISIBLE);
                buttonFix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ActivityMain.this, ActivityService.class);
                        startActivity(intent);
                    }
                });
                textViewMessage.setText(status.getStatusMessage());
                textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.red_700));
                break;
            default:
            imageViewOk.setImageResource(R.drawable.ic_ok_grey_50dp);
            imageViewWarning.setImageResource(R.drawable.ic_warning_amber_50dp);
            imageViewError.setImageResource(R.drawable.ic_error_grey_50dp);
            buttonFix.setVisibility(View.VISIBLE);
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
    public void showPasswordDialog()
    {
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

    void setupApplications() {
        GridView gridview = (GridView) findViewById(R.id.gridview);

        AppAdapter appAdapter = new AppAdapter(ActivityMain.this, showApps.getAllItemObject());
        gridview.setAdapter(appAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(showApps.getApp(position).getPackage_name()!=null){
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(showApps.getApp(position).getPackage_name());
                    startActivity(LaunchIntent);
                }else if(showApps.getApp(position).getClass_name()!=null){
                    try {
                        Class<?> c=Class.forName(showApps.getApp(position).getClass_name());
                        Intent intent=new Intent(ActivityMain.this,c);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
/*                if (showApps.getApp(position).getContent().equals("Intervention")) {
                    Intent launchIntent = new Intent(getApplicationContext(), ActivityInterventionApp.class);
                    startActivity(launchIntent);
                } else if (showApps.getApp(position).getContent().equals("Privacy Control")) {
                    Intent intent = new Intent();
                    intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityPrivacy");
                    startActivity(intent);
                } else if (showApps.getApp(position).getContent().equals("Report")) {

                } else if (showApps.getApp(position).getContent().equals("Report")) {
//                    Intent intent = new Intent();
//                    intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityPrivacy");
//                    startActivity(intent);

                }
*/            }
        });
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Status status= (Status) intent.getSerializableExtra("status");
            Log.d(TAG, "received..."+status.getStatusMessage());
            updateSystemHealth(status);
        }
    };
}
