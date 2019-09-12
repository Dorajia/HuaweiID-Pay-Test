package com.example.dora.huawei;

/**
 * Created by Dora on 3/10/17.
 */

public class BasicInfo {
    private String name;
    private String gender;
    private int age;
    private String email_address;
    private String phone_number;

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }
    public int getage() {
        return age;
    }
    public String getEmailAddress() {
        return email_address;
    }
    public String getPhoneNumber() {
        return phone_number;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setEmailAddress(String email_address) {
        this.email_address = email_address;
    }
    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }
}