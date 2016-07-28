package org.md2k.study.model_view.day_start_end;

import android.content.Intent;
import android.os.Handler;
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
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.notification.NotificationRequests;

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
    public static final int NO_BUTTON = 1;
    public static final int START_BUTTON = 2;
    public static final int END_BUTTON = 3;
    public static final int COMPLETE_BUTTON = 4;
    public static final String BUTTON = "button";
    public static final String PROMPT = "prompt";
    public static final String NOTIFICATION = "notification";
    public static final String WAKEUP = "wakeup";
    public static final String SLEEP = "sleep";
    public static final String DAY_START = "day_start";
    public static final String DAY_END = "day_end";
    public static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    Handler handler;

    long dayStartTime;
    long dayEndTime;
    long wakeupOffset;
    long sleepOffset;
    NotifierManager notifierManager;
    int stateDayStart;
    int stateDayEnd;

    public DayStartEndInfoManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        handler = new Handler();
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        dayStartTime = -1;
        dayEndTime = -1;
        wakeupOffset = -1;
        sleepOffset = -1;
        stateDayStart = NO_BUTTON;
        stateDayEnd = NO_BUTTON;
        notifierManager = new NotifierManager(modelManager.getContext());
    }

    public void set() throws DataKitException {
        Status lastStatus;
        readDayStartFromDataKit();
        readDayEndFromDataKit();
        readWakeupTimeFromDataKit();
        readSleepTimeFromDataKit();
        Log.d(TAG, "dayStartTime=" + dayStartTime + " dayEndTime=" + dayEndTime + " curTime=" + DateTime.getDateTime() + " diff=" + (DateTime.getDateTime() - dayStartTime));
        if (!isDayStarted())
            lastStatus = new Status(rank, Status.DAY_START_NOT_AVAILABLE);
        else lastStatus = new Status(rank, Status.SUCCESS);
        notifyIfRequired(lastStatus);
        handler.post(runnableDayStartButton);
        handler.post(runnableDayEndButton);
        handler.post(runnableDayStartPrompt);
        handler.post(runnableDayEndPrompt);
        handler.post(runnableDayStartNotification);
        handler.post(runnableDayEndNotification);
    }

    public void clear() {
        Log.d(TAG, "clear()...");
        dayStartTime = -1;
        dayEndTime = -1;
        wakeupOffset = -1;
        sleepOffset = -1;
        stateDayStart = NO_BUTTON;
        stateDayEnd = NO_BUTTON;
        status = new Status(rank, Status.NOT_DEFINED);
        if (notifierManager != null)
            notifierManager.clear();
        handler.removeCallbacks(runnableDayStartButton);
        handler.removeCallbacks(runnableDayEndButton);
        handler.removeCallbacks(runnableDayStartPrompt);
        handler.removeCallbacks(runnableDayEndPrompt);
        handler.removeCallbacks(runnableDayStartNotification);
        handler.removeCallbacks(runnableDayEndNotification);
    }

    Runnable runnableDayStartButton = new Runnable() {
        @Override
        public void run() {
            stateDayStart = NO_BUTTON;
            if (modelManager.getConfigManager().getConfig().getDay_start().getNotify(BUTTON) == null)
                return;
            long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(BUTTON).getOffset();
            String base = modelManager.getConfigManager().getConfig().getDay_start().getBase();
            long curTime = DateTime.getDateTime();
            long triggerTime = getTime(base, offset);
            if (isDayStarted()) {
                stateDayStart = NO_BUTTON;
                handler.postDelayed(this, triggerTime + DAY_IN_MILLIS - curTime);
            } else {
                if (curTime < triggerTime) {
                    stateDayStart = NO_BUTTON;
                    handler.postDelayed(this, triggerTime - curTime);
                } else {
                    stateDayStart = START_BUTTON;
                    handler.postDelayed(this, triggerTime + DAY_IN_MILLIS - curTime);
                }
            }
            Intent intent = new Intent(DayStartEndInfoManager.class.getSimpleName());
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(intent);
        }
    };
    Runnable runnableDayEndButton = new Runnable() {
        @Override
        public void run() {
            stateDayEnd = NO_BUTTON;
            if (modelManager.getConfigManager().getConfig().getDay_end().getNotify(BUTTON) == null)
                return;
            if (!isDayStarted()) return;
            if (isDayEnded()) {
                stateDayEnd = COMPLETE_BUTTON;
                return;
            }
            long offset = modelManager.getConfigManager().getConfig().getDay_end().getNotify(BUTTON).getOffset();
            String base = modelManager.getConfigManager().getConfig().getDay_end().getBase();
            long curTime = DateTime.getDateTime();
            long triggerTime = getTime(base, offset);
            if (curTime < triggerTime) {
                stateDayEnd = NO_BUTTON;
                handler.postDelayed(this, triggerTime - curTime);
            } else {
                stateDayEnd = END_BUTTON;
            }
            Intent intent = new Intent(DayStartEndInfoManager.class.getSimpleName());
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(intent);
        }
    };

    public long getWakeupShowTimestamp() {
        long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(BUTTON).getOffset();
        String base = modelManager.getConfigManager().getConfig().getDay_start().getBase();
        return getTime(base, offset);

    }

    Runnable runnableDayStartPrompt = new Runnable() {
        @Override
        public void run() {
            try {
                if (modelManager.getConfigManager().getConfig().getDay_start().getNotify(PROMPT) == null)
                    return;
                long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(PROMPT).getOffset();
                String base = modelManager.getConfigManager().getConfig().getDay_start().getBase();
                long curTime = DateTime.getDateTime();
                long triggerTime = getTime(base, offset);
                if (isDayStarted())
                    handler.postDelayed(this, triggerTime + DAY_IN_MILLIS - curTime);
                else {
                    if (curTime < triggerTime)
                        handler.postDelayed(this, triggerTime - curTime);
                    else {
                        showPrompt(DAY_START, modelManager.getConfigManager().getConfig().getDay_start().getNotify(PROMPT).getParameters());
                        handler.postDelayed(this, triggerTime + DAY_IN_MILLIS - curTime);
                    }
                }
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableDayEndPrompt = new Runnable() {
        @Override
        public void run() {
            try {
                if (modelManager.getConfigManager().getConfig().getDay_end().getNotify(PROMPT) == null)
                    return;
                if (!isDayStarted()) return;
                if (isDayEnded()) return;
                long offset = modelManager.getConfigManager().getConfig().getDay_end().getNotify(PROMPT).getOffset();
                String base = modelManager.getConfigManager().getConfig().getDay_end().getBase();
                long curTime = DateTime.getDateTime();
                long triggerTime = getTime(base, offset);
                if (curTime < triggerTime)
                    handler.postDelayed(this, triggerTime - curTime);
                else {
                    showPrompt(DAY_END, modelManager.getConfigManager().getConfig().getDay_end().getNotify(PROMPT).getParameters());
                }
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };
    Runnable runnableDayStartNotification = new Runnable() {
        @Override
        public void run() {
            try {
                if (modelManager.getConfigManager().getConfig().getDay_start().getNotify(NOTIFICATION) == null)
                    return;

                long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(NOTIFICATION).getOffset();
                String base = modelManager.getConfigManager().getConfig().getDay_start().getBase();
                long curTime = DateTime.getDateTime();
                long triggerTime = getTime(base, offset);
                if (isDayStarted())
                    handler.postDelayed(this, triggerTime + DAY_IN_MILLIS - curTime);
                else {
                    if (curTime < triggerTime)
                        handler.postDelayed(this, triggerTime - curTime);
                    else {
                        showNotification(DAY_START, modelManager.getConfigManager().getConfig().getDay_start().getNotify(NOTIFICATION).getParameters());
                        handler.postDelayed(this, triggerTime + DAY_IN_MILLIS - curTime);
                    }
                }
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };
    Runnable runnableDayEndNotification = new Runnable() {
        @Override
        public void run() {
            try {
                if (modelManager.getConfigManager().getConfig().getDay_end().getNotify(NOTIFICATION) == null)
                    return;
                if (!isDayStarted()) return;
                if (isDayEnded()) return;
                long offset = modelManager.getConfigManager().getConfig().getDay_end().getNotify(NOTIFICATION).getOffset();
                String base = modelManager.getConfigManager().getConfig().getDay_end().getBase();
                long curTime = DateTime.getDateTime();
                long triggerTime = getTime(base, offset);
                if (curTime < triggerTime)
                    handler.postDelayed(this, triggerTime - curTime);
                else {
                    showNotification(DAY_END, modelManager.getConfigManager().getConfig().getDay_start().getNotify(NOTIFICATION).getParameters());
                }
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };

    void showPrompt(final String type, ArrayList<String> parameters) throws DataKitException {
        Log.d(TAG, "showPrompt()...");
        NotificationRequests notificationRequests = new NotificationRequests();
        for (int i = 0; i < parameters.size(); i++)
            for (int j = 0; j < modelManager.getConfigManager().getNotificationRequests().getNotification_option().size(); j++) {
                if (modelManager.getConfigManager().getNotificationRequests().getNotification_option().get(j).getId().equals(parameters.get(i))) {
                    notificationRequests.getNotification_option().add(modelManager.getConfigManager().getNotificationRequests().getNotification_option().get(j));
                }
            }
        notifierManager.clear();
        if (notificationRequests.getNotification_option().size() == 0) return;
        notifierManager.set(new Callback() {
            @Override
            public void onResponse(String response) throws DataKitException {
                if (type.equals(DAY_START))
                    setDayStartTime(DateTime.getDateTime());
                else if (type.equals(DAY_END))
                    setDayEndTime(DateTime.getDateTime());
                reset();
            }
        }, notificationRequests);

    }

    void showNotification(String type, ArrayList<String> parameters) throws DataKitException {
        showPrompt(type, parameters);
    }

    int getButtonStatus() {
        if (stateDayStart == START_BUTTON) return START_BUTTON;
        else if (stateDayEnd == END_BUTTON) return END_BUTTON;
        else if (stateDayEnd == COMPLETE_BUTTON) return COMPLETE_BUTTON;
        else return NO_BUTTON;
    }


    protected long getTime(String base, long offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        switch (base) {
            case WAKEUP:
                return calendar.getTimeInMillis() + wakeupOffset + offset;
            case SLEEP:
                return calendar.getTimeInMillis() + sleepOffset + offset;
            case DAY_START:
                return dayStartTime + offset;
            case DAY_END:
                return dayEndTime + offset;
        }
        return 0;
    }

    public boolean isDayStarted() {
        if (dayStartTime == -1) return false;
        long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(BUTTON).getOffset();
        if (dayStartTime < getTime(WAKEUP, offset)) return false;
        else return true;
    }

    public boolean isDayEnded() {
        if (!isDayStarted()) return false;
        else if (dayStartTime < dayEndTime) return true;
        else return false;
    }

    private void readDayStartFromDataKit() throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        dayStartTime = -1;
        if (!dataKitAPI.isConnected()) return;
        try {
            DataSourceClient dataSourceClientDayStart = dataKitAPI.register(createDataSourceBuilderDayStart());
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientDayStart, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                dayStartTime = dataTypeLong.getSample();
            }
        } catch (Exception ignored) {

        }
    }

    private void readWakeupTimeFromDataKit() throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        wakeupOffset = -1;
        if (!dataKitAPI.isConnected()) return;
        try {
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(new DataSourceBuilder().setType(DataSourceType.WAKEUP));
            if (dataSourceClients.size() > 0) {
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), 1);
                if (dataTypes.size() != 0) {
                    DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                    wakeupOffset = dataTypeLong.getSample();
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void readSleepTimeFromDataKit() throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        sleepOffset = -1;
        if (!dataKitAPI.isConnected()) return;
        try {
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(new DataSourceBuilder().setType(DataSourceType.SLEEP));
            if (dataSourceClients.size() > 0) {
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), 1);
                if (dataTypes.size() != 0) {
                    DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                    sleepOffset = dataTypeLong.getSample();
                }
            }
        } catch (Exception ignored) {

        }
    }


    private void readDayEndFromDataKit() throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        dayEndTime = -1;
        if (!dataKitAPI.isConnected()) return;
        try {
            DataSourceClient dataSourceClientDayEnd = dataKitAPI.register(createDataSourceBuilderDayEnd());
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientDayEnd, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                dayEndTime = dataTypeLong.getSample();
            }
        } catch (Exception ignored) {

        }
    }

    private boolean writeDayStartToDataKit() throws DataKitException {
        Log.d(TAG, "writeDayStartToDataKit()...");
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), dayStartTime);
        DataSourceClient dataSourceClientDayStart = dataKitAPI.register(createDataSourceBuilderDayStart());
        dataKitAPI.insert(dataSourceClientDayStart, dataTypeLong);
        return true;
    }

    private boolean writeDayEndToDataKit() throws DataKitException {
        Log.d(TAG, "writeDayEndToDataKit()...");
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        if (!dataKitAPI.isConnected()) return false;
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), dayEndTime);
        DataSourceClient dataSourceClientDayEnd = dataKitAPI.register(createDataSourceBuilderDayEnd());
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


    public void setDayStartTime(long dayStartTime) throws DataKitException {
        Log.d(TAG, "setDayStartTime()...");
        this.dayStartTime = dayStartTime;
        writeDayStartToDataKit();
        reset();
    }

    public void setDayEndTime(long dayEndTime) throws DataKitException {
        Log.d(TAG, "setDayEndTime()...");
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
