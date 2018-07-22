package com.example.android.newsapp.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.newsapp.Models.Story;
import com.example.android.newsapp.Utils.NetworkUtils;

import java.util.List;

public class StoryAsyncLoader extends AsyncTaskLoader<List<Story>> {
    private String mStoriesUrl;

    public StoryAsyncLoader(Context context, String storiesUrl) {
        super(context);
        mStoriesUrl = storiesUrl;
    }

    @Override
    public List<Story> loadInBackground() {
        if (mStoriesUrl == null)
            return null;
        return NetworkUtils.fetchArticlesFromAPI(mStoriesUrl);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
