package org.md2k.study.user.application;

public class ShowApp {

    private String id;
    private String name;
    private String class_name;
    private String package_name;
    ShowApp(){

    }

    public ShowApp(String id, String name, String class_name, String package_name) {
        this.id = id;
        this.name = name;
        this.class_name = class_name;
        this.package_name = package_name;
    }

    public String getId() {
        return id;
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
