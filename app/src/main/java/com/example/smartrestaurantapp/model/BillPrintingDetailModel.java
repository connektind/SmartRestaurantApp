package com.example.smartrestaurantapp.model;

public  class BillPrintingDetailModel {
    String orderDetailsId;
    String orderId_product;
    String productId;
    String productQuantity;
    String modifierId;
    String modifierQuantity;
    String price;
    String isModifierType;
    String modifierTypeId;
    String lstModiferOptions;
    String productName;
    String additionalInformation_product;
    String lstModifier;
    public BillPrintingDetailModel(String orderDetailsId, String orderId_product, String productId, String productQuantity, String modifierId, String modifierQuantity, String price, String isModifierType, String modifierTypeId, String lstModiferOptions, String productName, String additionalInformation_product, String lstModifier) {
        this. orderDetailsId=orderDetailsId;
        this. orderId_product=orderId_product;
        this. productId=productId;
        this. productQuantity=productQuantity;
        this. modifierId=modifierId;
        this. modifierQuantity=modifierQuantity;
        this. price=price;
        this. isModifierType=isModifierType;
        this. modifierTypeId=modifierTypeId;
        this. lstModiferOptions=lstModiferOptions;
        this. productName=productName;
        this. additionalInformation_product=additionalInformation_product;
        this. lstModifier=lstModifier;
    }

    public String getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(String orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public String getOrderId_product() {
        return orderId_product;
    }

    public void setOrderId_product(String orderId_product) {
        this.orderId_product = orderId_product;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifierQuantity() {
        return modifierQuantity;
    }

    public void setModifierQuantity(String modifierQuantity) {
        this.modifierQuantity = modifierQuantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsModifierType() {
        return isModifierType;
    }

    public void setIsModifierType(String isModifierType) {
        this.isModifierType = isModifierType;
    }

    public String getModifierTypeId() {
        return modifierTypeId;
    }

    public void setModifierTypeId(String modifierTypeId) {
        this.modifierTypeId = modifierTypeId;
    }

    public String getLstModiferOptions() {
        return lstModiferOptions;
    }

    public void setLstModiferOptions(String lstModiferOptions) {
        this.lstModiferOptions = lstModiferOptions;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAdditionalInformation_product() {
        return additionalInformation_product;
    }

    public void setAdditionalInformation_product(String additionalInformation_product) {
        this.additionalInformation_product = additionalInformation_product;
    }

    public String getLstModifier() {
        return lstModifier;
    }

    public void setLstModifier(String lstModifier) {
        this.lstModifier = lstModifier;
    }
}
