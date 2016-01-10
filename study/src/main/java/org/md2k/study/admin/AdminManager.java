package org.md2k.study.admin;

import android.content.Context;

import org.md2k.study.Status;
import org.md2k.study.admin.install.InstallApps;
import org.md2k.study.admin.reset.ResetInfoManager;
import org.md2k.study.admin.sleep_wakeup.SleepInfoManager;
import org.md2k.study.admin.study_info.StudyInfoManager;

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
public class AdminManager {
    InstallApps installApps;
    StudyInfoManager studyInfoManager;
    SleepInfoManager sleepInfoManager;
    ResetInfoManager resetInfoManager;
    private static AdminManager instance;
    Context context;
    public static AdminManager getInstance(Context context){
        if(instance==null)
            instance=new AdminManager(context);
        return instance;
    }
    public void readFromDB(){
        studyInfoManager = new StudyInfoManager(context);
        sleepInfoManager=new SleepInfoManager(context);
    }

    private AdminManager(Context context) {
        this.context=context;
        installApps = InstallApps.getInstance(context);
        studyInfoManager = new StudyInfoManager(context);
        sleepInfoManager=new SleepInfoManager(context);
        resetInfoManager=ResetInfoManager.getInstance(context);
    }
    public Status getStatus(){
        if (installApps.getStatus().getStatusCode() == Status.APP_NOT_INSTALLED)
            return new Status(Status.APP_NOT_INSTALLED);
        if (studyInfoManager.getStatus().getStatusCode() == Status.USERID_NOT_DEFINED)
            return new Status(Status.USERID_NOT_DEFINED);
        return sleepInfoManager.getStatus();
    }
}
