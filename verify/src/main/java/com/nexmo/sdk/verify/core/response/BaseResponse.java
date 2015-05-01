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

package com.nexmo.sdk.verify.core.response;

import com.google.gson.annotations.SerializedName;

import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.verify.core.service.BaseService;
import com.nexmo.sdk.core.user.UserStatus;

/**
 * Generic class for handling all the responses from Nexmo Number SDK Service.
 * Used for marshaling/un marshaling basic response JSons into PoJos.
 */
public class BaseResponse {

    @SerializedName(BaseService.PARAM_RESULT_CODE)
    private int resultCode;

    @SerializedName(BaseService.PARAM_RESULT_MESSAGE)
    private String resultMessage;

    @SerializedName(BaseService.PARAM_TIMESTAMP)
    private String timestamp;

    public BaseResponse() {}

    public int getResultCode() {
        return this.resultCode;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }

    /**
     * Checks if the response result code is valid.
     */
    public boolean isValid(){
        return (this.resultCode == ResultCodes.RESULT_CODE_OK);
    }

    @Override
    public String toString() {
        return BaseService.PARAM_RESULT_CODE + ": " + this.resultCode  + ", " +
                BaseService.PARAM_RESULT_MESSAGE + ": " + (this.resultMessage != null ? this.resultMessage : "" ) + ", " +
                BaseService.PARAM_TIMESTAMP + ": " + (this.timestamp != null ? this.timestamp : "");
    }

    public static UserStatus getUserStatus(final String userStatus) {
        if (userStatus.equals(BaseService.USER_NEW))
            return UserStatus.USER_NEW;
        else if (userStatus.equals(BaseService.USER_PENDING))
            return UserStatus.USER_PENDING;
        else if(userStatus.equals(BaseService.USER_VERIFIED))
            return UserStatus.USER_VERIFIED;
        else if (userStatus.equals(BaseService.USER_FAILED))
            return UserStatus.USER_FAILED;
        else if(userStatus.equals(BaseService.USER_EXPIRED))
            return UserStatus.USER_EXPIRED;
        else if (userStatus.equals(BaseService.USER_BLACKLISTED))
            return UserStatus.USER_BLACKLISTED;
        else return UserStatus.USER_UNKNOWN;
    }

}
