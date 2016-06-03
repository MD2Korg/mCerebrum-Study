package org.md2k.study.model_view.privacy_control;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.privacy.PrivacyData;

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
    PrivacyData privacyData;

    public PrivacyControlManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        privacyData = null;
    }

    public void set() throws DataKitException {
        privacyData = readFromDataKit();
        Status curStatus=new Status(rank, Status.SUCCESS);
        notifyIfRequired(curStatus);
    }

    @Override
    public void clear() {
        privacyData = null;
        status = new Status(rank, Status.NOT_DEFINED);
    }

    private PrivacyData readFromDataKit() throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        PrivacyData privacyData = null;
        ArrayList<DataSourceClient> dataSourceClients= dataKitAPI.find(createDataSourceBuilder());
        if(dataSourceClients.size()>0) {
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), 1);
            if (dataTypes.size() != 0) {
                try {
                    DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
                    Gson gson = new Gson();
                    privacyData = gson.fromJson(dataTypeJSONObject.getSample().toString(), PrivacyData.class);
                }catch(Exception ignored){
                    privacyData=null;
                }
            }
        }
        return privacyData;
    }
    public Status getCurrentStatusDetails(){
        if (privacyData == null) return new Status(rank, Status.SUCCESS);
        if (!privacyData.isStatus()) return new Status(rank, Status.SUCCESS);
        if (privacyData.getStartTimeStamp() + privacyData.getDuration().getValue() < DateTime.getDateTime())
            return new Status(rank, Status.SUCCESS);
        return new Status(rank, Status.PRIVACY_ACTIVE);
    }

    private DataSourceBuilder createDataSourceBuilder() {
        return new DataSourceBuilder().setType(DataSourceType.PRIVACY);
    }

    public PrivacyData getPrivacyData() {
        return privacyData;
    }
}
