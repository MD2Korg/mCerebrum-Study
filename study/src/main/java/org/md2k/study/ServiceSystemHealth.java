package org.md2k.study;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.study.controller.ModelManager;
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
    private static final String TAG = ServiceSystemHealth.class.getSimpleName();
    ModelManager modelManager;
    public static boolean isRunning=false;
    public static int RANK_LIMIT=Status.RANK_BEGIN;
    public static String INTENT_RESTART="intent_restart";

    public void onCreate() {
        Log.d(TAG, "onCreate...");
        super.onCreate();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiverRestart, new IntentFilter(INTENT_RESTART));
        modelManager=ModelManager.getInstance(getApplicationContext());
        Log.d(TAG,"...onCreate");
    }

    private BroadcastReceiver mMessageReceiverRestart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stop();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()...");
        restart();
        isRunning=true;
        return START_STICKY;
    }
    void stop(){
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiverRestart);
        modelManager.clear();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start();

    }
    void start(){
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiverRestart, new IntentFilter(INTENT_RESTART));
        modelManager.read();
        modelManager.set();
    }
    void restart(){
        stop();
        start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiverRestart);
        modelManager.clear();
        isRunning=false;
        super.onDestroy();
    }
}