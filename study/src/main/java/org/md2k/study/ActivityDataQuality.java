package org.md2k.study;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.data_quality.DataQualityManager;
import org.md2k.utilities.data_format.DATA_QUALITY;

import java.util.ArrayList;

public class ActivityDataQuality extends ActivityBase {
    public static final String TAG = ActivityDataQuality.class.getSimpleName();
    ImageView[] imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isError) return;
        DataQualityManager dataQualityManager = (DataQualityManager) userManager.getModels(ModelManager.MODEL_DATA_QUALITY);
        if (dataQualityManager != null) {
            findViewById(R.id.linear_layout_header_dataquality).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_layout_content_dataquality).setVisibility(View.VISIBLE);
            addImageView();
        } else {
            findViewById(R.id.linear_layout_header_dataquality).setVisibility(View.GONE);
            findViewById(R.id.linear_layout_content_dataquality).setVisibility(View.GONE);
        }
    }

    void addImageView() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_dataquality_all);
        linearLayout.removeAllViews();
        ArrayList<DataSource> dataSources = ConfigManager.getInstance(this).getConfig().getData_quality();
        imageView = new ImageView[dataSources.size()];
        for (int i = 0; i < dataSources.size(); i++) {
            LinearLayout linearLayoutOne = new LinearLayout(this);
            linearLayoutOne.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            linearLayoutOne.setLayoutParams(LLParams);

            TextView textViewOne = new TextView(this);
            textViewOne.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageView imageViewOne = new ImageView(this);
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
            } else {
                switch (dataSources.get(i).getPlatform().getId()) {
                    case PlatformId.LEFT_WRIST:
                        textViewOne.setText("Wrist(L)");
                        break;
                    case PlatformId.RIGHT_WRIST:
                        textViewOne.setText("Wrist(R)");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            }
        }
    }

    public void updateDataQuality(Status[] status) {
        Status curStatus = status[0];
        for (int i = 0; i < status.length; i++) {
            switch (status[i].getStatusCode()) {
                case DATA_QUALITY.GOOD:
                    imageView[i].setImageResource(R.drawable.ic_ok_teal_50dp);
                    break;
                case DATA_QUALITY.BAND_OFF:
                    imageView[i].setImageResource(R.drawable.ic_error_red_50dp);
                    curStatus = status[i];
                    break;
                case DATA_QUALITY.NOT_WORN:
                    if (curStatus.getStatusCode() != DATA_QUALITY.BAND_OFF)
                        curStatus = status[i];
                case DATA_QUALITY.BAND_LOOSE:
                case DATA_QUALITY.NOISE:
                    if(curStatus.getStatusCode()!=DATA_QUALITY.BAND_OFF && curStatus.getStatusCode()!=DATA_QUALITY.NOT_WORN) {
                        if (curStatus.getStatusCode() != DATA_QUALITY.BAND_OFF)
                            curStatus = status[i];
                    }
                    imageView[i].setImageResource(R.drawable.ic_warning_amber_50dp);
                    break;
            }
        }
        ((TextView) findViewById(R.id.text_view_data_quality_message)).setText(curStatus.getStatusMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
