package org.md2k.study.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

import java.util.HashMap;

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
public class ModelManager {
    private static final String TAG = ModelManager.class.getSimpleName();
    private static ModelManager instance = null;
    private Context context;
    private HashMap<String, Model> modelHashMap;
    private ConfigManager configManager;
    private Status status;
    private boolean isUpdating;
    public static int RANK_LIMIT;



    public static ModelManager getInstance(Context context) {
        if (instance == null)
            instance = new ModelManager(context.getApplicationContext());
        return instance;
    }

    private ModelManager(Context context) {
        this.context = context;
        modelHashMap = new HashMap<>();
        isUpdating = false;
        RANK_LIMIT=Status.RANK_ADMIN_OPTIONAL;
        read();
    }

    public void clear() {
        LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(mMessageReceiverRestart);
        org.md2k.utilities.Report.Log.w(TAG,"time="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+",timestamp="+ DateTime.getDateTime()+",modelManager.clear()");
        isUpdating = true;
        for (int i = Status.RANK_SUCCESS; i <= Status.RANK_BEGIN; i++) {
            for (HashMap.Entry<String, Model> entry : modelHashMap.entrySet()) {
                if (entry.getValue() == null) continue;
                if (entry.getValue().getRank() == i)
                    entry.getValue().clear();
            }
        }
        isUpdating = false;
    }

    public void read() {
        org.md2k.utilities.Report.Log.w(TAG,"time="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+",timestamp="+ DateTime.getDateTime()+",modelManager.read()");
        modelHashMap.clear();
        configManager = new ConfigManager(context);
        if (!modelHashMap.containsKey(ModelFactory.MODEL_CONFIG_INFO))
            modelHashMap.put(ModelFactory.MODEL_CONFIG_INFO, ModelFactory.getModel(this, ModelFactory.MODEL_CONFIG_INFO, Status.RANK_CONFIG));
        if (configManager.isValid()) {
            for (int i = 0; i < configManager.getConfig().getActions().size(); i++) {
                if (!configManager.getConfig().getActions().get(i).isEnable()) continue;
                String id = configManager.getConfig().getActions().get(i).getId();
                if (id.equals(ModelFactory.MODEL_SELF_REPORT))
                    id = configManager.getConfig().getActions().get(i).getType();
                int rank = configManager.getConfig().getActions().get(i).getRank();
                Log.d(TAG, "ModelManager()...id=" + id + " rank=" + rank);
                if (modelHashMap.containsKey(id)) continue;
                modelHashMap.put(id, ModelFactory.getModel(this, id, rank));
            }
        }
        status = new Status(Status.RANK_BEGIN, Status.NOT_DEFINED);
        isUpdating = false;
    }

    public void set() {
        LocalBroadcastManager.getInstance(context.getApplicationContext()).registerReceiver(mMessageReceiverRestart, new IntentFilter(Constants.INTENT_RESTART));
        org.md2k.utilities.Report.Log.w(TAG,"time="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+",timestamp="+ DateTime.getDateTime()+",modelManager.set()");
        status = new Status(Status.RANK_BEGIN, Status.NOT_DEFINED);
        isUpdating = false;
        update();
    }
    private BroadcastReceiver mMessageReceiverRestart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            clear();
            set();
        }
    };

    public void update() {
        if (isUpdating) return;
        isUpdating = true;
        Log.w(TAG,"time="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+",timestamp="+ DateTime.getDateTime()+",modelManager.update()");
        Log.d(TAG, "update()...");
        Status lastStatus = status;
        while (true) {
            Status curStatus = findLatestStatus();
            Log.d(TAG, "update()...lastStatus=" + lastStatus.log() + " curStatus=" + curStatus.log() + " rankLimit=" + ModelManager.RANK_LIMIT);
            if (curStatus.getRank() == lastStatus.getRank() || curStatus.getStatus() == Status.SUCCESS || curStatus.getRank() == ModelManager.RANK_LIMIT) {
                lastStatus = curStatus;
                break;
            } else {
                if (curStatus.getRank() < lastStatus.getRank())
                    setNow(curStatus.getRank());
                else if (curStatus.getRank() > lastStatus.getRank())
                    clear(curStatus.getRank() - 1);

            }
            lastStatus = curStatus;
        }
        if (!status.equals(lastStatus)) {
            status = lastStatus;
            Intent intent = new Intent(Status.class.getSimpleName());
            intent.putExtra(Status.class.getSimpleName(), status);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        isUpdating = false;
    }

    private Status findLatestStatus() {
        Status curStatus = null;
        for (HashMap.Entry<String, Model> entry : modelHashMap.entrySet()) {
            if (entry.getValue() == null) continue;
            Status temp = entry.getValue().getStatus();
            Log.d(TAG, "findLatestStatus: " + entry.getKey() + " status=" + temp.log());
            if (temp.getStatus() == Status.SUCCESS) continue;
            if (curStatus == null || temp.getRank() > curStatus.getRank() || (temp.getRank() == curStatus.getRank() && temp.getStatus() > curStatus.getStatus()))
                curStatus = temp;
        }
        if (curStatus == null)
            curStatus = new Status(Status.RANK_SUCCESS, Status.SUCCESS);
        return curStatus;
    }

    private void setNow(int state) {
        org.md2k.utilities.Report.Log.w(TAG,"time="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+",timestamp="+ DateTime.getDateTime()+",modelManager.set("+state+")");
        Log.d(TAG, "set(" + state + ")...");
        if (state < Status.RANK_SUCCESS || state > Status.RANK_BEGIN) return;
        for (HashMap.Entry<String, Model> entry : modelHashMap.entrySet()) {
            if (entry.getValue() == null) continue;
            if (entry.getValue().getRank() == state) {
                entry.getValue().set();
            }
        }
    }

    private void clear(int state) {
        org.md2k.utilities.Report.Log.w(TAG,"time="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+",timestamp="+ DateTime.getDateTime()+",modelManager.clear("+state+")");
        Log.d(TAG, "clear(" + Status.RANK_SUCCESS + "..." + state + ")...");
        for (int s = Status.RANK_SUCCESS; s <= state; s++) {
            for (HashMap.Entry<String, Model> entry : modelHashMap.entrySet())
                if (entry.getValue().getRank() == state)
                    entry.getValue().clear();

        }
    }

    public Context getContext() {
        return context;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Status getStatus() {
        return status;
    }

    public Model getModel(String id) {
        if (modelHashMap.containsKey(id)) return modelHashMap.get(id);
        return null;
    }

    public boolean isUpdating() {
        return isUpdating;
    }
}
