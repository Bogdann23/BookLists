package com.example.android.booklists;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    private static final String LOG_TAG = BookActivity.class.getName();

    TextView noResultFound;
    TextView noInternetConnection;
    String endLink;
    String edit_text_data;
    static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    //SOURCE of method: https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean internetConnectionAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    /**
     * Adapter for the list of books
     */
    private BookAdapter mBookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //shows a message if no result is found
        noResultFound = (TextView) findViewById(R.id.no_data_available);

        //show message if no internet no Internet Connection
        noInternetConnection = (TextView) findViewById(R.id.no_internet_available);

        //find a reference to the Listview in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        //Create a new adapter that takes the list of books as input
        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());

        //Set the adapter on the ListView so the list can be populated in the user interface
        bookListView.setAdapter(mBookAdapter);


        //SOURCE for search query implementation:
        //https://stackoverflow.com/questions/20655294/how-to-add-android-add-edit-text-to-a-url-android-activity

        //find edit text search input.
        final EditText your_edit_text = (EditText) findViewById(R.id.add_name);
        Button searchButton = (Button) findViewById(R.id.queryButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (internetConnectionAvailable()) {
                    edit_text_data = your_edit_text.getText().toString();
                    endLink = GOOGLE_REQUEST_URL + edit_text_data.replaceAll("\\s+", "+");
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(endLink);
                    noInternetConnection.setVisibility(View.GONE);
                } else {
                    noInternetConnection.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     * <p>
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return a Book. We won't do
     * progress updates, so the second generic is just Void.
     * <p>
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Book}s as the result.
         */
        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Book> result = QueryUtils.fetchBookData(urls[0]);
            return result;

        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to Google. Then we update the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Book> data) {

            // Clear the adapter of previous book data
            mBookAdapter.clear();

            // If there is a valid list of {@link Books}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mBookAdapter.addAll(data);
                noResultFound.setVisibility(View.GONE);
            } else {
                noResultFound.setVisibility(View.VISIBLE);
            }

        }


    }
}