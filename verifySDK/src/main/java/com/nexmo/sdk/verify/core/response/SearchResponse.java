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
import com.nexmo.sdk.verify.event.UserStatus;
import com.nexmo.sdk.verify.core.service.BaseService;

/**
 * Class for marshaling/un marshaling verify search response JSons into PoJos.
 */
public class SearchResponse extends BaseResponse {

    @SerializedName(BaseService.PARAM_RESULT_USER_STATUS)
    private String userStatus;

    protected SearchResponse() {}

    /**
     * Get the user status.
     *
     * @return One of the values from {@link com.nexmo.sdk.verify.event.UserStatus}
     */
    public UserStatus getUserStatus() {
        return getUserStatus(this.userStatus);
    }

    @Override
    public String toString() {
        return super.toString() + ", " + BaseService.PARAM_RESULT_USER_STATUS + ": " + (this.userStatus != null ? this.userStatus : "");
    }

}
