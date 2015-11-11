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

/**
 * Wrapper that encapsulates a search request.
 */
public class SearchRequest extends BaseRequest {

    public SearchRequest() {
        super();
    }

    public SearchRequest(final String countryCode, final String phoneNumber) {
        super(countryCode, phoneNumber);
    }

}
