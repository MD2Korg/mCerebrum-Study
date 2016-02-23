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
import org.md2k.study.controller.AdminManager;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.controller.UserManager;
import org.md2k.study.model.app_service.AppServiceManager;
import org.md2k.study.model.day_start_end.DayStartEndInfoManager;
import org.md2k.study.model.privacy_control.PrivacyControlManager;
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
    public static final String TYPE="TYPE";
    public static final String VALUE="VALUE";
    public static final String TIMESTAMP="TIMESTAMP";
    public static final int CONNECTED=1;
    public static final int NOT_CONNECTED=2;
    public static final int SETTINGS=3;
    public static final int INSTALL=4;
    public static final int USERINFO_WAKEUP_SLEEP=5;
    public static final int ADMIN=6;
    public static final int DATA_QUALITY=7;


    private static final String TAG = ServiceSystemHealth.class.getSimpleName();
    Context context;
    Handler handler;
    ModelManager modelManager;
    AdminManager adminManager;
    UserManager userManager;
    AppServiceManager appServiceManager;
    public Status[] dataQuality;
    public long lastPrivacyTime=0;
    public Status lastStatus=new Status(Status.SUCCESS);
    public Status lastDayStatus=new Status(Status.SUCCESS);

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverStatus,
                new IntentFilter(INTENT_NAME));
        modelManager = ModelManager.getInstance(getApplicationContext());
        adminManager = AdminManager.getInstance(getApplicationContext());
        userManager = UserManager.getInstance(getApplicationContext());
        appServiceManager= (AppServiceManager) modelManager.getModel(ModelManager.MODEL_APP_SERVICE);

        handler = new Handler();
        handler.post(system_health);
        Log.d(TAG, "onCreate()");
    }
    Status checkAdminHealth(){
        Status status = modelManager.getStatus();
        if (status.getStatusCode() == Status.SUCCESS)
            status = adminManager.getStatus();
        Log.d(TAG,"status="+status.getStatusMessage());
        if(status.getStatusCode()!=Status.SUCCESS)
            if(status.getStatusCode()==Status.APP_NOT_RUNNING)
                ((AppServiceManager)modelManager.getModel(ModelManager.MODEL_APP_SERVICE)).start();
        return status;
    }
    void checkDataQuality(){
        if(dataQuality==null) return;
        Intent intent=new Intent(ActivityMain.INTENT_NAME);
        intent.putExtra(ActivityMain.TYPE, ActivityMain.DATA_QUALITY);
        intent.putExtra(ActivityMain.VALUE, dataQuality);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    void checkPrivacy(){
        long curPrivacyTime;
        PrivacyControlManager privacyControlManager= (PrivacyControlManager) userManager.getModels(ModelManager.MODEL_PRIVACY);
        if(privacyControlManager!=null) {
            if (privacyControlManager.getStatus().getStatusCode() == Status.PRIVACY_ACTIVE) {
                long remainingTime = privacyControlManager.getPrivacyData().getStartTimeStamp() + privacyControlManager.getPrivacyData().getDuration().getValue() - DateTime.getDateTime();
                if (remainingTime > 0) {
                    curPrivacyTime = remainingTime;
                } else
                    curPrivacyTime = -1;
            } else
                curPrivacyTime = -1;
            if (curPrivacyTime != lastPrivacyTime) {
                lastPrivacyTime = curPrivacyTime;
                Intent intent = new Intent(ActivityMain.INTENT_NAME);
                intent.putExtra(ActivityMain.TYPE, ActivityMain.PRIVACY);
                intent.putExtra(ActivityMain.VALUE, lastPrivacyTime);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        }
    }
    Status checkDayStartEnd(){
        Status curDayStatus=null;
        DayStartEndInfoManager dayStartEndInfoManager= (DayStartEndInfoManager) userManager.getModels(ModelManager.MODEL_DAY_START_END);
        if(dayStartEndInfoManager!=null) {
            curDayStatus = dayStartEndInfoManager.getStatus();
        }
        return curDayStatus;
    }
    public void sendMessage(Status curStatus, Status curDayStatus){
 /*       if(curStatus.getStatusCode()==lastStatus.getStatusCode() && curStatus.getStatusMessage().equals(lastStatus.getStatusMessage())
                && curDayStatus.getStatusCode()==lastDayStatus.getStatusCode() && curDayStatus.getStatusMessage().equals(lastDayStatus.getStatusMessage()))
            return;
*/
        if(curStatus.getStatusCode()==Status.SUCCESS){
            lastStatus=curDayStatus;
            Intent intent=new Intent(ActivityMain.INTENT_NAME);
            intent.putExtra(ActivityMain.TYPE, ActivityMain.STATUS);
            intent.putExtra(ActivityMain.VALUE, lastStatus);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            lastStatus=curStatus;
            Intent intent=new Intent(ActivityMain.INTENT_NAME);
            intent.putExtra(ActivityMain.TYPE, ActivityMain.STATUS);
            intent.putExtra(ActivityMain.VALUE, lastStatus);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        }
        if(lastDayStatus.getStatusCode()!=curDayStatus.getStatusCode()) {
            lastDayStatus=curDayStatus;
            Intent intent = new Intent(ActivityMain.INTENT_NAME);
            intent.putExtra(ActivityMain.TYPE, ActivityMain.DAY_START_END);
            intent.putExtra(ActivityMain.VALUE, lastDayStatus);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
    Runnable system_health = new Runnable() {
        @Override
        public void run() {
            checkDataQuality();
            checkPrivacy();
            Status curStatus=checkAdminHealth();
            Status curDayStatus=checkDayStartEnd();
            sendMessage(curStatus, curDayStatus);
            handler.postDelayed(system_health, Constants.HEALTH_CHECK_REPEAT);
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handler.removeCallbacks(system_health);
        handler.post(system_health);
        return START_STICKY; // or whatever your flag
    }
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
                    adminManager.reset();
                    userManager.reset();
                    break;
                case INSTALL:
                    adminManager.reset();
                    userManager.reset();
                    break;
                case SETTINGS:
                    adminManager.reset();
                    userManager.reset();
                    appServiceManager.stop();
                    break;
                case USERINFO_WAKEUP_SLEEP:
                    adminManager.reset();
                    userManager.reset();
                    break;
                case ADMIN:
                    adminManager.reset();
                    userManager.reset();
                    break;
                case DATA_QUALITY:
                    dataQuality= (Status[]) intent.getParcelableArrayExtra(VALUE);
                    break;
            }
            handler.removeCallbacks(system_health);
            handler.post(system_health);
        }
    };
}