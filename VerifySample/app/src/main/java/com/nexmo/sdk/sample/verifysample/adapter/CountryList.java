/*
 * Copyright (c) 2015 Nexmo Inc
 * All rights reserved.
 *
 * Licensed only under the Nexmo Verify SDK License Agreement located at
 *
 * https://www.nexmo.com/terms-use/verify-sdk/ (the “License”)
 *
 * You may not use, exercise any rights with respect to or exploit this SDK,
 * or any modifications or derivative works thereof, except in accordance
 * with the License.
 */

package com.nexmo.sdk.sample.verifysample.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.AdapterView;

import com.nexmo.sdk.sample.verifysample.R;
import com.nexmo.sdk.util.DeviceUtil;

/**
 *  Helper for reading the country list from resources, and accessing them.
 */
public class CountryList {

    private static final String TAG = CountryList.class.getSimpleName();
    private final Context context;
    private ArrayList<Country> countries = new ArrayList<>();

    /**
     * Initialize all available countries, based on resources.
     * @param context The app context.
     */
    public CountryList(final Context context) {
        this.context = context;
        String[] countryCodes;
        String[] countryNames;

        try {
            countryCodes = this.context.getResources().getStringArray(R.array.country_code_list);
            countryNames = this.context.getResources().getStringArray(R.array.country_name_list);
        } catch (Resources.NotFoundException exception) {
            Log.d(TAG, "Country resource arrays are empty.");
            return;
        }
        // Check that resource length is matching.
        if (countryCodes.length == countryNames.length)
            for (int i = 0; i < countryCodes.length; i++)
                this.countries.add(new Country(countryNames[i], countryCodes[i]));
        else
            Log.d(TAG,"Country resource arrays mismatch.");

    }

    public ArrayList<Country> getList() {
        return this.countries;
    }

    /**
     * Get the country code position in spinner, if possible.
     * @return The item position, -1 if code not found.
     */
    public int getCountryCodePosition(){
        String deviceCountryCode = DeviceUtil.getCountryCode(this.context);
        for (int index=0; index<this.countries.size(); index++)
            if (this.countries.get(index).getCode().equals(deviceCountryCode))
                return index;

        return AdapterView.INVALID_POSITION;
    }

    /**
     * The country code on a given index.
     * @param position Given position.
     * @return The country code.
     */
    public String getCode(final int position) {
        if (this.countries.size() > position)
            return this.countries.get(position).getCode();

        return null;
    }

}
