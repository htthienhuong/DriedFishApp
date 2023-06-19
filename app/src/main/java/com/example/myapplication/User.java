package com.example.myapplication;

import com.google.firebase.database.Exclude;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    public String fullName, day,month, year, email;
    private ArrayList<String> favouriteFishes;
    private ArrayList<String> viewedFishes;

    public User() {
        this.viewedFishes = new ArrayList<>();
        this.favouriteFishes = new ArrayList<>();
    }

    public User(String fullname, String day,String month,String year, String email){
        this();
        this.fullName = fullname;
        this.day = day;
        this.month = month;
        this.year = year;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Exclude
    public String getBirthday() {
        return String.format("%s/%s/%s", day, month, year);
    }

    @Exclude
    public void setBirthday(String birthday) {
        String[] parts = birthday
                .replaceAll("\\s+", "")
                .split("/");

        if (parts.length == 3) {
            this.day = parts[0];
            this.month = parts[1];
            this.year = parts[2];
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getFavouriteFishes() {
        return favouriteFishes;
    }

    @Exclude
    public ArrayList<Fish_Item> getFavouriteFishItem() {
        ArrayList<Fish_Item> favFish = new ArrayList<>();
        for (Fish_Item fish : Fish_Item.allFishes) {
            if (favouriteFishes.contains(fish.getClassLabel()))
                favFish.add(fish);
        }
        return favFish;
    }

    public void setFavouriteFishes(ArrayList<String> favouriteFishes) {
        this.favouriteFishes = favouriteFishes;
    }

    public void addFavouriteFish(String fish) {
        if (!this.favouriteFishes.contains(fish)) {
            this.favouriteFishes.add(fish);
        }
    }

    public void removeFavouriteFish(String fish) {
        if (favouriteFishes != null)
            favouriteFishes.remove(fish);
    }

    public ArrayList<String> getViewedFishes() {
        return viewedFishes;
    }

    public void setViewedFishes(ArrayList<String> viewedFishes) {
        this.viewedFishes = viewedFishes;
    }

    @Exclude
    public ArrayList<Fish_Item> getViewedFishItems() {
        ArrayList<Fish_Item> viewedFish = new ArrayList<>();
        for (Fish_Item fish : Fish_Item.allFishes) {
            if (viewedFishes.contains(fish.getClassLabel()))
                viewedFish.add(fish);
        }
        return viewedFish;
    }

    public void addViewedFish(String fishLabel) {
        if (!viewedFishes.contains(fishLabel))
            viewedFishes.add(fishLabel);
    }

    public void removeViewedFish(String fishLabel) {
        if (viewedFishes != null)
            viewedFishes.remove(fishLabel);
    }

    public void clearAllViewedFishes() {
        this.viewedFishes.clear();
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", email='" + email + '\'' +
                ", favouriteFishes=" + favouriteFishes +
                '}';
    }
}