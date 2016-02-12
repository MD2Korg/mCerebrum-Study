package org.md2k.study.operation.user_app;

import org.md2k.utilities.Report.Log;

public class UserApp {

    private static final String TAG = UserApp.class.getSimpleName();
    private String id;
    private String name;
    private String icon;
    private String package_name;
    private String class_name;
    private boolean show;
    public UserApp(String id, String name, String class_name, String package_name){
        this.id=id;
        this.name=name;
        this.class_name=class_name;
        this.package_name=package_name;
    }
    public void setIcon(boolean active){
        if(active)
            icon="ic_lock_red_48dp";
        else icon="ic_unlock_teal_48dp";
        Log.d(TAG, "icon=" + icon);
    }

    public UserApp(org.md2k.study.config.UserApp userApp) {
        this.id=userApp.getId();
        this.class_name=userApp.getClass_name();
        this.package_name=userApp.getPackage_name();
        this.show =userApp.isShow();
        this.name=userApp.getText();
        this.icon=userApp.getIcon();
    }

    public String getId() {
        return id;
    }
    public String getIcon(){
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getClass_name() {
        return class_name;
    }

    public String getPackage_name() {
        return package_name;
    }
}
