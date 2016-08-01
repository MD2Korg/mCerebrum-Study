package org.md2k.study.model_view.data_quality;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigDataQualityView;

import java.util.ArrayList;

/**
 * Created by monowar on 3/17/16.
 */
public class DataQualityInfo {
    public static final int QSIZE = 3;
    private static final String TAG = DataQualityInfo.class.getSimpleName();
    ConfigDataQualityView configDataQualityView;
    String message;
    int qualities[];
    int quality;
    int now;
    long lastReceivedTime;

    DataQualityInfo() {
        quality = Status.DATAQUALITY_OFF;
        now = 0;
        qualities = new int[QSIZE];
        message = "";
        lastReceivedTime =0;
    }

    public String getTitle() {
        if(configDataQualityView==null) return "";
        return configDataQualityView.getName();
    }

    public int getQuality() {
        if(DateTime.getDateTime()- lastReceivedTime >10000) {
            quality=Status.DATAQUALITY_OFF;
            Status curStatus = new Status(0, this.quality);
            message = getTitle() + " - " + curStatus.getMessage();
        }
        return quality;
    }


    public String getMessage() {
        return message;
    }

    public void setConfigDataQualityView(ArrayList<ConfigDataQualityView> configDataQualityViews, DataSource dataSource) {
        for(int i=0;i<configDataQualityViews.size();i++){
            DataSource confDataSource=configDataQualityViews.get(i).getPlotter().getDatasource();
            if(confDataSource.getType().equals(DataSourceType.RESPIRATION) && confDataSource.getType().equals(dataSource.getId())) {
                configDataQualityView = configDataQualityViews.get(i);
                return;
            }
            if(confDataSource.getType().equals(DataSourceType.ECG) && confDataSource.getType().equals(dataSource.getId())) {
                configDataQualityView = configDataQualityViews.get(i);
                return;
            }
            if(confDataSource.getPlatform()==null || confDataSource.getPlatform().getId()==null || confDataSource.getPlatform().getType()==null)
                continue;
            else if(confDataSource.getPlatform().getType().equals(dataSource.getPlatform().getType()) && confDataSource.getPlatform().getId().equals(dataSource.getPlatform().getId())) {
                configDataQualityView = configDataQualityViews.get(i);
                return;
            }
        }
    }

    public void set(DataSourceClient dataSourceClient, int value) {
        lastReceivedTime = DateTime.getDateTime();
        if (dataSourceClient.getDataSource().getId()!=null && (dataSourceClient.getDataSource().getId().equals(DataSourceType.RESPIRATION) || dataSourceClient.getDataSource().getId().equals(DataSourceType.ECG))) {
            now = now % QSIZE;
            this.qualities[now++] = value;
            this.quality = findMaximumOccurrence();
        } else
            this.quality = value;
        Status curStatus = new Status(0, this.quality);
        message = getTitle() + " - " + curStatus.getMessage();
    }

    int findMaximumOccurrence() {
        int count;
        int maxCount = 0;
        int value = 0;
        for (int i = 0; i < QSIZE; i++) {
            count = 1;
            for (int j = i + 1; j < QSIZE; j++) {
                if (qualities[i] == qualities[j]) {
                    count++;
                }
            }
            if (count > maxCount || (count == maxCount && qualities[i] == Status.DATAQUALITY_GOOD)) {
                maxCount = count;
                value = qualities[i];
            }
        }
        return value;
    }

}
