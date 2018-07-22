package com.example.android.newsapp.Models;

import android.text.TextUtils;

public class Contributor {
    private String mFirstName;

    private String mLastName;

    public Contributor(String firstName, String lastName) {
        mFirstName = firstName;
        mLastName = lastName;
    }

    /* If tags exist a contributor always has a firstName or a lastName or both.
     * If one of them doesn't exist the API returns it as an empty string
     * That's why we add a space between both unless both are present.
     */
    @Override
    public String toString() {
        StringBuilder fullName = new StringBuilder();
        if (!TextUtils.isEmpty(mFirstName))
            fullName.append(mFirstName);
        if (!TextUtils.isEmpty(mFirstName) && !TextUtils.isEmpty(mLastName))
            fullName.append(" ");
        if (!TextUtils.isEmpty(mLastName))
            fullName.append(mLastName);

        return fullName.toString();
    }
}
