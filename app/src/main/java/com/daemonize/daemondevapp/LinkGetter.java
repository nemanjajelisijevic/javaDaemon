package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Daemonize(eager = true)
public class LinkGetter<K> {

    private OkHttpClient client = new OkHttpClient();
    private static final String GOOGLE_SEARCH = "https://google.com/search?q=";

    @SideQuest(SLEEP = 10000)
    public List<String> getDeviantArt() throws InterruptedException {
        return getLinks("deviant art");
    }

    public List<String> getLinks(String search)  {

        Request request = new Request.Builder()
                .url(GOOGLE_SEARCH + search)
                .build();

        Response response;
        String responseString;
        try {
            response = client.newCall(request).execute();
             responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Log.d(Thread.currentThread().getName() + ", Thread Id: " + Long.toString(Thread.currentThread().getId()), responseString);

        return parseLinks(responseString);

    }

    private static List<String> parseLinks(final String html) {
        List<String> result = new ArrayList<String>();
        String pattern1 = "<h3 class=\"r\"><a href=\"/url?q=";
        String pattern2 = "\">";
        Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        Matcher m = p.matcher(html);

        while (m.find()) {
            String domainName = m.group(0).trim();

            /** remove the unwanted text */
            domainName = domainName.substring(domainName.indexOf("/url?q=") + 7);
            domainName = domainName.substring(0, domainName.indexOf("&amp;"));

            result.add(domainName);
        }
        return result;
    }

}
