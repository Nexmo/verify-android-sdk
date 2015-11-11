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

package com.nexmo.sdk.verify.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.R;
import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.core.config.Defaults;
import com.nexmo.sdk.util.DeviceUtil;
import com.nexmo.sdk.verify.core.event.BaseClientListener;
import com.nexmo.sdk.verify.core.event.VerifyServiceListener;
import com.nexmo.sdk.verify.core.request.VerifyRequest;
import com.nexmo.sdk.verify.core.service.VerifyService;
import com.nexmo.sdk.verify.event.*;
import com.nexmo.sdk.verify.event.VerifyError;
import com.nexmo.sdk.verify.ui.countriesAdapter.CountryAdapter;
import com.nexmo.sdk.verify.ui.countriesAdapter.CountryList;
import com.nexmo.sdk.verify.ui.response.ManagedVerifyResponse;

import java.io.IOException;

/**
 * Custom Verify screen that requires user input for country prefix and phone number.
 * On Send Code action, a CheckPhoneNumberActivity is launched using the data provided by the user.
 */
public class VerifyPhoneNumberActivity extends VerifyBaseActivity implements BaseClientListener {

    public static final String TAG = VerifyPhoneNumberActivity.class.getSimpleName();
    private CountryList countries;
    private Spinner countryCodeSpinner;
    private EditText phoneNumberEditText;
    private TextView countryPrefixText;
    private NexmoClient nexmoClient;
    // Internal listeners.
    private VerifyServiceListener verifyServiceListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nexmo_verify_phone_number);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            NexmoClient parcelable = extras.getParcelable(NexmoClient.class.getSimpleName());
            if(parcelable != null) {
                try {
                    this.nexmoClient = new NexmoClient.NexmoClientBuilder().context(getApplicationContext()).applicationId(parcelable.getApplicationId()).sharedSecretKey(parcelable.getSharedSecretKey()).environmentHost(parcelable.getEnvironmentHost()).build();
                } catch (ClientBuilderException e) {
                    e.printStackTrace();
                    return;
                }
                setup();
                prefillInput();
            }
        }
    }

    @Override
    public void handleErrorResult(int resultCode) {
        Log.d(TAG, "handleErrorResult " + resultCode);
        switch(resultCode) {
            case ResultCodes.INVALID_NUMBER: {
                this.phoneNumberEditText.setError(getResources().getString(R.string.nexmo_verify_invalid_number));
                notifyErrorListeners(VerifyError.INVALID_NUMBER);
                break;
            }
            case ResultCodes.INVALID_CREDENTIALS:
            case ResultCodes.BAD_APP_ID:{
                notifyErrorListeners(VerifyError.INVALID_CREDENTIALS);
                break;
            }
            case ResultCodes.REQUEST_REJECTED: {
                notifyErrorListeners(VerifyError.THROTTLED);
                break;
            }
            case ResultCodes.QUOTA_EXCEEDED: {
                notifyErrorListeners(VerifyError.QUOTA_EXCEEDED);
                break;
            }
            case ResultCodes.SDK_NOT_SUPPORTED: {
                notifyErrorListeners(VerifyError.SDK_REVISION_NOT_SUPPORTED);
                break;
            }
            case ResultCodes.OS_NOT_SUPPORTED: {
                notifyErrorListeners(VerifyError.OS_NOT_SUPPORTED);
                break;
            }
            default: {
                notifyErrorListeners(VerifyError.INTERNAL_ERR);
                break;
            }
        }
    }

    @Override
    public void notifyErrorListeners(com.nexmo.sdk.verify.event.VerifyError verifyError) {
        Log.d(TAG, "notifyErrorListeners " + verifyError.toString());
        broadcastManagedVerifyUpdate(new ManagedVerifyResponse(getPhoneNumberInput(), UserStatus.USER_NEW, verifyError, false));
    }

    @Override
    public void handleUserStateChanged(UserStatus userStatus) {
        Log.d(TAG, "handleUserStateChanged " + userStatus);
        switch(userStatus) {
            case USER_PENDING:{
                // Notify VerifyListener.onVerifyInProgress
                broadcastManagedVerifyUpdate(new ManagedVerifyResponse(getPhoneNumberInput(), UserStatus.USER_PENDING, null, false));

                // Launch the VerifyCheckCodeActivity. using nexmoClient and verifyObject
                launchView(CheckPhoneNumberActivity.class);
                break;
            }
            case USER_VERIFIED: {
                broadcastManagedVerifyUpdate(new ManagedVerifyResponse(getPhoneNumberInput(), UserStatus.USER_VERIFIED, null, false));

                displayToast(getResources().getString(R.string.nexmo_verify_dialog_message_verified));
                // User is already verified, going back.
                finish();
                break;
            }
            case USER_FAILED: {
                broadcastManagedVerifyUpdate(new ManagedVerifyResponse(getPhoneNumberInput(), UserStatus.USER_FAILED, null, false));

                displayToast(getResources().getString(R.string.nexmo_verify_dialog_message_failed));
                finish();

            }
            default:
            {
                broadcastManagedVerifyUpdate(new ManagedVerifyResponse(getPhoneNumberInput(), userStatus, null, false));
                break;
            }
        }
    }

    @Override
    public void handleNetworkException(IOException exception) {
        // Notify VerifyListener on the exception.
        broadcastManagedVerifyUpdate(new ManagedVerifyResponse(getPhoneNumberInput(), null, null, true));
    }

    private void setup() {
        this.countries = new CountryList(this);
        CountryAdapter adapter = new CountryAdapter(this, android.R.layout.simple_spinner_item, this.countries);
        this.countryCodeSpinner = (Spinner) findViewById(R.id.country_name_sp);
        this.countryPrefixText = (TextView) findViewById(R.id.country_prefix_tv);
        this.phoneNumberEditText = (EditText) findViewById(R.id.phone_number_et);
        final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromInputMethod(this.phoneNumberEditText.getWindowToken(), 0);
        this.countryCodeSpinner.setAdapter(adapter);
        this.countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != AdapterView.INVALID_POSITION)
                    countryPrefixText.setText("+" + countries.getPrefix(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                countryPrefixText.setText(null);
            }
        });
    }

    public void onSendCode(View view) {
        // Trigger verify with provided input and jump to the next view when UserStatus is PENDING
        String phoneNumber = getPhoneNumberInput();
        if(phoneNumber.length() >= Defaults.MIN_PHONE_NUMBER_LENGTH && phoneNumber.length() <= Defaults.MAX_PHONE_NUMBER_LENGTH)
            triggerVerify();
        else
            phoneNumberEditText.setError(getResources().getString(R.string.nexmo_verify_invalid_number));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_TO_CHECK_REQUEST_CODE) {
            switch(resultCode) {
                case TRY_AGAIN_RESULT_CODE: {
                    triggerVerify();
                    break;
                }
                case VERIFIED_RESULT_CODE: {
                    handleUserStateChanged(UserStatus.USER_VERIFIED);
                    finish();
                    break;
                }
                case FAILED_RESULT_CODE: {
                    handleUserStateChanged(UserStatus.USER_FAILED);
                    finish();
                    break;
                }
                case CANCEL_FAILED_RESULT_CODE: {
                    displayToast(getResources().getString(R.string.nexmo_verify_dialog_message_cancel_failed));
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * Pre-fill country code and phone number when possible.
     * Individual app permissions are taken into consideration.
     */
    private void prefillInput() {
        this.countryCodeSpinner.setSelection(this.countries.getCountryCodePosition());
        this.phoneNumberEditText.setText(DeviceUtil.getPhoneNumber(this));
    }

    private void triggerVerify() {
        this.verifyServiceListener = new VerifyServiceListener(this);
        VerifyService verifyService = VerifyService.getInstance();
        verifyService.init(new VerifyRequest(getCountryCodeSelection(), getPhoneNumberInput()));
        verifyService.start(this.nexmoClient, this.verifyServiceListener);
    }

    private String getPhoneNumberInput() {
        Editable phoneNumberEdit = this.phoneNumberEditText.getText();
        return phoneNumberEdit.toString();
    }

    private String getCountryCodeSelection() {
        int spinnerIndex = this.countryCodeSpinner.getSelectedItemPosition();
        if (spinnerIndex != AdapterView.INVALID_POSITION)
            return this.countries.getCode(spinnerIndex);
        return null;
    }

    @Override
    protected void onStop(){
        this.verifyServiceListener = null;
        super.onStop();
    }

    private void launchView(Class<CheckPhoneNumberActivity> activityClass) {
        Context appContext = this.nexmoClient.getContext();
        Intent checkCodeIntent = new Intent(appContext, activityClass);
        Bundle verifyRequestBundle = new Bundle();
        verifyRequestBundle.putParcelable(VerifyRequest.class.getSimpleName(), new VerifyRequest(getCountryCodeSelection(), getPhoneNumberInput()));
        verifyRequestBundle.putParcelable(NexmoClient.class.getSimpleName(), this.nexmoClient);
        checkCodeIntent.putExtras(verifyRequestBundle);
        startActivityForResult(checkCodeIntent, GO_TO_CHECK_REQUEST_CODE);
    }

}
