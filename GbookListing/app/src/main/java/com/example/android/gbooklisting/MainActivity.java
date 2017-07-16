package com.example.android.gbooklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * URL for book data from the GOOGLE BOOK
     */
    private static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;
    private RecyclerView mRecyclerView;
    private BookRecyclerAdapter mAdapter;
    private EditText mSearchEditText;
    private ImageView mSearch;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link RecyclerView} in the layout
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        // First, hide loading indicator so error message will be visible
        mLoadingIndicator.setVisibility(GONE);
        mSearchEditText = (EditText) findViewById(R.id.edit_text_search);
        mSearch = (ImageView) findViewById(R.id.search);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create a new adapter that takes an empty list of book as input
                mAdapter = new BookRecyclerAdapter(MainActivity.this, new ArrayList<Book>(), new BookRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Book book) {
                        String url = book.getInfoLinkId();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

                // Set the adapter on the {@link RecyclerView}
                // so the list can be populated in the user interface
                mRecyclerView.setAdapter(mAdapter);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                final ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = getLoaderManager();

                    // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                    // because this activity implements the LoaderCallbacks interface).
                    loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                } else {
                    // Otherwise, display error
                    // First, hide loading indicator so error message will be visible
                    mLoadingIndicator.setVisibility(GONE);

                    mRecyclerView.setVisibility(GONE);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);

                    // Update empty state with no connection error message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(GONE);

        String searchInput = mSearchEditText.getText().toString();

        if (searchInput.length() == 0) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.search_nothing), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        searchInput = searchInput.replace(" ", "+");
        String query = GOOGLE_REQUEST_URL + searchInput;

        Uri baseUri = Uri.parse(query);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> book) {
        // Hide loading indicator because the data has been loaded
        mLoadingIndicator.setVisibility(GONE);
        mEmptyStateTextView.setVisibility(View.VISIBLE);

        // Set empty state text to display "No book found."
        mEmptyStateTextView.setText(R.string.no_found);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (book != null && !book.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(GONE);
            mAdapter.addAll(book);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
