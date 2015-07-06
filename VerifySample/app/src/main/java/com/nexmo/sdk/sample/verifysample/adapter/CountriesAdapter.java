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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nexmo.sdk.sample.verifysample.R;

import java.util.ArrayList;

/**
 * Countries adaptor with basic UI, with no filtering.
 */
public class CountriesAdapter extends ArrayAdapter<Country> {
    private final ArrayList<Country> countries;
    private LayoutInflater layoutInflater;

    public CountriesAdapter(Context context, int textViewResourceId, CountryList countryList) {
        super(context, textViewResourceId, countryList.getList());
        this.countries = countryList.getList();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return this.countries.size();
    }

    @Override
    public Country getItem(int position){
        if (position > 0 && this.countries.size() > position)
            return this.countries.get(position);
        return null;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = this.layoutInflater.inflate(R.layout.country_item, null);
        TextView label = (TextView) convertView.findViewById(R.id.country_tv);
        Country country = super.getItem(position);
        if (country != null)
            label.setText(country.getCode());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        if (convertView == null)
            convertView = this.layoutInflater.inflate(R.layout.country_dropdown_item, null);
        TextView label = (TextView) convertView.findViewById(R.id.country_dropdown_tv);
        Country country = super.getItem(position);
        if (country != null)
            label.setText(country.getName());

        return convertView;
    }

}
