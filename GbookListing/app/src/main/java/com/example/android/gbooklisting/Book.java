package com.example.android.gbooklisting;

import java.util.ArrayList;

/**
 * Created by PB on 16/07/2017.
 */

public class Book {

    private String mThumbnail;

    private String mTitle;

    private ArrayList<String> mAuthor;

    private String mPublisher;

    private String mInfoLink;

    public Book(String thumbnail, String title, ArrayList<String> author, String publisher, String infoLink){
        mThumbnail = thumbnail;
        mTitle = title;
        mAuthor = author;
        mPublisher = publisher;
        mInfoLink = infoLink;
    }

    public String getThumbnailId(){return  mThumbnail;}

    public String getTitleId(){
        return mTitle;
    }


    public String getAuthorId(){
        String authors = checkAuthors();
        return authors;
    }

    public String getPublisherId() {
        return mPublisher;
    }

    public String checkAuthors() {
        String authors = mAuthor.get(0);
        if (mAuthor.size()>1) {
            for (int i=1; i < mAuthor.size(); i++) {
                authors += "\n" + mAuthor.get(i);
            }
        }
        return authors;
    }

    public String getInfoLinkId() {
        return mInfoLink;
    }
}


