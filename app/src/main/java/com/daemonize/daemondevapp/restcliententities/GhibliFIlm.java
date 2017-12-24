package com.daemonize.daemondevapp.restcliententities;

import java.util.Arrays;

public class GhibliFIlm {

    String id;
    String title;
    String description;
    String director;
    String producer;
    String release_date;
    String rt_score;
    String[] people;
    String[] species;
    String[] locations;
    String[] vehicles;
    String url;

    @Override
    public String toString() {
        return "\nGhibliFIlm {" +
                "\n  Id:\n    " + id +
                "\n  Title:\n    " + title +
                "\n  Description:\n    " + description +
                "\n  Director:\n    " + director +
                "\n  Producer:\n    " + producer +
                "\n  Release_date:\n    " + release_date +
                "\n  Rt_score:\n    " + rt_score +
                "\n  People:\n    " + Arrays.toString(people) +
                "\n  Species:\n    " + Arrays.toString(species) +
                "\n  Locations:\n    " + Arrays.toString(locations) +
                "\n  Vehicles:\n    " + Arrays.toString(vehicles) +
                "\n  Url:\n    " + url +
                "\n}\n";
    }
}
