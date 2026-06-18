package com.buildtogether.dto;

public class Investor {

    private Long id;
    private Long userId;
    private String companyName;
    private Double availableFunds;

    public Investor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getAvailableFunds() {
        return availableFunds;
    }

    public void setAvailableFunds(Double availableFunds) {
        this.availableFunds = availableFunds;
    }
}
