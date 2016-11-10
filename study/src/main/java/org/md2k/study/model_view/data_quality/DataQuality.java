package org.md2k.study.model_view.data_quality;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigDataQualityView;
import org.md2k.study.controller.ModelManager;
import org.md2k.utilities.Report.Log;

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
class DataQuality {
    private static final String TAG = DataQuality.class.getSimpleName();
    private static final long DELAY_TIME = 3500;
    private DataSource dataSource;
    private ReceiveCallBack receiveCallBack;
    private Context context;
    private DataSourceClient dataSourceClient;
    private Handler handlerSubscribe;
    private DataQualityInfo dataQualityInfo;
    private Handler handlerNoData;

    DataQuality(Context context, DataSource dataSource, DataQualityInfo dataQualityInfo, ReceiveCallBack receiveCallBack) {
        this.dataSource = dataSource;
        this.receiveCallBack = receiveCallBack;
        this.context = context;
        this.dataQualityInfo=dataQualityInfo;
        handlerSubscribe = new Handler();
        handlerNoData=new Handler();
    }

    private DataSource createDataSource(DataSource dataSource) {
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
        return dataSourceBuilder.build();
    }

    public void start() {
        Log.d(TAG,"DataQuality start()..."+dataSource.getType()+" "+dataSource.getId());
        handlerSubscribe.removeCallbacks(runnableSubscribe);
        handlerSubscribe.post(runnableSubscribe);
        handlerNoData.postDelayed(runnableNoData,DELAY_TIME);
    }
    private Runnable runnableNoData=new Runnable() {
        @Override
        public void run() {
            DataTypeInt sample = new DataTypeInt(DateTime.getDateTime(), Status.DATAQUALITY_OFF);
            receiveCallBack.onReceive(sample);
            handlerNoData.postDelayed(this,DELAY_TIME);
        }
    };

    private Runnable runnableSubscribe = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"runnableSubscribe..."+dataSource.getType()+" "+dataSource.getId());
//            if(dataSource.getId().equals("ECG"))
//                Log.d(TAG,"here");

            try {
                ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitAPI.getInstance(context).find(new DataSourceBuilder(createDataSource(dataSource)));
                if (dataSourceClientArrayList.size() == 0)
                    handlerSubscribe.postDelayed(this, 1000);
                else {
                    dataSourceClient = dataSourceClientArrayList.get(dataSourceClientArrayList.size() - 1);
                    final ArrayList<ConfigDataQualityView> configDataQualityViews = ModelManager.getInstance(context).getConfigManager().getConfig().getData_quality_view();
                    dataQualityInfo.setConfigDataQualityView(configDataQualityViews, dataSourceClient.getDataSource());
                    DataKitAPI.getInstance(context).subscribe(dataSourceClient, new OnReceiveListener() {
                        @Override
                        public void onReceived(final DataType dataType) {
                            if(dataType instanceof DataTypeInt) {
                                handlerNoData.removeCallbacks(runnableNoData);

                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            DataTypeInt sample = ((DataTypeInt) dataType);
                                            receiveCallBack.onReceive(sample);
                                        }catch (Exception ignored){

                                        }
                                    }
                                });
                                t.start();
                                handlerNoData.postDelayed(runnableNoData, DELAY_TIME);
                            }
                        }
                    });
                }
            } catch (DataKitException e) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.INTENT_RESTART));
            }
        }
    };
    void stop() {
        try {
            handlerSubscribe.removeCallbacks(runnableSubscribe);
            handlerNoData.removeCallbacks(runnableNoData);
//            handlerSubscribe.removeCallbacks(runnableCheckAvailability);
            if (dataSourceClient != null && DataKitAPI.getInstance(context).isConnected()) {
                DataKitAPI.getInstance(context).unsubscribe(dataSourceClient);
                dataSourceClient=null;
            }
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }
}
