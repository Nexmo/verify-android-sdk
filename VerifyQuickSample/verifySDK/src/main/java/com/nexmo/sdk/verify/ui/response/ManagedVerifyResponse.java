/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.nexmo.sdk.verify.ui.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.nexmo.sdk.verify.core.service.BaseService;
import com.nexmo.sdk.verify.event.VerifyError;
import com.nexmo.sdk.verify.event.UserStatus;

/**
 * Response object to be passed around by the UI back to {@link com.nexmo.sdk.verify.client.VerifyClient}
 */
public class ManagedVerifyResponse implements Parcelable {

    private String phone;
    private UserStatus userStatus;
    private VerifyError verifyError;
    private boolean ioExceptionOccured;

    public ManagedVerifyResponse(final String phone, final UserStatus userStatus, final VerifyError verifyError, final boolean exception) {
        this.phone = phone;
        this.userStatus = userStatus;
        this.verifyError = verifyError;
        this.ioExceptionOccured = exception;
    }

    public ManagedVerifyResponse(Parcel input) {
        this.phone = input.readString();
        this.userStatus = (UserStatus) input.readSerializable();
        this.verifyError = (VerifyError) input.readSerializable();
        this.ioExceptionOccured = input.readInt() == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ManagedVerifyResponse> CREATOR
            = new Parcelable.Creator<ManagedVerifyResponse>() {
        public ManagedVerifyResponse createFromParcel(Parcel in) {
            return new ManagedVerifyResponse(in);
        }

        public ManagedVerifyResponse[] newArray(int size) {
            return new ManagedVerifyResponse[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.phone);
        out.writeSerializable(this.userStatus);
        out.writeSerializable(this.verifyError);
        out.writeInt(this.ioExceptionOccured ? 0 : 1);
    }

    @Override
    public String toString() {
        return BaseService.PARAM_NUMBER + ": " + (this.phone != null ? this.phone : "") + ", " +
               BaseService.PARAM_RESULT_USER_STATUS + ": " + (this.userStatus != null ? this.userStatus.toString() : "") + ", " +
               BaseService.PARAM_RESULT_CODE + ": " + (this.verifyError != null ? this.verifyError.toString() : "") + ", " +
               "Exception" + ": " + (this.ioExceptionOccured ? "true" : "false");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public VerifyError getVerifyError() {
        return verifyError;
    }

    public void setVerifyError(VerifyError verifyError) {
        this.verifyError = verifyError;
    }

    public boolean isIoExceptionOccured() {
        return ioExceptionOccured;
    }

    public void setIoExceptionOccured(boolean ioExceptionOccured) {
        this.ioExceptionOccured = ioExceptionOccured;
    }

}
