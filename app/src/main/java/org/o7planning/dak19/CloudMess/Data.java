package org.o7planning.dak19.CloudMess;

public class Data {
    private String user;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String type;

    public Data(String user, int icon, String body, String title, String sented, String type) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.type = type;
    }

    public Data(){}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sentde) {
        this.sented = sented;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
