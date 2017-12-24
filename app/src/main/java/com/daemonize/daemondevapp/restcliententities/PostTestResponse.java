package com.daemonize.daemondevapp.restcliententities;

public class PostTestResponse {

    public String name;
    public String job;
    public String id;
    public String createdAt;

    @Override
    public String toString() {
        return "\nPostTestResponse{" +
                "\n name='" + name + '\'' +
                "\n job='" + job + '\'' +
                "\n id='" + id + '\'' +
                "\n createdAt='" + createdAt + '\'' +
                "\n}\n";
    }
}
