package org.md2k.study;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.study.admin.AdminManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.datakit.DataKitHandler;

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
    DataKitHandler dataKitHandler;
    Context context;
    Handler handler;

    public void onCreate() {
        super.onCreate();
        context=getBaseContext();
        handler = new Handler();
        handler.post(checkStatus);
        Log.d(TAG, "onCreate()");
    }

    Runnable checkStatus = new Runnable() {
        @Override
        public void run() {
            AdminManager adminManager=AdminManager.getInstance(context);
            Status status = adminManager.getStatus();
            Intent intent = new Intent("system_health");
            intent.putExtra("status", status);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            Log.d(TAG, "checkStatus...");

            handler.postDelayed(checkStatus, Constants.HEALTH_CHECK_REPEAT);
        }
    };

    @Override
    public void onDestroy() {
        handler.removeCallbacks(checkStatus);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
