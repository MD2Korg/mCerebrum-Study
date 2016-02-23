package org.md2k.study.model.user_info;

import android.content.Context;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeString;
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
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.study_info.StudyInfoManager;
import org.md2k.utilities.data_format.UserInfo;

import java.util.ArrayList;
import java.util.UUID;

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
public class UserInfoManager extends Model {
    private static final String TAG = UserInfoManager.class.getSimpleName() ;
    UserInfo userInfo;
    boolean isInDatabase;

    public UserInfoManager(Context context, DataKitAPI dataKitAPI, Operation operation) {
        super(context,dataKitAPI, operation);
        reset();
    }

    public void reset() {
        isInDatabase = false;
        userInfo = new UserInfo();
        readFromDataKit();
    }

    public void setUserId(String userId) {
        userInfo.setUser_id(userId);
        String study_id= ((StudyInfoManager)ModelManager.getInstance(context).getModel(ModelManager.MODEL_STUDY_INFO)).getStudy_id();
        UUID userUUID=new UUID(study_id.hashCode(),userId.hashCode());
        userInfo.setUuid(userUUID.toString());
    }

    public String getUserId() {
        return userInfo.getUser_id();
    }

    public Status getStatus() {
        if (!dataKitAPI.isConnected()) return new Status(Status.DATAKIT_NOT_AVAILABLE);
        if (isInDatabase) return new Status(Status.SUCCESS);
        return new Status(Status.USERID_NOT_DEFINED);
    }

    private void readFromDataKit() {
        if (!dataKitAPI.isConnected()) return;
        DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
        ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
        if (dataTypes.size() != 0) {
            DataTypeString dataTypeString = (DataTypeString) dataTypes.get(0);
            Gson gson = new Gson();
            userInfo = gson.fromJson(dataTypeString.getSample(), UserInfo.class);
            isInDatabase = true;
        }
    }
    public void save(){
        writeToDataKit();
    }

    private boolean writeToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        if (isInDatabase) return false;
        if (userInfo == null) return false;
        if (userInfo.getUser_id() == null) return false;
        if (userInfo.getUser_id().length() == 0) return false;
        Gson gson = new Gson();
        String sample = gson.toJson(userInfo);
        DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
        DataTypeString dataTypeString = new DataTypeString(DateTime.getDateTime(), sample);
        dataKitAPI.insert(dataSourceClient, dataTypeString);
        isInDatabase = true;
        return true;
    }

    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).setMetadata(METADATA.NAME, "Phone").build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.USER_INFO).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "User Info");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains user_id as a json object");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeString.class.getName());
        return dataSourceBuilder;
    }
}