package org.md2k.study.model_view.study_start_end;

import android.app.Activity;
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
public class UserViewStudyStartEnd extends UserView {
    public UserViewStudyStartEnd(Activity activity, Model model) {
        super(activity, model);
    }

    @Override
    public void addView() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_study_start_end, null);
        linearLayoutMain.addView(view);
        prepareButton();
    }

    @Override
    public void updateView() {
        if (view == null) return;

        activity.findViewById(R.id.button_study_start_end).setEnabled(true);
        StudyStartEndInfoManager studyStartEndInfoManager = (StudyStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_STUDY_START_END);
        Status status = studyStartEndInfoManager.getCurrentStatusDetails();
        if (status.getStatus() == Status.STUDY_START_NOT_AVAILABLE) {
            ((Button) activity.findViewById(R.id.button_study_start_end)).setText("Start Study");
            ((TextView) activity.findViewById(R.id.text_view_study_start)).setText(" - ");
            ((TextView) activity.findViewById(R.id.text_view_study_end)).setText(" - ");
        } else if (status.getStatus() == Status.STUDY_RUNNING) {
            ((Button) activity.findViewById(R.id.button_study_start_end)).setText("End Study");
            ((TextView) activity.findViewById(R.id.text_view_study_start)).setText(formatTime(studyStartEndInfoManager.getStudyStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_study_end)).setText(" - ");
        } else if (status.getStatus() == Status.STUDY_COMPLETED) {
            ((Button) activity.findViewById(R.id.button_study_start_end)).setText("Start Again");
            ((TextView) activity.findViewById(R.id.text_view_study_start)).setText(formatTime(studyStartEndInfoManager.getStudyStartTime()));
            ((TextView) activity.findViewById(R.id.text_view_study_end)).setText(formatTime(studyStartEndInfoManager.getStudyEndTime()));
        }
    }
    @Override
    public void stopView(){}



    private void prepareButton() {
        Button button = (Button) activity.findViewById(R.id.button_study_start_end);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyStartEndInfoManager studyStartEndInfoManager = (StudyStartEndInfoManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_STUDY_START_END);
                Status status = studyStartEndInfoManager.getCurrentStatusDetails();
                if (status.getStatus() == Status.STUDY_START_NOT_AVAILABLE || status.getStatus() == Status.STUDY_COMPLETED)
                    studyStartEndInfoManager.setStudyStartTime(DateTime.getDateTime());
                else if (status.getStatus() == Status.STUDY_RUNNING)
                    studyStartEndInfoManager.setStudyEndTime(DateTime.getDateTime());
                updateView();
            }
        });
    }

    private String formatTime(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }
}
