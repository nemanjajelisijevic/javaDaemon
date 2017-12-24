package com.daemonize.daemondevapp.restcliententities;


public class PostTestEntity {

    public String name;
    public String job;

    @Override
    public String toString() {
        return "\nPostTestEntity{" +
                "\n name='" + name + '\'' +
                "\n job='" + job + '\'' +
                "\n}\n";
    }
}
