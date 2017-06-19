package com.example.android.booklists;

// Helper methods related to requesting and receiving book data from Google.


import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a QueryUtils object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    //Query the Google dataset and return a list of Books objects.
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Book> books = extractItemsFromJson(jsonResponse);

        // Return the list of {@link Books}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            //set HTTP request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();
            //Receive the Response and make sense of it for the app
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Return a list of Book objects that has been built up from parsing a JSON response.
    private static List<Book> extractItemsFromJson(String bookJSON) {

        // Create an empty ArrayList that we can start adding books to and return it at the end of the method
        List<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject JsonObject = new JSONObject(bookJSON);
            //to get what we are looking for we need to access the JSON items array and object in the JSON code
            JSONArray booksArray = JsonObject.getJSONArray("items");
            //if we have results in the list
            if (booksArray.length() > 0) {

                // For each book in the bookArray, create an book object

                //now we need to loop through each book in the book array
                for (int i = 0; i < booksArray.length(); i++) {
                    //loop through each item(books) in the JSON books array
                    JSONObject currentBook = booksArray.getJSONObject(i);
                    //to retrieve author and title + thumbnail
                    JSONObject bookDetails = currentBook.getJSONObject("volumeInfo");

                    String title = bookDetails.getString("title");

                    JSONArray authorsArray = bookDetails.getJSONArray("authors"); //optJSONArray VS getJSONArray

                    //define variable to hold the authors from the string array looping
                    String author = null;
                    for (int j = 0; j < authorsArray.length(); j++) {
                        if (j == 0) {
                            author = authorsArray.getString(j);
                        } else {
                            author = author + ", " + authorsArray.getString(j);
                        }
                    }

                    // Extract out the title, author, details, and urls.
//!!!!!!!!!!!!!!CREATE a new Book object (constructor defined in Book.java class)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    Book book = new Book(author, title);
                    //we add the new book in the list of books
                    books.add(book);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of books
        return books;
    }
}

