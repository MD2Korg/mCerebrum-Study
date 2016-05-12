package org.md2k.study.model_view.data_quality;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.study.R;
import org.md2k.study.config.App;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;

import java.util.ArrayList;

public class ActivityDataQuality extends AppCompatActivity {
    private static final String TAG = ActivityDataQuality.class.getSimpleName();
    org.md2k.study.config.DataQuality dataQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_quality);
        int id = getIntent().getIntExtra("id", 0);
        dataQuality = ModelManager.getInstance(ActivityDataQuality.this).getConfigManager().getConfig().getData_quality().get(id);
        setupButtonPlotter();
        setupButtonVideo();
        setupButtonClose();
        setupMessage();
    }

    void setupButtonPlotter() {
        final Button button = (Button) findViewById(R.id.button_plotter);
        if (!dataQuality.plotter) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        this.setTitle(dataQuality.name);
        button.setText("Graph of " + dataQuality.name);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                App app = ModelManager.getInstance(ActivityDataQuality.this).getConfigManager().getConfig().getApps(ModelFactory.MODEL_PLOTTER);
                Intent intent=new Intent();
                intent.setClassName(app.getPackage_name(), "org.md2k.plotter.ActivityPlot");
                ArrayList<DataSourceClient> dataSourceClientArrayList=DataKitAPI.getInstance(ActivityDataQuality.this).find(new DataSourceBuilder(dataQuality.datasource_plot));
                if(dataSourceClientArrayList.size()>0) {
                    intent.putExtra(DataSourceClient.class.getSimpleName(), dataSourceClientArrayList.get(0));
                    startActivity(intent);
                }
            }
        });
    }

    void setupButtonVideo() {
        final Button button = (Button) findViewById(R.id.button_video);
        if (!dataQuality.video) {
            button.setVisibility(View.INVISIBLE);
            return;
        }
        button.setText(dataQuality.name+" help video");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dataQuality.video_link)));
            }
        });
    }
    void setupMessage(){
        TextView textView= (TextView) findViewById(R.id.textView_message);
        textView.setText(dataQuality.message_link);
        TextView textView1= (TextView) findViewById(R.id.textView_message_header);
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
