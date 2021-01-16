package com.example.smartrestaurantapp.model;

public class OrderProductModifierModel {
    String restaurantId_product;
    String modifierId_product;
    String modifierName_product;
    String price_product;
    String isActive_product;
    String productList_product;
    String modifierDetails_product;
    String modifierTypeId_product;
    String lstModifierType_product;
    String lstMenuModifierOptions_product;
    public OrderProductModifierModel(String restaurantId_product, String modifierId_product, String modifierName_product, String price_product, String isActive_product, String productList_product, String modifierDetails_product, String modifierTypeId_product, String lstModifierType_product, String lstMenuModifierOptions_product) {
        this. restaurantId_product=restaurantId_product;
        this. modifierId_product=modifierId_product;
        this. modifierName_product=modifierName_product;
        this. price_product=price_product;
        this. isActive_product=isActive_product;
        this. productList_product=productList_product;
        this. modifierDetails_product=modifierDetails_product;
        this. modifierTypeId_product=modifierTypeId_product;
        this. lstModifierType_product=lstModifierType_product;
        this. lstMenuModifierOptions_product=lstMenuModifierOptions_product;
    }

    public String getRestaurantId_product() {
        return restaurantId_product;
    }

    public void setRestaurantId_product(String restaurantId_product) {
        this.restaurantId_product = restaurantId_product;
    }

    public String getModifierId_product() {
        return modifierId_product;
    }

    public void setModifierId_product(String modifierId_product) {
        this.modifierId_product = modifierId_product;
    }

    public String getModifierName_product() {
        return modifierName_product;
    }

    public void setModifierName_product(String modifierName_product) {
        this.modifierName_product = modifierName_product;
    }

    public String getPrice_product() {
        return price_product;
    }

    public void setPrice_product(String price_product) {
        this.price_product = price_product;
    }

    public String getIsActive_product() {
        return isActive_product;
    }

    public void setIsActive_product(String isActive_product) {
        this.isActive_product = isActive_product;
    }

    public String getProductList_product() {
        return productList_product;
    }

    public void setProductList_product(String productList_product) {
        this.productList_product = productList_product;
    }

    public String getModifierDetails_product() {
        return modifierDetails_product;
    }

    public void setModifierDetails_product(String modifierDetails_product) {
        this.modifierDetails_product = modifierDetails_product;
    }

    public String getModifierTypeId_product() {
        return modifierTypeId_product;
    }

    public void setModifierTypeId_product(String modifierTypeId_product) {
        this.modifierTypeId_product = modifierTypeId_product;
    }

    public String getLstModifierType_product() {
        return lstModifierType_product;
    }

    public void setLstModifierType_product(String lstModifierType_product) {
        this.lstModifierType_product = lstModifierType_product;
    }

    public String getLstMenuModifierOptions_product() {
        return lstMenuModifierOptions_product;
    }

    public void setLstMenuModifierOptions_product(String lstMenuModifierOptions_product) {
        this.lstMenuModifierOptions_product = lstMenuModifierOptions_product;
    }
}
