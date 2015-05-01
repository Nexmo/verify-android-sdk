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

import java.util.HashSet;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nexmo.sdk.NexmoClient;

import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.core.event.ServiceListener;
import com.nexmo.sdk.verify.core.response.CheckResponse;
import com.nexmo.sdk.verify.core.response.TokenResponse;
import com.nexmo.sdk.verify.core.response.VerifyResponse;
import com.nexmo.sdk.verify.core.service.CheckService;
import com.nexmo.sdk.verify.core.service.TokenService;
import com.nexmo.sdk.verify.core.service.VerifyService;
import com.nexmo.sdk.verify.core.request.VerifyRequest;
import com.nexmo.sdk.core.user.UserStatus;

import com.nexmo.sdk.verify.event.VerifyClientListener;
import com.nexmo.sdk.verify.event.VerifyError;

import com.nexmo.sdk.util.DeviceUtil;
import com.nexmo.sdk.BuildConfig;

/**
 * The {@link com.nexmo.sdk.verify.client.VerifyClient} provides the entry point to verification flow provided by the Nexmo SDK.
 * <p> First step is to acquire a {@link VerifyClient} instance based on a built {@link NexmoClient} object.
 *  Remember to set a {@link VerifyClientListener} to receive status change notifications.
 *  <p> After a new {@link VerifyClient} is created the verification can be initiated by calling {@link VerifyClient#getVerifiedUser(String, String)}.
 * <p> Example usage:
 * <pre>
 *     VerifyClient myVerifyClient = new VerifyClient(myNexmoClient);
 *     myVerifyClient.addVerifyListener(this);
 *     myVerifyClient.getVerifiedUser(myCountryCode, myPhoneNo);
 * </pre>
 * <p> Anytime the PIN code has been received by the end user, it should be supplied to the verify client:
 * <pre>
 *     myVerifyClient.checkPinCode("pinCode");
 * </pre>
 *  A successful verification will be completed once the {@link VerifyClientListener#onUserVerified(VerifyClient)} event is invoked.
 */
public class VerifyClient {

    public static final String TAG = VerifyClient.class.getSimpleName();
    private NexmoClient nexmoClient;
    private VerifyRequest verifyRequest = new VerifyRequest();
    private HashSet<VerifyClientListener> verifyClientListeners;
    private ServiceListener<TokenResponse> tokenServiceListener;
    private ServiceListener<VerifyResponse> verifyServiceListener;
    private ServiceListener<CheckResponse> checkServiceListener;

    /**
     * Acquire a new {@link VerifyClient} instance.
     * The {@link VerifyClient} object provides the entry point to verification flow provided by the Nexmo SDK.
     * @param nexmoClient The {@link NexmoClient NexmoClient} is the Nexmo SDK entry point.
     */
    public VerifyClient(final NexmoClient nexmoClient) {
        this.verifyClientListeners = new HashSet<>();
        this.nexmoClient = nexmoClient;
        setupClientListeners();
    }

    /**
     * Adds a {@link VerifyClientListener} listener that handles events from the VerifyClient.
     *
     * @param verifyClientListener A verify client listener.
     */
    public void addVerifyListener(VerifyClientListener verifyClientListener) {
        this.verifyClientListeners.add(verifyClientListener);
    }

    /**
     * Remove a {@link VerifyClientListener} istener from receiving verify events.
     *
     * @param verifyClientListener A verify client listener.
     * @return {@code true} if the object was removed, {@code false} otherwise.
     */
    public boolean removeVerifyListener(VerifyClientListener verifyClientListener) {
        return this.verifyClientListeners.remove(verifyClientListener);
    }

    /**
     * Remove all verify client listeners associated to this {@link VerifyClient} instance.
     */
    public void removeVerifyListeners() {
        this.verifyClientListeners.clear();
    }

    /**
     * Verify the user of the current handset, with the handset SIM details.
     * <p> Verification with no supplied phone number along with country code is not permitted for SIM less handsets.
     * <p>  In this case, the end user does not need to enter their own phone number, as they will be automatically read from the SIM card.
     *
     * <p> Note: This method will only succeed if the current handset does contain a SIM card.
     * Otherwise, please call {@link VerifyClient#getVerifiedUser(String, String)} with values provided by the user.
     */
    public void getVerifiedUser() {
        Context appContext = this.nexmoClient.getContext();

        if (DeviceUtil.isSIMAvailable(appContext))
            getVerifiedUser(DeviceUtil.getCountryCode(appContext), DeviceUtil.getPhoneNumber(appContext));
        else {
            warnIfMissingListener();
            if (BuildConfig.DEBUG)
                Log.d(TAG, "SIM card cannot be read. Please use the VerifyClient.getVerifiedUser method and supply params " +
                            "for phone number and country code for the verification to be initiated.");
            notifyErrorListeners(VerifyError.NUMBER_REQUIRED);
        }
    }

    /**
     * Verify the user of the current handset, with the provided country code and phone number.
     * Retrieve a verified user object for the current handset.
     * <p> Verification is not permitted on a handset with different phone number than the one provided.
     * <p> In the case of a transient (typically network related) error an InternalNetworkException will be thrown.
     * <p> Listen to {@link VerifyClientListener} events to be notified of user verify state, as well as error events.
     *
     * <p> Note: This method will only succeed if the current handset does contain a SIM card and the provided parameters can be read and match the SIM details.
     *
     * @param countryCode The country code of the current SIM card.
     * @param phoneNumber The phone number of the current handset. Only mobile numbers are accepted.
     */
    public void getVerifiedUser(final String countryCode, final String phoneNumber) {
        warnIfMissingListener();

        if (this.verifyRequest.getUserStatus() == UserStatus.USER_PENDING) {
            notifyErrorListeners(VerifyError.VERIFICATION_ALREADY_STARTED);
        }
        else if (DeviceUtil.isNumberMatchingSIMCard(this.nexmoClient.getContext(), countryCode, phoneNumber)) {
            updateVerifyRequest(countryCode, phoneNumber);
            TokenService.getInstance().start(this.nexmoClient, null, this.tokenServiceListener);
        }
        else {
            // If handset contains a SIM, do not allow any other phone number values.
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Supplied phone number and country code do not match the device SIM card." +
                            "Please use getVerifiedUser method without supplying params.");
            notifyErrorListeners(VerifyError.PROVIDED_NUMBER_NOT_ACCEPTED);
        }
    }

    /**
     * Check verification enables you to check whether the PIN code the ned user has provided matches
     * the one Nexmo has sent.
     * <p> Validation on code length is made prior to checking the code against the service generated one.
     *
     * @param pinCode PIN code your end user has provided into your application (min 4 digits).
     */
    public void checkPinCode(final String pinCode) {
        warnIfMissingListener();

        if (this.verifyRequest.isPinCheckAvailable()) {
            if (TextUtils.isEmpty(pinCode)) {
                // Any empty pin will not be stored, neither sent to the service.
                if(BuildConfig.DEBUG)
                    Log.d(TAG, "Supplied phone number has an invalid length. Verify cannot be initiated.");
                notifyErrorListeners(VerifyError.INVALID_PIN_CODE);
            }
            else{
                updateVerifyRequestPin(pinCode);
                CheckService.getInstance().start(this.nexmoClient, this.verifyRequest, this.checkServiceListener);
            }
        }
        else
            notifyErrorListeners(VerifyError.CANNOT_PERFORM_CHECK);
    }

    private void setupClientListeners(){
        this.tokenServiceListener = new ServiceListener<TokenResponse>() {
            @Override
            public void onResponse(TokenResponse response) {
                switch (response.getResultCode()) {
                    case ResultCodes.RESULT_CODE_OK: {
                        updateVerifyRequestToken(response.getToken());
                        // Verify flow can be initiated, or check can be invoked with the new token.
                        if(verifyRequest.getUserStatus() == UserStatus.USER_NEW)
                            VerifyService.getInstance().start(nexmoClient, verifyRequest, verifyServiceListener);
                        else if(verifyRequest.getUserStatus() == UserStatus.USER_PENDING)
                            CheckService.getInstance().start(nexmoClient, verifyRequest, checkServiceListener);
                        break;
                    }
                    default: {
                        handleErrorResult(response.getResultCode());
                        break;
                    }
                }
            }

            @Override
            public void onFail(VerifyError errorCode, String reasonMessage) {
                notifyErrorListeners(errorCode);
            }
        };
        this.verifyServiceListener = new ServiceListener<VerifyResponse>() {
            @Override
            public void onResponse(VerifyResponse response) {
                switch (response.getResultCode()) {
                    case ResultCodes.RESULT_CODE_OK:
                    case ResultCodes.VERIFICATION_RESTARTED:
                    case ResultCodes.VERIFICATION_EXPIRED_RESTARTED: {
                        UserStatus userStatus = response.getUserStatus();
                        updateVerifyRequestStatus(userStatus);
                        // Trigger onVerifyInProgress for the VerifyClientListener.
                        if (userStatus == UserStatus.USER_PENDING)
                            notifyUserStateChangedListeners(UserStatus.USER_PENDING);
                        else if (userStatus == UserStatus.USER_EXPIRED)
                            notifyErrorListeners(VerifyError.USER_EXPIRED);
                        else if (userStatus == UserStatus.USER_BLACKLISTED)
                            notifyErrorListeners(VerifyError.USER_BLACKLISTED);
                            // Trigger onUserVerified event if user was already verified.
                        else if(userStatus == UserStatus.USER_VERIFIED) {
                            initVerifyRequest();
                            notifyUserStateChangedListeners(UserStatus.USER_VERIFIED);
                        }
                        else
                            // Unknown user status.
                            notifyErrorListeners(VerifyError.INTERNAL_ERR);
                        break;
                    }
                    default: {
                        handleErrorResult(response.getResultCode());
                        break;
                    }
                }
            }

            @Override
            public void onFail(VerifyError errorCode, String reasonMessage) {
                notifyErrorListeners(errorCode);
            }
        };
        this.checkServiceListener = new ServiceListener<CheckResponse>() {
            @Override
            public void onResponse(CheckResponse response) {
                switch (response.getResultCode()) {
                    case ResultCodes.RESULT_CODE_OK: {
                        if (response.getUserStatus() == UserStatus.USER_VERIFIED) {
                            initVerifyRequest();
                            notifyUserStateChangedListeners(UserStatus.USER_VERIFIED);
                        }
                        break;
                    }
                    default: {
                        handleErrorResult(response.getResultCode());
                        break;
                    }
                }
            }

            @Override
            public void onFail(VerifyError errorCode, String reasonMessage) {
                notifyErrorListeners(errorCode);
            }
        };
    }

    private void initVerifyRequest() {
        synchronized(this){
            this.verifyRequest = new VerifyRequest();
        }
    }

    private void updateVerifyRequest(final String countryCode, final String phoneNo) {
        synchronized(this){
            this.verifyRequest = new VerifyRequest();
            this.verifyRequest.setCountryCode(countryCode);
            this.verifyRequest.setPhoneNumber(phoneNo);
        }
    }

    private void updateVerifyRequestStatus(final UserStatus userStatus){
        synchronized(this){
            this.verifyRequest.setUserStatus(userStatus);
        }
    }

    private void updateVerifyRequestPin(final String pinCode) {
        synchronized(this) {
            this.verifyRequest.setPinCode(pinCode);
        }
    }

    private void updateVerifyRequestExpiredToken() {
        synchronized(this) {
            this.verifyRequest.setToken(null);
        }
    }

    private void updateVerifyRequestToken(final String token){
        synchronized(this) {
            this.verifyRequest.setToken(token);
        }
    }

    private void handleErrorResult(final int resultCode) {
        switch(resultCode) {
            case ResultCodes.INVALID_NUMBER: {
                notifyErrorListeners(VerifyError.INVALID_NUMBER);
                break;
            }
            case ResultCodes.INVALID_CREDENTIALS: {
                notifyErrorListeners(VerifyError.INVALID_CREDENTIALS);
                break;
            }
            case ResultCodes.INVALID_CODE_TOO_MANY_TIMES: {
                notifyErrorListeners(VerifyError.INVALID_CODE_TOO_MANY_TIMES);
                break;
            }
            case ResultCodes.INVALID_TOKEN: {
                // Generate a new token, but keep the userStatus.
                // If token continues to expire the service will send back a throttled error.
                updateVerifyRequestExpiredToken();
                TokenService.getInstance().start(this.nexmoClient, null, this.tokenServiceListener);
                break;
            }
            case ResultCodes.INVALID_PIN_CODE:
            case ResultCodes.INVALID_CODE: {
                notifyErrorListeners(VerifyError.INVALID_PIN_CODE);
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
            case ResultCodes.CANNOT_PERFORM_CHECK: {
                notifyErrorListeners(VerifyError.CANNOT_PERFORM_CHECK);
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

    private void warnIfMissingListener() {
        if (this.verifyClientListeners.isEmpty() && BuildConfig.DEBUG)
            Log.d(TAG, "Warning: There is no VerifyClientListener in place. " +
                        "Please set it on this VerifyClient instance to be able to receive verify events.");
    }

    /**
     * Notify all verify listeners of an error.
     * @param errorCode The specified error code.
     */
    private void notifyErrorListeners(final VerifyError errorCode) {
        if (!this.verifyClientListeners.isEmpty())
            for (VerifyClientListener listener : this.verifyClientListeners)
                listener.onError(this, errorCode);
    }

    /**
     * Notify all listeners of a user status change.
     * @param userStatus The new user status.
     */
    private void notifyUserStateChangedListeners(final UserStatus userStatus) {
        if (!this.verifyClientListeners.isEmpty()) {
            if (userStatus == UserStatus.USER_VERIFIED)
                for (VerifyClientListener listener : this.verifyClientListeners)
                    listener.onUserVerified(this);
            else if (userStatus == UserStatus.USER_PENDING)
                for (VerifyClientListener listener : this.verifyClientListeners)
                    listener.onVerifyInProgress(this);
        }
    }

}
