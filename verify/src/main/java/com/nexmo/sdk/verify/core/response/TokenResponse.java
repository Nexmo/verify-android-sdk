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

import com.nexmo.sdk.verify.core.service.TokenService;

/**
 * Class for marshaling/unmarshaling response tokens JSons into PoJos.
 */
public class TokenResponse extends BaseResponse {

    @SerializedName(TokenService.PARAM_TOKEN)
    private String token;

    protected TokenResponse(){}

    public String getToken() {
        return this.token;
    }

}
