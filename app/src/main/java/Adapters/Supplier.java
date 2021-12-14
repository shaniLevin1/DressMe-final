package Adapters;

import android.net.Uri;

public class Supplier {
    private String name, email, phone, id;

    public Supplier(){}

    public Supplier(String name, String email,String id){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;

    }
    public Supplier(String name,String email, String phone,String id){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() { return phone;}
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getId() {
        return id;
    }

}
