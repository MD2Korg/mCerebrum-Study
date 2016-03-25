package org.md2k.study.model_view.day_start_end;

import android.os.Handler;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;
import java.util.Calendar;
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
public class DayStartEndInfoManager extends Model {
    private static final String TAG = DayStartEndInfoManager.class.getSimpleName();
    DataSourceBuilder dataSourceBuilderDayStart;
    DataSourceClient dataSourceClientDayStart;
    DataSourceBuilder dataSourceBuilderDayEnd;
    DataSourceClient dataSourceClientDayEnd;
    long dayStartTime;
    long dayEndTime;
    Handler handler;

    public DayStartEndInfoManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        dataSourceBuilderDayStart = createDataSourceBuilderDayStart();
        dataSourceBuilderDayEnd = createDataSourceBuilderDayEnd();
        dayStartTime = -1;
        dayEndTime = -1;
        handler=new Handler();
    }

    public void set() {
        Status lastStatus;
        readDayStartFromDataKit();
        readDayEndFromDataKit();
        Log.d(TAG, "dayStartTime=" + dayStartTime+" dayEndTime="+dayEndTime+" curTime="+DateTime.getDateTime()+" diff="+(DateTime.getDateTime()-dayStartTime));

        if(isNewDay())
            lastStatus = new Status(rank, Status.DAY_START_NOT_AVAILABLE);
        else lastStatus=new Status(rank,Status.SUCCESS);
        notifyIfRequired(lastStatus);
    }
    private boolean isNewDay(){
        long curTime=DateTime.getDateTime();
        if(dayStartTime==-1) return true;
        if(dayStartTime<dayEndTime){
            if(!isToday(dayStartTime) && dayEndTime+4*60*60*1000<curTime) return true;
            else return false;
        }else{
            if(!isToday(dayStartTime) && dayStartTime+16*60*60*1000<curTime) return true;
            return false;
        }
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"runnable...set() start....");
            set();
            Log.d(TAG, "runnable...set() end....");
        }
    };

    public void clear() {
        Log.d(TAG, "clear()...");
        dayStartTime = -1;
        dayEndTime = -1;
        status = new Status(rank, Status.NOT_DEFINED);
    }

    boolean isToday(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        if (calendar.get(Calendar.YEAR) != calendarNow.get(Calendar.YEAR)) return false;
        if (calendar.get(Calendar.MONTH) != calendarNow.get(Calendar.MONTH)) return false;
        if (calendar.get(Calendar.DAY_OF_MONTH) != calendarNow.get(Calendar.DAY_OF_MONTH))
            return false;
        return true;
    }

    private void readDayStartFromDataKit() {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        dayStartTime = -1;
        dataSourceClientDayStart = dataKitAPI.register(dataSourceBuilderDayStart);
        ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientDayStart, 1);
        if (dataTypes.size() != 0) {
            DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
            dayStartTime = dataTypeLong.getSample();
        }
    }

    public Status getCurrentStatusDetails() {
        if (isNewDay()) {
            Log.d(TAG,"rank="+rank);
            set();
//            if(status.getStatus()!=Status.DAY_START_NOT_AVAILABLE) handler.post(runnable);
            return new Status(rank, Status.DAY_START_NOT_AVAILABLE);
        }
        if (dayEndTime > dayStartTime) return new Status(rank, Status.DAY_COMPLETED);

        return new Status(rank, Status.SUCCESS);
    }

    private void readDayEndFromDataKit() {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        dayEndTime = -1;
        dataSourceClientDayEnd = dataKitAPI.register(dataSourceBuilderDayEnd);
        ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientDayEnd, 1);
        if (dataTypes.size() != 0) {
            DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
            dayEndTime = dataTypeLong.getSample();
        }

    }

    private boolean writeDayStartToDataKit() {
        Log.d(TAG,"writeDayStartToDataKit()...");
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), dayStartTime);
        dataSourceClientDayStart = dataKitAPI.register(dataSourceBuilderDayStart);
        dataKitAPI.insert(dataSourceClientDayStart, dataTypeLong);
        return true;
    }

    private boolean writeDayEndToDataKit() {
        Log.d(TAG,"writeDayEndToDataKit()...");
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        if (!dataKitAPI.isConnected()) return false;
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), dayEndTime);
        dataSourceClientDayEnd = dataKitAPI.register(dataSourceBuilderDayEnd);
        dataKitAPI.insert(dataSourceClientDayEnd, dataTypeLong);
        return true;
    }

    DataSourceBuilder createDataSourceBuilderDayStart() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.DAY_START).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Day Start");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Represents when day started");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLong.class.getName());
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, "Day Start");
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(Long.MAX_VALUE));
        dataDescriptor.put(METADATA.UNIT, "millisecond");
        dataDescriptor.put(METADATA.DESCRIPTION, "Contains day start time in millisecond");
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        dataDescriptors.add(dataDescriptor);
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(dataDescriptors);
        return dataSourceBuilder;
    }

    DataSourceBuilder createDataSourceBuilderDayEnd() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.DAY_END).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Day End");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Represents when day ended");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLong.class.getName());
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, "Day End");
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(Long.MAX_VALUE));
        dataDescriptor.put(METADATA.UNIT, "millisecond");
        dataDescriptor.put(METADATA.DESCRIPTION, "Contains day end time in millisecond");
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        dataDescriptors.add(dataDescriptor);
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(dataDescriptors);
        return dataSourceBuilder;
    }


    public void setDayStartTime(long dayStartTime) {
        Log.d(TAG,"setDayStartTime()...");
        this.dayStartTime = dayStartTime;
        writeDayStartToDataKit();
        reset();
    }

    public void setDayEndTime(long dayEndTime) {
        Log.d(TAG,"setDayEndTime()...");
        this.dayEndTime = dayEndTime;
        writeDayEndToDataKit();
        reset();
    }

    public long getDayStartTime() {
        return dayStartTime;
    }

    public long getDayEndTime() {
        return dayEndTime;
    }
}
