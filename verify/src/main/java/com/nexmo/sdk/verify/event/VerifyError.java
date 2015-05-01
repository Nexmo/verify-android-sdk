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

/**
 * Verify error codes.
 * Identifies the error type which triggered a {@link VerifyClientListener#onError(com.nexmo.sdk.verify.client.VerifyClient, VerifyError)} event.
 */
public enum VerifyError {

    /** There is already a pending verification in progress. Handle {@link VerifyClientListener} events to check the progress. */
    VERIFICATION_ALREADY_STARTED,
    /** Missing or invalid phone number. */
    INVALID_NUMBER,
    /**
     * Provided number does not match the SIM phone number.
     * Please use the {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String)}
     * method without params for the verification to be initiated.
     */
    PROVIDED_NUMBER_NOT_ACCEPTED,
    /**
     * SIM less device: SIM card not found. Please use the {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String)}
     *  method and supply params for phone number and country code for the verification to be initiated.
     */
    NUMBER_REQUIRED,
    /** User must be in pending status to be able to perform a PIN check. */
    CANNOT_PERFORM_CHECK,
    /** Missing or invalid PIN code supplied. */
    INVALID_PIN_CODE,
    /** Ongoing verification has failed. A wrong PIN code was provided too many times. */
    INVALID_CODE_TOO_MANY_TIMES,
    /** Ongoing verification expired. Need to start verify again. */
    USER_EXPIRED,
    /** Ongoing verification rejected. User blacklisted for verification. */
    USER_BLACKLISTED,
    /** Throttled. Too many failed requests. */
    THROTTLED,
    /** Your account does not have sufficient credit to process this request. */
    QUOTA_EXCEEDED,
    /** Invalid app_id. Supplied app_id is not listed under your accepted application list. */
    INVALID_CREDENTIALS,
    /** The SDK revision is not supported anymore. */
    SDK_REVISION_NOT_SUPPORTED,
    /** Current Android OS version is not supported. */
    OS_NOT_SUPPORTED,
    /** Generic internal error, the service might be down for the moment. Please try again later. */
    INTERNAL_ERR

}
