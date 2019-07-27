package com.shellyambar.ambar.wonderworld.Models;

public class ClassModel {
    private String countryName;
    private String schoolName;
    private String className;


    public ClassModel(String countryName, String schoolName, String className, String classId) {
        this.countryName = countryName;
        this.schoolName = schoolName;
        this.className = className;

    }

    public ClassModel() {
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


}
