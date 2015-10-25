package org.md2k.study.groups.service;

import android.content.Context;

import org.md2k.study.OnDataUpdated;
import org.md2k.study.groups.AppInfo;
import org.md2k.study.groups.Group;
import org.md2k.study.groups.GroupManager;
import org.md2k.study.groups.app.ChildrenApp;
import org.md2k.utilities.Report.Log;

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
public class GroupService extends Group {
    private static final String TAG = GroupService.class.getSimpleName();
    int noService;
    int noServiceRunning;

    public GroupService(Context context, String name, OnDataUpdated onDataUpdated) {
        super(context, name, onDataUpdated);
    }

    public void add(AppInfo appInfo) {
        ChildrenService childrenService = new ChildrenService(context, appInfo, onDataUpdatedGroup);
        children.add(childrenService);
        noService=children.size();
    }
    public void refresh(){
        for(int i=0;i<children.size();i++){
            ChildrenService childrenService=(ChildrenService) children.get(i);
            childrenService.refresh();
        }
    }

    public String getName(){
        return name+" ("+noServiceRunning+"/"+noService+")";
    }

    OnDataUpdated onDataUpdatedGroup = new OnDataUpdated() {
        @Override
        synchronized public void onChange() {
            noServiceRunning = 0;
            noService = children.size();
            for (int i = 0; i < noService; i++) {
                ChildrenService childrenService = (ChildrenService) children.get(i);
                if (childrenService.serviceRunning)
                    noServiceRunning++;
            }
            Log.d(TAG, "noService=" + noService + " noServiceRunning=" + noServiceRunning);
            if (noService == noServiceRunning)
                status = GroupManager.GREEN;
            else status = GroupManager.RED;
            onDataUpdated.onChange();
        }
    };

}
