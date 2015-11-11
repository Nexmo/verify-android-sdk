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

package com.nexmo.sdk.verify.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@link UserObject} contains all the information on the user which initiated a verify request.
 */
public class UserObject implements Parcelable {

    private String phoneNumber;

    public UserObject(final String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<UserObject> CREATOR
            = new Parcelable.Creator<UserObject>() {
        public UserObject createFromParcel(Parcel in) {
            return new UserObject(in);
        }

        public UserObject[] newArray(int size) {
            return new UserObject[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phoneNumber);
    }

    private UserObject(Parcel input) {
        this.phoneNumber = input.readString();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
