package com.something.dennisthicklin.ntouchnewsradio;

/**
 * Created by dennisthicklin on 1/26/18.
 */

public class ModelClass {
    String image;


    public ModelClass(String image){
        this.image = image;

    }

    public ModelClass(){

    }

    public String getImage(){
        return image;
    }


    public void setImage(String image){
        this.image = image;
    }
}
