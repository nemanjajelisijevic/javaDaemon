package com.daemonize.daemondevapp.restcliententities;


public class PutTestReponse {

    public String name;
    public String job;
    public String updatedAt;

    @Override
    public String toString() {
        return "\nPostTestResponse{" +
                "\n name ='" + name + '\'' +
                "\n job ='" + job + '\'' +
                "\n updatedAt ='" + updatedAt + '\'' +
                "\n}\n";
    }

}
