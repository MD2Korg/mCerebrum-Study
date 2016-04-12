package org.md2k.study.model_view.data_quality;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;

/**
 * Created by monowar on 3/17/16.
 */
public class DataQualityInfo {
    public static final int QSIZE = 3;
    private static final String TAG = DataQualityInfo.class.getSimpleName();
    DataSource dataSource;
    String title;
    String message;
    int qualities[];
    int quality;
    int now;
    long lastReceivedTime;
    public static final long RESTART_TIME=300000;
    long lastTimeStampNoData;

    DataQualityInfo(DataSource dataSource) {
        quality = Status.DATAQUALITY_OFF;
        this.dataSource = dataSource;
        now = 0;
        qualities = new int[QSIZE];
        message = "";
        lastReceivedTime =0;
        setTitle();
    }

    void setTitle(){
        if (dataSource.getId() != null && dataSource.getId().equals(DataSourceType.RESPIRATION))
            title="Respiration";
        else if(dataSource.getId() != null && dataSource.getId().equals(DataSourceType.ECG))
            title="ECG";
        else if(dataSource.getPlatform()!=null && dataSource.getPlatform().getType()!=null && dataSource.getPlatform().getId()!=null){
            if(dataSource.getPlatform().getId().equals(PlatformId.LEFT_WRIST) && dataSource.getPlatform().getType().equals(PlatformType.MICROSOFT_BAND))
                title="MSBand (L)";
            else if(dataSource.getPlatform().getId().equals(PlatformId.RIGHT_WRIST) && dataSource.getPlatform().getType().equals(PlatformType.MICROSOFT_BAND))
                title="MSBand (R)";
            else if(dataSource.getPlatform().getId().equals(PlatformId.LEFT_WRIST) && dataSource.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST))
                title="WristA (L)";
            else if(dataSource.getPlatform().getId().equals(PlatformId.RIGHT_WRIST) && dataSource.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST))
                title="WristA (R)";
            else title="-";

        }
        else if(dataSource.getPlatform()!=null && dataSource.getPlatform().getId()!=null){
            if(dataSource.getPlatform().getId().equals(PlatformId.LEFT_WRIST))
                title="Wrist (L)";
            else if(dataSource.getPlatform().getId().equals(PlatformId.RIGHT_WRIST))
                title="Wrist (R)";
            else title="-";
        }
        else if(dataSource.getPlatform()!=null && dataSource.getPlatform().getType()!=null){
            if(dataSource.getPlatform().getType().equals(PlatformType.MICROSOFT_BAND))
                title="MSBand";
            else if(dataSource.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST))
                title="WristA";
            else title="-";
        }
        else
            title="-";
    }

    public String getTitle() {
        return title;
    }

    public int getQuality() {
        if(DateTime.getDateTime()- lastReceivedTime >10000) {
            quality=Status.DATAQUALITY_OFF;
            Status curStatus = new Status(0, this.quality);
            message = title + " - " + curStatus.getMessage();
        }
        return quality;
    }


    public String getMessage() {
        return message;
    }

    public void setQualities(DataSource dataSource, int value) {
        this.dataSource=dataSource;
        lastReceivedTime = DateTime.getDateTime();
        setTitle();
        if (dataSource.getId()!=null && (dataSource.getId().equals(DataSourceType.RESPIRATION) || dataSource.getId().equals(DataSourceType.ECG))) {
            now = now % QSIZE;
            this.qualities[now++] = value;
            this.quality = findMaximumOccurrence();
        } else
            this.quality = value;
        Status curStatus = new Status(0, this.quality);
        message = title + " - " + curStatus.getMessage();
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
