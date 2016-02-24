package org.md2k.study.model.sleep_info;

import android.content.Context;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.datatype.DataTypeLongArray;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;

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
public class SleepInfoManager extends Model {
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    long sleepTimeDB;
    long sleepTimeNew;

    public SleepInfoManager(Context context, DataKitAPI dataKitAPI, Operation operation) {
        super(context, dataKitAPI, operation);
        dataSourceBuilder = createDataSourceBuilder();
        reset();
    }

    public void reset() {
        sleepTimeNew = -1;
        sleepTimeDB = -1;
        readStudyInfoFromDataKit();
        if (!dataKitAPI.isConnected()) lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
        if (sleepTimeDB == -1)
            lastStatus= new Status(Status.SLEEP_NOT_DEFINED);
        lastStatus= new Status(Status.SUCCESS);
    }

    public Status getStatus() {
        return lastStatus;
    }

    public boolean isValid() {
        if (sleepTimeNew == -1) return false;
        if (sleepTimeDB == -1) return true;
        if (sleepTimeDB == sleepTimeNew) return false;
        return true;
    }

    private void readStudyInfoFromDataKit() {
        sleepTimeDB = -1;
        if (dataKitAPI.isConnected()) {
            dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                sleepTimeDB = dataTypeLong.getSample();
            }
        }
    }

    private boolean writeToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        if (!isValid()) return false;
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), sleepTimeNew);
        dataSourceClient = dataKitAPI.register(dataSourceBuilder);
        dataKitAPI.insert(dataSourceClient, dataTypeLong);
        sleepTimeDB = sleepTimeNew;
        return true;
    }
    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.SLEEP).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Sleep");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains sleep time");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLong.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }

    ArrayList<HashMap<String, String>> createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("Sleep time"));
        return dataDescriptors;
    }

    HashMap<String, String> createDescriptor(String name) {
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(24 * 60 * 60 * 1000));
        dataDescriptor.put(METADATA.UNIT, "millisecond");
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        return dataDescriptor;
    }
    public void save(){
        writeToDataKit();reset();
    }

    public long getSleepTimeDB() {
        return sleepTimeDB;
    }

    public long getSleepTimeNew() {
        return sleepTimeNew;
    }

    public void setSleepTimeNew(long sleepTimeNew) {
        this.sleepTimeNew = sleepTimeNew;
    }
}
