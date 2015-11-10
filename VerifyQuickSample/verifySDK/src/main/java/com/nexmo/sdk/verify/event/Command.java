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
 * Available commands for controlling the verification workflow, in order to further tailor your
 * end-user experience.
 * <p>You should only consider implementing this API if there is a strong need to change the verification process
 * while it is underway, due to user-detected issues. In some cases, end-users may wish to manually invoke a retry
 * - something to the effect of "Send me my code again". Normally Verify manages retry timing by automatically
 * determining if it needs to attempt delivering the code once again. This retry is invoked if a check is not
 * received within the average expected duration for completing a verification successfully.</p>
 * <p>The available commands are:
 * <ul>
 *     <li>{@link com.nexmo.sdk.verify.event.Command#LOGOUT} Once a user is in {@link UserStatus#USER_VERIFIED}
 *     state a Logout command can be performed. </li>
 *     <li>{@link com.nexmo.sdk.verify.event.Command#CANCEL} Using command 'Cancel', an outstanding request may be
 *     cancelled and a new one issued. This approach, helps you to trigger the same channel as the first attempt,
 *     since it initiates the Verification flow all over again. This mimics the traditional "Retry" or "Correct my number"
 *     button, where a user initiates Verify for the same or a new number because something went wrong.</li>
 *     <li>{@link com.nexmo.sdk.verify.event.Command#TRIGGER_NEXT_EVENT} Using the command 'trigger_next_event',
 *     Verify can be instructed to failover immediately, instead of waiting for the default duration. This will trigger
 *     the next attempt to deliver the verification code, typically over Text to Speech. A relatable way to think of
 *     this is a "Did not receive your SMS?" or "Call me instead" button; which may be made actionable if you determine
 *     that the phone number should have good cellular connectivity.</li>
 * </ul>
 * </p>
 * <p>To issue a verify command call {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
 * The {@link com.nexmo.sdk.verify.event.CommandListener} in place is responsible for event notifications.</p>
 * <p>Note that trying to deliver the PIN code again in a shorter duration does not necessarily translate into
 * better conversion, it also doesn't result in a good user experience owing to the fact that users take time
 * to complete the verification process.</p>
 */
public enum Command {

    /**
     * Once a user is in {@link com.nexmo.sdk.verify.event.UserStatus#USER_VERIFIED} state,
     * he can be un-verified using this command.
     * However, if the user is not verified before performing this action, a
     * {@link com.nexmo.sdk.verify.event.VerifyError#INVALID_USER_STATUS_FOR_COMMAND} error will be triggered.
     */
    LOGOUT,
    /**
     * Once a user is in {@link com.nexmo.sdk.verify.event.UserStatus#USER_PENDING} state, and the verification
     * flow is started for more than 30 seconds, it can be cancelled.
     * If the verification flow was initiated less than 30 seconds later, a
     * {@link com.nexmo.sdk.verify.event.VerifyError#COMMAND_NOT_SUPPORTED} error will be triggered.
     * However, if the user is not in pending state before performing this action, a
     * {@link com.nexmo.sdk.verify.event.VerifyError#INVALID_USER_STATUS_FOR_COMMAND} error will be triggered.
     */
    CANCEL,
    /**
     * Once a user is in {@link com.nexmo.sdk.verify.event.UserStatus#USER_PENDING} state, verify can be instructed to
     * trigger the next attempt to deliver the verification code, typically over Text To Speech.
     * However, if the user is not in {@link com.nexmo.sdk.verify.event.UserStatus#USER_PENDING} state before performing
     * this action, a {@link com.nexmo.sdk.verify.event.VerifyError#INVALID_USER_STATUS_FOR_COMMAND} error will be triggered.
     */
    TRIGGER_NEXT_EVENT

}
