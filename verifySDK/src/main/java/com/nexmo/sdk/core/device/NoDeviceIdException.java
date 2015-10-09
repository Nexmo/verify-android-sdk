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

package com.nexmo.sdk.core.device;

import java.io.IOException;

/**
 * Caused to DeviceId not being able to be retrieved.
 */
public class NoDeviceIdException extends IOException {

    /**
     * Constructs a new NoDeviceIdException that includes the current stack trace.
     */
    public NoDeviceIdException() {
    }

    /**
     * Constructs a new NoDeviceIdException with the current stack trace and the specified detail message.
     *
     * @param message The detail message for this exception. Accepts null.
     */
    public NoDeviceIdException(String message) {
        super(message);
    }

    /**
     * Constructs a new NoDeviceIdException with the current stack trace, the specified detail message and the specified cause.
     *
     * @param message   The detail message for this exception. Accepts null.
     * @param throwable The cause of this exception.
     */
    public NoDeviceIdException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new super(throwable); with the current stack trace and the specified cause.
     *
     * @param throwable The cause of this exception.
     */
    public NoDeviceIdException(final Throwable throwable) {
        super(throwable);
    }

}
