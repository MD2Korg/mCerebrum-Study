package org.md2k.study.model.config_info;

import android.content.Context;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.ConfigInfo;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;
import org.md2k.utilities.Files;

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
public class ConfigInfoManager extends Model {
    ConfigInfo configInfo;

    public ConfigInfoManager(Context context,DataKitAPI dataKitAPI, Operation operation) {
        super(context, dataKitAPI, operation);
        this.configInfo = ConfigManager.getInstance(context).getConfig().getConfig_info();
    }

    public Status getStatus() {
        if (configInfo.getRequired_files() == null || configInfo.getRequired_files().size() == 0)
            return new Status(Status.SUCCESS);
        for (int i = 0; i < configInfo.getRequired_files().size(); i++) {
            if (!Files.isExist(Constants.CONFIG_DIRECTORY_BASE + configInfo.getRequired_files().get(i)))
                return new Status(Status.CONFIG_FILE_NOT_EXIST);
        }
        return new Status(Status.SUCCESS);
    }

    @Override
    public void reset() {

    }

    public String getId() {
        return configInfo.getId();
    }

    public String getVersion() {
        return configInfo.getVersion();
    }

    public ArrayList<String> getRequired_files() {
        return configInfo.getRequired_files();
    }
}
