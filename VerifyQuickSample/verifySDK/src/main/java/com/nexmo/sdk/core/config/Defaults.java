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

/**
 * Defaults.
 */
public class Defaults {

    public static final long MAX_ALLOWABLE_TIME_DELTA = 5 * 60 * 1000;
    public static final int CONNECTION_TIMEOUT = 15 * 1000;
    public static final int CONNECTION_READ_TIMEOUT = 10 * 1000;
    public static final int MIN_CODE_LENGTH = 4;
    public static final int MAX_CODE_LENGTH = 6;
    public static final int MIN_PHONE_NUMBER_LENGTH = 2;
    public static final int MAX_PHONE_NUMBER_LENGTH = 15;

}
