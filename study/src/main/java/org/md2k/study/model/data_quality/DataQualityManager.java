package org.md2k.study.model.data_quality;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;
import org.md2k.study.system_health.ServiceSystemHealth;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.DATA_QUALITY;

import java.io.FileNotFoundException;
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
public class DataQualityManager extends Model {
    private static final String TAG = DataQualityManager.class.getSimpleName();
    ArrayList<DataQuality> dataQualities;
    Status[] dataQuality;

    public DataQualityManager(Context context, DataKitAPI dataKitAPI, Operation operation) {
        super(context, dataKitAPI, operation);
        dataQualities = new ArrayList<>();
        ArrayList<DataSource> dataSources = ConfigManager.getInstance(context).getConfig().getData_quality();
        dataQuality=new Status[dataSources.size()];
        for (int i = 0; i < dataSources.size(); i++)
            dataQualities.add(new DataQuality(context, dataKitAPI, dataSources.get(i)));
        for(int i=0;i<dataSources.size();i++)
            dataQuality[i]=new Status(DATA_QUALITY.BAND_OFF,"Band Off");
    }

    public void reset() {
        stop();
        start();
    }
    String getMessage(int sample){
        switch (sample) {
            case DATA_QUALITY.BAND_OFF:
                return "Not Connected";
            case DATA_QUALITY.NOT_WORN:
            case DATA_QUALITY.NOISE:
            case DATA_QUALITY.BAND_LOOSE:
                return "Bad Quality";
            case DATA_QUALITY.GOOD:
                return  "Good";
        }
        return "";
    }

    public void start() {
        Log.d(TAG, "dataquality=start");
        for (int i = 0; i < dataQualities.size(); i++) {
            final int finalI = i;
            dataQualities.get(i).start(new ReceiveCallBack() {
                @Override
                public void onReceive(DataSource dataSource, int sample[]) {
                    if(sample.length==1){
                        String message = dataQualities.get(finalI).getName() + "-"+getMessage(sample[0]);
                        dataQuality[finalI] = new Status(sample[0], message);

                    }else{
                        String message="Respiration - "+getMessage(sample[0]);
                        dataQuality[0] = new Status(sample[0], message);
                        message="ECG - "+getMessage(sample[1]);
                        dataQuality[1] = new Status(sample[1], message);
                    }
                    Intent intent=new Intent(ServiceSystemHealth.INTENT_NAME);
                    intent.putExtra(ServiceSystemHealth.TYPE, ServiceSystemHealth.DATA_QUALITY);
                    intent.putExtra(ServiceSystemHealth.VALUE, dataQuality);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                }
            });
        }
    }

    public void stop() {
        Log.d(TAG,"dataquality=stop");
        for (int i = 0; i < dataQualities.size(); i++)
            dataQualities.get(i).stop();
        ArrayList<DataSource> dataSources = ConfigManager.getInstance(context).getConfig().getData_quality();
        for(int i=0;i<dataSources.size();i++)
            dataQuality[i]=new Status(DATA_QUALITY.BAND_OFF,"Band Off");
    }

    @Override
    public Status getStatus() {
        return null;
    }
}
