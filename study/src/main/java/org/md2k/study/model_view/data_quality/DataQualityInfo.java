package org.md2k.study.model_view.data_quality;

import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.DataQuality;

/**
 * Created by monowar on 3/17/16.
 */
public class DataQualityInfo {
    public static final int QSIZE = 3;
    private static final String TAG = DataQualityInfo.class.getSimpleName();
    org.md2k.study.config.DataQuality dataQuality;
    String title;
    String message;
    int qualities[];
    int quality;
    int now;
    long lastReceivedTime;
    public static final long RESTART_TIME=300000;
    long lastTimeStampNoData;

    DataQualityInfo(org.md2k.study.config.DataQuality dataQuality) {
        quality = Status.DATAQUALITY_OFF;
        this.dataQuality = dataQuality;
        now = 0;
        qualities = new int[QSIZE];
        message = "";
        lastReceivedTime =0;
        setTitle();
    }

    void setTitle(){
        if(dataQuality.name!=null) title=dataQuality.name;
        else {
            if (dataQuality.datasource_quality.getId() != null && dataQuality.datasource_quality.getId().equals(DataSourceType.RESPIRATION))
                title = "Respiration";
            else if (dataQuality.datasource_quality.getId() != null && dataQuality.datasource_quality.getId().equals(DataSourceType.ECG))
                title = "ECG";
            else if (dataQuality.datasource_quality.getPlatform() != null && dataQuality.datasource_quality.getPlatform().getType() != null && dataQuality.datasource_quality.getPlatform().getId() != null) {
                if (dataQuality.datasource_quality.getPlatform().getId().equals(PlatformId.LEFT_WRIST) && dataQuality.datasource_quality.getPlatform().getType().equals(PlatformType.MICROSOFT_BAND))
                    title = "Left Wrist(M)";
                else if (dataQuality.datasource_quality.getPlatform().getId().equals(PlatformId.RIGHT_WRIST) && dataQuality.datasource_quality.getPlatform().getType().equals(PlatformType.MICROSOFT_BAND))
                    title = "Right Wrist(M)";
                else if (dataQuality.datasource_quality.getPlatform().getId().equals(PlatformId.LEFT_WRIST) && dataQuality.datasource_quality.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST))
                    title = "Left Wrist(A)";
                else if (dataQuality.datasource_quality.getPlatform().getId().equals(PlatformId.RIGHT_WRIST) && dataQuality.datasource_quality.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST))
                    title = "Right Wrist(A)";
                else title = "-";

            } else if (dataQuality.datasource_quality.getPlatform() != null && dataQuality.datasource_quality.getPlatform().getId() != null) {
                if (dataQuality.datasource_quality.getPlatform().getId().equals(PlatformId.LEFT_WRIST))
                    title = "Left Wrist";
                else if (dataQuality.datasource_quality.getPlatform().getId().equals(PlatformId.RIGHT_WRIST))
                    title = "Right Wrist";
                else title = "-";
            } else if (dataQuality.datasource_quality.getPlatform() != null && dataQuality.datasource_quality.getPlatform().getType() != null) {
                if (dataQuality.datasource_quality.getPlatform().getType().equals(PlatformType.MICROSOFT_BAND))
                    title = "Wrist(M)";
                else if (dataQuality.datasource_quality.getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST))
                    title = "Wrist(A)";
                else title = "-";
            } else
                title = "-";
        }
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

    public void setQualities(DataQuality dataQuality, int value) {
        this.dataQuality=dataQuality;
        lastReceivedTime = DateTime.getDateTime();
        setTitle();
        if (dataQuality.datasource_quality.getId()!=null && (dataQuality.datasource_quality.getId().equals(DataSourceType.RESPIRATION) || dataQuality.datasource_quality.getId().equals(DataSourceType.ECG))) {
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
