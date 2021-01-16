package com.example.smartrestaurantapp.model;

public class OrderProductSubModifierModel {
    String modifierOptionId_sub;
    String modifierOptionName_sub;
    String modifierId_sub;
    String price_sub;
    String isActive_sub;
    public OrderProductSubModifierModel(String modifierOptionId_sub, String modifierOptionName_sub, String modifierId_sub, String price_sub, String isActive_sub) {
        this. modifierOptionId_sub=modifierOptionId_sub;
        this. modifierOptionName_sub=modifierOptionName_sub;
        this. modifierId_sub=modifierId_sub;
        this. price_sub=price_sub;
        this. isActive_sub=isActive_sub;
    }

    public String getModifierOptionId_sub() {
        return modifierOptionId_sub;
    }

    public void setModifierOptionId_sub(String modifierOptionId_sub) {
        this.modifierOptionId_sub = modifierOptionId_sub;
    }

    public String getModifierOptionName_sub() {
        return modifierOptionName_sub;
    }

    public void setModifierOptionName_sub(String modifierOptionName_sub) {
        this.modifierOptionName_sub = modifierOptionName_sub;
    }

    public String getModifierId_sub() {
        return modifierId_sub;
    }

    public void setModifierId_sub(String modifierId_sub) {
        this.modifierId_sub = modifierId_sub;
    }

    public String getPrice_sub() {
        return price_sub;
    }

    public void setPrice_sub(String price_sub) {
        this.price_sub = price_sub;
    }

    public String getIsActive_sub() {
        return isActive_sub;
    }

    public void setIsActive_sub(String isActive_sub) {
        this.isActive_sub = isActive_sub;
    }
}
