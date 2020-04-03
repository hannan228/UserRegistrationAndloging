package com.example.OnlineRescueSystem.Model;

public class Registration {

    private String image;
    private String name;
    private String CNIC;
    private String phoneNumber;
    private String address;
    private String timeStamp;

    public Registration(String image, String name, String CNIC, String phoneNumber, String address, String timeStamp) {
        this.image = image;
        this.name = name;
        this.CNIC = CNIC;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Registration() {
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
