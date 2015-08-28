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

package com.nexmo.sdk.sample.verifysample;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.nexmo.sdk.sample.verifysample.fragment.MainFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TextUtils.isEmpty(Config.NexmoAppId) || TextUtils.isEmpty(Config.NexmoSharedSecretKey))
            Toast.makeText(this, "Mandatory Nexmo config was not set.", Toast.LENGTH_LONG).show();
        else {
            setContentView(R.layout.activity_main);
            if (savedInstanceState == null)
                getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
        }
    }

}
