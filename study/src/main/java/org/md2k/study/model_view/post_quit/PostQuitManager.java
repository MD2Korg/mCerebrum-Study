package org.md2k.study.model_view.post_quit;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;
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
public class PostQuitManager extends Model {
    private static final String TAG = PostQuitManager.class.getSimpleName();

    private long dataDB;
    private long dataNew;

    public PostQuitManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status = new Status(rank, Status.POST_QUIT_NOT_DEFINED);
        dataNew = -1;
        dataDB = -1;
    }

    public void clear() {
        dataNew = -1;
        dataDB = -1;
        status = new Status(rank, Status.POST_QUIT_NOT_DEFINED);
    }

    void setPostQuit(long timeStamp) {
        dataNew = timeStamp;
    }

    public void set() {
        readFromDataKit();
        update();
    }

    private void update() {
        Status lastStatus;
        if (dataDB == -1)
            lastStatus = new Status(rank, Status.POST_QUIT_NOT_DEFINED);
        else lastStatus = new Status(rank, Status.SUCCESS);
        notifyIfRequired(lastStatus);
    }

    private boolean isValid() {
        if (dataNew == -1) return false;
        if (dataDB == -1) return true;
        if (dataDB == dataNew) return false;
        return true;
    }

    private void readFromDataKit() {
        try {
            dataDB = -1;
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                dataDB = dataTypeLong.getSample();
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    private boolean writeToDataKit() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            if (!isValid()) return false;
            DataTypeLong dataTypeLong=new DataTypeLong(DateTime.getDateTime(), dataNew);
            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            dataKitAPI.insert(dataSourceClient, dataTypeLong);
            dataDB = dataNew;
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
            return false;
        }
        return true;
    }

    private DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.POST_QUIT).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "PostQuit Time");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Defines the time of Post Quit day");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, long.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }

    private ArrayList<HashMap<String, String>> createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("PostQuit day"));
        return dataDescriptors;
    }

    private HashMap<String, String> createDescriptor(String name) {
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.UNIT, "timestamp");
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        return dataDescriptor;
    }

    public void save() {
        if (writeToDataKit())
            dataDB = dataNew;
        set();
    }

    public Status getStatus() {
        String msg = "";
        if (dataNew != -1) msg = DateTime.convertTimeStampToDateTime(dataNew,"EEE, d MMM yyyy, HH:mm:ss");
        else if (dataDB != -1) msg = DateTime.convertTimeStampToDateTime(dataNew,"EEE, d MMM yyyy, HH:mm:ss");
        if (dataDB != -1 || dataNew != -1) return new Status(rank, Status.SUCCESS, msg);
        return new Status(rank, Status.POST_QUIT_NOT_DEFINED);
    }
}

