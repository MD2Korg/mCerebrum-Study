package org.md2k.study;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.md2k.datakitapi.exception.DataKitException;
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

    public void onCreate() {
        Log.d(TAG, "onCreate...");
        super.onCreate();
        modelManager=ModelManager.getInstance(getApplicationContext());
        Log.d(TAG,"...onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()...");
        try {
            modelManager.clear();
            modelManager.read();
            modelManager.set();
        } catch (DataKitException e) {
            Log.e(TAG,"Error...in clearing ... onStartCommand()..");
            e.printStackTrace();
        }
        isRunning=true;
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        try {
            modelManager.clear();
        } catch (DataKitException e) {
            e.printStackTrace();
        }
        isRunning=false;

        super.onDestroy();
    }
}