package org.md2k.study.operation.user;

public class UserApp {

    private String id;
    private boolean value;
    private String name;
    private String icon;
    private String class_name;
    private String package_name;
    public UserApp(String id, String name, String class_name, String package_name){
        this.id=id;
        this.name=name;
        this.class_name=class_name;
        this.package_name=package_name;
    }

    public UserApp(String id, boolean value) {
        this.id = id;
        this.value=value;
        switch(id){
            case "intervention":
                name="Intervention";
                icon="ic_intervention_48dp";
                class_name="org.md2k.study.view.intervention.ActivityInterventionApp";
                break;
            case "smoking_self_report":
                name="Smoking Report";
                icon="ic_smoking_teal_48dp";
                class_name="org.md2k.study.view.selfreport.ActivitySelfReport";
                break;
            case "plotter":
                name="Plotter";
                icon="ic_plot_teal_48dp";
                package_name= "org.md2k.plotter";
                break;
            case "privacy":
                name="Privacy Control";
                icon="ic_lock_red_48dp";
                package_name="org.md2k.datakit";
                class_name="org.md2k.datakit.ActivityPrivacy";
                break;
            case "stop":
                name="Stop";
                icon="ic_stop_teal_48dp";
                class_name="org.md2k.study.view.service.ActivityService";
        }
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
