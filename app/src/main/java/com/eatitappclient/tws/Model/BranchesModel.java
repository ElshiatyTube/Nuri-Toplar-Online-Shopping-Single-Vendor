package com.eatitappclient.tws.Model;

public class BranchesModel {
    private String branchId;
    private String branchName,branchImage,MapLinkLocation,branchAddress,branchPhone;
   private Double branchDeliveryPrice;

    public BranchesModel() {
    }

    public String getBranchPhone() {
        return branchPhone;
    }

    public void setBranchPhone(String branchPhone) {
        this.branchPhone = branchPhone;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchImage() {
        return branchImage;
    }

    public void setBranchImage(String branchImage) {
        this.branchImage = branchImage;
    }

    public String getMapLinkLocation() {
        return MapLinkLocation;
    }

    public void setMapLinkLocation(String mapLinkLocation) {
        MapLinkLocation = mapLinkLocation;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public Double getBranchDeliveryPrice() {
        return branchDeliveryPrice;
    }

    public void setBranchDeliveryPrice(Double branchDeliveryPrice) {
        this.branchDeliveryPrice = branchDeliveryPrice;
    }
}
