package com.daemonize.daemondevapp.restcliententities;


import java.util.List;

public class DelayedGetResponse {

    public int page;
    public int per_page;
    public int total;
    public int total_pages;
    public List<Data> data;

    @Override
    public String toString() {
        return "\nDelayedGetResponse {" +
                "\n    page=" + page +
                "\n    per_page=" + per_page +
                "\n    total=" + total +
                "\n    total_pages=" + total_pages +
                "\n    data=" + data.toString() +
                "\n}\n";
    }
}
