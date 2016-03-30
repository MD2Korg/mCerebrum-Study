package org.md2k.study.model_view.data_quality;

import android.content.Context;
import android.os.Handler;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;

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
    DataSource dataSource;
    DataKitAPI dataKitAPI;
    ReceiveCallBack receiveCallBack;
    ArrayList<DataSourceClient> dataSourceClientArrayList;
    Context context;
    DataSourceClient dataSourceClient;
    Handler handler;

    public DataQuality(Context context, DataSource dataSource, ReceiveCallBack receiveCallBack) {
        this.dataSource = dataSource;
        this.receiveCallBack=receiveCallBack;
        this.context=context;
        handler=new Handler();
    }

    public DataSource createDataSource(DataSource dataSource) {
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
        dataSourceBuilder.setType(DataSourceType.STATUS);
        return dataSourceBuilder.build();
    }
    public void start(){
        dataKitAPI = DataKitAPI.getInstance(context);
        handler.post(runnableSubscribe);
    }
    Runnable runnableSubscribe=new Runnable() {
        @Override
        public void run() {
            dataSourceClientArrayList = dataKitAPI.find(new DataSourceBuilder(createDataSource(dataSource)));
            if(dataSourceClientArrayList.size()==0)
                handler.postDelayed(this, 1000);
            else {
                for (int i = 0; i < dataSourceClientArrayList.size(); i++) {
                    dataSourceClient = dataSourceClientArrayList.get(i);
                    dataKitAPI.subscribe(dataSourceClient, new OnReceiveListener() {
                        @Override
                        public void onReceived(DataType dataType) {
                            receiveCallBack.onReceive(dataSource, dataSourceClient, ((DataTypeIntArray) dataType).getSample());
                        }
                    });
                }
            }
        }
    };
    public void stop() {
        handler.removeCallbacks(runnableSubscribe);
        for(int i=0;i<dataSourceClientArrayList.size();i++)
        if (dataSourceClientArrayList.get(i) != null && dataKitAPI!=null && dataKitAPI.isConnected())
            dataKitAPI.unsubscribe(dataSourceClientArrayList.get(i));
    }
}
