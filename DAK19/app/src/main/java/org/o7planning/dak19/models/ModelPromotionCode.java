package org.o7planning.dak19.models;

public class ModelPromotionCode {
    private String id,timestamp,promoDescription,promoCode,promoPrice,
            minimumOrderPrice,expireDate;

    public ModelPromotionCode() {
    }

    public ModelPromotionCode(String id, String timestamp, String description, String promoCode, String promoPrice, String minimumOrderPrice, String expireDate) {
        this.id = id;
        this.timestamp = timestamp;
        this.promoDescription = description;
        this.promoCode = promoCode;
        this.promoPrice = promoPrice;
        this.minimumOrderPrice = minimumOrderPrice;
        this.expireDate = expireDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPromoDescription() {
        return promoDescription;
    }

    public void promoDescription(String description) {
        this.promoDescription = description;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoPrice() {
        return promoPrice;
    }

    public void setPromoPrice(String promoPrice) {
        this.promoPrice = promoPrice;
    }

    public String getMinimumOrderPrice() {
        return minimumOrderPrice;
    }

    public void setMinimumOrderPrice(String minimumOrderPrice) {
        this.minimumOrderPrice = minimumOrderPrice;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
