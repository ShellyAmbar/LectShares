package com.shellyambar.ambar.wonderworld.Models;

public class HeaderModel {


    private String lectureName;
    private String countryName;
    private String schoolName;
    private String className;
    private String lectureId;
    private String publisherName;



    public HeaderModel( String lectureName, String lectureId,String countryName
            ,String schoolName,String className,String publisherName) {

        this.lectureName = lectureName;
        this.lectureId = lectureId;
        this.className=className;
        this.countryName=countryName;
        this.schoolName=schoolName;
        this.publisherName=publisherName;
    }

    public HeaderModel() {
    }


    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
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

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }
}
