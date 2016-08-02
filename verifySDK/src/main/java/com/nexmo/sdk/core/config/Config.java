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

package com.nexmo.sdk.core.config;

import org.apache.http.protocol.HTTP;

/**
 * General configurations.
 */
public class Config {

    /** Environment endpoint: production or sandbox. */
    /** Used for applications deployed in production. */
    public static final String ENDPOINT_PRODUCTION = "https://api.nexmo.com/sdk/";
    /** Used during development and testing. Not available yet. */
    public static final String ENDPOINT_SANDBOX = "";

    /** Current Nexmo SDK version. */
    public static final String SDK_REVISION_CODE = "1.2.0";

    /** Custom HTTP header. */
    public static final String OS_ANDROID = "ANDROID";

    /** Default encoding for GET and POST parameters. */
    public static final String PARAMS_ENCODING = HTTP.UTF_8;

}
