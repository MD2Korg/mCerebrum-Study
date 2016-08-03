package org.md2k.study.model_view.data_quality;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.config.ConfigDataQualityView;
import org.md2k.study.controller.ModelManager;
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
    ReceiveCallBack receiveCallBack;
    Context context;
    DataSourceClient dataSourceClient;
    Handler handler;
    long lastReceivedTimeStamp;
    DataQualityInfo dataQualityInfo;

    public DataQuality(Context context, DataSource dataSource, DataQualityInfo dataQualityInfo, ReceiveCallBack receiveCallBack) {
        this.dataSource = dataSource;
        this.receiveCallBack = receiveCallBack;
        this.context = context;
        this.dataQualityInfo=dataQualityInfo;
        handler = new Handler();
    }

    public DataSource createDataSource(DataSource dataSource) {
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
        return dataSourceBuilder.build();
    }

    public void start() {
        Log.d(TAG,"DataQuality start()..."+dataSource.getType()+" "+dataSource.getId());
        handler.post(runnableSubscribe);
    }

    Runnable runnableSubscribe = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"runnableSubscribe..."+dataSource.getType()+" "+dataSource.getId());
//            if(dataSource.getId().equals("ECG"))
//                Log.d(TAG,"here");

            try {
                ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(context).find(new DataSourceBuilder(createDataSource(dataSource)));
                if (dataSourceClientArrayList.size() == 0)
                    handler.postDelayed(this, 1000);
                else {
                    lastReceivedTimeStamp = DateTime.getDateTime();
                    dataSourceClient = dataSourceClientArrayList.get(dataSourceClientArrayList.size() - 1);
                    final ArrayList<ConfigDataQualityView> configDataQualityViews = ModelManager.getInstance(context).getConfigManager().getConfig().getData_quality_view();
                    dataQualityInfo.setConfigDataQualityView(configDataQualityViews, dataSourceClient.getDataSource());
                    handler.removeCallbacks(runnableCheckAvailability);
                    handler.postDelayed(runnableCheckAvailability, RESTART_TIME);
                    DataKitAPI.getInstance(context).subscribe(dataSourceClient, new OnReceiveListener() {
                        @Override
                        public void onReceived(final DataType dataType) {
                            if(dataType instanceof DataTypeInt) {
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            int sample = ((DataTypeInt) dataType).getSample();
                                            if (sample != DATA_QUALITY.BAND_OFF)
                                                lastReceivedTimeStamp = DateTime.getDateTime();
                                            receiveCallBack.onReceive(dataSourceClient, sample);
                                        }catch (Exception ignored){

                                        }
                                    }
                                });
                                t.start();
                            }
                        }
                    });
                }
            } catch (DataKitException e) {
                handler.postDelayed(this, 1000);
                Log.e(TAG,"error subscribing..runnableSubscribe...");
                e.printStackTrace();
            }
        }
    };
    Runnable runnableCheckAvailability = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableCheckAvailability()...check if data received..in time..");
            if (DateTime.getDateTime() - lastReceivedTimeStamp > RESTART_TIME) {
                try {
                    if (dataSourceClient.getDataSource().getPlatform().getType().equals(PlatformType.AUTOSENSE_CHEST) || dataSourceClient.getDataSource().getPlatform().getType().equals(PlatformType.AUTOSENSE_WRIST)) {
                        Log.d(TAG, "runnableCheckAvailability()...autosense restart");
                        Intent intent = new Intent();
                        ConfigApp app = ModelManager.getInstance(context).getConfigManager().getConfig().getApps("autosense");
                        intent.setClassName(app.getPackage_name(), app.getService());
                        context.stopService(intent);
                    } else if (dataSourceClient.getDataSource().getPlatform().getType().equals(PlatformType.MICROSOFT_BAND)) {
                        Log.d(TAG, "runnableCheckAvailability()... microsoft_band restart");
                        Intent intent = new Intent();
                        ConfigApp app = ModelManager.getInstance(context).getConfigManager().getConfig().getApps("microsoftband");
                        intent.setClassName(app.getPackage_name(), app.getService());
                        context.stopService(intent);
                    }
                }catch (Exception e){

                }
            }
            handler.postDelayed(this, RESTART_TIME);
        }
    };

    public void stop() {
        try {
            handler.removeCallbacks(runnableSubscribe);
            handler.removeCallbacks(runnableCheckAvailability);
            if (dataSourceClient != null && DataKitAPI.getInstance(context).isConnected())
                DataKitAPI.getInstance(context).unsubscribe(dataSourceClient);
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }
}
