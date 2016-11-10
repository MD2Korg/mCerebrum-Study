package org.md2k.study.model_view.data_quality;

import android.os.Handler;

import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigDataQualityView;
import org.md2k.utilities.data_format.DATA_QUALITY;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
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
class DataQualityInfo {
    private static final String TAG = DataQualityInfo.class.getSimpleName();
    private static final long TIME_LIMIT_NODATA = 30*1000;
    private static final long TIME_LIMIT_GOOD_TO_NOTWORN = 15*1000;
    private static final long TIME_LIMIT_NOTWORN_TO_GOOD = 9*1000;
    ConfigDataQualityView configDataQualityView;
    private String message;
    private ArrayList<DataTypeInt> qualities;
    private int quality;
    private Handler handler;

    DataQualityInfo() {
        quality = Status.DATAQUALITY_OFF;
        qualities=new ArrayList<>();
        message = "";
        handler=new Handler();
    }

    public String getTitle() {
        if(configDataQualityView==null) return "";
        return configDataQualityView.getName();
    }

    int getQuality() {
        return quality;
    }


    public String getMessage() {
        return message;
    }

    void setConfigDataQualityView(ArrayList<ConfigDataQualityView> configDataQualityViews, DataSource dataSource) {
        for(int i=0;i<configDataQualityViews.size();i++){
            DataSource confDataSource=configDataQualityViews.get(i).getPlotter().getDatasource();
            if(confDataSource.getType().equals(DataSourceType.RESPIRATION) && confDataSource.getType().equals(dataSource.getId())) {
                configDataQualityView = configDataQualityViews.get(i);
                return;
            }
            if(confDataSource.getType().equals(DataSourceType.ECG) && confDataSource.getType().equals(dataSource.getId())) {
                configDataQualityView = configDataQualityViews.get(i);
                return;
            }
            if(confDataSource.getPlatform()==null || confDataSource.getPlatform().getId()==null || confDataSource.getPlatform().getType()==null) {
            }
            else if(confDataSource.getPlatform().getType().equals(dataSource.getPlatform().getType()) && confDataSource.getPlatform().getId().equals(dataSource.getPlatform().getId())) {
                configDataQualityView = configDataQualityViews.get(i);
                return;
            }
        }
    }

    public void set(DataTypeInt value) {
        long currentTime = DateTime.getDateTime();
        for(Iterator<DataTypeInt> i=qualities.iterator();i.hasNext();) {
            DataTypeInt dataTypeInt = i.next();
            if(dataTypeInt.getDateTime()+ TIME_LIMIT_NODATA <currentTime)
                i.remove();
        }
        int lastSample=translate(value.getSample());
        qualities.add(new DataTypeInt(value.getDateTime(), lastSample));
        switch(quality){
            case Status.DATAQUALITY_OFF:
                quality=lastSample;
                break;
            case Status.DATAQUALITY_NOT_WORN:
                quality=getQualityNotWornTo();
                break;
            case Status.DATAQUALITY_GOOD:
                quality=getQualityGoodTo();
                break;
        }
        Status curStatus = new Status(0, this.quality);
        message = getTitle() + " - " + curStatus.getMessage();
    }
    private int getQualityGoodTo(){
        int countNoData=0;
        int countGoodData=0;
        long currentTime=DateTime.getDateTime();
        for(int i=0;i<qualities.size();i++) {
            if (qualities.get(i).getSample() == Status.DATAQUALITY_OFF)
                countNoData++;
            else if(qualities.get(i).getSample()==Status.DATAQUALITY_GOOD && qualities.get(i).getDateTime()+ TIME_LIMIT_GOOD_TO_NOTWORN >=currentTime)
                countGoodData++;
        }
        if(qualities.size()==countNoData)
            return Status.DATAQUALITY_OFF;
        else if(countGoodData>0) return Status.DATAQUALITY_GOOD;
        else return Status.DATAQUALITY_NOT_WORN;
    }

    private int getQualityNotWornTo(){
        int countNoData=0;
        int countGoodData=0;
        long currentTime=DateTime.getDateTime();
        for(int i=0;i<qualities.size();i++) {
            if (qualities.get(i).getSample() == Status.DATAQUALITY_OFF)
                countNoData++;
            else if(qualities.get(i).getSample()==Status.DATAQUALITY_GOOD && qualities.get(i).getDateTime()+ TIME_LIMIT_NOTWORN_TO_GOOD >=currentTime)
                countGoodData++;
        }
        if(qualities.size()==countNoData)
            return Status.DATAQUALITY_OFF;
        else if(countGoodData>0) return Status.DATAQUALITY_GOOD;
        else return Status.DATAQUALITY_NOT_WORN;
    }

    private int translate(int value) {
        switch (value) {
            case DATA_QUALITY.GOOD:
                return Status.DATAQUALITY_GOOD;
            case DATA_QUALITY.BAND_OFF:
                return Status.DATAQUALITY_OFF;
            case DATA_QUALITY.BAND_LOOSE:
            case DATA_QUALITY.NOISE:
            case DATA_QUALITY.MISSING:
            case DATA_QUALITY.NOT_WORN:
            case DATA_QUALITY.BAD:
                return Status.DATAQUALITY_NOT_WORN;
            default:
                return Status.DATAQUALITY_OFF;
        }
    }

}
