package org.md2k.study.model_view.privacy_control;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.utilities.Report.Log;

import java.util.Locale;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class UserViewPrivacyControl extends UserView {
    private static final String TAG = UserViewPrivacyControl.class.getSimpleName();
    private Handler handler;
    private boolean isActive;

    public UserViewPrivacyControl(Activity activity, Model model) {
        super(activity, model);
        handler = new Handler();
        isActive = false;
    }

    @Override
    public void stopView() {
        handler.removeCallbacks(runnablePrivacy);
    }

    @Override
    public void updateView() {
        if (view == null) return;
        handler.removeCallbacks(runnablePrivacy);
        handler.post(runnablePrivacy);
    }

    private Runnable runnablePrivacy = new Runnable() {
        @Override
        public void run() {
            activity.findViewById(R.id.button_privacy).setEnabled(true);
            Log.d(TAG, "updateView()...");
            PrivacyControlManager privacyControlManager = (PrivacyControlManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_PRIVACY);
            if (privacyControlManager == null) handler.postDelayed(this, 1000);
            else {
                Status status = privacyControlManager.getCurrentStatusDetails();
                if (status.getStatus() == Status.PRIVACY_ACTIVE) {
                    long remainingTime = privacyControlManager.getPrivacyData().getStartTimeStamp() + privacyControlManager.getPrivacyData().getDuration().getValue() - DateTime.getDateTime();
                    if (remainingTime > 0) {
                        remainingTime /= 1000;
                        int sec = (int) (remainingTime % 60);
                        int min = (int) (remainingTime / 60);
                        ((TextView) activity.findViewById(R.id.text_view_privacy)).setText("Resumed after " + String.format("%02d:%02d", min, sec));
                        ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.red_700));
                        activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
                        ((Button) activity.findViewById(R.id.button_privacy)).setTextColor(Color.WHITE);
                        ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn Off");
                        handler.postDelayed(this, 1000);
                        isActive = true;
                    } else {
                        String text = "Not Active";
                        long remainingUsage = privacyControlManager.getRemainingTime();
                        if (remainingUsage != Long.MAX_VALUE) {
                            remainingUsage = remainingUsage / (1000 * 60);
                            text = text + String.format(Locale.ENGLISH, "\n(Daily Remaining Usage: %0d Minutes)", remainingUsage);
                        }
                        ((TextView) activity.findViewById(R.id.text_view_privacy)).setText(text);
                        ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
                        activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
                        ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn On");
                        ((Button) activity.findViewById(R.id.button_privacy)).setTextColor(Color.BLACK);
                        isActive = false;
                    }
                } else {
                    String text = "Not Active";
                    long remainingUsage = privacyControlManager.getRemainingTime();
                    if (remainingUsage != Long.MAX_VALUE) {
                        remainingUsage = remainingUsage / (1000 * 60);
                        text = text + String.format(Locale.ENGLISH, "\n(Daily Usage Remaining: %d Minutes)", remainingUsage);
                    }
                    ((TextView) activity.findViewById(R.id.text_view_privacy)).setText(text);
                    ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
                    activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
                    ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn On");
                    ((Button) activity.findViewById(R.id.button_privacy)).setTextColor(Color.BLACK);
                    isActive = false;
                }
                if (!isActive) {
                    long timeLeft = privacyControlManager.getRemainingTime();
                    if (timeLeft < 5 * 60 * 1000) {
                        String text = "Not Active\n(Daily Usage Remaining: 0 Minute)";
                        ((TextView) activity.findViewById(R.id.text_view_privacy)).setText(text);

                        ((Button) activity.findViewById(R.id.button_privacy)).setText("Max Used");
                        ((Button) activity.findViewById(R.id.button_privacy)).setEnabled(false);
                        ((Button) activity.findViewById(R.id.button_privacy)).setTextColor(Color.GRAY);
                    }
                }
            }
        }
    };

    @Override
    public void addView() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_privacy_control, null);
        linearLayoutMain.addView(view);
        prepareButton();
    }

    private void prepareButton() {
        Button button = (Button) activity.findViewById(R.id.button_privacy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                PrivacyControlManager privacyControlManager = (PrivacyControlManager) model;//ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_PRIVACY);
                intent.setClassName(privacyControlManager.getAction().getPackage_name(), privacyControlManager.getAction().getClass_name());
                if (!isActive) {
                    long remainingTime = privacyControlManager.getRemainingTime();
                    if (remainingTime <= 0) {
                        Toast.makeText(activity, "Privacy usage exceeded", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "remaining time=" + remainingTime);
                    intent.putExtra("REMAINING_TIME", remainingTime);
                }
                activity.startActivity(intent);
            }
        });
    }
}
