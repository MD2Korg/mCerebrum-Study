package org.md2k.study;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import org.md2k.study.config.ViewContent;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.study.model_view.app_reset.AppResetManager;
import org.md2k.study.model_view.config_info.ActivityConfigDownload;
import org.md2k.study.model_view.privacy_control.PrivacyControlManager;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.Files;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;

import java.io.File;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();
    private ProgressDialog progressDialog = null;
    ModelManager modelManager;
    Handler handler;
    ArrayList<UserView> userViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

//        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            Log.d(TAG, "closing...");
            Intent intent = new Intent(this, ServiceSystemHealth.class);
            stopService(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ServiceSystemHealth.INTENT_NAME));
        Log.d(TAG, "broadcast...set...");
        handler = new Handler();
        userViews = new ArrayList<>();
        Log.d(TAG, "onCreate()..isServiceRunning=" + ServiceSystemHealth.isRunning);
        if (!ServiceSystemHealth.isRunning) {
            this.progressDialog = ProgressDialog.show(this, "Please wait..", "Loading...", true, false);
            Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
            startService(intent);
            handler.post(runnableServiceIsRunning);
        } else
            createUI();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    void createUI() {
        Log.d(TAG, "createUI()...");
        modelManager = ModelManager.getInstance(ActivityMain.this);
        Status status = modelManager.getStatus();
        Log.d(TAG, "createUI()...status=" + status.log());
        if (status.getRank() == Status.RANK_CONFIG && status.getStatus() != Status.SUCCESS){

        }
//            showDownloadConfigWindow();
        else {
            Log.d(TAG, "here");
            updateUI();
        }
    }

    void updateUI() {
        if (modelManager == null) return;
        if (userViews.size() == 0)
            addUserView();
        Status status = modelManager.getStatus();
        for (int i = 0; i < userViews.size(); i++) {
            Log.d(TAG, "modelmanager Status=" + status.log() + " view status=" + userViews.get(i).getModel().getRank());
            if (status.getRank() > Status.RANK_USER_REQUIRED) {
                userViews.get(i).disableView();
                Log.d(TAG, "userView disabled");
            } else {
                if (userViews.get(i).getModel() instanceof PrivacyControlManager) {
                    Log.d(TAG, "privacyManager..view.." + i);
                    PrivacyControlManager privacyControlManager = ((PrivacyControlManager) userViews.get(i).getModel());
                    privacyControlManager.set();
                }
                userViews.get(i).enableView();
                Log.d(TAG, "userView enabled");
            }
        }
        Log.d(TAG, "updateUI...");
    }


    void showDownloadConfigWindow() {
        Log.d(TAG, "showDownloadConfigWindow()..");
        for (int i = 0; i < userViews.size(); i++)
            userViews.get(i).stop();
        userViews.clear();
        Intent intentDownload = new Intent(ActivityMain.this, ActivityConfigDownload.class);
        intentDownload.putExtra(Status.class.getSimpleName(), new Status(Status.RANK_CONFIG, Status.CONFIG_FILE_NOT_EXIST));
        startActivity(intentDownload);
    }

    public void addUserView() {
        ArrayList<ViewContent> viewContents = modelManager.getConfigManager().getConfig().getUser_view().getView_contents();
        LinearLayout linearLayoutMain = (LinearLayout) findViewById(R.id.linear_layout_main);
        linearLayoutMain.removeAllViews();

        for (int i = 0; i < viewContents.size(); i++) {
            if (!viewContents.get(i).isEnable()) continue;
            UserView userView = UserView.getUserView(this, viewContents.get(i).getId());
            if (userView != null) {
                userViews.add(userView);
            }
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()...");
        updateUI();
        super.onResume();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Status status = intent.getParcelableExtra(Status.class.getSimpleName());
            Log.d(TAG, "received broadcast...rank =" + status.getRank());
            switch (status.getRank()) {
                case Status.RANK_CONFIG:
                    if (status.getStatus() != Status.SUCCESS) {
                        Log.d(TAG, "broadcast...showDownloadConfig()..." + status.log());
                        showDownloadConfigWindow();
                    } else updateUI();
                    break;
                default:
                    updateUI();
            }
        }
    };

    Runnable runnableServiceIsRunning = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableServiceIsRunning...isServiceRunning=" + ServiceSystemHealth.isRunning);
            if (!ServiceSystemHealth.isRunning)
                handler.postDelayed(this, 500);
            else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                createUI();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                intent = new Intent(this, ActivityAdmin.class);
                startActivity(intent);
                break;
            case R.id.action_tutorial:
                openPDF();
                break;
            case R.id.action_reset_app:
                Model model = modelManager.getModel(ModelFactory.MODEL_APP_RESET);
                if (model != null) {
                    ((AppResetManager) model).resetApp();
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    void openPDF() {
        if (!Files.isExist(Constants.CONFIG_DIRECTORY + "tutorial.pdf"))
            return;
        Intent intent = new Intent();
        intent.setPackage("com.adobe.reader");
        File file = new File(Constants.CONFIG_DIRECTORY + "tutorial.pdf");
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);
    }
}
