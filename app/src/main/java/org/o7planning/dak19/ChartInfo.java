package org.o7planning.dak19;

public class ChartInfo {
    int cost;
    String date;
    public ChartInfo(String date, int cost) {
        this.date = date;
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
