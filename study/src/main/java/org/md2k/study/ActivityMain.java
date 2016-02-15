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
import android.text.InputType;
import android.view.Gravity;
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

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.study.config.StudyConfigManager;
import org.md2k.study.default_config.DefaultConfigManager;
import org.md2k.study.operation.OperationManager;
import org.md2k.study.operation.user_app.UserApp;
import org.md2k.study.view.service.ActivityService;
import org.md2k.study.view.user.AppAdapter;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.data_format.DATA_QUALITY;

public class ActivityMain extends AppCompatActivity {
    public static final String TAG = ActivityMain.class.getSimpleName();
    public static final int RIP=0;
    public static final int ECG=1;
    public static final int ASWRIST=2;
    public static final int MSBAND=3;
    OperationManager operationManager;
    int lastStatus;
    int dataQuality[]=new int[4];
    String dataQualityName[]={"RIP","ECG","AS-WRIST","MSBAND"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DefaultConfigManager.prepareConfig(getApplicationContext())) {
            Toast.makeText(ActivityMain.this, "Configuration failure...", Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("system_health"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverDataQuality,
                new IntentFilter("data_quality"));

        StudyConfigManager.getInstance(getApplicationContext());
        operationManager = OperationManager.getInstance(getApplicationContext());
        operationManager.connect();
    }

    @Override
    protected void onResume() {
        StudyConfigManager.getInstance(getApplicationContext());
        showApplication();
        Status status=operationManager.getStatus();
        lastStatus=status.getStatusCode();
        updateStatus(operationManager.getStatus());
        super.onResume();
    }

    void updateStatus(Status status) {
        TextView textView_status = (TextView) findViewById(R.id.textView_status);
        Button button = (Button) findViewById(R.id.button_status);
        int imgResource;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_health);


        switch (status.getStatusCode()) {
            case Status.SUCCESS:
                linearLayout.setBackground(ContextCompat.getDrawable(this, R.color.teal_50));
                textView_status.setText(status.getStatusMessage());
                textView_status.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
                imgResource = R.drawable.ic_ok_teal_50dp;
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_teal));
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0);
                button.setText("OK");
                button.setOnClickListener(null);
                lastStatus=Status.SUCCESS;
                break;
            case Status.APP_NOT_INSTALLED:
            case Status.SLEEPSTART_NOT_DEFINED:
            case Status.SLEEPEND_NOT_DEFINED:
            case Status.USERID_NOT_DEFINED:
            case Status.APP_CONFIG_ERROR:
            case Status.CONFIG_FILE_NOT_EXIST:
            case Status.CLEAR_OLD_DATA:
            case Status.DATAKIT_NOT_AVAILABLE:

            case Status.DATAQUALITY_BAD:
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
        Log.d(TAG, "onStop()...");
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
        Intent intent;
        switch (item.getItemId()) {
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
        alertDialog.setTitle("PASSWORD (Admin Access)");
        alertDialog.setMessage("Enter Password");
        final EditText input = new EditText(ActivityMain.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
                            Intent intent = new Intent(ActivityMain.this, ActivityAdmin.class);
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
//        alertDialog.show();
        AlertDialog dialog = alertDialog.show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }

    boolean isPrivacyControlActive() {
        if (operationManager.privacyControlManager.getStatus().getStatusCode() == Status.SUCCESS) {
            Log.d(TAG, "isPrivacyControlActive()...false");
            return false;
        } else {
            Log.d(TAG, "isPrivacyControlActive()...true");
            return true;
        }
    }

    void showApplication() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        UserApp userApp = operationManager.userApps.find("privacy");
        if (userApp != null) {
            userApp.setIcon(isPrivacyControlActive());
        }

        AppAdapter appAdapter = new AppAdapter(ActivityMain.this, operationManager.userApps.getApp());
        gridview.setAdapter(appAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!operationManager.isStudySetupValid()) {
                    Toast.makeText(ActivityMain.this, "Please configure the study first...", Toast.LENGTH_SHORT).show();
                } else {
                    String packageName = operationManager.userApps.getApp(position).getPackage_name();
                    String className = operationManager.userApps.getApp(position).getClass_name();
                    if (packageName != null && className != null) {
                        Intent intent = new Intent();
                        intent.setClassName(packageName, className);
                        startActivity(intent);
                    } else if (packageName != null) {
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                        startActivity(LaunchIntent);
                    } else if (className != null) {
                        try {
                            Class<?> c = Class.forName(className);
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
            Status status = (Status) intent.getParcelableExtra("status");
            Log.d(TAG, "received..." + status.getStatusMessage());
            lastStatus=status.getStatusCode();
            updateStatus(status);
        }
    };
    private void updateDataQuality(ImageView imageView, int status){
        switch(status){
            case DATA_QUALITY.GOOD:
                imageView.setImageResource(R.drawable.ic_ok_teal_50dp);
                break;
            case DATA_QUALITY.BAND_OFF:
                imageView.setImageResource(R.drawable.ic_error_red_50dp);
                break;
            case DATA_QUALITY.NOT_WORN:
            case DATA_QUALITY.BAND_LOOSE:
            case DATA_QUALITY.NOISE:
                imageView.setImageResource(R.drawable.ic_warning_amber_50dp);
                break;
        }
        updateStatusDataQuality();
    }
    public void updateStatusDataQuality(){
        String msg="";
        int flag=0;
        if(lastStatus!=Status.SUCCESS) return;
        for(int i=0;i<4;i++) {
            if (dataQuality[i] != DATA_QUALITY.GOOD) {
                flag = 1;
                msg = dataQualityName[i];
                switch (dataQuality[i]) {
                    case DATA_QUALITY.BAND_OFF:
                        msg = msg + " - not connected.";
                        break;
                    case DATA_QUALITY.NOT_WORN:
                        msg = msg + " - not worn.";
                        break;
                    case DATA_QUALITY.BAND_LOOSE:
                        msg = msg + " - band is not worn properly";
                        break;
                    case DATA_QUALITY.NOISE:
                        msg = msg + " - band is not worn properly";
                        break;
                }
                break;
            }
        }

        if(flag==0)
            updateStatus(new Status(Status.SUCCESS));
        else{
            updateStatus(new Status(Status.DATAQUALITY_BAD,msg));
        }


    }
    private BroadcastReceiver mMessageReceiverDataQuality = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive ... DataQuality()...");
            DataSource dataSource = (DataSource) intent.getParcelableExtra("datasource");
            ImageView imageView;
            int sample[]=intent.getIntArrayExtra("sample");
            Log.d(TAG, "platformtype=" + dataSource.getPlatform().getType() + " datasource_type=" + dataSource.getType()+" sample="+sample[0]);
            switch(dataSource.getPlatform().getType()){
                case PlatformType.MICROSOFT_BAND:
                    imageView= (ImageView) findViewById(R.id.imageView_microsoftband);
                    dataQuality[MSBAND]=sample[0];
                    updateDataQuality(imageView,sample[0]);
                    break;
                case PlatformType.AUTOSENSE_CHEST:
                    imageView= (ImageView) findViewById(R.id.imageView_respiration);
                    updateDataQuality(imageView,sample[0]);
                    imageView= (ImageView) findViewById(R.id.imageView_ecg);
                    updateDataQuality(imageView,sample[1]);
                    dataQuality[RIP]=sample[0];
                    dataQuality[ECG]=sample[1];
                    break;
                case PlatformType.AUTOSENSE_WRIST:
                    imageView= (ImageView) findViewById(R.id.imageView_autosense_wrist);
                    updateDataQuality(imageView,sample[0]);
                    dataQuality[ASWRIST]=sample[0];
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverDataQuality);
        operationManager.close();
        super.onDestroy();
    }
}
