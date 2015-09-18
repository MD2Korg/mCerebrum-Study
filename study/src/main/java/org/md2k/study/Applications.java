package org.md2k.study;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.utilities.Apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Applications {
    private static Applications instance=null;
    public static final int PACKAGENAME=0;
    public static final int APPLICATION=1;
    public static final int SETTINGS=2;
    public static final int SERVICE=3;
    public static final int DOWNLOADLINK=4;
    public static final int INSTALLED=5;

    ArrayList<App> apps;
    Context context;
    public static Applications getInstance(Context context){
        if(instance==null)
            instance=new Applications(context);
        return instance;
    }
    public ArrayList<App> getApps(){
        return apps;
    }
    private Applications(Context context){
        this.context=context;
        readFile(context, Constants.FILENAME_APPINFO);
        apps =filterApplication(apps,DOWNLOADLINK);
    }
    boolean isMatch(App app,int filterType){
        boolean result=true;
        switch(filterType){
            case PACKAGENAME:
                if(app.packagename==null || app.packagename.length()==0) result=false;
                break;
            case APPLICATION:
                if(app.application==null || app.application.length()==0) result=false;
                break;
            case SETTINGS:
                if(app.settings==null || app.settings.length()==0) result=false;
                break;
            case SERVICE:
                if(app.service==null || app.service.length()==0) result=false;
                break;
            case DOWNLOADLINK:
                if(app.downloadlink==null || app.downloadlink.length()==0) result=false;
                break;
            case INSTALLED:
                if (!Apps.isPackageInstalled(context, app.getPackagename())) result=false;
                break;
        }
        return result;
    }
    public ArrayList<App> filterApplication(ArrayList<App> apps, int filterType){
        ArrayList<App> selApps =new ArrayList<>();
        for(int i=0;i< apps.size();i++){
            if(isMatch(apps.get(i),filterType))
                selApps.add(apps.get(i));
        }
        return selApps;
    }
    public void readFile(Context context, String filename){
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<App>>() {}.getType();
            apps = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getTypes(ArrayList<App> appList){
        boolean flag;
        ArrayList<String> types=new ArrayList<>();
        if(appList==null) return types;
        for(int i=0;i<appList.size();i++){
            flag=false;
            for(int j=0;j<types.size();j++)
                if(types.get(j).equals(appList.get(i).type)){
                    flag=true;
                    break;
                }
            if(!flag)
                types.add(appList.get(i).type);
        }
        return types;
    }
}
