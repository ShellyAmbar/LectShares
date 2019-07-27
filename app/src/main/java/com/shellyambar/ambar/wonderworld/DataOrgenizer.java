package com.shellyambar.ambar.wonderworld;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DataOrgenizer {

    private ArrayList<String> countries;
    private ArrayList<String> schools;
    private ArrayList<String> classes;

    private HashMap<String, ArrayList<String>> countries_schools;
    private HashMap<String, ArrayList<String>> schools_classes;
    private HashMap<String, ArrayList<String>> lecture_record;
    private Context context=null;

    DataOrgenizer(Context context) {
        this.countries =new ArrayList<String>();
        this.schools=new ArrayList<String>();
        this.classes=new ArrayList<String>();
        this.countries_schools =new HashMap<String, ArrayList<String>>() ;
        this.schools_classes =new HashMap<String, ArrayList<String>>() ;
        this.lecture_record=new HashMap<String, ArrayList<String>>() ;
        this.context = context;

        countries.add("None");
        schools.add("None");
        classes.add("None");
    }

    public DataOrgenizer() {

    }

    public Context getContext() {
        return context;
    }



    void addNewCountry(String newCountryName){
        if(!countries.contains(newCountryName)){
            countries.add(newCountryName);
            countries_schools.put(newCountryName, new ArrayList<String>());
        }


    }
    void addNewSchoolOfCountry(String countryName, String schoolName){

        if(countries_schools.containsKey(countryName)){
            if(!Objects.requireNonNull(countries_schools.get(countryName)).contains(schoolName)){
                Objects.requireNonNull(countries_schools.get(countryName)).add(schoolName);
                schools_classes.put(schoolName, new ArrayList<String>());
            }


        }
    }
    void addNewSchoolClass(String countryName, String schoolName, String className){
        if(countries_schools.containsKey(countryName)){

            if(schools_classes.containsKey(schoolName)){

                if(! Objects.requireNonNull(schools_classes.get(schoolName)).contains(className)){
                    Objects.requireNonNull(schools_classes.get(schoolName)).add(className);

                }



            }
        }

    }
    public void addNewLecture(String countryName,String schoolName, String className,String lectureName ){
        if(countries_schools.containsKey(countryName)){

            if(schools_classes.containsKey(schoolName)){

                if(Objects.requireNonNull(schools_classes.get(schoolName)).contains(className)){


                    if(lecture_record.containsKey(lectureName)){
                        Toast.makeText(context, lectureName+" Is already exist", Toast.LENGTH_SHORT).show();
                    }else{

                        lecture_record.put(lectureName,new ArrayList<String>());
                    }

                }else{
                    Toast.makeText(context, "No school "+className+" exist", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(context, "No school "+schoolName +" exist", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(context, countryName +" does not exist!", Toast.LENGTH_SHORT).show();
        }

    }
    public void addNewRecord(String countryName,String schoolName, String className,String lectureName
            ,String recordName ){
        if(countries_schools.containsKey(countryName)){

            if(schools_classes.containsKey(schoolName)){

                if(schools_classes.get(schoolName).contains(className)){


                    if(lecture_record.containsKey(lectureName)){

                        if(lecture_record.get(lectureName).contains(recordName)){

                            Toast.makeText(context, recordName + " Is already exist", Toast.LENGTH_SHORT).show();
                        }else{
                            Objects.requireNonNull(lecture_record.get(lectureName)).add(recordName);
                        }

                    }else{

                        Toast.makeText(context, "No  "+lectureName+" exist", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(context, "No  "+className+" exist", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(context, "No school "+schoolName +" exist", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(context, countryName +" does not exist!", Toast.LENGTH_SHORT).show();
        }

    }


    ArrayList<String> getCountriesList(){

            return countries;

    }
    ArrayList<String> getSchoolsPerCountryList(String countryName){
        if(countries_schools.containsKey(countryName) ){
            if(!Objects.requireNonNull(countries_schools.get(countryName)).isEmpty()){
                return countries_schools.get(countryName);
            }else{

                return schools;
            }


        }else{

            return schools;
        }

    }
    ArrayList<String> getClassesPerSchoolList(String schoolName){

        if(schools_classes.containsKey(schoolName) ){

            if(!Objects.requireNonNull(schools_classes.get(schoolName)).isEmpty()){
                return schools_classes.get(schoolName);
            }else{

                return classes;
            }


        }
        else{

            return classes;
        }

    }
    public ArrayList<String> getRecordsListPerClassOfSchoolOfCountry(String lectureName){
       return lecture_record.get(lectureName);
    }

    public boolean isCountryExist(String countryName){
        if(countries.contains(countryName)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isSchoolOfCountryExist(String countryName, String schoolName){
       if(countries.contains(countryName)){
           if(countries_schools.get(countryName).contains(schoolName)){
               return true;
           }else{
               return false;
           }
       }else{
           return false;
       }
    }

    public boolean isClassOfSchoolOfCountryExist(String countryName,String schoolName,String className){

       if(isCountryExist(countryName)){

          if(schools_classes.containsKey(schoolName)){
              if(schools_classes.get(schoolName).contains(className)){
                  return true;
              }else{
                  return false;
              }

          }else{
              return false;
          }
       }else{
           return false;
       }
    }



}
