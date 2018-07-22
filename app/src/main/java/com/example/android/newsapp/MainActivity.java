package com.example.android.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.newsapp.Adapters.StoryAdapter;
import com.example.android.newsapp.Loaders.StoryAsyncLoader;
import com.example.android.newsapp.Models.Story;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {
    // Used gradle properties because that's a more secure way to protect
    // API keys if uploaded to github ( ofc gradle.properties should be added to .gitignore )
    private static final String GUARDIAN_API_URL
            = "http://content.guardianapis.com/search";

    private static final int STORIES_LOADER_ID = 1;

    private StoryAdapter adapter;

    private ProgressBar mProgressBar;

    private TextView mErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progress_bar);
        mErrorTv = findViewById(R.id.error_msg);

        List<Story> stories = new ArrayList<>();
        adapter = new StoryAdapter(stories, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        StoryAdapter.StoryItemsDivider itemDecor = new StoryAdapter.StoryItemsDivider(this);
        recyclerView.addItemDecoration(itemDecor);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = cm.getActiveNetworkInfo() != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected)
            getSupportLoaderManager().initLoader(STORIES_LOADER_ID, null, this);
        else {
            mProgressBar.setVisibility(View.GONE);
            mErrorTv.setText(R.string.no_internet);
            mErrorTv.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader<List<Story>> onCreateLoader(int id, @Nullable Bundle args) {
        String topic = getPreferenceStringValue(R.string.pref_topic_key, R.string.pref_topic_default);
        String orderBy = getPreferenceStringValue(R.string.pref_order_by_key, R.string.pref_order_by_default);
        boolean hasThumbnails = getPreferenceBooleanValue(R.string.pref_thumbnail_key, R.bool.pref_thumbnail_default);
        boolean hasContributors = getPreferenceBooleanValue(R.string.pref_contributors_key, R.bool.pref_contributors_default);

        Uri baseUri = Uri.parse(GUARDIAN_API_URL);
        Uri.Builder builder = baseUri.buildUpon();
        // set up the API key
        builder.appendQueryParameter(getString(R.string.api_key_key), BuildConfig.GuardianApiKey);
        // set up the topic
        builder.appendQueryParameter(getString(R.string.topic_query_param), topic);
        // news order
        builder.appendQueryParameter(getString(R.string.order_by_query_param), orderBy);
        // Display thumbnails ?
        if (hasThumbnails)
            builder.appendQueryParameter(getString(R.string.show_fields_query_param), getString(R.string.thumbnails_query_value));
        // display contributors ?
        if (hasContributors)
            builder.appendQueryParameter(getString(R.string.show_tags_query_param), getString(R.string.contributors_query_value));

        return new StoryAsyncLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Story>> loader, List<Story> stories) {
        adapter.clear();
        mProgressBar.setVisibility(View.GONE);
        if (stories != null && !stories.isEmpty()) {
            adapter.addAll(stories);
        } else {
            mErrorTv.setText(R.string.error_occurred);
            mErrorTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Story>> loader) {
        adapter.clear();
    }

    /**
     * A helper method to extract current preference String value
     *
     * @param key          preference's key
     * @param defaultValue preference's default value
     * @return preference current value
     */
    private String getPreferenceStringValue(int key, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(
                getString(key),
                getString(defaultValue)
        );
    }

    /**
     * A helper method to extract current preference boolean value
     *
     * @param key          preference's key
     * @param defaultValue preference's default value
     * @return preference current value
     */
    private boolean getPreferenceBooleanValue(int key, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(
                getString(key),
                getResources().getBoolean(defaultValue)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItemId = item.getItemId();
        if (selectedMenuItemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
