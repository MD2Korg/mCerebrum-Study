package org.md2k.study.user.application;

public class App {

    private String content;
    private String packageName;

    public App(String content, String packageName) {
        this.content = content;
        this.packageName = packageName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
