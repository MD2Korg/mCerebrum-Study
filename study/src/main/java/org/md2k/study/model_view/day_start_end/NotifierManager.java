package org.md2k.study.model_view.day_start_end;

import android.content.Context;
import android.os.Handler;

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
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.notification.NotificationRequests;
import org.md2k.utilities.data_format.notification.NotificationResponse;

import java.util.ArrayList;

/**
 * Created by monowar on 3/10/16.
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
    long lastAckTimeStamp=0;
    long lastRequestTimeStamp=0;

    public NotifierManager(Context context) {
        Log.d(TAG, "NotifierManager()...");
        this.context = context;
        handler = new Handler();
        handlerSubscribeResponse = new Handler();
        handlerSubscribeAck=new Handler();
    }

    public void set(Callback callback, NotificationRequests notificationRequests) throws DataKitException {
        Log.d(TAG,"NotifierManager...set()...");
        Log.d(TAG, "datakit register ... before register()");
        dataSourceClientRequest = DataKitAPI.getInstance(context).register(new DataSourceBuilder().setType(DataSourceType.NOTIFICATION_REQUEST));
        Log.d(TAG, "datakit register ... after register() " + dataSourceClientRequest.getStatus().getStatusMessage());
        this.notificationRequests = notificationRequests;
        this.callback = callback;
        Log.d(TAG, "before runnableSubscribeResponse..");
        handlerSubscribeResponse.post(runnableSubscribeResponse);
        handlerSubscribeAck.post(runnableSubscribeAcknowledge);
        lastAckTimeStamp=0;
        lastRequestTimeStamp=DateTime.getDateTime();
        handler.post(runnableNotify);
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
            dataSourceClientResponses = null;
            Log.d(TAG, "...clear()");
        }catch (Exception e){

        }
    }

    Runnable runnableNotify = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableNotify...");
            try {
                if(lastRequestTimeStamp>lastAckTimeStamp) {
                    insertDataToDataKit(notificationRequests);
                    handler.postDelayed(this,2000);
                }

            } catch (DataKitException e) {
                e.printStackTrace();
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
                }
            } catch (DataKitException e) {
                e.printStackTrace();
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
                }
            } catch (DataKitException e) {
                e.printStackTrace();
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
                                e.printStackTrace();
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
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            lastAckTimeStamp=DateTime.getDateTime();
                        }
                    });
                    t.start();
                }
            });
        }
    }


    private void insertDataToDataKit(NotificationRequests notificationRequests) throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(context);
        Gson gson=new Gson();
        JsonObject sample = new JsonParser().parse(gson.toJson(notificationRequests)).getAsJsonObject();
        DataTypeJSONObject dataTypeJSONObject=new DataTypeJSONObject(DateTime.getDateTime(), sample);
        dataKitAPI.insert(dataSourceClientRequest, dataTypeJSONObject);
        Log.d(TAG, "...insertDataToDataKit()");
    }
}
