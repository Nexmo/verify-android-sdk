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
 * ClientBuilderException indicates that an instance of NexmoClient cannot be acquired.
 * In most cases this means some mandatory params are not being set, and building a NexmoClient fails.
 * When exceptions are thrown, they should be caught by the application code.
 */
public class ClientBuilderException extends Exception {

    /**
     * Constructs a new ClientBuilderException that includes the current stack trace.
     */
    public ClientBuilderException() {}

    /**
     * Constructs a new ClientBuilderException with the current stack trace and the specified detail message.
     *
     * @param message The detail message for this exception. Accepts null.
     */
    public ClientBuilderException(String message) {
        super(message);
    }

    /**
     * Constructs a new ClientBuilderException with the current stack trace, the specified detail message and the specified cause.
     *
     * @param message     The detail message for this exception. Accepts null.
     * @param throwable   The cause of this exception.
     */
    public ClientBuilderException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new ClientBuilderException with the current stack trace and the specified cause.
     *
     * @param throwable The cause of this exception.
     */
    public ClientBuilderException(Throwable throwable) {
        super(throwable);
    }

    public static void appendExceptionCause(StringBuilder stringBuilder, String cause) {
        if (stringBuilder.length() > 0 )
            stringBuilder.append(" , ");
        stringBuilder.append(cause);
    }

}
