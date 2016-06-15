package org.md2k.study.model_view.day_start_end;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.AlertDialogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
public class UserViewDayStartEnd extends UserView {
    private static final String TAG = UserViewDayStartEnd.class.getSimpleName();

    public UserViewDayStartEnd(Activity activity, Model model) {
        super(activity, model);
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, new IntentFilter(DayStartEndInfoManager.class.getSimpleName()));
    }

    @Override
    public void addView() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_day_start_end, null);
        linearLayoutMain.addView(view);
        prepareButton();
    }

    @Override
    public void stopView() {
    }


    @Override
    public void updateView() {
        Log.d(TAG, "enableView () .. UserViewDayStartEnd");
        if (view == null) return;
        activity.findViewById(R.id.button_day_start_end).setEnabled(true);
        DayStartEndInfoManager dayStartEndInfoManager = ((DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END));
        int buttonStatus = dayStartEndInfoManager.getButtonStatus();
        if (buttonStatus == DayStartEndInfoManager.START_BUTTON) {
            ((Button) activity.findViewById(R.id.button_day_start_end)).setText("Start Day");
            ((Button) activity.findViewById(R.id.button_day_start_end)).setVisibility(View.VISIBLE);
            ((Button) activity.findViewById(R.id.button_day_start_end)).setEnabled(true);
            ((Button) activity.findViewById(R.id.button_day_start_end)).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
            ((Button) activity.findViewById(R.id.button_day_start_end)).setTextColor(Color.WHITE);
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(" - ");
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText(" - ");
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setVisibility(View.INVISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume_title)).setVisibility(View.INVISIBLE);
        } else if (buttonStatus == DayStartEndInfoManager.END_BUTTON) {
            ((Button) activity.findViewById(R.id.button_day_start_end)).setText("End Day");
            ((Button) activity.findViewById(R.id.button_day_start_end)).setVisibility(View.VISIBLE);
            ((Button) activity.findViewById(R.id.button_day_start_end)).setEnabled(true);
            ((Button) activity.findViewById(R.id.button_day_start_end)).setTextColor(Color.BLACK);
            ((Button) activity.findViewById(R.id.button_day_start_end)).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(formatTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText("-");
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setVisibility(View.INVISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume_title)).setVisibility(View.INVISIBLE);

        } else if (dayStartEndInfoManager.isDayStarted() && dayStartEndInfoManager.isDayEnded()) {
            ((Button) activity.findViewById(R.id.button_day_start_end)).setText("Day Ended");
            activity.findViewById(R.id.button_day_start_end).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.button_day_start_end).setEnabled(false);
            ((Button) activity.findViewById(R.id.button_day_start_end)).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
            ((Button) activity.findViewById(R.id.button_day_start_end)).setTextColor(Color.BLACK);
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(formatTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText(formatTime(dayStartEndInfoManager.getDayEndTime()));
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume_title)).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setText(formatTime(dayStartEndInfoManager.getTime(DayStartEndInfoManager.WAKEUP, 0)));
        } else if (dayStartEndInfoManager.isDayStarted()) {
            activity.findViewById(R.id.button_day_start_end).setEnabled(false);
            activity.findViewById(R.id.button_day_start_end).setVisibility(View.INVISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(formatTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText("-");
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setVisibility(View.INVISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume_title)).setVisibility(View.INVISIBLE);
        } else {
            activity.findViewById(R.id.button_day_start_end).setEnabled(false);
            activity.findViewById(R.id.button_day_start_end).setVisibility(View.INVISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText("-");
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText("-");
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume_title)).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_resume)).setText(formatTime(dayStartEndInfoManager.getWakeupShowTimestamp()));
        }
    }

    private String formatTime(long timestamp) {
        if (timestamp == -1) return "-";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a (MM/dd)");
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateView();
        }
    };

    private void prepareButton() {
        Button button = (Button) activity.findViewById(R.id.button_day_start_end);
        Log.d(TAG, "Button clicked");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END);
                int state = dayStartEndInfoManager.getButtonStatus();
                if (state == DayStartEndInfoManager.START_BUTTON) {
                    showAlertDialog(Status.DAY_START_NOT_AVAILABLE);
                } else if (state == DayStartEndInfoManager.END_BUTTON) {
                    showAlertDialog(Status.SUCCESS);
                }
                updateView();
            }
        });
    }

    public void showAlertDialog(final int status) {
        final DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END);
        if (status == Status.DAY_START_NOT_AVAILABLE) {
            AlertDialogs.AlertDialog(activity, "Start Day", "Do you want to start the day?", R.drawable.ic_info_teal_48dp, "Yes", "Cancel", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        try {
                            dayStartEndInfoManager.setDayStartTime(DateTime.getDateTime());
                            updateView();
                        } catch (DataKitException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    } else {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            AlertDialogs.AlertDialog(activity, "End Day", "Do you want to end the day?", R.drawable.ic_info_teal_48dp, "Yes", "Cancel", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        try {
                            if (status == Status.SUCCESS)
                                dayStartEndInfoManager.setDayEndTime(DateTime.getDateTime());
                            updateView();
                        } catch (DataKitException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    } else {
                        dialog.dismiss();
                    }
                }
            });
        }

    }
}

