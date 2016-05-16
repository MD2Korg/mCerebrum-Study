package org.md2k.study.model_view.day_start_end;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.utilities.Report.Log;

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
        addView();
    }

    @Override
    public void disableView() {
        activity.findViewById(R.id.button_day_start_end).setEnabled(false);
        ((Button) activity.findViewById(R.id.button_day_start_end)).setText("Start Day");
        activity.findViewById(R.id.button_day_start_end).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
        ((Button)activity.findViewById(R.id.button_day_start_end)).setTextColor(Color.WHITE);
        ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(" - ");
        ((TextView) activity.findViewById(R.id.text_view_day_end)).setText(" - ");
    }

    private void addView() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_day_start_end, null);
        linearLayoutMain.addView(view);
        prepareButton();
    }
    public void stop(){}

    void prepareButton() {
        Button button = (Button) activity.findViewById(R.id.button_day_start_end);
        Log.d(TAG, "Button clicked");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END);
                Status status = dayStartEndInfoManager.getCurrentStatusDetails();
                if (status.getStatus() == Status.DAY_START_NOT_AVAILABLE) {
                    showAlertDialog(Status.DAY_START_NOT_AVAILABLE);
                } else if (status.getStatus() == Status.SUCCESS) {
                    showAlertDialog(Status.SUCCESS);
                }
                enableView();
            }
        });
    }

    public void showAlertDialog(final int status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END);
        if (status == Status.DAY_START_NOT_AVAILABLE) {
            builder.setTitle("Start Day");
            builder.setMessage("Do you want to start the day?");
        } else {
            builder.setTitle("End Day");
            builder.setMessage("Do you want to end the day?");
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status == Status.DAY_START_NOT_AVAILABLE)
                    dayStartEndInfoManager.setDayStartTime(DateTime.getDateTime());
                else if (status == Status.SUCCESS)
                    dayStartEndInfoManager.setDayEndTime(DateTime.getDateTime());
                enableView();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void enableView() {
        Log.d(TAG,"enableView () .. UserViewDayStartEnd");
        if (view == null) return;
        activity.findViewById(R.id.button_day_start_end).setEnabled(true);
        final DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END);
        Status status = dayStartEndInfoManager.getCurrentStatusDetails();
        if (status.getStatus() == Status.DAY_START_NOT_AVAILABLE) {
            ((Button) activity.findViewById(R.id.button_day_start_end)).setText("Start Day");
            activity.findViewById(R.id.button_day_start_end).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
            ((Button)activity.findViewById(R.id.button_day_start_end)).setTextColor(Color.WHITE);
            activity.findViewById(R.id.button_day_start_end).setEnabled(true);
            activity.findViewById(R.id.button_day_start_end).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(" - ");
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText(" - ");
        } else if (status.getStatus() == Status.SUCCESS) {
            ((Button) activity.findViewById(R.id.button_day_start_end)).setText("End Day");
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(formatTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText("-");
            if (dayStartEndInfoManager.getDayStartTime() + getRequiredTime() > DateTime.getDateTime()) {
                activity.findViewById(R.id.button_day_start_end).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.button_day_start_end).setEnabled(false);
            } else {
                activity.findViewById(R.id.button_day_start_end).setEnabled(true);
                activity.findViewById(R.id.button_day_start_end).setVisibility(View.VISIBLE);
            }
        } else {
            ((Button) activity.findViewById(R.id.button_day_start_end)).setText("Day Ended");
            activity.findViewById(R.id.button_day_start_end).setEnabled(false);
            activity.findViewById(R.id.button_day_start_end).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.text_view_day_start)).setText(formatTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_day_end)).setText(formatTime(dayStartEndInfoManager.getDayEndTime()));
        }
    }

    long getRequiredTime() {
        final DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DAY_START_END);
        String[] parameters = dayStartEndInfoManager.getAction().getParameters();
        if (parameters == null || parameters.length == 0) return 0;
        Log.d(TAG, "parameter=" + parameters[0]);
        return Long.parseLong(parameters[0]);

    }

    String formatTime(long timestamp) {
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
}
