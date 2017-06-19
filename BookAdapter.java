package com.example.android.booklists;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    //Construct a new Book adapter with param "context" and "books" which is the data source of the adapter
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }


    //Return a list item view that displays information about the earthquake at the given position in the list of books
    @Override//override to control how views item list get created
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        Book currentBook = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_listdesign, parent, false);
        }

        // Find the TextView with view ID author
        TextView authorView = (TextView) convertView.findViewById(R.id.Author);
        // Display the author of the current book in that TextView
        authorView.setText(currentBook.getAuthor());

        // Find the TextView with view ID title
        TextView titleView = (TextView) convertView.findViewById(R.id.BookTitle);
        // Display the title of the current book in that TextView
        titleView.setText(currentBook.getTitle());

        return convertView;
    }
}
