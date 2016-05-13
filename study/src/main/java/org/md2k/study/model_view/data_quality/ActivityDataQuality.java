package org.md2k.study.model_view.data_quality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.study.R;
import org.md2k.study.config.App;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;

import java.util.ArrayList;

public class ActivityDataQuality extends AppCompatActivity {
    private static final String TAG = ActivityDataQuality.class.getSimpleName();
    org.md2k.study.config.DataQuality dataQualityConfig;
    DataQualityInfo dataQualityInfo;
    public static final String VIDEO_LINK = "VIDEO_LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_quality);
        int id = getIntent().getIntExtra("id", 0);
        dataQualityConfig = ModelManager.getInstance(ActivityDataQuality.this).getConfigManager().getConfig().getData_quality().get(id);
        DataQualityManager dataQualityManager = (DataQualityManager) ModelManager.getInstance(this).getModel(ModelFactory.MODEL_DATA_QUALITY);
        dataQualityInfo = dataQualityManager.dataQualityInfos.get(id);
        setupButtonPlotter();
        setupButtonVideo();
        setupButtonClose();
        setupMessage();
    }

    void setupButtonPlotter() {
        final Button button = (Button) findViewById(R.id.button_plotter);
        if (!dataQualityConfig.plotter) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        this.setTitle(dataQualityInfo.title);
        button.setText("Graph of " + dataQualityInfo.title);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                App app = ModelManager.getInstance(ActivityDataQuality.this).getConfigManager().getConfig().getApps(ModelFactory.MODEL_PLOTTER);
                Intent intent = new Intent();
                intent.setClassName(app.getPackage_name(), "org.md2k.plotter.ActivityPlot");
                DataSourceClient dataSourceClient = getDataSourceClient();
                if (dataSourceClient != null) {
                    intent.putExtra(DataSourceClient.class.getSimpleName(), dataSourceClient);
                    startActivity(intent);
                }

                ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(ActivityDataQuality.this).find(new DataSourceBuilder(dataQualityConfig.datasource_plot));
                if (dataSourceClientArrayList.size() > 0) {
                }
            }
        });
    }

    DataSourceClient getDataSourceClient() {
        DataSource curDataSource;
        if (isWristType()) {
            PlatformBuilder platformBuilder = new PlatformBuilder(dataQualityConfig.datasource_plot.getPlatform());
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setPlatform(platformBuilder.build());
            ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(ActivityDataQuality.this).find(dataSourceBuilder);
            if (dataSourceClientArrayList.size() == 0) return null;
            DataSource dataSource = dataSourceClientArrayList.get(dataSourceClientArrayList.size() - 1).getDataSource();
            if (dataSource.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST)) {
                curDataSource = dataSourceBuilder.setType(dataQualityConfig.datasource_plot.getType()+"_X").build();
            } else curDataSource = dataQualityConfig.datasource_plot;
        } else {
            curDataSource = dataQualityConfig.datasource_plot;
        }
        ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(ActivityDataQuality.this).find(new DataSourceBuilder(curDataSource));
        if (dataSourceClientArrayList.size() > 0)
            return dataSourceClientArrayList.get(dataSourceClientArrayList.size() - 1);
        else return null;
    }

    boolean isWristType() {
        if (dataQualityConfig.datasource_plot.getPlatform() == null) return false;
        if (dataQualityConfig.datasource_plot.getPlatform().getId() == null) return false;
        if (dataQualityConfig.datasource_plot.getPlatform().getId().equals(PlatformId.LEFT_WRIST))
            return true;
        if (dataQualityConfig.datasource_plot.getPlatform().getId().equals(PlatformId.RIGHT_WRIST))
            return true;
        return false;
    }

    void setupButtonVideo() {
        final Button button = (Button) findViewById(R.id.button_video);
        if (!dataQualityConfig.video) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        button.setText(dataQualityInfo.title + " help video");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDataQuality.this, ActivityYouTube.class);
                intent.putExtra(VIDEO_LINK, dataQualityConfig.video_link);
                startActivity(intent);
            }
        });
    }

    void setupMessage() {
        TextView textView = (TextView) findViewById(R.id.textView_message);
        textView.setText(dataQualityConfig.message_link);
        TextView textView1 = (TextView) findViewById(R.id.textView_message_header);
        textView1.setText("Tips to get good signal");

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
