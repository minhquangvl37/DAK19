package org.o7planning.dak19.models;

public class ModelOrderItem {
    private String pId, id, cost,name,price,quantity;

    public ModelOrderItem() {
    }

    public ModelOrderItem(String pId, String id, String cost, String name, String price, String quantity) {
        this.pId = pId;
        this.id = id;
        this.cost = cost;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
