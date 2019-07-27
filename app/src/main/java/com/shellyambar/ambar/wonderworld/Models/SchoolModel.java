package com.shellyambar.ambar.wonderworld.Models;

public class SchoolModel {

    private String countryName;
    private String schoolName;


    public SchoolModel(String countryName, String schoolName) {
        this.countryName = countryName;
        this.schoolName = schoolName;

    }

    public SchoolModel() {
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }


}
