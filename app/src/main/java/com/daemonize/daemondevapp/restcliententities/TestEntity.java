package com.daemonize.daemondevapp.restcliententities;


public class TestEntity {

    public String name;
    public String age;
    public String description;

    @Override
    public String toString() {
        return "\nTestEntity{" +
                "\nname='" + name + '\'' +
                "\nage='" + age + '\'' +
                "\ndescription='" + description + '\'' +
                "\n}\n";
    }
}
