package org.md2k.study.model_view.data_quality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.study.R;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;

import java.util.ArrayList;

public class ActivityDataQuality extends AppCompatActivity {
    private static final String TAG = ActivityDataQuality.class.getSimpleName();
    DataQualityInfo dataQualityInfo;
    public static final String VIDEO_LINK = "VIDEO_LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_quality);
        int id = getIntent().getIntExtra("id", 0);
        dataQualityInfo=((DataQualityManager) ModelManager.getInstance(this).getModel(ModelFactory.MODEL_DATA_QUALITY)).dataQualityInfos.get(id);
        setupButtonPlotter();
        setupButtonVideo();
        setupButtonClose();
        setupMessage();
    }
    void setupButtonPlotter() {
        final Button button = (Button) findViewById(R.id.button_plotter);
        if (!dataQualityInfo.configDataQualityView.plotter.enable) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        this.setTitle(dataQualityInfo.getTitle());
        button.setText("Graph of " + dataQualityInfo.getTitle());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConfigApp app = ModelManager.getInstance(ActivityDataQuality.this).getConfigManager().getConfig().getApps(ModelFactory.MODEL_PLOTTER);
                Intent intent = new Intent();
                intent.setClassName(app.getPackage_name(), "org.md2k.plotter.ActivityPlot");
                DataSourceClient dataSourceClient = getDataSourceClient();
                if (dataSourceClient != null) {
                    intent.putExtra(DataSourceClient.class.getSimpleName(), dataSourceClient);
                    startActivity(intent);
                }
                ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(ActivityDataQuality.this).find(new DataSourceBuilder(dataQualityInfo.configDataQualityView.plotter.datasource));
                if (dataSourceClientArrayList.size() > 0) {
                }
            }
        });
    }

    DataSourceClient getDataSourceClient() {
        ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(ActivityDataQuality.this).find(new DataSourceBuilder(dataQualityInfo.configDataQualityView.plotter.datasource));
        if (dataSourceClientArrayList.size() > 0)
            return dataSourceClientArrayList.get(dataSourceClientArrayList.size() - 1);
        else return null;
    }


    void setupButtonVideo() {
        final Button button = (Button) findViewById(R.id.button_video);
        if (!dataQualityInfo.configDataQualityView.video.enable) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        button.setText(dataQualityInfo.getTitle() + " help video");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDataQuality.this, ActivityYouTube.class);
                intent.putExtra(VIDEO_LINK, dataQualityInfo.configDataQualityView.video.link);
                startActivity(intent);
            }
        });
    }

    void setupMessage() {
        TextView textView = (TextView) findViewById(R.id.textView_message);
        textView.setText(dataQualityInfo.configDataQualityView.message.text);
        TextView textView1 = (TextView) findViewById(R.id.textView_message_header);
        textView1.setText("If you are experiencing bad data quality:");

    }

    void setupButtonClose() {
        final Button buttonClose = (Button) findViewById(R.id.button_1);
        buttonClose.setText("Close");
        buttonClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
