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

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.cache.MySharedPref;
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
    private static final int START_SCREEN = 0;
    private static final int SETTINGS = 1;
    private static final int START_STUDY = 2;
    private static final int EXIT = 3;
    Handler handler;
    ProgressDialog progressDialog;
    boolean isAlertDialogShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()...");
        handler = new Handler();
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_start_screen);
        Log.d(TAG, "...onCreate()");
        progressDialog = new ProgressDialog(this, android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        isAlertDialogShown = false;
        MySharedPref.getInstance(this).clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()...");
        prepare();
    }

    void prepare() {
        if (isStudyRunning()) {
            state = START_STUDY;
            serviceStarted();
        } else {
            state = START_SCREEN;
            handler.post(runnableStopService);
        }
    }

    boolean isStudyRunning() {
        return ServiceSystemHealth.isRunning && ServiceSystemHealth.RANK_LIMIT == Status.RANK_SUCCESS && (ModelManager.getInstance(this).getStatus().getRank()<=Status.RANK_ADMIN_OPTIONAL);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        handler.removeCallbacks(runnableWaitLoading);
        handler.removeCallbacks(runnableStopService);
        handler.removeCallbacks(runnableStartService);
        state = EXIT;
        handler.post(runnableStopService);
        super.onDestroy();
    }


    void fixConfigIfRequired() {
        if (!ModelManager.getInstance(this).getConfigManager().isExist()) {
            clearModelManager();
            copyDefaultConfig();
            loadModelManager();
        } else if (!ModelManager.getInstance(this).getConfigManager().isValid() && !isAlertDialogShown) {
            isAlertDialogShown=true;
            AlertDialogs.AlertDialog(this, "Configuration file is out of date", "Please download the latest configuration file from \"Settings\".", R.drawable.ic_info_teal_48dp, "Ok", null, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (DialogInterface.BUTTON_POSITIVE == which) {
                        clearModelManager();
                        copyDefaultConfig();
                        loadModelManager();
                        isAlertDialogShown = false;
                    }
                }
            });
        } else
            setUI();
    }

    void clearModelManager() {
        try {
            ModelManager.getInstance(this).clear();
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }

    void showProgressBar() {
        try {
            progressDialog.show();
        }catch (Exception ignored){

        }
    }

    void hideProgressBar() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception ignored){

        }
    }

    void loadModelManager() {
        try {
            Log.d(TAG, "startService...");
            ServiceSystemHealth.RANK_LIMIT = Status.RANK_ADMIN_OPTIONAL;
            ModelManager.getInstance(this).clear();
            ModelManager.getInstance(this).read();
            ModelManager.getInstance(this).set();
            handler.removeCallbacks(runnableWaitLoading);
            handler.postDelayed(runnableWaitLoading, 500);
        } catch (Exception ignored) {

        }
    }

    void copyDefaultConfig() {
        Log.d(TAG, "copyDefaultConfig()...");
        FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
        FileManager.copyAssets(ActivityStartScreen.this, "default.zip", Constants.TEMP_DIRECTORY);
        FileManager.unzip(Constants.TEMP_DIRECTORY + "default.zip", Constants.CONFIG_DIRECTORY_ROOT);
    }

    void setUI() {
        Log.d(TAG,"setUI()...");
        setButtonStartStudy();
        setButtonSettings();
        setButtonUpdatemCerebrum();
        setButtonExit();
        setImageViewLogo();
        setTextConfigFileName();
        setTextVersion();
        Log.d(TAG, "updateUI...state=" + state);
        Status status = ModelManager.getInstance(ActivityStartScreen.this).getStatus();
        Log.d(TAG, "updateUI...state=" + state + " status=" + status.log());
        if (status.getRank() > Status.RANK_ADMIN_OPTIONAL) {
            disableButton((Button) findViewById(R.id.button_start_study));
            activeButton((Button) findViewById(R.id.button_settings));
        } else {
            activeButton((Button) findViewById(R.id.button_start_study));
            enableButton((Button) findViewById(R.id.button_settings));
        }
    }

    void setButtonStartStudy() {
        Button button = (Button) findViewById(R.id.button_start_study);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "runnableWaitStart...isServiceRunning=" + ServiceSystemHealth.isRunning + " Service_Rank=" + ServiceSystemHealth.RANK_LIMIT);
                state = START_STUDY;
                ServiceSystemHealth.RANK_LIMIT = Status.RANK_SUCCESS;
                handler.post(runnableStartService);
            }
        });
    }

    void setButtonSettings() {
        Button button = (Button) findViewById(R.id.button_settings);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = SETTINGS;
                ServiceSystemHealth.RANK_LIMIT = Status.RANK_ADMIN_OPTIONAL;
                handler.post(runnableStartService);
            }
        });
    }
    void checkUpdates(){
        if (ModelManager.getInstance(ActivityStartScreen.this).getConfigManager().getConfig().getConfig_info().isAuto_update()) {
            final AppInstallManager appInstallManager = (AppInstallManager) ModelManager.getInstance(ActivityStartScreen.this).getModel(ModelFactory.MODEL_APP_INSTALL);
            showProgressBar();
            appInstallManager.updateVersionAll(0, new OnDataChangeListener() {
                @Override
                public void onDataChange(int index, String str) {
                    if (appInstallManager.getAppInstallList().size() == index) {
                        int status = Integer.parseInt(str);
                        if (status != Status.SUCCESS)
                            setUI();
                        hideProgressBar();
                    }
                }
            });
        }

    }

    void setButtonUpdatemCerebrum() {
        Button button = (Button) findViewById(R.id.button_update);
        if (ModelManager.getInstance(this).getConfigManager().getConfig().getStart_screen().isUpdate_app()) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AppInstallManager appInstallManager = (AppInstallManager) ModelManager.getInstance(ActivityStartScreen.this).getModel(ModelFactory.MODEL_APP_INSTALL);
                    checkUpdates();
                    appInstallManager.getAppInstallList("study").setLatestVersionName(ActivityStartScreen.this, new OnDataChangeListener() {
                        @Override
                        public void onDataChange(int now, String str) {
                            if (!appInstallManager.getAppInstallList("study").isUpdateAvailable())
                                Toast.makeText(ActivityStartScreen.this, "mCerebrum is up-to-date...", Toast.LENGTH_SHORT).show();
                            else {
                                AlertDialogs.AlertDialog(ActivityStartScreen.this, "Update Available(" + str + ")", "Do you want to update now?", R.drawable.ic_info_teal_48dp, "Yes", "No", null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            appInstallManager.getAppInstallList().get(0).downloadAndInstallApp(ActivityStartScreen.this);
                                        }
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

    void setButtonExit() {
        Button button = (Button) findViewById(R.id.button_exit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = EXIT;
                handler.post(runnableStopService);
                finish();
            }
        });
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

    void setTextVersion() {
        try {
            ((TextView) findViewById(R.id.text_view_version)).setMovementMethod(LinkMovementMethod.getInstance());
            String version = "Version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.text_view_version)).setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void setTextConfigFileName() {
        try {
            if (ModelManager.getInstance(this).getConfigManager().getConfig().getStart_screen().isConfig_text()) {
                findViewById(R.id.text_view_config).setVisibility(View.VISIBLE);
                String configName = ModelManager.getInstance(this).getConfigManager().getConfig().getConfig_info().getFilename();
                String configVersion = ModelManager.getInstance(this).getConfigManager().getConfig().getConfig_info().getVersion();
                ((TextView) findViewById(R.id.text_view_config)).setText("Config File: " + configName + " (" + configVersion + ")");
            } else {
                findViewById(R.id.text_view_config).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            ((TextView) findViewById(R.id.text_view_config)).setText("");
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

    void serviceStarted() {
        Intent intent;
        try {
            switch (state) {
                case SETTINGS:
                    intent = new Intent(ActivityStartScreen.this, ActivityAdmin.class);
                    startActivity(intent);
                    break;
                case START_STUDY:
                    intent = new Intent(ActivityStartScreen.this, ActivityMain.class);
                    startActivity(intent);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void serviceStopped() {
        switch (state) {
            case EXIT:
                clearModelManager();
                break;
            case START_SCREEN:
                loadModelManager();
                handler.removeCallbacks(runnableWaitLoading);
                handler.postDelayed(runnableWaitLoading, 500);
                break;
        }
    }

    Runnable runnableStartService = new Runnable() {
        @Override
        public void run() {
            if (!ServiceSystemHealth.isRunning) {
                hideProgressBar();
                showProgressBar();
                Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
                startService(intent);
                handler.postDelayed(this, 500);
            } else {
                hideProgressBar();
                serviceStarted();
            }
        }
    };
    Runnable runnableStopService = new Runnable() {
        @Override
        public void run() {
            if (ServiceSystemHealth.isRunning) {
                hideProgressBar();
                showProgressBar();
                Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
                stopService(intent);
                handler.postDelayed(this, 500);
            } else {
                hideProgressBar();
                serviceStopped();
            }
        }
    };
    Runnable runnableWaitLoading = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "isUpdating=" + ModelManager.getInstance(ActivityStartScreen.this).isUpdating());
            if (ModelManager.getInstance(ActivityStartScreen.this).isUpdating()) {
                hideProgressBar();
                showProgressBar();
                handler.postDelayed(this, 500);
            } else {
                hideProgressBar();
                fixConfigIfRequired();
                setUI();

            }
        }
    };
}
