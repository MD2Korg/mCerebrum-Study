package org.md2k.study.model.clear_data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.Model;
import org.md2k.study.model.app_service.AppServiceManager;
import org.md2k.study.model.clear_config.ClearConfig;

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
public class ClearDataManager extends Model {

    public void delete(Activity activity) {
        lastStatus = new Status(Status.DATABASE_NOT_AVAILABLE);
        ((AppServiceManager) ModelManager.getInstance(activity).getModel(ModelManager.MODEL_APP_SERVICE)).stop();
        Intent intent = new Intent();
        intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityDataKitSettings");
        activity.startActivity(intent);
    }

    @Override
    public Status getStatus() {
        return lastStatus;
    }

    public void setStatus(Status status) {
        lastStatus = status;
    }

    @Override
    public void reset() {

    }

    public ClearDataManager(Context context, DataKitAPI dataKitAPI, Operation operation) {
        super(context, dataKitAPI, operation);
        lastStatus = new Status(Status.SUCCESS);
    }
}