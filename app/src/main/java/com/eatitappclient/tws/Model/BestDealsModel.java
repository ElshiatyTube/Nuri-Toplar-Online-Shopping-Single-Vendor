package com.eatitappclient.tws.Model;

public class BestDealsModel {
    private String key;
    private String name,image,id,description;
    private Double price;
    private boolean availability;



    //For Search
    private int positionInList=-1;


    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public BestDealsModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }





    public int getPositionInList() {
        return positionInList;
    }

    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }
}
