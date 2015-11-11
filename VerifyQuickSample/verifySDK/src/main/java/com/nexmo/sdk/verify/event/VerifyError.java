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

import com.nexmo.sdk.verify.client.VerifyClient;

/**
 * Verify error codes.
 * Identifies the error type which triggered a {@link VerifyClientListener#onError(com.nexmo.sdk.verify.client.VerifyClient, VerifyError, UserObject)} event.
 */
public enum VerifyError {

    /** There is already a pending verification in progress. Handle {@link VerifyClientListener} events to check the progress. */
    VERIFICATION_ALREADY_STARTED,
    /**
     * PIN code cannot be checked because there is no verification in progress.
     * Please use the {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String)}
     * method for the verification to be initiated and wait for a PIN code.
     */
    VERIFICATION_NOT_STARTED,
    /** Missing or invalid phone number. */
    INVALID_NUMBER,
    /**
     * @deprecated
     * Provided number does not match the SIM phone number.
     * Please use the {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String)}
     * method without params for the verification to be initiated.
     */
    PROVIDED_NUMBER_NOT_ACCEPTED,
    /**
     * SIM less device: SIM card not found. Please use the {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String)}
     * method and supply params for phone number and country code for the verification to be initiated.
     */
    NUMBER_REQUIRED,
    /**
     * There is no Device ID available for this device. You will not be able to use the Nexmo Verify API to to verify against this device.
     * Please use another device.
     */
    DEVICE_ID_NOT_FOUND,
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
    /** Current user cannot be found. This means there is no ongoing verification, or the last verification did not succeed. */
    USER_UNKNOWN,
    /** Ongoing verification has failed. This usually occurs when the wrong PIN code was send too many times. */
    USER_FAILED,
    /** Throttled. Too many failed requests. */
    THROTTLED,
    /** Your account does not have sufficient credit to process this request. */
    QUOTA_EXCEEDED,
    /** Invalid app_id. Supplied app_id is not listed under your accepted application list. */
    INVALID_CREDENTIALS,
    /** A search request is already in progress, please wait until completion. */
    GET_VERIFIED_USER_ALREADY_STARTED,
    /**
     * Invalid user status for the command action.
     * <ul>For example:
     *  <li>Calling {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
     *  with {@link com.nexmo.sdk.verify.event.Command#LOGOUT} action cannot be accomplished for an unverified user. </li>
     *  <li>Calling {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
     *  with {@link com.nexmo.sdk.verify.event.Command#CANCEL} action cannot be accomplished for a user that is not
     *  in {@link com.nexmo.sdk.verify.event.UserStatus#USER_PENDING} state.</li>
     * </ul>
     */
    INVALID_USER_STATUS_FOR_COMMAND,
    /**
     * Command is not permitted, reason can be:
     * <ul>
     *     <li>In case of {@link com.nexmo.sdk.verify.event.Command#CANCEL} command:
     *     Verification requests cannot be canceled within the first 30 seconds.
     *     You must wait at least 30s for the request to be allowed for cancellation.</li>
     *     <li>In case of {@link com.nexmo.sdk.verify.event.Command#TRIGGER_NEXT_EVENT} command:
     *     No more events are left to execute.
     *     All the attempts to deliver the code for this request have been completed
     *     and there are no more events to skip to.</li>
     * </ul>
     */
    COMMAND_NOT_SUPPORTED,
    /**
     * Stateless verification not allowed. User must be in {@link UserStatus#USER_VERIFIED} state in order to perform
     * {@link com.nexmo.sdk.verify.client.VerifyClient#verifyStandalone(String, String)}
     * Please perform a {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String)} or
     * {@link VerifyClient#getVerifiedUserFromDefaultManagedUI()}} before doing any stateless verifications.
     */
    INVALID_USER_STATUS_FOR_STATELESS_VERIFICATION,
    /** The SDK revision is not supported anymore. */
    SDK_REVISION_NOT_SUPPORTED,
    /** Current Android OS version is not supported. */
    OS_NOT_SUPPORTED,
    /** Generic internal error, the service might be down for the moment. Please try again later. */
    INTERNAL_ERR

}
