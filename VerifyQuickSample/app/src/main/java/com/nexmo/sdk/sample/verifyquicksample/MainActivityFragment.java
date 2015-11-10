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

package com.nexmo.sdk.sample.verifyquicksample;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nexmo.sdk.verify.client.VerifyClient;
import com.nexmo.sdk.verify.event.UserObject;
import com.nexmo.sdk.verify.event.VerifyClientListener;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 * This is a basic usage for Verify with auto-managed UI components.
 */
public class MainActivityFragment extends Fragment {

    public static final String TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,  savedInstanceState);

        // attach the verify listener
        final SampleApplication application = (SampleApplication) getActivity().getApplication();
        Button verifyBtn = (Button) getActivity().findViewById(R.id.verify_btn);

        application.getVerifyClient().addVerifyListener(new VerifyClientListener() {
            @Override
            public void onVerifyInProgress(VerifyClient verifyClient, UserObject user) {
                Log.d(TAG, "onVerifyInProgress for number: " + user.getPhoneNumber());
            }

            @Override
            public void onUserVerified(VerifyClient verifyClient, UserObject user) {
                Log.d(TAG, "onUserVerified for number: " + user.getPhoneNumber());
            }

            @Override
            public void onError(VerifyClient verifyClient, com.nexmo.sdk.verify.event.VerifyError errorCode, UserObject user) {
                Log.d(TAG, "onError: " + errorCode + " for number: " + user.getPhoneNumber());
            }

            @Override
            public void onException(IOException exception) {
                Log.d(TAG, "onException");
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate the Verify UI auto-managed flow.
                application.getVerifyClient().getVerifiedUserFromDefaultManagedUI();
            }
        });
    }

}
