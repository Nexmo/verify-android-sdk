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

package com.nexmo.sdk.verify.core.event;

import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.core.event.ServiceListener;

import com.nexmo.sdk.verify.core.response.VerifyResponse;
import com.nexmo.sdk.verify.core.service.CommandService;

import com.nexmo.sdk.verify.event.*;

import java.io.IOException;

/**
 * SDK internal network response callbacks for Command actions.
 */
public class CommandServiceListener extends ServiceListener<VerifyResponse> {

    private final CommandListener commandListener;
    private final Command command;

    public CommandServiceListener(final Command command,
                                  final CommandListener commandListener,
                                  final BaseClientListener clientListener) {
        super(clientListener);
        this.command = command;
        this.commandListener = commandListener;
    }

    /**
     * Get the public listener attached to {@link com.nexmo.sdk.verify.client.VerifyClient#command}
     * @return The listener.
     */
    public CommandListener getCommandListener(){
        return this.commandListener;
    }

    public void onResponse(VerifyResponse response) {
        switch(response.getResultCode()) {
            case ResultCodes.RESULT_CODE_OK: {
                commandListener.onSuccess(this.command);
                break;
            }
            case ResultCodes.INVALID_TOKEN: {
                // Issue a new token request.
                CommandService.getInstance().start(CommandService.getInstance().getNexmoClient(), this);
                break;
            }
            default: {
                commandListener.onError(this.command,
                                        formatResultCode(response.getResultCode()),
                                        response.getResultMessage());
            }
        }
    }

    @Override
    public void onFail(com.nexmo.sdk.verify.event.VerifyError errorCode, String reasonMessage) {
        commandListener.onError(this.command,
                                errorCode,
                                reasonMessage);
    }

    /**
     * A request was timed out because of network connectivity exception.
     * Triggered in case of network error, such as UnknownHostException or SocketTimeout exception.
     *
     * @param exception The exception.
     */
    @Override
    public void onException(final IOException exception) {
        commandListener.onException(exception);
    }

}
