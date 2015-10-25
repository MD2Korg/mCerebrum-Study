package org.md2k.study.groups.app;

import android.content.Context;

import org.md2k.study.OnDataUpdated;
import org.md2k.study.groups.AppInfo;
import org.md2k.study.groups.Group;
import org.md2k.study.groups.GroupManager;

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
public class GroupApp extends Group {
    public int noApp;
    public int noInstalledApp;
    public int noUpdateAvailableApp;

    public GroupApp(Context context, String name, OnDataUpdated onDataUpdated) {
        super(context, name, onDataUpdated);
    }

    public void add(AppInfo appInfo) {
        ChildrenApp childrenApp = new ChildrenApp(context, appInfo, onDataUpdatedGroup);
        children.add(childrenApp);
        noApp = children.size();
    }
    public String getName(){
        return name+" ("+noInstalledApp+"/"+noApp+")";
    }

    OnDataUpdated onDataUpdatedGroup = new OnDataUpdated() {
        @Override
        synchronized public void onChange() {
            noInstalledApp = 0;
            noApp = children.size();
            for (int i = 0; i < noApp; i++) {
                ChildrenApp childrenApp = (ChildrenApp) children.get(i);
                if (childrenApp.currentVersionNameLong == -1) continue;
                if (childrenApp.currentVersionNameLong >= childrenApp.latestVersionNameLong)
                    noInstalledApp++;
                else if (childrenApp.latestVersionNameLong < childrenApp.currentVersionNameLong)
                    noUpdateAvailableApp++;
            }
            if (noApp == noInstalledApp)
                status = GroupManager.GREEN;
            else if (noUpdateAvailableApp>0)
                status = GroupManager.YELLOW;
            else
                status = GroupManager.RED;
            onDataUpdated.onChange();
        }
    };
}
