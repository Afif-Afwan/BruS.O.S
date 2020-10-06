package com.example.fyp_02;

public class HealthComplicationsModel {
    String health, description;

    public HealthComplicationsModel(){

    }

    public HealthComplicationsModel(String health, String description){
        this.health = health;
        this.description = description;
    }

    public String getHealth(){
        return health;
    }

    public String getDescription(){
        return description;
    }
}
