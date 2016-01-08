package org.md2k.study.systemhealth.sensorquality;

import android.content.Context;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Constants;
import org.md2k.study.systemhealth.Child;
import org.md2k.study.systemhealth.SystemHealthManager;
import org.md2k.study.systemhealth.SensorQualityInfo;
import org.md2k.utilities.datakit.DataKitHandler;

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
public class ChildSensorQuality extends Child {
    String platformType;
    String dataSourceType;
    String location;
    DataKitHandler dataKitHandler = null;

    ChildSensorQuality(Context context, SensorQualityInfo sensorQualityInfo) {
        super(context, "");
//        super(context, "", onDataUpdated);
        String loc = "";
        platformType = sensorQualityInfo.platform_type;
        dataSourceType = sensorQualityInfo.datasource_type;
        location = sensorQualityInfo.location;
        if (location != null) {
            if (location.startsWith("LEFT"))
                loc = "--Left";
            else if (location.startsWith("RIGHT"))
                loc = "--Right";
        }
        String p = platformType;
        switch (platformType) {
            case PlatformType.AUTOSENSE_CHEST:
                p = "Autosense";
                break;
            case PlatformType.MICROSOFT_BAND:
                p = "Microsoft Band";
                break;
            case PlatformType.PHONE:
                p = "Phone";
                break;
        }
        if (dataSourceType.equals(DataSourceType.BAND_CONTACT))
            name = p + loc;
        else
            name = p + loc + "-" + dataSourceType;
    }

    public void setDataKitHandler(DataKitHandler dataKitHandler) {
        this.dataKitHandler = dataKitHandler;
    }

    private boolean isValidLocation(DataSourceClient dataSourceClient) {
        if (platformType.equals(PlatformType.MICROSOFT_BAND)) {
            String curLocation = dataSourceClient.getDataSource().getMetadata().get("location");
            return !(curLocation == null || !curLocation.equals(location));
        } else return true;
    }

    private DataSourceClient getDataSourceClient(DataSourceBuilder dataSourceBuilder) {
        ArrayList<DataSourceClient> dataSourceClients;
        dataSourceClients = dataKitHandler.find(dataSourceBuilder);
        for (int j = 0; j < dataSourceClients.size(); j++) {
            if (isValidLocation(dataSourceClients.get(j)))
                return dataSourceClients.get(j);
        }
        return null;

    }

    private DataSourceBuilder createDataSourceBuilder(String dataSourceType) {
        Platform platform = new PlatformBuilder().setType(platformType).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setType(dataSourceType);
        return dataSourceBuilder;
    }

    private DataType getLastSample(DataSourceClient dataSourceClient) {
        ArrayList<DataType> dataTypes = dataKitHandler.query(dataSourceClient, 1);
        if (dataTypes == null || dataTypes.size() == 0) return null;
        return dataTypes.get(0);

    }

    private long getLastSampleTime(DataSourceClient dataSourceClient) {
        DataType dataType = getLastSample(dataSourceClient);
        if (dataType == null) return -1;
        else return dataType.getDateTime();
    }

    public void measureDataQuality() {
        long lastSampleTime;
        if (dataKitHandler == null)
            status = SystemHealthManager.RED;
        else {
            if (platformType.equals(PlatformType.MICROSOFT_BAND))
                lastSampleTime = getLastSampleTime(getDataSourceClient(createDataSourceBuilder(DataSourceType.ACCELEROMETER)));
            else
                lastSampleTime = getLastSampleTime(getDataSourceClient(createDataSourceBuilder(dataSourceType)));
            if (DateTime.getDateTime() - lastSampleTime > Constants.HEALTH_CHECK_REPEAT)
                status = SystemHealthManager.RED;
            else {
                if (platformType.equals(PlatformType.MICROSOFT_BAND)) {
                    DataTypeInt dataTypeInt = (DataTypeInt) getLastSample(getDataSourceClient(createDataSourceBuilder(DataSourceType.BAND_CONTACT)));
                    if (dataTypeInt == null || dataTypeInt.getSample() != 1)
                        status = SystemHealthManager.YELLOW;
                    else status = SystemHealthManager.GREEN;
                } else
                    status = SystemHealthManager.GREEN;
            }
        }
        updateStatus();
    }

    public void updateStatus() {
//        super.updateStatus();
//        if (onDataUpdated != null) onDataUpdated.onChange();
    }

    public void refresh() {
        measureDataQuality();
    }
}
