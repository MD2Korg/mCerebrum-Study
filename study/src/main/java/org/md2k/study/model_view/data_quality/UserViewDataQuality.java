package org.md2k.study.model_view.data_quality;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;


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
public class UserViewDataQuality extends UserView {
    private static final String TAG = UserViewDataQuality.class.getSimpleName();
    ImageView[] imageView;
    TextView[] textViews;
    Handler handler;

    public UserViewDataQuality(Activity activity, Model model) {
        super(activity, model);
        Log.d(TAG, "UserViewDataQuality()...");
        handler=new Handler();
    }

    @Override
    public void updateView(){
        String message = null;
        boolean isAllGood = true;
        boolean isStatusSuccess;
        DataQualityManager dataQualityManager = (DataQualityManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_DATA_QUALITY);
        if (dataQualityManager == null || textViews.length == 0) {
            return;
        }
        if (dataQualityManager.getStatus().getStatus() == Status.SUCCESS)
            isStatusSuccess = true;
        else isStatusSuccess = false;

        ArrayList<DataQualityInfo> dataQualityInfos = dataQualityManager.dataQualityInfos;
        for (int i = 0; i < dataQualityInfos.size(); i++) {
            if (isStatusSuccess == false) {
                imageView[i].setImageResource(R.drawable.ic_error_red_50dp);
                message = "";
            } else {
                if(dataQualityInfos.get(i).getTitle()!="")
                    textViews[i].setText(dataQualityInfos.get(i).getTitle());
                switch (dataQualityInfos.get(i).getQuality()) {
                    case Status.DATAQUALITY_GOOD:
                        imageView[i].setImageResource(R.drawable.ic_ok_teal_50dp);
                        break;
                    case Status.DATAQUALITY_OFF:
                        imageView[i].setImageResource(R.drawable.ic_error_red_50dp);
                        message = dataQualityInfos.get(i).getMessage();
                        isAllGood = false;
                        break;
                    case Status.DATAQUALITY_NOT_WORN:
                    case Status.DATAQUALITY_LOOSE:
                    case Status.DATAQUALITY_NOISY:
                        if (message == null) message = dataQualityInfos.get(i).getMessage();
                        imageView[i].setImageResource(R.drawable.ic_warning_red_48dp);
                        isAllGood = false;
                        break;
                }
            }
        }
        if (isAllGood && isStatusSuccess) {
            ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setText("Everything is good");
            ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
        } else {
            ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setText(message);
            ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setTextColor(ContextCompat.getColor(activity, R.color.red_900));
        }

    }

    @Override
    public void stopView() {
        handler.removeCallbacks(runnableUpdateView);

    }

    @Override
    public void addView() {
        Log.d(TAG, "addView()...");
        addLayout();
        addImageView();
        handler.post(runnableUpdateView);
    }

    private void addLayout() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_data_quality, null);
        linearLayoutMain.addView(view);
    }

    private void addImageView() {
        Log.d(TAG, "addImageView()...");
        ArrayList<DataSource> dataSources = ModelManager.getInstance(activity).getConfigManager().getConfig().getData_quality();
        LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout_dataquality_all);
        imageView = new ImageView[dataSources.size()];
        textViews = new TextView[dataSources.size()];
        for (int i = 0; i < dataSources.size(); i++) {
            LinearLayout linearLayoutOne = new LinearLayout(activity);
            linearLayoutOne.setClickable(true);
            linearLayoutOne.setBackground(ContextCompat.getDrawable(activity, android.R.drawable.btn_default_small));
            final int finalI = i;
            linearLayoutOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ActivityDataQuality.class);
                    intent.putExtra("id", finalI);
                    activity.startActivity(intent);
                }
            });
            linearLayoutOne.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            linearLayoutOne.setLayoutParams(LLParams);
            TextView textViewOne = new TextView(activity);
            textViewOne.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageView imageViewOne = new ImageView(activity);
            linearLayoutOne.addView(textViewOne);
            linearLayoutOne.addView(imageViewOne);
            imageView[i] = imageViewOne;
            textViews[i] = textViewOne;
            linearLayout.addView(linearLayoutOne);
            imageViewOne.setImageResource(R.drawable.ic_ok_teal_50dp);
            LinearLayout.LayoutParams ll_params_imageView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            ll_params_imageView.gravity = Gravity.CENTER_HORIZONTAL;

            imageViewOne.setLayoutParams(ll_params_imageView);
            imageViewOne.requestLayout();
            imageViewOne.getLayoutParams().height = 60;
            imageViewOne.getLayoutParams().width = 60;
            if (dataSources.get(i).getId() != null)
                textViewOne.setText(dataSources.get(i).getId().toLowerCase());
            else
                textViewOne.setText(dataSources.get(i).getType().toLowerCase());
        }
    }

    Runnable runnableUpdateView = new Runnable() {
        @Override
        public void run() {
            updateView();
            handler.postDelayed(this, 5000);
        }
    };

}
