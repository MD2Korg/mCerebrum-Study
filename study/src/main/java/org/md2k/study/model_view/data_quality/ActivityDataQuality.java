package org.md2k.study.model_view.data_quality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.study.Constants;
import org.md2k.study.R;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;

import java.util.ArrayList;

public class ActivityDataQuality extends AppCompatActivity {
    private static final String TAG = ActivityDataQuality.class.getSimpleName();

    public static final String VIDEO_LINK = "VIDEO_LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            int id = getIntent().getIntExtra("id", 0);
            DataQualityInfo dataQualityInfo = ((DataQualityManager) ModelManager.getInstance(this).getModel(ModelFactory.MODEL_DATA_QUALITY)).dataQualityInfos.get(id);
            if (dataQualityInfo.configDataQualityView == null) {
                Toast.makeText(this, "Could not connect the device. Wait for a minute and try again...", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                setContentView(R.layout.activity_data_quality);
                setupButtonPlotter(dataQualityInfo);
                setupButtonVideo(dataQualityInfo);
                setupButtonClose();
                setupMessage(dataQualityInfo);
            }
        }catch (Exception e){
            Toast.makeText(this, "Could not connect the device. Wait for a minute and try again...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    void setupButtonPlotter(final DataQualityInfo dataQualityInfo) {
        try {
            final Button button = (Button) findViewById(R.id.button_plotter);
            if (!dataQualityInfo.configDataQualityView.getPlotter().isEnable()) {
                button.setVisibility(View.INVISIBLE);
                return;
            }
            this.setTitle(dataQualityInfo.getTitle());
            button.setText("Graph of " + dataQualityInfo.getTitle());
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        ConfigApp app = ModelManager.getInstance(ActivityDataQuality.this).getConfigManager().getConfig().getApps(ModelFactory.MODEL_PLOTTER);
                        Intent intent = new Intent();
                        intent.setClassName(app.getPackage_name(), "org.md2k.plotter.ActivityPlot");
                        DataSourceClient dataSourceClient = getDataSourceClient(dataQualityInfo);
                        if (dataSourceClient != null) {
                            intent.putExtra(DataSourceClient.class.getSimpleName(), dataSourceClient);
                            startActivity(intent);
                        }
                    } catch (DataKitException e) {
                        LocalBroadcastManager.getInstance(ActivityDataQuality.this).sendBroadcast(new Intent(Constants.INTENT_RESTART));
                    }
                }
            });
        }catch (Exception ignored){
            LocalBroadcastManager.getInstance(ActivityDataQuality.this).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    DataSourceClient getDataSourceClient(DataQualityInfo dataQualityInfo) throws DataKitException {
        ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(ActivityDataQuality.this).find(new DataSourceBuilder(dataQualityInfo.configDataQualityView.getPlotter().getDatasource()));
        if (dataSourceClientArrayList.size() > 0)
            return dataSourceClientArrayList.get(dataSourceClientArrayList.size() - 1);
        else return null;
    }


    void setupButtonVideo(final DataQualityInfo dataQualityInfo) {
        final Button button = (Button) findViewById(R.id.button_video);
        if (!dataQualityInfo.configDataQualityView.getVideo().isEnable()) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        button.setText(dataQualityInfo.getTitle() + " help video");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDataQuality.this, ActivityYouTube.class);
                intent.putExtra(VIDEO_LINK, dataQualityInfo.configDataQualityView.getVideo().getLink());
                startActivity(intent);
            }
        });
    }

    void setupMessage(DataQualityInfo dataQualityInfo) {
        TextView textView = (TextView) findViewById(R.id.textView_message);
        textView.setText(dataQualityInfo.configDataQualityView.getMessage().getText());
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
