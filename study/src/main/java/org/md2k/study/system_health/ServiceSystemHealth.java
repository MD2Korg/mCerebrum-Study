package org.md2k.study.system_health;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.ActivityMain;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.controller.UserManager;
import org.md2k.study.model.app_service.AppServiceManager;
import org.md2k.study.model.day_start_end.DayStartEndInfoManager;
import org.md2k.utilities.Report.Log;

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

public class ServiceSystemHealth extends Service {
    public static final String INTENT_NAME = "MONITOR_HEALTH";
    public static final String TYPE = "TYPE";
    public static final String VALUE = "VALUE";
    public static final int CONNECTED = 1;
    public static final int NOT_CONNECTED = 2;
    public static final int DAY_START_END = 3;
    public static final int ADMIN = 6;
    public static final int DATA_QUALITY = 7;


    Context context;
    Handler handler;
    ModelManager modelManager;
    UserManager adminManager;
    UserManager userManager;
    boolean isDataQualityAvailable;
    boolean isDayStartEnd;
    public Status lastAdminStatus = null;
    public Status[] lastDataQualityStatus = null;
    public Status lastDayStartEndStatus = null;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverStatus,
                new IntentFilter(INTENT_NAME));
        modelManager = ModelManager.getInstance(getApplicationContext());
        adminManager = modelManager.getAdminManager();
        userManager = modelManager.getUserManager();
        isDataQualityAvailable = userManager.getModel(ModelManager.MODEL_DATA_QUALITY) != null;
        isDayStartEnd = userManager.getModel(ModelManager.MODEL_DAY_START_END) != null;
        handler = new Handler();
        handler.post(system_health);
    }

    Status getAdminStatus() {
        Status status = modelManager.getStatus();
        if (status.getStatusCode() == Status.SUCCESS)
            status = adminManager.getStatus();
        if (status.getStatusCode() == Status.SUCCESS) {
            modelManager.getModel(ModelManager.MODEL_APP_SERVICE).start();
        }
        return status;
    }

    void prepareBandOff() {
        for (int i = 0; i < lastDataQualityStatus.length; i++) {
            lastDataQualityStatus[i] = new Status(Status.DATAQUALITY_OFF);
        }

    }

    void sendMessageDataQuality() {
        if (lastDataQualityStatus == null) return;
        if (lastAdminStatus.getStatusCode() != Status.SUCCESS)
            prepareBandOff();
        else {
            long timestamp = DateTime.getDateTime();
            for (int i = 0; i < lastDataQualityStatus.length; i++) {
                if (timestamp - lastDataQualityStatus[i].getTimestamp() > 30000)
                    lastDataQualityStatus[i] = new Status(Status.DATAQUALITY_OFF);
            }
        }
        Intent intent = new Intent(ActivityMain.INTENT_NAME);
        intent.putExtra(ActivityMain.TYPE, ActivityMain.DATA_QUALITY);
        intent.putExtra(ActivityMain.VALUE, lastDataQualityStatus);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    void sendMessageDayStartEnd() {
        Intent intent = new Intent(ActivityMain.INTENT_NAME);
        intent.putExtra(ActivityMain.TYPE, ActivityMain.DAY_START_END);
        intent.putExtra(ActivityMain.VALUE, lastDayStartEndStatus);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    Status getDayStartEndStatus(Status adminStatus) {
        Status curDayStatus = null;
        if (adminStatus.getStatusCode() != Status.SUCCESS) return new Status(Status.DAY_ERROR);
        DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) userManager.getModel(ModelManager.MODEL_DAY_START_END);
        if (dayStartEndInfoManager != null) {
            curDayStatus = dayStartEndInfoManager.getStatus();
        }
        return curDayStatus;
    }

    public void sendMessageStatus() {
        Intent intent = new Intent(ActivityMain.INTENT_NAME);
        intent.putExtra(ActivityMain.TYPE, ActivityMain.STATUS);
        if (lastAdminStatus.getStatusCode() != Status.SUCCESS)
            intent.putExtra(ActivityMain.VALUE, lastAdminStatus);
        else if (lastDayStartEndStatus != null)
            intent.putExtra(ActivityMain.VALUE, lastDayStartEndStatus);
        else
            intent.putExtra(ActivityMain.VALUE, lastAdminStatus);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    Runnable system_health = new Runnable() {
        @Override
        public void run() {
            if (modelManager.isInstalled() && !modelManager.isConnected())
                modelManager.connect();
            Status curAdminStatus, curDayStartEndStatus = null;
            curAdminStatus = getAdminStatus();
            if (isDayStartEnd)
                curDayStartEndStatus = getDayStartEndStatus(curAdminStatus);
            if (curDayStartEndStatus == null) {
                if (lastAdminStatus == null || curAdminStatus.getStatusCode() != lastAdminStatus.getStatusCode()) {
                    lastAdminStatus = curAdminStatus;
                    sendMessageStatus();
                }
            } else if (lastAdminStatus == null || lastDayStartEndStatus == null || curAdminStatus.getStatusCode() != lastAdminStatus.getStatusCode() || curDayStartEndStatus.getStatusCode() != lastDayStartEndStatus.getStatusCode()) {
                lastAdminStatus = curAdminStatus;
                lastDayStartEndStatus = curDayStartEndStatus;
                sendMessageStatus();
                sendMessageDayStartEnd();
            }
            sendMessageDataQuality();
            handler.postDelayed(system_health, Constants.HEALTH_CHECK_REPEAT);
        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverStatus);
        handler.removeCallbacks(system_health);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BroadcastReceiver broadcastReceiverStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(TYPE, -1)) {
                case CONNECTED:
                    modelManager.stop();
                    modelManager.clear();
                    modelManager.set();
                    modelManager.start();
                    handler.removeCallbacks(system_health);
                    handler.post(system_health);
                    break;
                case DATA_QUALITY:
                    lastDataQualityStatus = (Status[]) intent.getParcelableArrayExtra(VALUE);
                    if (isDataQualityAvailable) sendMessageDataQuality();
                    break;
                case DAY_START_END:
                    handler.removeCallbacks(system_health);
                    handler.post(system_health);
                    break;
                case NOT_CONNECTED:
                    if (modelManager.isInstalled())
                        modelManager.connect();
                    break;
            }
        }
    };
}