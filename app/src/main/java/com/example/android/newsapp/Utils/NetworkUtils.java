package com.example.android.newsapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.example.android.newsapp.Models.Contributor;
import com.example.android.newsapp.Models.Story;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NetworkUtils {
    private static final String GET_REQUEST = "GET";

    /**
     * Converts a String to URL
     *
     * @param stringUrl string form of the url
     */
    private static URL toURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String fetchArticlesFromApi(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestMethod(GET_REQUEST);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = convertStreamToString(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // clean up
            if (connection != null)
                connection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * Instead of using a library like Picasso this method was used.
     * To fetch thumbnails from urls and convert them to Bitmap
     *
     * @param thumbnailUrl a String url for the thumbnail image
     * @return Bitmap of the thumbnail image
     */
    private static Bitmap fetchArticleThumbnail(String thumbnailUrl) throws IOException {
        if (TextUtils.isEmpty(thumbnailUrl))
            return null;
        URL url = toURL(thumbnailUrl);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap thumbnailBitmap = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET_REQUEST);
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(2000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                thumbnailBitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }

        return thumbnailBitmap;
    }

    /**
     * General helper method to convert
     * InputStream into a String
     */
    private static String convertStreamToString(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            try {
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }

    private static List<Story> extractStories(String jsonResponse) throws IOException {
        if (TextUtils.isEmpty(jsonResponse))
            return null;

        List<Story> stories = new ArrayList<>();
        Story newStory;

        try {
            JSONObject jsonStories = new JSONObject(jsonResponse);
            JSONObject response = jsonStories.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject story = results.getJSONObject(i);
                String title = story.getString("webTitle");
                String sectionName = story.getString("sectionName");
                String webUrl = story.getString("webUrl");
                // check if date exists, if not return empty string
                String dateString = story.optString("webPublicationDate");
                Date date = toDate(dateString);
                // check if thumbnails exist, if not pass null
                JSONObject fields = story.optJSONObject("fields");
                String thumbnailUrl = null;
                if (fields != null)
                    thumbnailUrl = fields.optString("thumbnail");
                Bitmap thumbnail = fetchArticleThumbnail(thumbnailUrl);
                // check if contributors exist if not pass null
                JSONArray tags = story.optJSONArray("tags");
                List<Contributor> contributors = extractContributorsFromTags(tags);

                newStory = new Story(title, sectionName, webUrl, date, thumbnail, contributors);
                stories.add(newStory);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stories;
    }

    private static Date toDate(String stringDate) {
        if (TextUtils.isEmpty(stringDate))
            return null;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Tags field may have more than one contributor that's why
     * we have to loop through it
     *
     * @param tags the json response field containing contributors
     *             if tags is null it means the field wasn't requested.
     */
    private static List<Contributor> extractContributorsFromTags(JSONArray tags) throws JSONException {
        if (tags == null || tags.length() == 0)
            return null;

        List<Contributor> contributors = new ArrayList<>();
        for (int i = 0; i < tags.length(); i++) {
            JSONObject contributorJSON = tags.getJSONObject(i);
            String firstName = contributorJSON.optString("firstName");
            String lastName = contributorJSON.optString("lastName");
            Contributor contributor = new Contributor(firstName, lastName);

            contributors.add(contributor);
        }

        return contributors;
    }

    public static List<Story> fetchArticlesFromAPI(String stringUrl) {
        URL url = toURL(stringUrl);
        List<Story> stories = null;
        try {
            String jsonResponse = fetchArticlesFromApi(url);
            stories = extractStories(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stories;
    }
}
