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

import java.io.IOException;

public interface GenericExceptionListener {

    /**
     * A request was timed out because of network connectivity exception.
     * Triggered in case of network error, such as UnknownHostException or SocketTimeout exception.
     * @param exception The exception.
     */
    public void onException(final IOException exception);

}
