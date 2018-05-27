package com.example.android.projectfanta;

import java.io.Serializable;
import java.sql.Time;

public class Intake implements Serializable {
    private long creationTime;
    private double servings;
    private String food;

    /**
     * Creates an intake with appropriate nutrient values
     * @param food the food that was eaten
     */
    public Intake(String food) {
        this.creationTime = System.currentTimeMillis();
        this.food = food;
        this.servings = 1.0;
    }

    /**
     * Creates an intake with appropriate nutrient values
     * @param food the food that was eaten
     * @param servingSize how many servings of the food were eaten
     */
    public Intake(String food, double servingSize) {
        this.creationTime = System.currentTimeMillis();
        this.food = food;
        this.servings = servingSize;
    }

    /**
     * Creates an intake with appropriate nutrient values
     * @param food the food that was eaten
     * @param servingSize how many servings of the food were eaten
     * @param creationTime the time of creation
     */
    public Intake(String food, double servingSize, long creationTime) {
        this.creationTime = creationTime;
        this.food = food;
        this.servings = servingSize;
    }

    public Intake() { };

    /**
     * Gets the nutrient value for this intake (different than value for food)
     * @param key the nutrient string name
     * @return the value of that nutrient for this intake
     */
//    public Double getNutrient(String key) {
//        Double value = food.getNutrient(key);
//        if(value != null) return value * servings;
//        else return null;
//    }

    public long getCreationTime() { return creationTime;  }

    //public Time getTimeObject() { return new Time(creationTime); }

    public double getServings() { return servings; }

    public String getFood() { return food; }
}
