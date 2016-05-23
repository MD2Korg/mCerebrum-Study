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
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.NotificationAcknowledge;
import org.md2k.utilities.data_format.NotificationRequest;

import java.util.ArrayList;

/**
 * Created by monowar on 3/10/16.
 */
public class NotifierManager {
    private static final String TAG = NotifierManager.class.getSimpleName();
    private Context context;
    private Handler handler;
    private DataSourceClient dataSourceClientRequest;
    private ArrayList<DataSourceClient> dataSourceClientAcknowledges;
    private ArrayList<NotificationRequest> notificationRequests;
    private Handler handlerSubscribe;
    private Callback callback;

    public NotifierManager(Context context) {
        Log.d(TAG, "NotifierManager()...");
        this.context = context;
        handler = new Handler();
        handlerSubscribe = new Handler();
    }

    public void set(Callback callback, ArrayList<NotificationRequest> notificationRequests) throws DataKitException {
        Log.d(TAG,"NotifierManager...set()...");
        Log.d(TAG, "datakit register ... before register()");
        dataSourceClientRequest = DataKitAPI.getInstance(context).register(new DataSourceBuilder().setType(DataSourceType.NOTIFICATION_REQUEST));
        Log.d(TAG, "datakit register ... after register() " + dataSourceClientRequest.getStatus().getStatusMessage());
        this.notificationRequests = notificationRequests;
        this.callback = callback;
        Log.d(TAG, "before runnableSubscribe..");
        handlerSubscribe.post(runnableSubscribe);
        handler.postDelayed(runnableNotify,3000);
    }
    public void clear() {
        try {
            Log.d(TAG, "clear()...");
            handler.removeCallbacks(runnableNotify);
            handlerSubscribe.removeCallbacks(runnableSubscribe);
            if (dataSourceClientAcknowledges != null)
                for (int i = 0; i < dataSourceClientAcknowledges.size(); i++)
                    DataKitAPI.getInstance(context).unsubscribe(dataSourceClientAcknowledges.get(i));
            dataSourceClientAcknowledges = null;
            Log.d(TAG, "...clear()");
        }catch (Exception e){

        }
    }

    Runnable runnableNotify = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableNotify...");
            try {
                insertDataToDataKit(notificationRequests);
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };
    Runnable runnableSubscribe = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "runnableSubscribe...run()");
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.NOTIFICATION_ACKNOWLEDGE);
                dataSourceClientAcknowledges = DataKitAPI.getInstance(context).find(dataSourceBuilder);
                Log.d(TAG, "DataSourceClients...size=" + dataSourceClientAcknowledges.size());
                if (dataSourceClientAcknowledges.size() == 0) {
                    handlerSubscribe.postDelayed(this, 1000);
                } else {
                    subscribeNotificationAcknowledge();
                }
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };


    void subscribeNotificationAcknowledge() throws DataKitException {
        Log.d(TAG, "subscribeNotificationAcknowledge...");
        for (int i = 0; i < dataSourceClientAcknowledges.size(); i++) {
            DataKitAPI.getInstance(context).subscribe(dataSourceClientAcknowledges.get(i), new OnReceiveListener() {
                @Override
                public void onReceived(final DataType dataType) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataType;
                                Gson gson = new Gson();
                                NotificationAcknowledge notificationAcknowledge = gson.fromJson(dataTypeJSONObject.getSample().toString(), NotificationAcknowledge.class);
                                Log.d(TAG, "notification_acknowledge = " + notificationAcknowledge.getStatus());
                                handler.removeCallbacks(runnableNotify);
                                switch (notificationAcknowledge.getStatus()) {
                                    case NotificationAcknowledge.OK:
                                    case NotificationAcknowledge.CANCEL:
                                        callback.onResponse(notificationAcknowledge.getStatus());
                                        clear();
                                        break;
                                    case NotificationAcknowledge.TIMEOUT:
                                        callback.onResponse(notificationAcknowledge.getStatus());
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


    private void insertDataToDataKit(ArrayList<NotificationRequest> notificationRequests) throws DataKitException {
        Log.d(TAG, "insertDataToDataKit()...notificationRequests..size="+notificationRequests.size());
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(context);
        for (NotificationRequest notificationRequest : notificationRequests) {
            Gson gson = new Gson();
            JsonObject sample = new JsonParser().parse(gson.toJson(notificationRequest)).getAsJsonObject();
            DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
            dataKitAPI.insert(dataSourceClientRequest, dataTypeJSONObject);
        }
        Log.d(TAG, "...insertDataToDataKit()");
    }
}
