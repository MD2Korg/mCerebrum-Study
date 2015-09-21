package org.md2k.study;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.utilities.Apps;

import java.util.ArrayList;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, activity
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * activity list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * activity SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF activity SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class UI {
    static void createOverviewUI(Activity activity) {
        TableLayout tableLayout = (TableLayout) activity.findViewById(R.id.tableLayout_overview);
        tableLayout.removeAllViews();

        TableRow tableRowHeader = new TableRow(activity);

        tableRowHeader.addView(new TextView(activity));

        TextView tv1 = new TextView(activity);
        tv1.setText("Available");
        tv1.setTextColor(activity.getResources().getColor(R.color.teal_a400));

        //tv1.setLayoutParams(trl);
        tableRowHeader.addView(tv1);


        TextView tv2 = new TextView(activity);
        tv2.setText("Status");
        tv2.setTextColor(activity.getResources().getColor(R.color.teal_a400));

        TableRow.LayoutParams tls = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        tls.gravity = Gravity.CENTER;
        tv2.setLayoutParams(tls);
        tableRowHeader.addView(tv2);

        TextView tv3 = new TextView(activity);
        TableRow.LayoutParams tlf = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        tlf.gravity = Gravity.CENTER;
        tv3.setLayoutParams(tlf);

        tv3.setText("Fix");
        tv3.setTextColor(activity.getResources().getColor(R.color.teal_a400));
        //tv3.setLayoutParams(trl);
        tableRowHeader.addView(tv3);

        tableLayout.addView(tableRowHeader);

    }

    static void createDeviceUI(Activity activity) {
        ArrayList<Device> devices = Devices.getInstance(activity).getDevices();
        TableLayout tableLayout = (TableLayout) activity.findViewById(R.id.tablelayout_device);
        tableLayout.removeAllViews();

        TableRow tableRowHeader = new TableRow(activity);

        tableRowHeader.addView(new TextView(activity));

        TextView tv1 = new TextView(activity);
        tv1.setText("Configured");
        tv1.setTextColor(activity.getResources().getColor(R.color.teal_a400));

        //tv1.setLayoutParams(trl);
        tableRowHeader.addView(tv1);


        TextView tv2 = new TextView(activity);
        tv2.setText("Status");
        tv2.setTextColor(activity.getResources().getColor(R.color.teal_a400));

        TableRow.LayoutParams tls = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        tls.gravity = Gravity.CENTER;
        tv2.setLayoutParams(tls);
        tableRowHeader.addView(tv2);

        TextView tv3 = new TextView(activity);
        TableRow.LayoutParams tlf = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        tlf.gravity = Gravity.CENTER;
        tv3.setLayoutParams(tlf);

        tv3.setText("Fix");
        tv3.setTextColor(activity.getResources().getColor(R.color.teal_a400));
        //tv3.setLayoutParams(trl);
        tableRowHeader.addView(tv3);

        tableLayout.addView(tableRowHeader);

    }

    static void updateDevice(Activity activity) {
        ArrayList<Device> devices = Devices.getInstance(activity).getDevices();
        TableLayout tableLayout = (TableLayout) activity.findViewById(R.id.tablelayout_device);

        for (int i = 0; i < devices.size(); i++) {
            String name;
            TableRow tableRow = new TableRow(activity);

            TextView textViewName = new TextView(activity);
            if(devices.get(i).platformtype.equals(PlatformType.AUTOSENSE_CHEST))
                name="AutoSense (C)";
            else if(devices.get(i).platformtype.equals(PlatformType.MICROSOFT_BAND) && devices.get(i).location.equals("LEFT_WRIST"))
                name="MSBand (L)";
            else if(devices.get(i).platformtype.equals(PlatformType.MICROSOFT_BAND) && devices.get(i).location.equals("RIGHT_WRIST"))
                name="MSBand (R)";
            else if(devices.get(i).platformtype.equals(PlatformType.PHONE))
                name="Phone";
            else continue;
            textViewName.setText(name);
            tableRow.addView(textViewName);

            TextView textViewAvailable = new TextView(activity);
            textViewAvailable.setText("1 (out of 5)");
            tableRow.addView(textViewAvailable);

            ImageView imageView = new ImageView(activity);
            TableRow.LayoutParams trli = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            trli.gravity = Gravity.CENTER;

            imageView.setLayoutParams(trli);
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.error));
            tableRow.addView(imageView);
//            LinearLayout ll=new LinearLayout(activity);


            Button button = new Button(activity, null, android.R.attr.buttonStyleSmall);
            button.setText("Fix");
            TableRow.LayoutParams trlb = new TableRow.LayoutParams(80, 120, 1f);
            trlb.setMargins(0, 5, 0, 5);
            button.setLayoutParams(trlb);

            button.setBackground(activity.getResources().getDrawable(R.drawable.button_red));
            //          ll.addView(button);
            tableRow.addView(button);
            tableLayout.addView(tableRow);
        }
    }

    static int getAppCount(Activity activity) {
        ArrayList<Application> applications = Applications.getInstance(activity).applications;
        return applications.size();
    }
    static int getServiceCount(Activity activity) {
        ArrayList<Application> applications = Applications.getInstance(activity).applications;
        applications = Applications.getInstance(activity).filterApplication(applications, Applications.SERVICE);
        return applications.size();
    }
    static int getServiceRunningCount(Activity activity) {
        int count = 0;
        ArrayList<Application> applications = Applications.getInstance(activity).applications;
        applications = Applications.getInstance(activity).filterApplication(applications, Applications.SERVICE);
        for (int i = 0; i < applications.size(); i++)
            if (Apps.isServiceRunning(activity, applications.get(i).getService()))
                count++;
        return count;
    }

    static int getAppInstalledCount(Activity activity) {
        int count = 0;
        ArrayList<Application> applications = Applications.getInstance(activity).applications;
        for (int i = 0; i < applications.size(); i++) {
            if (Apps.isPackageInstalled(activity, applications.get(i).getPackagename()))
                count++;
        }
        return count;
    }

    static void updateAppCount(final Activity activity) {
        int appCount = getAppCount(activity);
        int appInstalled = getAppInstalledCount(activity);
        TableLayout tableLayout = (TableLayout) activity.findViewById(R.id.tableLayout_overview);
        TableRow tableRow = new TableRow(activity);

        TextView textViewName = new TextView(activity);
        textViewName.setText("Applications");
        tableRow.addView(textViewName);

        TextView textViewAvailable = new TextView(activity);
        textViewAvailable.setText(String.valueOf(appInstalled) + " (out of " + String.valueOf(appCount) + ")");
        tableRow.addView(textViewAvailable);

        ImageView imageView = new ImageView(activity);
        TableRow.LayoutParams trli = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        trli.gravity = Gravity.CENTER;

        imageView.setLayoutParams(trli);
        if (appCount == appInstalled)
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ok));
        else
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.error));
        tableRow.addView(imageView);

        Button button = new Button(activity, null, android.R.attr.buttonStyleSmall);
        button.setText("Fix");
        TableRow.LayoutParams trlb = new TableRow.LayoutParams(80, 120, 1f);
        trlb.setMargins(0, 5, 0, 5);
        button.setLayoutParams(trlb);
        button.setBackground(activity.getResources().getDrawable(R.drawable.button_red));
        if(appCount==appInstalled){
            button.setVisibility(View.INVISIBLE);
        }
        else{
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity,ActivityAppList.class);
                    activity.startActivity(intent);

                }
            });
        }
        tableRow.addView(button);
        tableLayout.addView(tableRow);

    }
    static void updateServiceCount(final Activity activity) {
        int serviceCount = getServiceCount(activity);
        int serviceRunning = getServiceRunningCount(activity);
        TableLayout tableLayout = (TableLayout) activity.findViewById(R.id.tableLayout_overview);
        TableRow tableRow = new TableRow(activity);

        TextView textViewName = new TextView(activity);
        textViewName.setText("Services");
        tableRow.addView(textViewName);

        TextView textViewAvailable = new TextView(activity);
        textViewAvailable.setText(String.valueOf(serviceRunning) + " (out of " + String.valueOf(serviceCount) + ")");
        tableRow.addView(textViewAvailable);

        ImageView imageView = new ImageView(activity);
        TableRow.LayoutParams trli = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        trli.gravity = Gravity.CENTER;

        imageView.setLayoutParams(trli);
        if (serviceCount == serviceRunning)
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ok));
        else
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.error));
        tableRow.addView(imageView);

        Button button = new Button(activity, null, android.R.attr.buttonStyleSmall);
        button.setText("Fix");
        TableRow.LayoutParams trlb = new TableRow.LayoutParams(80, 120, 1f);
        trlb.setMargins(0, 5, 0, 5);
        button.setLayoutParams(trlb);
        button.setBackground(activity.getResources().getDrawable(R.drawable.button_red));
        if(serviceCount==serviceRunning){
            button.setVisibility(View.INVISIBLE);
        }
        else{
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity,ActivityServiceList.class);
                    activity.startActivity(intent);

                }
            });
        }
        tableRow.addView(button);
        tableLayout.addView(tableRow);

    }
    
}
