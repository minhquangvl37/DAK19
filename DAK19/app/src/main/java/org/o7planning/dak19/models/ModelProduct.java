package org.o7planning.dak19.models;

public class ModelProduct {
    private String productId, productTitle,productDescription,productCategory,productQuantity,
            originalPrice,productIcon,discountPrice,discountNote,discountAvaliable,
            timestamp,uid;

    public ModelProduct() {
    }

    public ModelProduct(String productId, String productTitle, String productDescription,
                        String productCategory, String productQuantity, String originalPrice, String productIcon,
                        String discountPrice, String discountNote, String discountAvaliable, String timestamp, String uid) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.originalPrice = originalPrice;
        this.productIcon = productIcon;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvaliable = discountAvaliable;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountAvaliable() {
        return discountAvaliable;
    }

    public void setDiscountAvaliable(String discountAvaliable) {
        this.discountAvaliable = discountAvaliable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
