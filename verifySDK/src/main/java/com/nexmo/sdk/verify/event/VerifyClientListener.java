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
 * A VerifyClientListener handles all verify related events.
 * <p> To register for receiving events call {@link VerifyClient#addVerifyListener(VerifyClientListener)}
 * in your project.
 *
 * <p> To unregister from receiving events {@link VerifyClient#removeVerifyListeners()} or
 * {@link VerifyClient#removeVerifyListener(VerifyClientListener)} can be called at all times.
 *
 * <p> When the verification is successfully started {@link VerifyClientListener#onVerifyInProgress(VerifyClient)} is invoked.
 *
 * <p> If the verification cannot be started {@link VerifyClientListener#onError} is invoked describing the error.
 *
 * <p> After the correct PIN code has been sent via {@link VerifyClient#checkPinCode(String)} a successful verification
 * ends up with {@link VerifyClientListener#onUserVerified(VerifyClient)} being triggered.
 *
 *  <p> If the verification is in progress but it cannot be completed due to either:
 *  <li>
 *      <ul>Incorrect PIN code being submitted, then {@link VerifyClientListener#onError(VerifyClient, VerifyError)}
 *          is invoked with {@link VerifyError#INVALID_PIN_CODE}</ul>
 *      <ul>Incorrect PIN code being submitted too many times, then {@link VerifyClientListener#onError(VerifyClient, VerifyError)}
 *          is invoked with {@link com.nexmo.sdk.verify.event.VerifyError#INVALID_CODE_TOO_MANY_TIMES}</ul>
 *  </li>
 *
 * <p> Example usage:
 * <pre>
 *     myVerifyClient.addVerifyClientListener(new VerifyClientListener() {
 *         &#64;Override
 *         public void onVerifyInProgress(final VerifyClient verifyClient) {
 *              // Update the application UI here if needed.
 *              // The user should received the PIN code anytime now.
 *         }
 *
 *         &#64;Override
 *         public void onUserVerified(final VerifyClient verifyClient) {
 *              // Update the application UI here if needed.
 *         }
 *
 *         &#64;Override
 *         public void onError(final VerifyClient verifyClient, final com.nexmo.sdk.verify.event.VerifyError errorCode) {
 *              // Update the application UI here if needed.
 *         }
 *
 *         &#64;Override
 *         public void onException(final IOException exception) {
 *              // Update the application UI here if needed. Most probably there is a network connectivity exception.
 *         }
 *     }
 * </pre>
 */
public interface VerifyClientListener extends GenericExceptionListener {

    /**
     * Called when a verify action has been triggered and the user is expected to receive the PIN code.
     * The PIN code is generated after this callback is triggered, so be advised to call
     * {@link VerifyClient#checkPinCode(String)}  at a later stage, otherwise it would fail.
     *
     * @param verifyClient The verify client that triggered a new verify.
     */
     public void onVerifyInProgress(final VerifyClient verifyClient);

    /**
     * Called when a verification is completed, or the user was already verified.
     * A successful verification can be achieved after the PIN code Nexmo sent has been matched with the one provided by the user.
     *
     * @param verifyClient The verify client that triggered a new verify.
     */
     public void onUserVerified(final VerifyClient verifyClient);

    /**
     * The request has been rejected or failed.
     * @param verifyClient The verify client that triggered a new verify.
     * @param errorCode The {@link VerifyError} codes to describe the error.
     */
    public void onError(final VerifyClient verifyClient,
                        final com.nexmo.sdk.verify.event.VerifyError errorCode);

}
