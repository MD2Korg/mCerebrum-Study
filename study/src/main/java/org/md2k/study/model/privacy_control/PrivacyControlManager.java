package org.md2k.study.model.privacy_control;

import android.content.Context;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

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
public class PrivacyControlManager extends Model {
    private static final String TAG = PrivacyControlManager.class.getSimpleName();
    DataSourceBuilder dataSourceBuilder;
    ArrayList<DataSourceClient> dataSourceClient;
    PrivacyData privacyData;

    public PrivacyControlManager(Context context, ConfigManager configManager, DataKitAPI dataKitAPI, Operation operation) {
        super(context, configManager, dataKitAPI, operation);
        dataSourceBuilder = createDataSourceBuilder();
    }

    @Override
    public void start() {
        update();
    }

    public void set(){
        privacyData = readFromDataKit();        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);

    }
    public void stop(){

    }
    public void update(){
        privacyData = readFromDataKit();
    }

    @Override
    public void clear() {

    }

    public Status getStatus() {
        if (privacyData == null) return new Status(Status.SUCCESS);
        if (privacyData.isStatus() == false) return new Status(Status.SUCCESS);
        if (privacyData.getStartTimeStamp() + privacyData.getDuration().getValue() < DateTime.getDateTime())
            return new Status(Status.SUCCESS);
        return new Status(Status.PRIVACY_ACTIVE);
    }

    private PrivacyData readFromDataKit() {
        PrivacyData privacyData = null;
        if (dataKitAPI.isConnected()) {
            dataSourceClient = dataKitAPI.find(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient.get(0), 1);
            if (dataTypes.size() != 0) {
                DataTypeString dataTypeString = (DataTypeString) dataTypes.get(0);
                Gson gson = new Gson();
                privacyData = gson.fromJson(dataTypeString.getSample(), PrivacyData.class);
            }
        }
        return privacyData;
    }

    private DataSourceBuilder createDataSourceBuilder() {
        return new DataSourceBuilder().setType(DataSourceType.PRIVACY);
    }

    public PrivacyData getPrivacyData() {
        return privacyData;
    }
}
