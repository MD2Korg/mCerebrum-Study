package org.md2k.study;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import org.md2k.study.controller.ModelManager;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.Report.Log;

import io.fabric.sdk.android.Fabric;

public class ActivityStartScreen extends AppCompatActivity {
    private static final String TAG = ActivityStartScreen.class.getSimpleName();
    Handler handler;
    ProgressDialog progress;
    boolean firstTime=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()...");
        handler = new Handler();
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_start_screen);
        setButtonExit();
        setButtonStart();
        setImageViewLogo();
        setButtonSettings();
        Log.d(TAG, "...onCreate()");

    }

    @Override
    public void onStart() {
        super.onStart();
        firstTime=true;
        Log.d(TAG, "onStart()...");
        handler.removeCallbacks(runnableWaitEnd);
        handler.removeCallbacks(runnableWaitStart);
        handler.removeCallbacks(runnableWaitStartCheck);
        if (progress != null)
            progress.dismiss();
        if (ServiceSystemHealth.isRunning && ModelManager.getInstance(this).getStatus().getRank() <= Status.RANK_ADMIN_OPTIONAL) {
            Log.d(TAG, "service running..with rank=" + ServiceSystemHealth.RANK_LIMIT);
            Intent intent = new Intent(ActivityStartScreen.this, ActivityMain.class);
            startActivity(intent);
        } else if (!ServiceSystemHealth.isRunning) {
            Log.d(TAG, "start service...");
            ServiceSystemHealth.RANK_LIMIT = Status.RANK_ADMIN_OPTIONAL;
            Intent intent = new Intent(ActivityStartScreen.this, ServiceSystemHealth.class);
            startService(intent);
            progress = ProgressDialog.show(this, "Loading...", "", true);
            handler.post(runnableWaitStartCheck);
        } else {
            Log.d(TAG, "service running...");
            handler.post(runnableWaitStartCheck);
        }
    }

    void setImageViewLogo() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView_logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://md2k.org"));
                startActivity(intent);
            }
        });
    }

    void setButtonExit() {
        Button button = (Button) findViewById(R.id.button_exit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void setButtonStart() {
        Button button = (Button) findViewById(R.id.button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "runnableWaitStart...isServiceRunning=" + ServiceSystemHealth.isRunning+" Service_Rank="+ServiceSystemHealth.RANK_LIMIT);
                ServiceSystemHealth.RANK_LIMIT = Status.RANK_SUCCESS;
                progress = ProgressDialog.show(ActivityStartScreen.this, "Loading...", "", true);
                Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
                startService(intent);
                handler.post(runnableWaitStart);
            }
        });
    }

    void setButtonSettings() {
        Button button = (Button) findViewById(R.id.button_settings);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceSystemHealth.RANK_LIMIT = Status.RANK_ADMIN_OPTIONAL;
                progress = ProgressDialog.show(ActivityStartScreen.this, "Loading...", "", true);
                Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
                startService(intent);
                handler.post(runnableWaitStart);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (ServiceSystemHealth.isRunning) {
            if (ModelManager.getInstance(this).getStatus().getRank() >= Status.RANK_ADMIN_OPTIONAL) {
                Intent intent = new Intent(ActivityStartScreen.this, ServiceSystemHealth.class);
                stopService(intent);
            }
        }
        super.onDestroy();
    }

    Runnable runnableWaitStartCheck = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableWaitStartCheck...isServiceRunning=" + ServiceSystemHealth.isRunning);
            if (!ServiceSystemHealth.isRunning)
                handler.postDelayed(this, 100);
            else {
                if(firstTime) {firstTime=false;handler.postDelayed(runnableWaitStartCheck, 500);}
                else {
                    progress.dismiss();
                    Status status = ModelManager.getInstance(ActivityStartScreen.this).getStatus();
                    Log.d(TAG, "status=" + status.log());
                    if (status.getStatus() == Status.CONFIG_FILE_NOT_EXIST) {
                        findViewById(R.id.button_start).setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_teal));
                        findViewById(R.id.button_start).setEnabled(false);
                        findViewById(R.id.button_settings).setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_red));
                    } else if (status.getRank() <= Status.RANK_ADMIN_OPTIONAL) {
                        findViewById(R.id.button_start).setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_red));
                        findViewById(R.id.button_start).setEnabled(true);
                        findViewById(R.id.button_settings).setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_teal));
                    } else {
                        findViewById(R.id.button_start).setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_teal));
                        findViewById(R.id.button_start).setEnabled(true);
                        findViewById(R.id.button_settings).setBackground(ContextCompat.getDrawable(ActivityStartScreen.this, R.drawable.button_red));
                    }
                    progress = ProgressDialog.show(ActivityStartScreen.this, "Loading...", "", true);
                    Intent intent = new Intent(ActivityStartScreen.this, ServiceSystemHealth.class);
                    stopService(intent);
                    Log.d(TAG, "service stopping...");
                    handler.post(runnableWaitEnd);
                }
            }
        }
    };
    Runnable runnableWaitEnd = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableWaitEnd...isServiceRunning=" + ServiceSystemHealth.isRunning);
            if (ServiceSystemHealth.isRunning)
                handler.postDelayed(this, 100);
            else {
                progress.dismiss();
            }
        }
    };
    Runnable runnableWaitStart = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableWaitStart...isServiceRunning=" + ServiceSystemHealth.isRunning+" Service_Rank="+ServiceSystemHealth.RANK_LIMIT);
            if (!ServiceSystemHealth.isRunning)
                handler.postDelayed(this, 100);
            else {
                progress.dismiss();
                if (ServiceSystemHealth.RANK_LIMIT == Status.RANK_SUCCESS) {
                    Intent intent = new Intent(ActivityStartScreen.this, ActivityMain.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ActivityStartScreen.this, ActivityAdmin.class);
                    startActivity(intent);
                }
            }
        }
    };
}
