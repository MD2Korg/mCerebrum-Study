package org.md2k.study.model_view.day_start_end;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.application.Application;
import org.md2k.datakitapi.source.application.ApplicationBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Constants;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.notification.NotificationRequests;
import org.md2k.utilities.data_format.notification.NotificationResponse;

import java.util.ArrayList;

/**
 * Copyright (c) 2016, The University of Memphis, MD2K Center
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
public class NotifierManager {
    private static final String TAG = NotifierManager.class.getSimpleName();
    private Context context;
    private Handler handler;
    private Handler handlerSubscribeResponse;
    private Handler handlerSubscribeAck;
    private DataSourceClient dataSourceClientRequest;
    private ArrayList<DataSourceClient> dataSourceClientResponses;
    private ArrayList<DataSourceClient> dataSourceClientAcks;
    private NotificationRequests notificationRequests;
    private Callback callback;
    long lastAckTimeStamp = 0;
    long lastRequestTimeStamp = 0;

    public NotifierManager(Context context) {
        Log.d(TAG, "NotifierManager()...");
        this.context = context;
        handler = new Handler();
        handlerSubscribeResponse = new Handler();
        handlerSubscribeAck = new Handler();
    }

    public void set() {
        try {
            dataSourceClientRequest = DataKitAPI.getInstance(context).register(new DataSourceBuilder().setType(DataSourceType.NOTIFICATION_REQUEST));
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    public void trigger(Callback callback, NotificationRequests notificationRequests) throws DataKitException {
        this.notificationRequests = notificationRequests;
        this.callback = callback;
        Log.d(TAG, "before runnableSubscribeResponse..");

        handler.removeCallbacks(runnableNotify);
        handlerSubscribeResponse.removeCallbacks(runnableSubscribeResponse);
        handlerSubscribeAck.removeCallbacks(runnableSubscribeAcknowledge);

        handlerSubscribeResponse.post(runnableSubscribeResponse);
        lastAckTimeStamp = 0;
        lastRequestTimeStamp = DateTime.getDateTime();
    }

    public void clear() {
        try {
            Log.d(TAG, "clear()...");
            handler.removeCallbacks(runnableNotify);
            handlerSubscribeResponse.removeCallbacks(runnableSubscribeResponse);
            handlerSubscribeAck.removeCallbacks(runnableSubscribeAcknowledge);
            if (dataSourceClientResponses != null)
                for (int i = 0; i < dataSourceClientResponses.size(); i++)
                    DataKitAPI.getInstance(context).unsubscribe(dataSourceClientResponses.get(i));
            if (dataSourceClientAcks != null) {
                for (int i = 0; i < dataSourceClientAcks.size(); i++)
                    DataKitAPI.getInstance(context).unsubscribe(dataSourceClientAcks.get(i));
            }
            dataSourceClientAcks = null;
            Log.d(TAG, "...clear()");
        } catch (Exception ignored) {

        }
    }

    Runnable runnableNotify = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableNotify...");
            if (lastRequestTimeStamp > lastAckTimeStamp) {
                insertDataToDataKit(notificationRequests);
                handler.postDelayed(this, 10000);
            }

        }
    };
    Runnable runnableSubscribeResponse = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "runnableSubscribeResponse...run()");
                Application application = new ApplicationBuilder().setId("org.md2k.notificationmanager").build();
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.NOTIFICATION_RESPONSE).setApplication(application);
                dataSourceClientResponses = DataKitAPI.getInstance(context).find(dataSourceBuilder);
                Log.d(TAG, "DataSourceClients...size=" + dataSourceClientResponses.size());
                if (dataSourceClientResponses.size() == 0) {
                    handlerSubscribeResponse.postDelayed(this, 1000);
                } else {
                    subscribeNotificationResponse();
                    handlerSubscribeAck.post(runnableSubscribeAcknowledge);
                }
            } catch (DataKitException e) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.INTENT_RESTART));
            }
        }
    };
    Runnable runnableSubscribeAcknowledge = new Runnable() {
        @Override
        public void run() {
            try {
                Application application = new ApplicationBuilder().setId("org.md2k.notificationmanager").build();
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.NOTIFICATION_ACKNOWLEDGE).setApplication(application);
                dataSourceClientAcks = DataKitAPI.getInstance(context).find(dataSourceBuilder);
                Log.d(TAG, "DataSourceClients...size=" + dataSourceClientAcks.size());
                if (dataSourceClientAcks.size() == 0) {
                    handlerSubscribeAck.postDelayed(this, 1000);
                } else {
                    subscribeNotificationAck();
                    handler.post(runnableNotify);
                }
            } catch (DataKitException e) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.INTENT_RESTART));
            }
        }
    };


    void subscribeNotificationResponse() throws DataKitException {
        Log.d(TAG, "subscribeNotificationResponse...");
        for (int i = 0; i < dataSourceClientResponses.size(); i++) {
            DataKitAPI.getInstance(context).subscribe(dataSourceClientResponses.get(i), new OnReceiveListener() {
                @Override
                public void onReceived(final DataType dataType) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataType;
                                Gson gson = new Gson();
                                NotificationResponse notificationResponse = gson.fromJson(dataTypeJSONObject.getSample().toString(), NotificationResponse.class);
                                Log.d(TAG, "notification_acknowledge = " + notificationResponse.getStatus());
                                handler.removeCallbacks(runnableNotify);
                                switch (notificationResponse.getStatus()) {
                                    case NotificationResponse.OK:
                                    case NotificationResponse.CANCEL:
                                        callback.onResponse(notificationResponse.getStatus());
                                        clear();
                                        break;
                                    case NotificationResponse.TIMEOUT:
                                        callback.onResponse(notificationResponse.getStatus());
                                        clear();
                                        break;
                                }
                            } catch (DataKitException e) {
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.INTENT_RESTART));
                            } catch (Exception ignored) {

                            }
                        }
                    });
                    t.start();
                }
            });
        }
    }

    void subscribeNotificationAck() throws DataKitException {
        for (int i = 0; i < dataSourceClientAcks.size(); i++) {
            DataKitAPI.getInstance(context).subscribe(dataSourceClientAcks.get(i), new OnReceiveListener() {
                @Override
                public void onReceived(final DataType dataType) {
                    lastAckTimeStamp = DateTime.getDateTime();
                }
            });
        }
    }


    private void insertDataToDataKit(NotificationRequests notificationRequests) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(context);
            Gson gson = new Gson();
            JsonObject sample = new JsonParser().parse(gson.toJson(notificationRequests)).getAsJsonObject();
            DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
            dataKitAPI.insert(dataSourceClientRequest, dataTypeJSONObject);
            Log.d(TAG, "...insertDataToDataKit()");
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }
}
