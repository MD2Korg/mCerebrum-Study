package org.md2k.study.model_view.user_app;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import org.md2k.study.R;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
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
public class UserViewUserApp extends UserView {
    private static final String TAG = UserViewUserApp.class.getSimpleName();
    public ExpandableHeightGridView gridViewApplication;

    public UserViewUserApp(Activity activity, Model model) {
        super(activity, model);
        addView();
    }

    @Override
    public void addView() {
        Log.d(TAG, "addView()...");
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.layout_user_app, null);
            linearLayoutMain.addView(view);
            addUserApp();
        }
    }

    @Override
    public void updateView() {

    }

    @Override
    public void stopView() {

    }

    private void addUserApp() {
        Log.d(TAG, "addUserApp()...");
        final UserAppManager userAppManager = (UserAppManager) ModelManager.getInstance(activity).getModel(ModelFactory.MODEL_USER_APP);
        gridViewApplication = (ExpandableHeightGridView) activity.findViewById(R.id.gridview);
        Log.d(TAG, "addUserApp()...size=" + userAppManager.getUserApps().size());
        AppAdapter appAdapter = new AppAdapter(activity, userAppManager.getUserApps());
        gridViewApplication.setAdapter(appAdapter);
        gridViewApplication.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = userAppManager.userApps.get(position).getAction().getPackage_name();
                String className = userAppManager.userApps.get(position).getAction().getClass_name();
                if (packageName != null && className != null) {
                    Intent intent = new Intent();
                    intent.setClassName(packageName, className);
                    if (userAppManager.userApps.get(position).getAction().getId().endsWith(ModelFactory.MODEL_SELF_REPORT)) {
                        String idd = userAppManager.userApps.get(position).getAction().getId();
                        String type = userAppManager.userApps.get(position).getAction().getType();
                        intent.putExtra("id", idd);
                        intent.putExtra("type", type);
                    }
                    activity.startActivity(intent);
                } else if (packageName != null) {
                    Intent LaunchIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
                    activity.startActivity(LaunchIntent);
                } else if (className != null) {
                    try {
                        Class<?> c = Class.forName(className);
                        Intent intent = new Intent(activity, c);
                        activity.startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

}
