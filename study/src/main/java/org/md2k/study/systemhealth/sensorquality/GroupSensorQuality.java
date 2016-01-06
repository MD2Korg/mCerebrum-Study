package org.md2k.study.systemhealth.sensorquality;

import android.content.Context;

import org.md2k.study.systemhealth.Group;
import org.md2k.study.systemhealth.SensorQualityInfo;
import org.md2k.utilities.datakit.DataKitHandler;

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
public class GroupSensorQuality extends Group {
    int noSensor;
    int noSensorQualityGreen;
    int noSensorQualityYellow;
    int noSensorQualityRed;
    public GroupSensorQuality(Context context, String name){
        super(context,name);
//        super(context,name, onDataUpdated);
    }
    public void setDataKitHandler(DataKitHandler dataKitHandler){
        for(int i=0;i<children.size();i++){
            ChildSensorQuality childrenSensorQuality=(ChildSensorQuality)children.get(i);
            childrenSensorQuality.setDataKitHandler(dataKitHandler);
        }
    }
    public void add(SensorQualityInfo sensorQualityInfo) {
        ChildSensorQuality childrenSensorQuality = new ChildSensorQuality(context, sensorQualityInfo);
        children.add(childrenSensorQuality);
        noSensor=children.size();
    }
    public String getName(){
        return name+" ("+ noSensorQualityGreen +"/"+noSensor+")";
    }
}
