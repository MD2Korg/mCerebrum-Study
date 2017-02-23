package org.md2k.study.model_view.user_info;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
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
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.study_info.StudyInfoManager;
import org.md2k.utilities.Report.Log;
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
    private static final String TAG = UserInfoManager.class.getSimpleName();
    private UserInfo userInfo;
    boolean isInDatabase;

    public UserInfoManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status = new Status(rank, Status.USERID_NOT_DEFINED);
        isInDatabase = false;
        userInfo = null;
    }

    public void clear() {
        isInDatabase = false;
        userInfo = null;
        status = new Status(rank, Status.USERID_NOT_DEFINED);
    }

    public void set() {
        isInDatabase = false;
        readFromDataKit();
        update();
    }

    private void update() {
        Status lastStatus;
        if (isInDatabase) lastStatus = new Status(rank, Status.SUCCESS);
        else lastStatus = new Status(rank, Status.USERID_NOT_DEFINED);
        notifyIfRequired(lastStatus);
    }

    public void setUserId(String userId) {
        userInfo = new UserInfo();
        userInfo.setUser_id(userId);
        String study_id = ((StudyInfoManager) ModelManager.getInstance(modelManager.getContext()).getModel(ModelFactory.MODEL_STUDY_INFO)).getStudy_id();
        UUID userUUID = UUID.nameUUIDFromBytes((study_id + userId).getBytes());
        userInfo.setUuid(userUUID.toString());
    }

    public Status getStatus() {
        if (isInDatabase) return new Status(rank, Status.SUCCESS, userInfo.getUser_id());
        else if (userInfo != null && userInfo.getUser_id() != null && userInfo.getUser_id().length() != 0)
            return new Status(rank, Status.SUCCESS, userInfo.getUser_id());
        else return new Status(rank, Status.USERID_NOT_DEFINED);
    }

    private void readFromDataKit() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
                Gson gson = new Gson();
                userInfo = gson.fromJson(dataTypeJSONObject.getSample().toString(), UserInfo.class);
                isInDatabase = true;
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    public void save() {
        writeToDataKit();
        update();
    }

    private boolean writeToDataKit(){
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            if (isInDatabase) return false;
            if (userInfo == null) return false;
            if (userInfo.getUser_id() == null) return false;
            if (userInfo.getUser_id().length() == 0) return false;
            Gson gson = new Gson();
            JsonObject sample = new JsonParser().parse(gson.toJson(userInfo)).getAsJsonObject();

            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
            dataKitAPI.insert(dataSourceClient, dataTypeJSONObject);
            isInDatabase = true;
            return true;
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
            return false;
        }
    }

    private DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).setMetadata(METADATA.NAME, "Phone").build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.USER_INFO).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "User Info");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains user_id as a json object");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeJSONObject.class.getName());
        return dataSourceBuilder;
    }
}
