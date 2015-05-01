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

import com.nexmo.sdk.core.user.UserStatus;
import com.nexmo.sdk.verify.core.response.BaseResponse;

/**
 * Wrapper that encapsulates the current verify request information.
 */
public final class VerifyRequest implements Parcelable {

    private String countryCode;
    private String phoneNumber;
    private String token;
    private UserStatus userStatus;
    private String pinCode;

    public VerifyRequest() {
        userStatus = UserStatus.USER_NEW;
    }

    public VerifyRequest(Parcel input) {
        this.countryCode = input.readString();
        this.phoneNumber = input.readString();
        this.token = input.readString();
        this.userStatus = BaseResponse.getUserStatus(input.readString());
        this.pinCode = input.readString();
    }

    /**
     * Checks if the required information for a pin check is available.
     */
    public boolean isPinCheckAvailable() {
        if (TextUtils.isEmpty(this.countryCode))
            return false;

        if (TextUtils.isEmpty(this.phoneNumber))
            return false;

        if (TextUtils.isEmpty(this.token))
            return false;

        return this.userStatus == UserStatus.USER_PENDING;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getUserStatus() {
        return this.userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<VerifyRequest> CREATOR
            = new Parcelable.Creator<VerifyRequest>() {
        public VerifyRequest createFromParcel(Parcel in) {
            return new VerifyRequest(in);
        }

        public VerifyRequest[] newArray(int size) {
            return new VerifyRequest[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.countryCode);
        out.writeString(this.phoneNumber);
        out.writeString(this.token);
        out.writeString(this.userStatus.toString());
        out.writeString(this.pinCode);
    }

}
