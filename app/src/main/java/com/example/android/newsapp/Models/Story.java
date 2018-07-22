package com.example.android.newsapp.Models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class Story {
    private String mTitle;

    private String mSectionName;

    private String mUrl;

    private Date mDate;

    private Bitmap mThumbnail;

    /**
     * Because a news story can have more than one contributor/author, a List
     * of Strings is necessary to store them with each item representing a
     * contributor.
     */
    private List<Contributor> mContributors;

    public Story(String title, String sectionName, String url, Date date, Bitmap thumbnail, List<Contributor> contributors) {
        mTitle = title;
        mSectionName = sectionName;
        mUrl = url;
        mDate = date;
        mThumbnail = thumbnail;
        mContributors = contributors;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getUrl() {
        return mUrl;
    }

    public Date getDate() {
        return mDate;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public List<Contributor> getContributors() {
        return mContributors;
    }
}
