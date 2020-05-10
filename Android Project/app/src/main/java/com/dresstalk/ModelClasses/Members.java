package com.dresstalk.ModelClasses;

public class Members {
    private String fname,lname,phone,password,image,address;

    public Members(){

    }

    public Members(String fname, String lname, String phone, String password, String image, String address) {
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
    }

    public String getfname() {
        return fname;
    }

    public void setfname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
