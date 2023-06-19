package com.example.myapplication;

import android.os.Parcelable;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String nameRecipe;
    private String ingredients;
    private String instructions;

    public Recipe(String nameRecipe, String ingredients, String instructions) {
        this.nameRecipe = nameRecipe;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Recipe(String nameRecipe){
        this.nameRecipe = nameRecipe;
    }
    public String getNameRecipe() {
        return nameRecipe;
    }

    public void setNameRecipe(String nameRecipe) {
        this.nameRecipe = nameRecipe;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Recipe(){
    }
}
