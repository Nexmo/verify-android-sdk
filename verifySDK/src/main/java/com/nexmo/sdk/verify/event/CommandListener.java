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

import com.nexmo.sdk.verify.core.event.GenericExceptionListener;

/**
 * A CommandListener handles all the command related events.
 *
 * <p>Command are triggered by calling {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
 * <ul>The available commands are:
 *  <li>{@link com.nexmo.sdk.verify.event.Command#LOGOUT}</li>
 *  <li>{@link com.nexmo.sdk.verify.event.Command#CANCEL} Note: verification requests cannot be cancelled
 *  within the first 30 seconds.</li>
 *  <li>{@link com.nexmo.sdk.verify.event.Command#TRIGGER_NEXT_EVENT}</li>
 * </ul>
 * <p>Make sure the user is in the right status when trying to perform a command, otherwise
 * a {@link com.nexmo.sdk.verify.event.VerifyError#INVALID_USER_STATUS_FOR_COMMAND} error will be triggered.</p>
 *
 * <p>Remember to set {@link com.nexmo.sdk.verify.event.CommandListener} in order to receive command updates.
 * <p> Example usage, performing a {@link com.nexmo.sdk.verify.event.Command#LOGOUT} for an already verified user:
 * <pre>
 *     VerifyClient myVerifyClient = new VerifyClient(myNexmoClient);
 *     myVerifyClient.command(myCountryCode, myPhoneNo, Command.LOGOUT new CommandListener() {
 *         &#64;Override
 *         public void onSuccess(Command command) {
 *              // Update the application UI here if needed.
 *         }
 *
 *         &#64;Override
 *         public void onError(Command command, final com.nexmo.sdk.verify.event.VerifyError errorCode, String errorMessage) {
 *              // Update the application UI here if needed.
 *         }
 *     }
 * </pre>
 */
public interface CommandListener extends GenericExceptionListener {

    /**
     * Indicates the command request was successful.
     * @param command The command that was issued.
     */
    public void onSuccess(final Command command);

    /**
     * The request has been rejected.
     *
     * <ul>For example, user had an invalid status for the command action:
     *     {@link com.nexmo.sdk.verify.event.VerifyError#INVALID_USER_STATUS_FOR_COMMAND}
     *      <li>Calling {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
     *      with {@link com.nexmo.sdk.verify.event.Command#LOGOUT} action cannot be accomplished for an unverified user. </li>
     *      <li>Calling {@link com.nexmo.sdk.verify.client.VerifyClient#command(String, String, Command, CommandListener)}
     *      with {@link com.nexmo.sdk.verify.event.Command#CANCEL} action cannot be accomplished for a user that is not
     *      in {@link com.nexmo.sdk.verify.event.UserStatus#USER_PENDING} state.</li>
     * </ul>
     * @param command       The command that was issued.
     * @param errorCode     The {@link VerifyError} codes to describe the error.
     * @param errorMessage  The message that describes the {@link VerifyError}.
     */
    public void onError(final Command command,
                        final com.nexmo.sdk.verify.event.VerifyError errorCode,
                        final String errorMessage);

}
