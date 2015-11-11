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

package com.nexmo.sdk.verify.core.request;

import com.nexmo.sdk.verify.event.Command;

/**
 * Wrapper that encapsulates a command request.
 */
public class CommandRequest extends BaseRequest {

    private Command command;

    public CommandRequest() {
        super();
    }

    public CommandRequest(final String countryCode,
                          final String phoneNumber,
                          final Command command) {
        super(countryCode, phoneNumber);
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

}