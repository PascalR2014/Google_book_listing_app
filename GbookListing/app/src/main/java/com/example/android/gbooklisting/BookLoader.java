package com.example.android.gbooklisting;

/**
 * Created by PB on 16/07/2017.
 */

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books.
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
