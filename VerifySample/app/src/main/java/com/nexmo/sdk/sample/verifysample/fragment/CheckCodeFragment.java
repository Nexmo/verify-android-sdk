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

package com.nexmo.sdk.sample.verifysample.fragment;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nexmo.sdk.sample.verifysample.R;
import com.nexmo.sdk.sample.verifysample.SampleApplication;

import com.nexmo.sdk.verify.client.VerifyClient;
import com.nexmo.sdk.verify.event.*;

/**
 * Fragment containing a simple view for checking the PIN code received from a pending verify request.
 *
 */
public class CheckCodeFragment extends Fragment {

    public static final String TAG = CheckCodeFragment.class.getSimpleName();
    private static final int MIN_CODE_SIZE = 4;

    public CheckCodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_check_code, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SampleApplication application = (SampleApplication) getActivity().getApplication();
        if (application.getVerifyClient() != null)
            application.getVerifyClient().removeVerifyListeners();
    }

    @Override
    public void onResume(){
        super.onResume();

        final Activity activity = getActivity();
        final SampleApplication application = (SampleApplication) activity.getApplication();
        application.getVerifyClient().addVerifyListener(new VerifyClientListener() {
            @Override
            public void onVerifyInProgress(final VerifyClient verifyClient, final UserObject userObject) {
            }

            @Override
            public void onUserVerified(final VerifyClient verifyClient, final UserObject userObject) {
                Log.d(TAG, "onUserVerified ");
                showToast("User verified!");
            }

            @Override
            public void onError(final VerifyClient verifyClient, final com.nexmo.sdk.verify.event.VerifyError errorCode, final UserObject userObject) {
                Log.d(TAG, "onError " + errorCode);
                showToast("onError.code: " + errorCode.toString());
            }

            @Override
            public void onException(final IOException exception) {
                Log.d(TAG, "onException " + exception.getMessage());
                showToast("No internet connectivity.");
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();

        final Activity activity = getActivity();
        final SampleApplication application = (SampleApplication) activity.getApplication();
        application.getVerifyClient().removeVerifyListeners();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        final SampleApplication application = (SampleApplication) activity.getApplication();
        final InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        final EditText code_et = (EditText) activity.findViewById(R.id.code_et);
        code_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If DONE or Enter were pressed, validate the input.
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    inputMethodManager.hideSoftInputFromWindow(code_et.getWindowToken(), 0);
                    Editable codeEdit = code_et.getText();
                    if (TextUtils.isEmpty(codeEdit.toString()) || codeEdit.toString().length() < MIN_CODE_SIZE)
                        code_et.setError(getResources().getString(R.string.error_code));
                    return true;
                }
                return false;
            }
        });

        Button confirm_btn = (Button) activity.findViewById(R.id.confirm_bv);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(code_et.getWindowToken(), 0);
                Editable codeEdit = code_et.getText();

                if (codeEdit != null) {
                    String pinCode = codeEdit.toString();
                    if (TextUtils.isEmpty(pinCode) || pinCode.length() < MIN_CODE_SIZE)
                        code_et.setError(getResources().getString(R.string.error_code));
                    else
                        application.getVerifyClient().checkPinCode(pinCode);
                }
            }
        });

    }

    private void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
