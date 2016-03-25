package org.md2k.study.model_view.user_status;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.study.model_view.day_start_end.DayStartEndInfoManager;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.Report.Log;


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
public class UserViewUserStatus extends UserView {
    private static final String TAG = UserViewUserStatus.class.getSimpleName();

    public UserViewUserStatus(Activity activity, Model model) {
        super(activity, model);
        addView();
    }

    @Override
    public void disableView() {
        enableView();
    }

    private void addView() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_status, null);
        linearLayoutMain.addView(view);
    }


    @Override
    public void enableView() {
        ModelManager modelManager = ModelManager.getInstance(activity);
        final Status status = modelManager.getStatus();
        Log.d(TAG, "statusview...enableview...status=" + status.log());
        String msg = status.getMessage();
        if (status.getStatus() == Status.SUCCESS) {
            if (modelManager.getModel(ModelFactory.MODEL_DAY_START_END) != null) {
                msg = ((DayStartEndInfoManager) modelManager.getModel(ModelFactory.MODEL_DAY_START_END)).getCurrentStatusDetails().getMessage();
            }
        }
        Button button = (Button) activity.findViewById(R.id.button_status);
        ((TextView) activity.findViewById(R.id.textView_status)).setText(msg);
        if (status.getStatus() == Status.SUCCESS) {
            activity.findViewById(R.id.layout_health).setBackground(ContextCompat.getDrawable(activity, R.color.teal_50));
            ((TextView) activity.findViewById(R.id.textView_status)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
            button.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok_teal_50dp, 0);
            button.setText("OK");
            button.setEnabled(false);
            button.setOnClickListener(null);
            //            button.setVisibility(View.INVISIBLE);

        } else {
            activity.findViewById(R.id.layout_health).setBackground(ContextCompat.getDrawable(activity, R.color.red_200));
            ((TextView) activity.findViewById(R.id.textView_status)).setTextColor(ContextCompat.getColor(activity, R.color.red_900));
            button.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_grey_50dp, 0);
            button.setText("FIX");
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status.getStatus() == Status.DAY_START_NOT_AVAILABLE) {
                        showAlertDialog(Status.DAY_START_NOT_AVAILABLE);
                    } else if (status.getStatus() == Status.STUDY_START_NOT_AVAILABLE)
                        showAlertDialog(Status.STUDY_START_NOT_AVAILABLE);
                    else {
                        Intent intent = new Intent(activity, ActivityAdmin.class);
                        activity.startActivity(intent);

                    }
                }
            });
        }
    }

    void showAlertDialog(int type) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        if (type == Status.DAY_START_NOT_AVAILABLE) {
            builder.setTitle("Day is not started");
            builder.setMessage("Please Click \"Day Start\" button");
        } else if (type == Status.STUDY_START_NOT_AVAILABLE) {
            builder.setTitle("Study is not started");
            builder.setMessage("Please Click \"Study Start\" button");
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();

    }

}
