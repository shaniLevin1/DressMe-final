package Adapters;

import android.net.Uri;

public class Dress {
    private String name, description, burrowTime, available, category, color, size, location;

    public Dress(){}

    public Dress(String name, String description, String burrowTime,String available, String category,
                 String color, String size, String location){
        this.name = name;
        this.description = description;
        this.burrowTime = burrowTime;
        this.available = available;
        this.category = category;
        this.color = color;
        this.size = size;
        this.location = location;
    }
    //set & get
    public String getName () { return this.name;}
    public void setName (String name) { this.name = name;}
    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}
    public String getBurrowTime() {return this.burrowTime;}
    public void setBurrowTime(String burrowTime) {this.burrowTime = burrowTime;}
    public String getAvailable() {return this.available;}
    public void setAvailable(String available) {this.available = available;}
    public String getCategory() {return this.category;}
    public void setCategory(String category) {this.category = category;}
    public String getColor1() {return this.color;}
    public void setColor1(String color) {this.color = color;}
    public String getSize() {return this.size;}
    public void setSize(String size) {this.category = size;}
    public String getLocation() {return this.location;}
    public void setLocation(String location) {this.location = location;}

}

