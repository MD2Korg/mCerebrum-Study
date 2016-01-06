package org.md2k.study.systemhealth.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.study.systemhealth.Child;
import org.md2k.study.systemhealth.DeviceInfo;
import org.md2k.utilities.Apps;
import org.md2k.utilities.datakit.DataKitHandler;

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
public class ChildDeviceSettings extends Child {
    String platformType;
    String location;
    ArrayList<String> dataSourceType;
    int noDataSourceType;
    int noDataSourceTypeConnected;
    DataKitHandler dataKitHandler=null;
    String package_name;
    String settings_name;
    public ChildDeviceSettings(final Context context, DeviceInfo deviceInfo){
        super(context, deviceInfo.platformtype+"-"+deviceInfo.datasourcetype);
        platformType=deviceInfo.platformtype;
        location=deviceInfo.location;
        dataSourceType=deviceInfo.datasourcetype;
        noDataSourceType=dataSourceType.size();
        package_name=deviceInfo.package_name;
        settings_name=deviceInfo.settings;
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Apps.isPackageInstalled(context,package_name)){
                    showAlertDialogAppNotInstalled(context);
                }else{
                    String lists="";
                    for(int i=0;i<dataSourceType.size();i++){
                        lists+=dataSourceType.get(i)+"\n";
                    }
                    showAlertDialogDataSourceTypeList(context,lists);
                }
            }
        };

    }
    void showAlertDialogAppNotInstalled(final Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Error: App Not Installed")
//                .setIcon(R.drawable.ic_error_outline_white_24dp)
                .setMessage(platformType + " application is not installed.\n\n Please install the application first")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
    void showAlertDialogDataSourceTypeList(final Context context, String lists){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("DataSourceType")
//                .setIcon(R.drawable.ic_error_outline_white_24dp)
                .setMessage("Please select the following sensors and start the service.\n\n"+lists)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClassName(package_name, settings_name);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    public String getName(){
        String loc="";
        if(location!=null) {
            if (location.startsWith("LEFT"))
                loc = "--Left";
            else if(location.startsWith("RIGHT"))
                loc = "--Right";
        }
        String p=platformType;
        if(platformType.equals(PlatformType.AUTOSENSE_CHEST))
            p="Autosense";
        else if(platformType.equals(PlatformType.MICROSOFT_BAND))
            p="Microsoft Band";
        else if(platformType.equals(PlatformType.PHONE))
            p="Phone";
        return p+loc+" ("+noDataSourceTypeConnected+"/"+noDataSourceType+")";
    }
    public void setDataKitHandler(DataKitHandler dataKitHandler){
        this.dataKitHandler=dataKitHandler;
    }

    public void setNoDataSourceTypeConnected(){
        if(dataKitHandler==null) return;
        int noCurrentDataSourceTypeConnected=0;
        Platform platform=new PlatformBuilder().setType(platformType).build();
        DataSourceBuilder dataSourceBuilder= new DataSourceBuilder().setPlatform(platform);
        ArrayList<DataSourceClient> dataSourceClients;
        dataSourceClients=dataKitHandler.find(dataSourceBuilder);
  //      for(int i=0;i<dataSourceClients.size();i++){
  //          if(dataSourceClients.get(i).getStatus().getStatusCode()==StatusCodes.DATASOURCE_PUBLISHED)
  //              noCurrentDataSourceTypeConnected++;
  //      }
        if(noCurrentDataSourceTypeConnected!=noDataSourceType){
            noDataSourceTypeConnected=noCurrentDataSourceTypeConnected;
            updateStatus();
        }
        else {
            noCurrentDataSourceTypeConnected=0;
            for (int i = 0; i < dataSourceType.size(); i++) {
                dataSourceBuilder = dataSourceBuilder.setType(dataSourceType.get(i));
                dataSourceClients = dataKitHandler.find(dataSourceBuilder);
    //            for (int j = 0; j < dataSourceClients.size(); j++) {
      //              if (dataSourceClients.get(j).getStatus().getStatusCode() == StatusCodes.DATASOURCE_PUBLISHED) {
        //                noCurrentDataSourceTypeConnected++;
          //              break;
            //        }
              //  }
            }
            if(noCurrentDataSourceTypeConnected!=noDataSourceTypeConnected){
                noDataSourceTypeConnected=noCurrentDataSourceTypeConnected;
                updateStatus();
            }
        }
    }
    public void updateStatus(){
/*        if(this.noDataSourceTypeConnected==this.noDataSourceType) {
            status = SystemHealthManager.GREEN;
            super.updateStatus();
        }
        else {
            status= SystemHealthManager.RED;
            super.updateStatus();
        }
        if(onDataUpdated!=null) onDataUpdated.onChange();
*/    }
    public void refresh(){
        setNoDataSourceTypeConnected();
    }
}
