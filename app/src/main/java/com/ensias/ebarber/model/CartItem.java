package com.ensias.ebarber.model;

public class CartItem {
    String serviceName,SalonName,ServiceImageName,serviceProviderId;
    int price;

    public CartItem(String serviceName, String salonName, String serviceImageName, int price,String serviceProviderId) {
        this.serviceName = serviceName;
        SalonName = salonName;
        ServiceImageName = serviceImageName;
        this.price = price;
        this.serviceProviderId=serviceProviderId;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getSalonName() {
        return SalonName;
    }

    public String getServiceImageName() {
        return ServiceImageName;
    }

    public int getPrice() {
        return price;
    }
}
