package Adapters;

import java.util.ArrayList;

public class Supplier {
    private String name, email, phone, id;
    private ArrayList<Dress> list_dress;

    public Supplier(){}

    public Supplier(String name, String email,String phone,String id){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;

    }

    public Supplier(String name,String email, String phone,String id,ArrayList<Dress> list_dress){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.list_dress=list_dress;
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
    public ArrayList<Dress> getDressList() {
        return list_dress;
    }
    public void setDressList(ArrayList<Dress> list_dress) {
        this.list_dress=list_dress;
    }

}
