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
    private static Applications instance = null;
    public static final int PACKAGENAME = 0;
    public static final int SETTINGS = 1;
    public static final int SERVICE = 2;
    public static final int DOWNLOADLINK = 3;
    public static final int INSTALLED = 4;

    ArrayList<Application> applications;
    Context context;

    public static Applications getInstance(Context context) {
        if (instance == null)
            instance = new Applications(context);
        return instance;
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    private Applications(Context context) {
        this.context = context;
        readFile(context, Constants.FILENAME_APPINFO);
        applications = filterApplication(applications, DOWNLOADLINK);
        applications = filterApplication(applications,PACKAGENAME);
    }

    boolean isMatch(Application application, int filterType) {
        boolean result = true;
        switch (filterType) {
            case PACKAGENAME:
                if (application.packagename == null || application.packagename.length() == 0) result = false;
                break;
            case SETTINGS:
                if (application.settings == null || application.settings.length() == 0) result = false;
                break;
            case SERVICE:
                if (application.service == null || application.service.length() == 0) result = false;
                break;
            case DOWNLOADLINK:
                if (application.downloadlink == null || application.downloadlink.length() == 0) result = false;
                break;
            case INSTALLED:
                if (!Apps.isPackageInstalled(context, application.getPackagename())) result = false;
                break;
        }
        return result;
    }

    public ArrayList<Application> filterApplication(ArrayList<Application> applications, int filterType) {
        ArrayList<Application> selApplications = new ArrayList<>();
        for (int i = 0; i < applications.size(); i++) {
            if (isMatch(applications.get(i), filterType))
                selApplications.add(applications.get(i));
        }
        return selApplications;
    }

    public void readFile(Context context, String filename) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Application>>() {
            }.getType();
            applications = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTypes(ArrayList<Application> applicationList) {
        boolean flag;
        ArrayList<String> types = new ArrayList<>();
        if (applicationList == null) return types;
        for (int i = 0; i < applicationList.size(); i++) {
            flag = false;
            for (int j = 0; j < types.size(); j++)
                if (types.get(j).equals(applicationList.get(i).type)) {
                    flag = true;
                    break;
                }
            if (!flag)
                types.add(applicationList.get(i).type);
        }
        return types;
    }
}
