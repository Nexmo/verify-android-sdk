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
import com.nexmo.sdk.verify.core.event.GenericExceptionListener;

/**
 * A SearchListener handles all the search related events. It enables you to get the user status
 * even if a verification is still in progress.
 *
 * <p> To register for receiving search events add the listener to the search
 * call {@link VerifyClient#getUserStatus(String, String, SearchListener)}
 * <p> Example usage:
 * <pre>
 *     myVerifyClient.getUserStatus("GB", "070000000000", new SearchListener() {
 *         &#64;Override
 *         public void onUserStatus(final UserStatus userStatus) {
 *              // Update the application UI here if needed.
 *         }
 *
 *         &#64;Override
 *         public void onError(final com.nexmo.sdk.verify.event.VerifyError errorCode) {
 *              // Update the application UI here if needed.
 *         }
 *     }
 * </pre>
 */
public interface SearchListener extends GenericExceptionListener {

    /**
     * Indicates the current user status.
     * @param userStatus   The user status, one of the following:
     *                     {@link UserStatus#USER_PENDING}
     *                     {@link UserStatus#USER_VERIFIED}
     *                     {@link UserStatus#USER_UNVERIFIED}
     *                     {@link UserStatus#USER_EXPIRED}
     *                     {@link UserStatus#USER_FAILED}
     *                     {@link UserStatus#USER_BLACKLISTED}
     *                     {@link UserStatus#USER_UNKNOWN}
     */
    public void onUserStatus(final UserStatus userStatus);

    /**
     * The search request has been rejected.
     * @param errorCode The {@link VerifyError} codes to describe the error.
     * @param errorMessage The message that describes the {@link VerifyError}.
     */
    public void onError(final com.nexmo.sdk.verify.event.VerifyError errorCode,
                        final String errorMessage);

}
