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

import com.nexmo.sdk.verify.core.service.BaseService;

/**
 * User status codes.
 * <ul>
 *     <li>Each verify flow starts from a {@link #USER_NEW} status.</li>
 *     <li>After {@link com.nexmo.sdk.verify.client.VerifyClient#getVerifiedUser(String, String) call} is successful,
 *     the user status will be changed to {@link #USER_PENDING}. From this moment on, the user is expected to receive a PIN code.</li>
 *     <li>User can get into {@link #USER_FAILED} status if incorrect pin was entered 3 times.</li>
 *     <li>User can get into {@link #USER_EXPIRED} status if the correct PIN was not entered in due time.</li>
 * </ul>
 */
public enum UserStatus {

    USER_NEW(BaseService.USER_NEW),
    /** When user is in pending state, the verify flow has been initiated. However, this doesn't ensure the token remains valid. */
    USER_PENDING(BaseService.USER_PENDING),
    USER_VERIFIED(BaseService.USER_VERIFIED),
    /** User is in {@link UserStatus#USER_UNVERIFIED} status only when an explicit {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
     * with {@link com.nexmo.sdk.verify.event.Command#LOGOUT} action was invoked for an already verified user.
     */
    USER_UNVERIFIED(BaseService.USER_UNVERIFIED),
    USER_FAILED(BaseService.USER_FAILED),
    USER_EXPIRED(BaseService.USER_EXPIRED),
    USER_BLACKLISTED(BaseService.USER_BLACKLISTED),
    USER_UNKNOWN(BaseService.USER_UNKNOWN);

    private String value;

    UserStatus(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    private String getValue() {
        return this.value;
    }

}
