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

package com.nexmo.sdk.core.client;

/**
 * Internal error codes sent by the SDK service. They describe an unrecoverable error.
 */
public class ResultCodes {

    /**The request was successfully accepted by Nexmo. */
    public static final int RESULT_CODE_OK = 0;

    /** Invalid app_id. Supplied app_id is not listed under your accepted application list. */
    public static final int BAD_APP_ID = 2;

    /** Invalid token. Expired token needs to be re-generated. */
    public static final int INVALID_TOKEN = 3;

    /** Invalid match for supplied app_id and sharedSecretKey. You are not authorized to use Nexmo services. **/
    public static final int INVALID_CREDENTIALS = 4;

    /** Generic internal error, the service might be down for the moment. Please try again later. */
    public static final int INTERNAL_ERROR = 5;

    /** Your account does not have sufficient credit to process this request. */
    public static final int QUOTA_EXCEEDED = 9;

    /** Missing or invalid PIN code supplied. */
    public static final int INVALID_PIN_CODE = 16;

    /** A wrong PIN code was provided too many times. */
    public static final int INVALID_CODE_TOO_MANY_TIMES = 17;

    /**
     * Verification requests cannot be canceled within the first 30 seconds.
     * Trigger next event is also not permitted.
     */
    public static final int COMMAND_NOT_SUPPORTED = 19;

    /** Missing or invalid phone number. */
    public static final int INVALID_NUMBER = 53;

    /** Missing or invalid PIN code. */
    public static final int INVALID_CODE = 54;

    /** User must be in pending status to be able to perform a PIN check. */
    public static final int CANNOT_PERFORM_CHECK = 55;

    /** User verified with another phone number - we will verify again. */
    public static final int VERIFICATION_RESTARTED = 56;

    /** Verified User returning after too long a duration - we will verify again. */
    public static final int VERIFICATION_EXPIRED_RESTARTED = 57;

    /** This Number SDK revision is not supported anymore. Please upgrade the SDK version to be able to perform verifications. */
    public static final int SDK_NOT_SUPPORTED = 58;

    /** The device Android OS version is not supported. */
    public static final int OS_NOT_SUPPORTED = 59;

    /** Throttled. Too many failed requests. */
    public static final int REQUEST_REJECTED = 60;

    /** Invalid user status for the command action. */
    public static final int INVALID_USER_STATUS_FOR_COMMAND = 62;

    /**  Logout is not available because the user is not in verified status. */
    public static final int INVALID_USER_STATUS_FOR_LOGOUT = 63;

}
