package org.md2k.study;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.data_quality.DataQualityManager;
import org.md2k.study.model.privacy_control.PrivacyControlManager;
import org.md2k.study.system_health.ServiceSystemHealth;

import java.util.ArrayList;

public class ActivityPrivacy extends ActivityBase {
    Handler handlerPrivacy;
    PrivacyControlManager privacyControlManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerPrivacy = new Handler();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverStatus,
                new IntentFilter(ServiceSystemHealth.INTENT_NAME));

    }
    private BroadcastReceiver broadcastReceiverStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(privacyControlManager==null) return;
            switch (intent.getIntExtra(ServiceSystemHealth.TYPE, -1)) {
                case ServiceSystemHealth.CONNECTED:
                    updateUI();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (!modelManager.isValid()) return;
        privacyControlManager = (PrivacyControlManager) ModelManager.getInstance(this).getUserManager().getModel(ModelManager.MODEL_PRIVACY);
        setupUI();
    }

    void setupUI() {
        if (privacyControlManager != null) {
            findViewById(R.id.linear_layout_header_privacy).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_layout_content_privacy).setVisibility(View.VISIBLE);
            findViewById(R.id.button_privacy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClassName(privacyControlManager.getOperation().getPackage_name(), privacyControlManager.getOperation().getClass_name());
                    startActivity(intent);
                }
            });
            updateUI();
        } else {
            findViewById(R.id.linear_layout_header_privacy).setVisibility(View.GONE);
            findViewById(R.id.linear_layout_content_privacy).setVisibility(View.GONE);
        }
    }

    Runnable privacyRunnable = new Runnable() {
        @Override
        public void run() {
            if (updatePrivacyUI())
                handlerPrivacy.postDelayed(privacyRunnable, 1000);
        }
    };

    void updateUI() {
        privacyControlManager.set();
        privacyControlManager.update();
        handlerPrivacy.post(privacyRunnable);
    }

    boolean updatePrivacyUI() {
        if (privacyControlManager.getStatus().getStatusCode() == Status.PRIVACY_ACTIVE) {
            long remainingTime = privacyControlManager.getPrivacyData().getStartTimeStamp() + privacyControlManager.getPrivacyData().getDuration().getValue() - DateTime.getDateTime();
            if (remainingTime > 0) {
                remainingTime /= 1000;
                int sec = (int) (remainingTime % 60);
                int min = (int) (remainingTime / 60);
                ((TextView) findViewById(R.id.text_view_privacy)).setText("Resumed after " + String.format("%02d:%02d", min, sec));
                ((TextView) findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(this, R.color.red_700));
                findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(this, R.drawable.button_red));
                ((Button) findViewById(R.id.button_privacy)).setText("Stop");
                return true;
            }
        }
        ((TextView) findViewById(R.id.text_view_privacy)).setText("Inactive");
        ((TextView) findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(this, R.color.teal_700));
        findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(this, R.drawable.button_teal));
        ((Button) findViewById(R.id.button_privacy)).setText("Activate");
        return false;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverStatus);
        super.onDestroy();
    }
}
