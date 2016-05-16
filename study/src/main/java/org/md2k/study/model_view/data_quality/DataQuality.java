package org.md2k.study.model_view.data_quality;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.config.ConfigManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.DATA_QUALITY;

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
public class DataQuality {
    private static final String TAG = DataQuality.class.getSimpleName();
    public static final long RESTART_TIME = 120000;
    DataSource dataSource;
    DataKitAPI dataKitAPI;
    ReceiveCallBack receiveCallBack;
    Context context;
    DataSourceClient dataSourceClient;
    Handler handler;
    long lastReceivedTimeStamp;

    public DataQuality(Context context, DataSource dataSource, ReceiveCallBack receiveCallBack) {
        this.dataSource = dataSource;
        this.receiveCallBack = receiveCallBack;
        this.context = context;
        handler = new Handler();
    }

    public DataSource createDataSource(DataSource dataSource) {
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
        return dataSourceBuilder.build();
    }

    public void start() {
        dataKitAPI = DataKitAPI.getInstance(context);
        handler.post(runnableSubscribe);
    }

    Runnable runnableSubscribe = new Runnable() {
        @Override
        public void run() {
            ArrayList<DataSourceClient> dataSourceClientArrayList = dataKitAPI.find(new DataSourceBuilder(createDataSource(dataSource)));
            if (dataSourceClientArrayList.size() == 0)
                handler.postDelayed(this, 1000);
            else {
                lastReceivedTimeStamp = DateTime.getDateTime();
                handler.postDelayed(runnableCheckAvailability, RESTART_TIME);
                dataSourceClient = dataSourceClientArrayList.get(dataSourceClientArrayList.size()-1);
                dataKitAPI.subscribe(dataSourceClient, new OnReceiveListener() {
                    @Override
                    public void onReceived(DataType dataType) {
                        int sample = ((DataTypeInt) dataType).getSample();
                        if (sample != DATA_QUALITY.BAND_OFF)
                            lastReceivedTimeStamp = DateTime.getDateTime();
                        receiveCallBack.onReceive(dataSourceClient, sample);
                    }
                });
            }
        }
    };
    Runnable runnableCheckAvailability = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableCheckAvailability()...");
            if (DateTime.getDateTime() - lastReceivedTimeStamp > RESTART_TIME) {
                if (dataSourceClient.getDataSource().getPlatform().getType().equals(PlatformType.AUTOSENSE_CHEST) || dataSourceClient.getDataSource().getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST)) {
                    Log.d(TAG, "runnableCheckAvailability()...autosense");
                    Intent intent = new Intent();
                    ConfigApp app = ConfigManager.getInstance(context).getConfig().getApps("autosense");
                    intent.setClassName(app.getPackage_name(), app.getService());
                    context.stopService(intent);
                    context.startService(intent);
                } else if (dataSourceClient.getDataSource().getPlatform().getType().equals(PlatformType.MICROSOFT_BAND)) {
                    Log.d(TAG, "runnableCheckAvailability()...microsoftband");
                    Intent intent = new Intent();
                    ConfigApp app = ConfigManager.getInstance(context).getConfig().getApps("microsoftband");
                    intent.setClassName(app.getPackage_name(), app.getService());
                    context.stopService(intent);
                    context.startService(intent);
                }
            }
            handler.postDelayed(this, RESTART_TIME);
        }
    };

    public void stop() {
        handler.removeCallbacks(runnableSubscribe);
        handler.removeCallbacks(runnableCheckAvailability);
            if (dataSourceClient != null && dataKitAPI != null && dataKitAPI.isConnected())
                dataKitAPI.unsubscribe(dataSourceClient);
    }
}
