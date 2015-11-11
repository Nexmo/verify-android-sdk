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

package com.nexmo.sdk.verify.client;

import android.test.mock.MockContext;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.core.client.ClientBuilderException;

public class MockNexmoClient {
    private NexmoClient nexmoClient = null;

    public MockNexmoClient() {
        buildClient();
    }

    private void buildClient() {
        try {
            nexmoClient = new NexmoClient.NexmoClientBuilder()
                    .context(new MockContext())
                    .applicationId("app_dummy")
                    .sharedSecretKey("secret_dummy")
                    .build();
        } catch (ClientBuilderException e) {
            e.printStackTrace();
        }
    }

    public NexmoClient getClient() {
        return nexmoClient;
    }

    public String getClientHost() {
        return nexmoClient.getEnvironmentHost();
    }

    public String getSharedSecretKey() {
        return nexmoClient.getSharedSecretKey();
    }
}
