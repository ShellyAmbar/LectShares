package com.shellyambar.ambar.wonderworld.Models;

public class AudioModel {

    private String lectureName;
    private String recordName;
    private String recordURI;
    private String recordId;
    private String dateAudioUploaded;
    private String publisherName;
    private String countryName;
    private String schoolName;
    private String className;
    private String lectureId;
    private String is_playing;


    public AudioModel(String lectureName, String recordName, String recordURI
            , String recordId, String dateAudioUploaded, String publisherName,
                      String lectureId,String countryName
            ,String schoolName,String className, String is_playing ) {
        this.lectureName = lectureName;
        this.recordName = recordName;
        this.recordURI = recordURI;
        this.recordId = recordId;
        this.dateAudioUploaded = dateAudioUploaded;
        this.publisherName = publisherName;
        this.lectureId = lectureId;
        this.className=className;
        this.countryName=countryName;
        this.schoolName=schoolName;
        this.is_playing=is_playing;
    }



    public AudioModel() {
    }

    public String getIs_playing() {
        return is_playing;
    }

    public void setIs_playing(String is_playing) {
        this.is_playing = is_playing;
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

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordURI() {
        return recordURI;
    }

    public void setRecordURI(String recordURI) {
        this.recordURI = recordURI;
    }



    public String getDateAudioUploaded() {
        return dateAudioUploaded;
    }

    public void setDateAudioUploaded(String dateAudioUploaded) {
        this.dateAudioUploaded = dateAudioUploaded;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
}
