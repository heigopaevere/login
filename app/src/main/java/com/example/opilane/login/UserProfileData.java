package com.example.opilane.login;

/**
 * Created by opilane on 28.03.2018.
 */

public class UserProfileData {
    public String eesNimi;
    public String perekonnaNimi;
    public String epost;

    public UserProfileData(String eesNimi, String perekonnaNimi, String epost) {
        this.eesNimi = eesNimi;
        this.perekonnaNimi = perekonnaNimi;
        this.epost = epost;

    }

    public UserProfileData(){

    }

    public String getEesNimi(){
        return eesNimi;
    }

    public void setEesNimi(String eesNimi){
        this.eesNimi= eesNimi;
    }
    public String getPerekonnaNimi(){
        return perekonnaNimi;
    }

    public void setPerekonnaNimi(String perekonnaNimi){
        this.perekonnaNimi = perekonnaNimi;

    }

    public String getEpost(){
        return epost;
    }
    public void setEpost(String epost){
        this.epost = epost;
    }
}
