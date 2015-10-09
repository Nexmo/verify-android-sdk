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

import com.nexmo.sdk.verify.event.UserStatus;
import com.nexmo.sdk.verify.core.response.BaseResponse;

/**
 * Wrapper that encapsulates the current verify request information.
 */
public class VerifyRequest extends BaseRequest implements Parcelable {

    private UserStatus userStatus;
    private String pinCode;

    public VerifyRequest(final String countryCode, final String phoneNumber) {
        super(countryCode, phoneNumber);
    }

    public VerifyRequest() {
        this.userStatus = UserStatus.USER_NEW;
    }

    public VerifyRequest(Parcel input) {
        super(input);
        this.userStatus = BaseResponse.getUserStatus(input.readString());
        this.pinCode = input.readString();
    }

    public VerifyRequest(final String countryCode,
                         final String phoneNumber,
                         final String token,
                         final UserStatus userStatus,
                         final String pinCode) {
        super(countryCode, phoneNumber, token);
        this.userStatus = userStatus;
        this.pinCode = pinCode;
    }

    /**
     * Checks if the required information for a pin check is available.
     */
    public boolean isPinCheckAvailable() {
        return (!TextUtils.isEmpty(super.getCountryCode()) && !TextUtils.isEmpty(super.getPhoneNumber()));
    }

    public UserStatus getUserStatus() {
        return this.userStatus;
    }

    public void setUserStatus(final UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getPinCode() {
        return this.pinCode;
    }

    public void setPinCode(final String pinCode) {
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
        super.writeToParcel(out, flags);
        out.writeString(this.userStatus.toString());
        out.writeString(this.pinCode);
    }

}
