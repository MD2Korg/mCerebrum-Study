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
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigDayStartEnd;
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
    private static final int NO_BUTTON = 1;
    static final int START_BUTTON = 2;
    static final int END_BUTTON = 3;
    private static final int COMPLETE_BUTTON = 4;
    private static final String BUTTON = "button";
    private static final String PROMPT = "prompt";
    private static final String NOTIFICATION = "notification";
    private static final String SYSTEM = "system";
    private static final String WAKEUP = "wakeup";
    private static final String SLEEP = "sleep";
    private static final String DAY_START = "day_start";
    private static final String DAY_END = "day_end";
    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    private Handler handler;

    private long dayStartTime;
    private long dayEndTime;
    private long wakeupOffset;
    private long sleepOffset;
    private NotifierManager notifierManager;
    private int stateDayStart;
    private int stateDayEnd;

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

    public void set() {
        Status lastStatus;
        readDayStartFromDataKit();
        readDayEndFromDataKit();
        readWakeupTimeFromDataKit();
        readSleepTimeFromDataKit();
        notifierManager.set();
        Log.d(TAG, "dayStartTime=" + dayStartTime + " dayEndTime=" + dayEndTime + " curTime=" + DateTime.getDateTime() + " diff=" + (DateTime.getDateTime() - dayStartTime));
        if (!isDayStarted())
            lastStatus = new Status(rank, Status.DAY_START_NOT_AVAILABLE);
        else lastStatus = new Status(rank, Status.SUCCESS);
        notifyIfRequired(lastStatus);
        handler.removeCallbacks(runnableDayStart);
        handler.removeCallbacks(runnableDayEnd);
        handler.post(runnableDayStart);
        handler.post(runnableDayEnd);
        Intent intent = new Intent(DayStartEndInfoManager.class.getSimpleName());
        LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(intent);
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
        handler.removeCallbacks(runnableDayStart);
        handler.removeCallbacks(runnableDayEnd);
    }

    private Runnable runnableDayStart = new Runnable() {
        @Override
        public void run() {
            stateDayStart = NO_BUTTON;
            boolean showButton = isShowRequired(DAY_START, BUTTON);
            boolean showPrompt = isShowRequired(DAY_START, PROMPT);
            boolean showNotification = isShowRequired(DAY_START, NOTIFICATION);
            boolean showSystem = isShowRequired(DAY_START, SYSTEM);
            long showButtonTime = getShowTime(DAY_START, BUTTON);
            long showPromptTime = getShowTime(DAY_START, PROMPT);
            long showNotificationTime = getShowTime(DAY_START, NOTIFICATION);
            long showSystemTime = getShowTime(DAY_START, SYSTEM);
            long minTime = Long.MAX_VALUE;
            Log.d(TAG, "runnableDayStart...showButton=" + showButton + " showButtonTime=" + showButtonTime);
            Log.d(TAG, "runnableDayStart...showPrompt=" + showPrompt + " showPromptTime=" + showPromptTime);
            Log.d(TAG, "runnableDayStart...showNotification=" + showNotification + " showNotificationTime=" + showNotificationTime);
            Log.d(TAG, "runnableDayStart...showSystem=" + showSystem + " showSystemTime=" + showSystemTime);
            if (showSystem) {
                setDayStartTime(DateTime.getDateTime());
            } else if (showNotification) {
                stateDayStart = START_BUTTON;
                showPrompt(DAY_START, modelManager.getConfigManager().getConfig().getDay_start().getNotify(NOTIFICATION).getParameters());
            } else if (showPrompt) {
                stateDayStart = START_BUTTON;
                showPrompt(DAY_START, modelManager.getConfigManager().getConfig().getDay_start().getNotify(PROMPT).getParameters());
            } else if (showButton) {
                stateDayStart = START_BUTTON;
            }
            if (showButtonTime != -1 && minTime > showButtonTime)
                minTime = showButtonTime;
            if (showPromptTime != -1 && minTime > showPromptTime)
                minTime = showPromptTime;
            if (showNotificationTime != -1 && minTime > showNotificationTime)
                minTime = showNotificationTime;
            if (showSystemTime != -1 && minTime > showSystemTime)
                minTime = showSystemTime;
            Log.d(TAG, "runnableDayStart: min_time=" + minTime);
            if (minTime != Long.MAX_VALUE) {
                handler.postDelayed(this, minTime);
            }
        }
    };
    private Runnable runnableDayEnd = new Runnable() {
        @Override
        public void run() {
            stateDayEnd = NO_BUTTON;
            if (!isDayStarted()) {
                stateDayEnd = NO_BUTTON;
                return;
            } else if (isDayEnded()) {
                stateDayEnd = COMPLETE_BUTTON;
                return;
            }
            boolean showButton = isShowRequired(DAY_END, BUTTON);
            boolean showPrompt = isShowRequired(DAY_END, PROMPT);
            boolean showNotification = isShowRequired(DAY_END, NOTIFICATION);
            boolean showSystem = isShowRequired(DAY_END, SYSTEM);
            long showButtonTime = getShowTime(DAY_END, BUTTON);
            long showPromptTime = getShowTime(DAY_END, PROMPT);
            long showNotificationTime = getShowTime(DAY_END, NOTIFICATION);
            long showSystemTime = getShowTime(DAY_END, SYSTEM);
            long minTime = Long.MAX_VALUE;
            Log.d(TAG, "runnableDayEnd...showButton=" + showButton + " showButtonTime=" + showButtonTime);
            Log.d(TAG, "runnableDayEnd...showPrompt=" + showPrompt + " showPromptTime=" + showPromptTime);
            Log.d(TAG, "runnableDayEnd...showNotification=" + showNotification + " showNotificationTime=" + showNotificationTime);
            Log.d(TAG, "runnableDayEnd...showSystem=" + showSystem + " showSystemTime=" + showSystemTime);
            if (showSystem) {
                setDayEndTime(DateTime.getDateTime());
            } else if (showNotification) {
                stateDayEnd = END_BUTTON;
                showPrompt(DAY_END, modelManager.getConfigManager().getConfig().getDay_end().getNotify(NOTIFICATION).getParameters());
            } else if (showPrompt) {
                stateDayEnd = END_BUTTON;
                showPrompt(DAY_END, modelManager.getConfigManager().getConfig().getDay_end().getNotify(PROMPT).getParameters());
            } else if (showButton) {
                stateDayEnd = END_BUTTON;
                Intent intent = new Intent(DayStartEndInfoManager.class.getSimpleName());
                LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(intent);
            }
            if (showButtonTime != -1 && minTime > showButtonTime)
                minTime = showButtonTime;
            if (showPromptTime != -1 && minTime > showPromptTime)
                minTime = showPromptTime;
            if (showNotificationTime != -1 && minTime > showNotificationTime)
                minTime = showNotificationTime;
            if (showSystemTime != -1 && minTime > showSystemTime)
                minTime = showSystemTime;
            Log.d(TAG, "runnableDayEndTime...minTime=" + minTime);
            if (minTime != Long.MAX_VALUE) {
                handler.postDelayed(this, minTime);
            }
        }
    };

    private boolean isShowRequired(String dayType, String type) {
        ConfigDayStartEnd configDayStartEnd;
        boolean statusDay;
        if (dayType.equals(DAY_START)) {
            statusDay = isDayStarted();
            configDayStartEnd = modelManager.getConfigManager().getConfig().getDay_start();
        } else {
            statusDay = isDayEnded();
            configDayStartEnd = modelManager.getConfigManager().getConfig().getDay_end();
        }
        if (statusDay) return false;
        if (configDayStartEnd.getNotify(type) == null) return false;
        long offset = configDayStartEnd.getNotify(type).getOffset();
        String base = configDayStartEnd.getNotify(type).getBase();
        long curTime = DateTime.getDateTime();
        long triggerTime = getTime(base, offset);
        return curTime >= triggerTime;
    }

    private long getShowTime(String dayType, String type) {
        ConfigDayStartEnd configDayStartEnd;
        boolean statusDay;
        if (dayType.equals(DAY_START)) {
            configDayStartEnd = modelManager.getConfigManager().getConfig().getDay_start();
            statusDay = isDayStarted();
        } else {
            configDayStartEnd = modelManager.getConfigManager().getConfig().getDay_end();
            statusDay = isDayEnded();
        }
        if (configDayStartEnd.getNotify(type) == null) return -1;
        long offset = configDayStartEnd.getNotify(type).getOffset();
        String base = configDayStartEnd.getNotify(type).getBase();
        long curTime = DateTime.getDateTime();
        long triggerTime = getTime(base, offset);
        if (!statusDay && curTime < triggerTime) {
            return triggerTime - curTime;
        } else return triggerTime + DAY_IN_MILLIS - curTime;
    }


    long getWakeupShowTimestamp() {
        long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(PROMPT).getOffset();
        String base = modelManager.getConfigManager().getConfig().getDay_start().getNotify(PROMPT).getBase();
        long nextDayStart = getTime(base, offset);
        if (nextDayStart < dayStartTime || nextDayStart < dayEndTime) nextDayStart += DAY_IN_MILLIS;
        return nextDayStart;
    }

    private void showPrompt(final String type, ArrayList<String> parameters) {
        try {
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
            notifierManager.trigger(new Callback() {
                @Override
                public void onResponse(String response) {
                    if (type.equals(DAY_START))
                        setDayStartTime(DateTime.getDateTime());
                    else if (type.equals(DAY_END))
                        setDayEndTime(DateTime.getDateTime());
                    reset();
                }
            }, notificationRequests);
        } catch (DataKitException e) {
            e.printStackTrace();
        }

    }

    int getButtonStatus() {
        if (stateDayStart == START_BUTTON) return START_BUTTON;
        else if (stateDayEnd == END_BUTTON) return END_BUTTON;
        else if (stateDayEnd == COMPLETE_BUTTON) return COMPLETE_BUTTON;
        else return NO_BUTTON;
    }


    private long getTime(String base, long offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        switch (base) {
            case WAKEUP:
                return calendar.getTimeInMillis() + wakeupOffset + offset;
            case SLEEP:
                long curBase;
                if (wakeupOffset > sleepOffset) curBase = DAY_IN_MILLIS;
                else curBase = 0;
                return calendar.getTimeInMillis() + sleepOffset + offset + curBase;
            case DAY_START:
                return dayStartTime + offset;
            case DAY_END:
                return dayEndTime + offset;
        }
        return 0;
    }

    boolean isDayStarted() {
        if (dayStartTime == -1) return false;
        long offset = modelManager.getConfigManager().getConfig().getDay_start().getNotify(BUTTON).getOffset();
        return dayStartTime >= getTime(WAKEUP, offset);
    }

    boolean isDayEnded() {
        return isDayStarted() && dayStartTime < dayEndTime;
    }

    private void readDayStartFromDataKit() {
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
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    private void readWakeupTimeFromDataKit() {
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

    private void readSleepTimeFromDataKit() {
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
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }


    private void readDayEndFromDataKit() {
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

    private void writeDayStartToDataKit() {
        try {
            Log.d(TAG, "writeDayStartToDataKit()...");
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), dayStartTime);
            DataSourceClient dataSourceClientDayStart = dataKitAPI.register(createDataSourceBuilderDayStart());
            dataKitAPI.insert(dataSourceClientDayStart, dataTypeLong);
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    private void writeDayEndToDataKit() {
        try {
            Log.d(TAG, "writeDayEndToDataKit()...");
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), dayEndTime);
            DataSourceClient dataSourceClientDayEnd = dataKitAPI.register(createDataSourceBuilderDayEnd());
            dataKitAPI.insert(dataSourceClientDayEnd, dataTypeLong);
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    private DataSourceBuilder createDataSourceBuilderDayStart() {
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

    private DataSourceBuilder createDataSourceBuilderDayEnd() {
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


    void setDayStartTime(long dayStartTime) {
        Log.d(TAG, "setDayStartTime()...");
        this.dayStartTime = dayStartTime;
        writeDayStartToDataKit();
        reset();
    }

    void setDayEndTime(long dayEndTime) {
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
