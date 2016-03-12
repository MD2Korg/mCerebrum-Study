package org.md2k.study.view.data_quality;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.view.user.UserView;

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
    Activity activity;
    ImageView[] imageView;
    Status[] status;

    public UserViewDataQuality(Activity activity){
        this.activity=activity;
        addView();
    }
    public void addView(){
        addLayout();
        addImageView();
    }
    private void addLayout(){
        LinearLayout linearLayoutMain= (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        View child=activity.getLayoutInflater().inflate(R.layout.layout_data_quality, null);
        linearLayoutMain.addView(child);
    }
    private void addImageView() {
        LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout_dataquality_all);
        ArrayList<DataSource> dataSources = ModelManager.getInstance(activity).getConfigManager().getConfig().getData_quality();
        imageView = new ImageView[dataSources.size()];
        for (int i = 0; i < dataSources.size(); i++) {
            LinearLayout linearLayoutOne = new LinearLayout(activity);
            linearLayoutOne.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            linearLayoutOne.setLayoutParams(LLParams);

            TextView textViewOne = new TextView(activity);
            textViewOne.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageView imageViewOne = new ImageView(activity);
            linearLayoutOne.addView(textViewOne);
            linearLayoutOne.addView(imageViewOne);
            imageView[i] = imageViewOne;
            linearLayout.addView(linearLayoutOne);
            imageViewOne.setImageResource(R.drawable.ic_error_red_50dp);
            LinearLayout.LayoutParams ll_params_imageView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            ll_params_imageView.gravity = Gravity.CENTER_HORIZONTAL;

            imageViewOne.setLayoutParams(ll_params_imageView);
            imageViewOne.requestLayout();
            imageViewOne.getLayoutParams().height = 60;
            imageViewOne.getLayoutParams().width = 60;
            if (dataSources.get(i).getType() != null) {
                switch (dataSources.get(i).getType()) {
                    case DataSourceType.RESPIRATION:
                        textViewOne.setText("Respiration");
                        break;
                    case DataSourceType.ECG:
                        textViewOne.setText("ECG");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            } else if(dataSources.get(i).getPlatform().getId()!=null){
                switch (dataSources.get(i).getPlatform().getId()) {
                    case PlatformId.LEFT_WRIST:
                        textViewOne.setText("Wrist (L)");
                        break;
                    case PlatformId.RIGHT_WRIST:
                        textViewOne.setText("Wrist (R)");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            }else if(dataSources.get(i).getPlatform().getType()!=null){
                switch (dataSources.get(i).getPlatform().getType()) {
                    case PlatformType.MICROSOFT_BAND:
                        textViewOne.setText("Microsoft Band");
                        break;
                    case PlatformType.AUTOSENSE_WRIST:
                        textViewOne.setText("AutoSense Wrist");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            }
        }
    }
    public void setStatus(Status[] status){
        this.status=status;
    }
    public void updateView() {
        Status curStatus = status[0];
        for (int i = 0; i < status.length; i++) {
            switch (status[i].getStatusCode()) {
                case Status.DATAQUALITY_GOOD:
                    imageView[i].setImageResource(R.drawable.ic_ok_teal_50dp);
                    break;
                case Status.DATAQUALITY_OFF:
                    imageView[i].setImageResource(R.drawable.ic_error_red_50dp);
                    curStatus = status[i];
                    break;
                case Status.DATAQUALITY_NOT_WORN:
                    if (curStatus.getStatusCode() != Status.DATAQUALITY_OFF)
                        curStatus = status[i];
                case Status.DATAQUALITY_LOOSE:
                case Status.DATAQUALITY_NOISY:
                    if (curStatus.getStatusCode() != Status.DATAQUALITY_OFF && curStatus.getStatusCode() != Status.DATAQUALITY_NOT_WORN) {
                        if (curStatus.getStatusCode() != Status.DATAQUALITY_OFF)
                            curStatus = status[i];
                    }
                    imageView[i].setImageResource(R.drawable.ic_warning_amber_50dp);
                    break;
            }
        }
        ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setText(curStatus.getStatusMessage());
        if (curStatus.getStatusCode() == Status.DATAQUALITY_GOOD)
            ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
        else
            ((TextView) activity.findViewById(R.id.text_view_data_quality_message)).setTextColor(ContextCompat.getColor(activity,R.color.red_900));
    }

}
