package com.example.android.booklists;


public class Book {

    private String mAuthor;
    private String mTitle;

    //Constructs a new Book object.
    public Book(String author, String title) {
        mAuthor = author;
        mTitle = title;
    }

    //returns the author
    public String getAuthor() {
        return mAuthor;
    }

    //returns the title
    public String getTitle() {
        return mTitle;
    }

}

