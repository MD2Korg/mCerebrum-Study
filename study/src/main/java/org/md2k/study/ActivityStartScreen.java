package org.md2k.study;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.app_install.AppInstallManager;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.FileManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.AlertDialogs;

import io.fabric.sdk.android.Fabric;

public class ActivityStartScreen extends AppCompatActivity {
    private static final String TAG = ActivityStartScreen.class.getSimpleName();
    int state;
    private static final int UI = 0;
    private static final int START = 1;
    private static final int SETTINGS = 2;
    Handler handler;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()...");
        handler = new Handler();
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_start_screen);
        Log.d(TAG, "...onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()...");
        handler.removeCallbacks(runnableWaitServiceStart);
        fixConfigIfRequired();
        setButtonExit();
        setButtonStartStudy();
        setButtonUpdatemCerebrum();
        setImageViewLogo();
        setButtonSettings();
        updateTextConfigFileName();
        updateTextVersion();
        Log.d(TAG,"isServiceRunning="+ServiceSystemHealth.isRunning);

        if (!ServiceSystemHealth.isRunning) {
            state=UI;
            startService(Status.RANK_ADMIN_OPTIONAL);
        } else {
            if (ModelManager.getInstance(this).getStatus().getRank() > Status.RANK_ADMIN_OPTIONAL) {
                state=UI;
                handler.post(runnableWaitServiceStart);
            }
            else {
                if (ServiceSystemHealth.RANK_LIMIT == Status.RANK_SUCCESS) {
                    state=START;
                    handler.post(runnableWaitServiceStart);
                } else {
                    state=UI;
                    handler.post(runnableWaitServiceStart);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()...");
        handler.removeCallbacks(runnableWaitServiceStart);
        if (ServiceSystemHealth.isRunning) {
            if (ModelManager.getInstance(this).getStatus().getRank() >= Status.RANK_ADMIN_OPTIONAL)
                stopService();
        }
        super.onDestroy();
    }

    Runnable runnableWaitServiceStart = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableWaitServiceStart...isServiceRunning=" + ServiceSystemHealth.isRunning);
            if (!ServiceSystemHealth.isRunning)
                handler.postDelayed(this, 2000);
            else {
                Log.d(TAG,"isUpdating="+ModelManager.getInstance(ActivityStartScreen.this).isUpdating());
                if (ModelManager.getInstance(ActivityStartScreen.this).isUpdating())
                    handler.postDelayed(this, 500);
                else {
                    if(mProgressDialog!=null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    doWork();
                }
            }
        }
    };
    void doWork(){
        Log.d(TAG,"doWork...state="+state);
        if(state==UI){
            Status status = ModelManager.getInstance(ActivityStartScreen.this).getStatus();
            Log.d(TAG,"doWork...state="+state+" status="+status.log());
            if (status.getRank() > Status.RANK_ADMIN_OPTIONAL) {
                disableButton((Button) findViewById(R.id.button_start_study));
                activeButton((Button) findViewById(R.id.button_settings));
            } else {
                activeButton((Button) findViewById(R.id.button_start_study));
                enableButton((Button) findViewById(R.id.button_settings));
            }
            stopService();
        }
        else if (state==START){
            startActivityMain();
        }else if(state==SETTINGS){
            startActivitySettings();
        }

    }

    void disableButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_teal));
        button.setEnabled(false);
        button.setTextColor(Color.GRAY);
    }

    void enableButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_teal));
        button.setEnabled(true);
        button.setTextColor(Color.BLACK);
    }

    void activeButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_red));
        button.setEnabled(true);
        button.setTextColor(Color.WHITE);

    }

    void updateTextVersion() {
        try {
            ((TextView) findViewById(R.id.text_view_version)).setMovementMethod(LinkMovementMethod.getInstance());
            String version = "Version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.text_view_version)).setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void updateTextConfigFileName() {
        try {
            if (ModelManager.getInstance(this).getConfigManager().getConfig().getStart_screen().isConfig_text()) {
                findViewById(R.id.text_view_config).setVisibility(View.VISIBLE);
                String configName = ModelManager.getInstance(this).getConfigManager().getConfig().getConfig_info().getFilename();
                ((TextView) findViewById(R.id.text_view_config)).setText("Config File: " + configName);
            } else {
                findViewById(R.id.text_view_config).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            ((TextView) findViewById(R.id.text_view_config)).setText("");
        }
    }

    void setImageViewLogo() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView_logo);
        if (ModelManager.getInstance(this).getConfigManager().getConfig().getStart_screen().isMd2k_logo()) {
            imageView.setVisibility(View.VISIBLE);
            if (ModelManager.getInstance(this).getConfigManager().getConfig().getStart_screen().isMd2k_link()) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("http://md2k.org"));
                        startActivity(intent);
                    }
                });
            } else {
                imageView.setOnClickListener(null);
            }
        } else imageView.setVisibility(View.INVISIBLE);
    }

    void setButtonExit() {
        Button button = (Button) findViewById(R.id.button_exit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "datakit is connected=" + DataKitAPI.getInstance(ActivityStartScreen.this).isConnected());
                finish();
            }
        });
    }

    void setButtonStartStudy() {
        Button button = (Button) findViewById(R.id.button_start_study);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "runnableWaitStart...isServiceRunning=" + ServiceSystemHealth.isRunning + " Service_Rank=" + ServiceSystemHealth.RANK_LIMIT);
                state=START;
                startService(Status.RANK_SUCCESS);
            }
        });
    }

    void setButtonUpdatemCerebrum() {
        Button button = (Button) findViewById(R.id.button_update);
        if (ModelManager.getInstance(this).getConfigManager().getConfig().getStart_screen().isUpdate_app()) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AppInstallManager appInstallManager = (AppInstallManager) ModelManager.getInstance(ActivityStartScreen.this).getModel(ModelFactory.MODEL_APP_INSTALL);
                    appInstallManager.getAppInstallList("study").setLatestVersionName(ActivityStartScreen.this, new OnDataChangeListener() {
                        @Override
                        public void onDataChange(String str) {
                            if (!appInstallManager.getAppInstallList("study").isUpdateAvailable())
                                Toast.makeText(ActivityStartScreen.this, "mCerebrum is up-to-date...", Toast.LENGTH_SHORT).show();
                            else {
                                AlertDialogs.AlertDialog(ActivityStartScreen.this, "Update Available(" + str + ")", "Do you want to update now?",R.drawable.ic_info_teal_48dp, "Yes", "No",null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            appInstallManager.getAppInstallList().get(0).downloadAndInstallApp(ActivityStartScreen.this);
                                        }
                                        finish();
                                    }
                                });

                            }

                        }
                    });
                }
            });
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

    void setButtonSettings() {
        Button button = (Button) findViewById(R.id.button_settings);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state=SETTINGS;
                startService(Status.RANK_ADMIN_OPTIONAL);
            }
        });
    }

    void fixConfigIfRequired() {
        try {
            if (!ModelManager.getInstance(this).getConfigManager().isValid()) {
                stopService();
                copyDefaultConfig();
                readConfig();
            }
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }

    void readConfig() throws DataKitException {
        ModelManager.getInstance(this).clear();
        ModelManager.getInstance(this).read();
    }

    void copyDefaultConfig() {
        FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
        FileManager.copyAssets(ActivityStartScreen.this, "default.zip", Constants.TEMP_DIRECTORY);
        FileManager.unzip(Constants.TEMP_DIRECTORY + "default.zip", Constants.CONFIG_DIRECTORY_ROOT);
    }

    void startService(int rank) {
        Log.d(TAG,"startService...rank="+rank);
        ServiceSystemHealth.RANK_LIMIT = rank;
        Intent intent = new Intent(ActivityStartScreen.this, ServiceSystemHealth.class);
        mProgressDialog = ProgressDialog.show(ActivityStartScreen.this, "Please wait ...", "Loading ...", true);
        handler.post(runnableWaitServiceStart);
        startService(intent);
    }

    void stopService() {
        Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
        stopService(intent);
    }

    void startActivityMain() {
        Intent intent = new Intent(ActivityStartScreen.this, ActivityMain.class);
        startActivity(intent);
    }
    void startActivitySettings() {
        Intent intent = new Intent(ActivityStartScreen.this, ActivityAdmin.class);
        startActivity(intent);
    }
}
