package org.md2k.study;

import android.app.Dialog;
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

import org.md2k.datakitapi.messagehandler.ResultCallback;
import org.md2k.study.cache.MySharedPref;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.app_install.ActivityInstallApp;
import org.md2k.study.model_view.app_install.AppInstallManager;
import org.md2k.study.model_view.config_info.ActivityConfigDownload;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.FileManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.Report.LogStorage;
import org.md2k.utilities.UI.AlertDialogs;
import org.md2k.utilities.permission.PermissionInfo;

import io.fabric.sdk.android.Fabric;

public class ActivityStartScreen extends AppCompatActivity {
    private static final String TAG = ActivityStartScreen.class.getSimpleName();
    boolean isPermission=false;
    Handler handler;
    ProgressDialog progressDialog;
    boolean isAlertDialogShown;
    ModelManager modelManager;
    boolean isWaitLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_start_screen);
        final PermissionInfo permissionInfo=new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if(result){
                    isPermission=true;
                    load();
                }else{
                    isPermission=false;
                    finish();
                }
            }
        });
    }
    private void load(){
        LogStorage.startLogFileStorageProcess(getApplicationContext().getPackageName());
        modelManager = ModelManager.getInstance(getApplicationContext());
        progressDialog = new ProgressDialog(this, android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        isAlertDialogShown = false;
        if (isStudyRunning()) {
            startStudy();
        } else {
            loadModelManager();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()...");
        if(isPermission) {
            if (isStudyRunning()) {
                startStudy();
            } else {
                setUI();
            }
        }
    }

    void startStudy() {
        Intent intent = new Intent(ActivityStartScreen.this, ActivityMain.class);
        startActivity(intent);
    }

    void startSettings() {
        Intent intent = new Intent(ActivityStartScreen.this, ActivityAdmin.class);
        startActivity(intent);
    }

    boolean isStudyRunning() {
        return ModelManager.RANK_LIMIT == Status.RANK_SUCCESS && (ModelManager.getInstance(this).getStatus().getRank() <= Status.RANK_ADMIN_OPTIONAL);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        if(isPermission)
            modelManager.clear();
        super.onDestroy();
    }

    void loadModelManager() {
        ModelManager.RANK_LIMIT = Status.RANK_ADMIN_OPTIONAL;
        modelManager.clear();
        modelManager.read();
        modelManager.set();
        handler.removeCallbacks(runnableWaitLoading);
        isWaitLoading = true;
        handler.post(runnableWaitLoading);
    }


    void fixConfigIfRequired() {
        if (!modelManager.getConfigManager().isExist()) {
            modelManager.clear();
            copyDefaultConfig();
            loadModelManager();
            setUI();
        } else if (!ModelManager.getInstance(this).getConfigManager().isValid() && !isAlertDialogShown) {
            try {
                MySharedPref.getInstance(this).write(Constants.CONFIG_ZIP_FILENAME, ModelManager.getInstance(this).getConfigManager().getConfig().getConfig_info().getFilename());
            } catch (Exception e) {
                MySharedPref.getInstance(this).write(Constants.CONFIG_ZIP_FILENAME, "default");
            }

            isAlertDialogShown = true;
            AlertDialogs.AlertDialog(this, "Configuration file is out of date", "Please download the latest configuration file from \"Settings\".", R.drawable.ic_info_teal_48dp, "Ok", null, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (DialogInterface.BUTTON_POSITIVE == which) {
                        modelManager.clear();
                        String filename=MySharedPref.getInstance(ActivityStartScreen.this).read(Constants.CONFIG_ZIP_FILENAME);
                        if(filename!=null && !filename.equals("default")){
                            FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
                            Intent intent = new Intent(ActivityStartScreen.this, ActivityConfigDownload.class);
                            intent.putExtra(Status.class.getSimpleName(), new Status(0,0));
                            startActivityForResult(intent, 1);
                        } else {
                            copyDefaultConfig();
                            loadModelManager();
                            isAlertDialogShown = false;
                            setUI();
                        }
                    } else {
                        setUI();
                    }
                }
            });
        } else
            setUI();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                loadModelManager();
                isAlertDialogShown = false;
            }
        }
    }

    void showProgressBar() {
        try {
            progressDialog.show();
        } catch (Exception ignored) {

        }
    }

    void hideProgressBar() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception ignored) {

        }
    }


    void copyDefaultConfig() {
        Log.d(TAG, "copyDefaultConfig()...");
        FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
        FileManager.copyAssets(ActivityStartScreen.this, "default.zip", Constants.TEMP_DIRECTORY);
        FileManager.unzip(Constants.TEMP_DIRECTORY + "default.zip", Constants.CONFIG_DIRECTORY_ROOT);
    }
    @Override
    public void onResume(){
        setUI();
        super.onResume();
    }

    void setUI() {
        Log.d(TAG, "setUI()...");
        try {
            setButtonStartStudy();
            setButtonSettings();
            setButtonUpdatemCerebrum();
            setButtonExit();
            setImageViewLogo();
            setTextConfigFileName();
            setTextVersion();
        }catch (Exception ignored){

        }
    }

    void setButtonStartStudy() {
        Button button = (Button) findViewById(R.id.button_start_study);
        if (modelManager.getStatus().getRank() > Status.RANK_ADMIN_OPTIONAL)
            disableButton((Button) findViewById(R.id.button_start_study));
        else activeButton((Button) findViewById(R.id.button_start_study));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelManager.RANK_LIMIT = Status.RANK_SUCCESS;
                modelManager.set();
                startStudy();
            }
        });
    }

    void setButtonSettings() {
        Button button = (Button) findViewById(R.id.button_settings);
        if (modelManager.getStatus().getRank() > Status.RANK_ADMIN_OPTIONAL)
            activeButton((Button) findViewById(R.id.button_settings));
        else enableButton((Button) findViewById(R.id.button_settings));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettings();
            }
        });
    }

    void setButtonUpdatemCerebrum() {
        Button button = (Button) findViewById(R.id.button_update);
        if (modelManager.getConfigManager().getConfig().getStart_screen().isUpdate_app()) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AppInstallManager appInstallManager = (AppInstallManager) ModelManager.getInstance(ActivityStartScreen.this).getModel(ModelFactory.MODEL_APP_INSTALL);
                    showProgressBar();
                    appInstallManager.updateVersionAll(0, new OnDataChangeListener() {
                        @Override
                        public void onDataChange(int index, String str) {
                            if (appInstallManager.getAppInstallList().size() == index) {
                                int status = Integer.parseInt(str);
                                if (status != Status.SUCCESS) {
                                    AlertDialogs.AlertDialog(ActivityStartScreen.this, "Update Available", "Do you want to update?", R.drawable.ic_info_teal_48dp, "Yes", "Cancel", null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which== Dialog.BUTTON_POSITIVE){
                                                Intent intent=new Intent(ActivityStartScreen.this, ActivityInstallApp.class);
                                                startActivity(intent);
                                            }else{
                                                setUI();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(ActivityStartScreen.this, "mCerebrum is up-to-date...", Toast.LENGTH_SHORT).show();
                                }
                                hideProgressBar();
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
                modelManager.clear();
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

    Runnable runnableWaitLoading = new Runnable() {
        @Override
        public void run() {
            if (isWaitLoading) {
                hideProgressBar();
                showProgressBar();
                isWaitLoading = false;
                handler.postDelayed(this, 1000);
            } else {
                Log.d(TAG, "isUpdating=" + ModelManager.getInstance(ActivityStartScreen.this).isUpdating());
                if (ModelManager.getInstance(ActivityStartScreen.this).isUpdating()) {
                    handler.postDelayed(this, 1000);
                } else {
                    hideProgressBar();
                    fixConfigIfRequired();
                }
            }
        }
    };
}
