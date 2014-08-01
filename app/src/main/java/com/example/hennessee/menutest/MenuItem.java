package com.example.hennessee.menutest;

/**
 * Created by Kyle on 01/08/2014.
 */
public class MenuItem {
    public static enum SCategory {SPECIAL, APP, MAIN, DESSERT};

    public String name;
    public String comment;
    public double price;
    public SCategory category;
}
