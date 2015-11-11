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

package com.nexmo.sdk.verify.core.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.nexmo.sdk.verify.core.service.BaseService;
import com.nexmo.sdk.verify.core.service.TokenService;

/**
 * Base request encapsulation used for requests that only require handset information and the generated token.
 * Used when invoking search, cancel, logout or skip methods.
 *
 */
public class BaseRequest implements Parcelable {
    private String countryCode;
    private String phoneNumber;
    private String token;

    public BaseRequest(final String countryCode, final String phoneNumber) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
    }

    public BaseRequest(final String countryCode, final String phoneNumber, final String token) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

    protected BaseRequest() {
    }

    public BaseRequest(Parcel input) {
        this.countryCode = input.readString();
        this.phoneNumber = input.readString();
        this.token = input.readString();
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return (!TextUtils.isEmpty(this.token));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BaseRequest> CREATOR
            = new Parcelable.Creator<BaseRequest>() {
        public BaseRequest createFromParcel(Parcel in) {
            return new BaseRequest(in);
        }

        public BaseRequest[] newArray(int size) {
            return new VerifyRequest[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.countryCode);
        out.writeString(this.phoneNumber);
        out.writeString(this.token);
    }

    public boolean isStarted() {
        return (!TextUtils.isEmpty(this.countryCode) && !TextUtils.isEmpty(this.phoneNumber));
    }

    @Override
    public String toString() {
        return BaseService.PARAM_COUNTRY_CODE + ": " + (this.countryCode != null ? this.countryCode : "") + ", " +
               BaseService.PARAM_NUMBER + ": " + (this.phoneNumber != null ? this.phoneNumber : "") + ", " +
                TokenService.PARAM_TOKEN + ": " + (this.token != null ? this.token : "");
    }

}
