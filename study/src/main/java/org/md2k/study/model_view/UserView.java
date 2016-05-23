package org.md2k.study.model_view;

import android.app.Activity;
import android.view.View;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.data_quality.UserViewDataQuality;
import org.md2k.study.model_view.day_start_end.UserViewDayStartEnd;
import org.md2k.study.model_view.privacy_control.UserViewPrivacyControl;
import org.md2k.study.model_view.study_start_end.UserViewStudyStartEnd;
import org.md2k.study.model_view.user_app.UserViewUserApp;
import org.md2k.study.model_view.user_status.UserViewUserStatus;

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
public abstract class UserView {
    private static final String TAG = UserView.class.getSimpleName();
    protected View view;
    protected Model model;
    protected Activity activity;

    public abstract void enableView() throws DataKitException;
    public UserView(Activity activity, Model model){
        this.activity=activity;
        this.model=model;
    }
    public abstract void stop();
    public abstract void disableView();
    public static UserView getUserView(Activity activity, String id){
        switch(id){
            case ModelFactory.MODEL_DATA_QUALITY:
                return new UserViewDataQuality(activity, ModelManager.getInstance(activity).getModel(id));
            case ModelFactory.MODEL_DAY_START_END:
                return new UserViewDayStartEnd(activity, ModelManager.getInstance(activity).getModel(id));
            case ModelFactory.MODEL_STUDY_START_END:
                return new UserViewStudyStartEnd(activity, ModelManager.getInstance(activity).getModel(id));
            case ModelFactory.MODEL_PRIVACY:
                return new UserViewPrivacyControl(activity, ModelManager.getInstance(activity).getModel(id));
            case ModelFactory.MODEL_USER_APP:
                return new UserViewUserApp(activity,ModelManager.getInstance(activity).getModel(id));
            case ModelFactory.MODEL_USER_STATUS:
                return new UserViewUserStatus(activity,ModelManager.getInstance(activity).getModel(id));
            default: return null;
        }
    }

    public Model getModel() {
        return model;
    }
}
