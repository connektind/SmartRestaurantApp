package com.example.smartrestaurantapp.model;

import java.io.Serializable;

public class AvailableOrderModel implements Serializable {
    String orderId;
    String restaurantId;
    String subTotal;
    String tax;
    String deliveryFee;
    String total;
    String orderDate;
    String deliveryTypeId;
    String orderStatusId;
    String billingDetailsId;
    String additionalInformation;
    String coupounId;
    String customerId;
    String isCreateAccount;
    String isDiffShippingAddr;
    String shippingDetailsId;
    String processingFee;
    String lstOrderDetails;
    String billingDetails;
    String shippingDetails;
    String customer;
    String tipAmount;
    String deliveryType;
    String paymentType;


    public AvailableOrderModel(String orderId, String restaurantId, String subTotal, String tax, String deliveryFee, String total, String orderDate, String deliveryTypeId, String orderStatusId, String billingDetailsId, String additionalInformation, String coupounId, String customerId, String isCreateAccount, String isDiffShippingAddr, String shippingDetailsId, String processingFee, String lstOrderDetails, String billingDetails, String shippingDetails, String customer, String tipAmount,String deliveryType,String paymentType) {
        this. orderId=orderId;
        this. restaurantId=restaurantId;
        this. subTotal=subTotal;
        this. tax=tax;
        this. deliveryFee=deliveryFee;
        this. total=total;
        this. orderDate=orderDate;
        this. deliveryTypeId=deliveryTypeId;
        this. orderStatusId=orderStatusId;
        this. billingDetailsId=billingDetailsId;
        this. additionalInformation=additionalInformation;
        this. coupounId=coupounId;
        this. customerId=customerId;
        this. isCreateAccount=isCreateAccount;
        this. isDiffShippingAddr=isDiffShippingAddr;
        this. shippingDetailsId=shippingDetailsId;
        this. processingFee=processingFee;
        this. lstOrderDetails=lstOrderDetails;
        this. billingDetails=billingDetails;
        this. shippingDetails=shippingDetails;
        this. customer=customer;
        this. tipAmount=tipAmount;
        this.deliveryType=deliveryType;
        this.paymentType=paymentType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(String deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public String getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(String orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getBillingDetailsId() {
        return billingDetailsId;
    }

    public void setBillingDetailsId(String billingDetailsId) {
        this.billingDetailsId = billingDetailsId;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getCoupounId() {
        return coupounId;
    }

    public void setCoupounId(String coupounId) {
        this.coupounId = coupounId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getIsCreateAccount() {
        return isCreateAccount;
    }

    public void setIsCreateAccount(String isCreateAccount) {
        this.isCreateAccount = isCreateAccount;
    }

    public String getIsDiffShippingAddr() {
        return isDiffShippingAddr;
    }

    public void setIsDiffShippingAddr(String isDiffShippingAddr) {
        this.isDiffShippingAddr = isDiffShippingAddr;
    }

    public String getShippingDetailsId() {
        return shippingDetailsId;
    }

    public void setShippingDetailsId(String shippingDetailsId) {
        this.shippingDetailsId = shippingDetailsId;
    }

    public String getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(String processingFee) {
        this.processingFee = processingFee;
    }

    public String getLstOrderDetails() {
        return lstOrderDetails;
    }

    public void setLstOrderDetails(String lstOrderDetails) {
        this.lstOrderDetails = lstOrderDetails;
    }

    public String getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(String billingDetails) {
        this.billingDetails = billingDetails;
    }

    public String getShippingDetails() {
        return shippingDetails;
    }

    public void setShippingDetails(String shippingDetails) {
        this.shippingDetails = shippingDetails;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }
}
