package com.ensias.ebarber.model;

public class Services {
    String serviceName;
    int serviceCharges;
    String imageName;

    public String getImageName() {
        return imageName;
    }

    public Services(String serviceName, int serviceCharges,String imageName) {

        this.serviceName = serviceName;
        this.serviceCharges = serviceCharges;
        this.imageName=imageName;
    }

    public Services() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(int serviceCharges) {
        this.serviceCharges = serviceCharges;
    }


}
