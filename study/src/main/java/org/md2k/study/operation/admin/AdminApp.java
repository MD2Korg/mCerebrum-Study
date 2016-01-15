package org.md2k.study.operation.admin;

public class AdminApp {

    private String id;
    private boolean value;

    public AdminApp(String id, boolean value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public boolean isValue() {
        return value;
    }
}
