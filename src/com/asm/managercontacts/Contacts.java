package com.asm.managercontacts;

import java.io.Serializable;
import java.util.List;
public class Contacts implements Serializable {
    //Seri..  lưu trữ và chuyển đổi trạng thái của 1 đối tượng (Object) vào 1 byte stream sao cho byte stream này
    // có thể chuyển đổi ngược trở lại thành một Object.
    private int phone;
    private String name;
    private String address;
    private String gender;
    public Contacts(int phone, String name, String address, String gender) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.gender = gender;
    }
    public Contacts() {

    }

    public int getPhone() {
        return phone;
    } public void setPhone(int phone) {
        this.phone = phone;
    }
    public String getName() {
        return name;
    } public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    } public void setAddress(String address) {
        this.address = address;
    }
    public String getGender() {
        return gender;
    } public void setGender(String gender) {
        this.gender = gender;
    }
    @Override
    public String toString(){
        return phone+", "+name+", "+address+", "+gender;
    }
}
