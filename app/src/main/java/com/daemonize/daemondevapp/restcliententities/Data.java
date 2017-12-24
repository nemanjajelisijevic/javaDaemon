package com.daemonize.daemondevapp.restcliententities;


public class Data {

    public int id;
    public String first_name;
    public String last_name;
    public String avatar;

    @Override
    public String toString() {
        return "\n    Data {" +
                "\n         id = " + id +
                "\n         first_name = '" + first_name + '\'' +
                "\n         last_name = '" + last_name + '\'' +
                "\n         avatar = '" + avatar + '\'' +
                "\n    }\n";
    }
}
