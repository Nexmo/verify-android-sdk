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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.R;
import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.core.config.Defaults;
import com.nexmo.sdk.verify.client.VerifyClient;
import com.nexmo.sdk.verify.core.event.BaseClientListener;
import com.nexmo.sdk.verify.core.event.CheckServiceListener;
import com.nexmo.sdk.verify.core.event.CommandServiceListener;
import com.nexmo.sdk.verify.core.request.CommandRequest;
import com.nexmo.sdk.verify.core.request.VerifyRequest;
import com.nexmo.sdk.verify.core.service.CheckService;
import com.nexmo.sdk.verify.core.service.CommandService;
import com.nexmo.sdk.verify.event.Command;
import com.nexmo.sdk.verify.event.CommandListener;
import com.nexmo.sdk.verify.event.UserStatus;
import com.nexmo.sdk.verify.event.VerifyError;
import com.nexmo.sdk.verify.ui.response.ManagedVerifyResponse;

import java.io.IOException;

/**
 * Custom Check oin code screen that is waiting for the user to fill-in the received code.
 * On Continue action, a check code is performed and result displayed in a toast.
 */
public class CheckPhoneNumberActivity extends VerifyBaseActivity implements BaseClientListener {

    public static final String TAG = CheckPhoneNumberActivity.class.getSimpleName();
    private TextView infoTextView;
    private EditText codeEditText;
    private Button continueButton;
    private Button cancelButton;
    private Button tryAgainButton;
    private Button callInsteadButton;
    private ProgressBar activity_indicator;
    private VerifyRequest verifyRequest;
    private boolean tryAgainInProgress = false;// try again is waiting for cancel to be ready.
    private NexmoClient nexmoClient;
    private BroadcastReceiver verifyClientBroadcastReceiver;
    // Internal listeners.
    private CheckServiceListener checkServiceListener;
    private CommandServiceListener commandServiceListener;
    private CommandListener commandListener = new CommandListener() {
        @Override
        public void onSuccess(Command command) {
            activity_indicator.setVisibility(View.INVISIBLE);
            // Command was completed, go back if Canceled
            switch (command) {
                case CANCEL:{
                    if (tryAgainInProgress) {
                        tryAgainInProgress = false;
                        setResult(TRY_AGAIN_RESULT_CODE);
                        finish();
                    }
                    else {
                        displayToast(getResources().getString(R.string.nexmo_verify_dialog_message_canceled));
                        setResult(CANCELED_RESULT_CODE);
                        finish();
                    }
                    break;
                }
                case TRIGGER_NEXT_EVENT: {
                    // if call me instead display a different status info.
                    infoTextView.setText(String.format(getResources().getString(R.string.nexmo_tts_code_info), verifyRequest.getPhoneNumber()));
                    break;
                }
                default: {
                    break;
                }
            }
        }

        @Override
        public void onError(Command command, VerifyError errorCode, String errorMessage) {
            Log.d(TAG, "Command " + command.toString() + " cannot be performed. errorCode: " + errorCode.name());
            activity_indicator.setVisibility(View.INVISIBLE);
            if (tryAgainInProgress)
                tryAgainInProgress = false;
            if (errorCode == VerifyError.COMMAND_NOT_SUPPORTED || errorCode == VerifyError.INVALID_USER_STATUS_FOR_COMMAND) {
                if (command == Command.CANCEL) {
                    setResult(CANCEL_FAILED_RESULT_CODE);
                    finish();
                }
            }
            // externally broadcasting command events is not needed at this point.
        }

        @Override
        public void onException(IOException exception) {
            Log.d(TAG, "Command " + " onException: " + exception.toString());
            activity_indicator.setVisibility(View.INVISIBLE);
            if (tryAgainInProgress)
                tryAgainInProgress = false;
            // Notify VerifyListener on the exception.
            broadcastManagedVerifyUpdate(new ManagedVerifyResponse(verifyRequest.getPhoneNumber(), null, null, true));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nexmo_check_phone_number);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.verifyRequest = extras.getParcelable(VerifyRequest.class.getSimpleName());
            NexmoClient parcelable = extras.getParcelable(NexmoClient.class.getSimpleName());
            if(parcelable != null) {
                try {
                    this.nexmoClient = new NexmoClient.NexmoClientBuilder()
                            .context(getApplicationContext())
                            .applicationId(parcelable.getApplicationId())
                            .sharedSecretKey(parcelable.getSharedSecretKey())
                            .environmentHost(parcelable.getEnvironmentHost())
                            .build();
                } catch (ClientBuilderException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        setup();
    }

    @Override
    public void onResume() {
        super.onResume();
        setup();
    }

    @Override
    public void handleErrorResult(int resultCode) {
        this.activity_indicator.setVisibility(View.INVISIBLE);
        switch(resultCode) {
            case ResultCodes.INVALID_PIN_CODE:
            case ResultCodes.INVALID_CODE: {
                codeEditText.setError(getResources().getString(R.string.nexmo_check_pin_error));
                notifyErrorListeners(VerifyError.INVALID_PIN_CODE);
                break;
            }
            case ResultCodes.INVALID_CODE_TOO_MANY_TIMES:
            case ResultCodes.CANNOT_PERFORM_CHECK: {
                codeEditText.setError(getResources().getString(R.string.nexmo_check_pin_user_failed));
                setResult(FAILED_RESULT_CODE, new Intent());
                finish();
                break;
            }
            default: {
                handleUserStateChanged(UserStatus.USER_FAILED);
                break;
            }
        }
    }

    @Override
    public void notifyErrorListeners(com.nexmo.sdk.verify.event.VerifyError verifyError) {
        Log.d(TAG, "notifyErrorListeners " + verifyError.toString());
        broadcastManagedVerifyUpdate(new ManagedVerifyResponse(this.verifyRequest.getPhoneNumber(), this.verifyRequest.getUserStatus(), verifyError, false));
    }

    @Override
    public void handleUserStateChanged(UserStatus userStatus) {
        Log.d(TAG, "handleUserStateChanged " + userStatus.toString());
        this.activity_indicator.setVisibility(View.INVISIBLE);
        switch(userStatus) {
            case USER_VERIFIED:{
                broadcastManagedVerifyUpdate(new ManagedVerifyResponse(this.verifyRequest.getPhoneNumber(), UserStatus.USER_VERIFIED, null, false));
                setResult(VERIFIED_RESULT_CODE);
                finish();
                break;
            }
            default:
            {
                // any other error: update status.
                broadcastManagedVerifyUpdate(new ManagedVerifyResponse(this.verifyRequest.getPhoneNumber(), userStatus, null, false));
                break;
            }
        }
    }

    @Override
    public void handleNetworkException(IOException exception) {
        Log.d(TAG, "handleNetworkException " + exception.toString());
        this.activity_indicator.setVisibility(View.INVISIBLE);
        // Notify VerifyListener on the exception.
        broadcastManagedVerifyUpdate(new ManagedVerifyResponse(this.verifyRequest.getPhoneNumber(), null, null, true));
    }

    @Override
    public void onCancelVerification(View view) {
        this.activity_indicator.setVisibility(View.VISIBLE);
        // trigger cancel command, and dismiss this activity only when successful.
        CommandServiceListener commandServiceListener = new CommandServiceListener(
                Command.CANCEL, this.commandListener, this);
        CommandService service = CommandService.getInstance();
        service.init(new CommandRequest(this.verifyRequest.getCountryCode(), this.verifyRequest.getPhoneNumber(), Command.CANCEL));
        service.start(this.nexmoClient, commandServiceListener);
    }

    public void onTryAgain(View view) {
        this.tryAgainButton.setVisibility(View.GONE);
       // cancel pending verification and issue a new verify.
        this.tryAgainInProgress = true;
        onCancelVerification(view);
    }

    public void onCallInstead(View view) {
        this.activity_indicator.setVisibility(View.VISIBLE);

        CommandServiceListener commandServiceListener = new CommandServiceListener(
                Command.TRIGGER_NEXT_EVENT, this.commandListener, this);
        CommandService service = CommandService.getInstance();
        service.init(new CommandRequest(this.verifyRequest.getCountryCode(), this.verifyRequest.getPhoneNumber(), Command.TRIGGER_NEXT_EVENT));
        service.start(this.nexmoClient, commandServiceListener);
    }

    public void onCheck(View view){
        String codeInput = getCodeInput();
        if (TextUtils.isEmpty(codeInput) || codeInput.length() < Defaults.MIN_CODE_LENGTH)
            codeEditText.setError(getResources().getString(R.string.nexmo_check_pin_too_short));
        else if (codeInput.length() > Defaults.MAX_CODE_LENGTH)
            codeEditText.setError(getResources().getString(R.string.nexmo_check_pin_too_long));
        else
            triggerCheck();
    }

    private void setup(){
        this.infoTextView = (TextView) findViewById(R.id.check_code_info_tv);
        this.codeEditText = (EditText) findViewById(R.id.phone_number_et);
        this.codeEditText.setText(null);
        this.continueButton = (Button) findViewById(R.id.continue_check_btn);
        this.activity_indicator = (ProgressBar) findViewById(R.id.action_progress);
        this.activity_indicator.setVisibility(View.INVISIBLE);
        this.activity_indicator.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.nexmo_default_background), PorterDuff.Mode.SRC_IN);
        this.infoTextView.setText(String.format(getResources().getString(R.string.nexmo_sms_code_info), this.verifyRequest.getPhoneNumber()));
        setupCommands();
    }

    private void hideAllButtons() {
        continueButton.setVisibility(View.GONE);
        hideCommandButtons();
    }

    private void hideCommandButtons() {
        cancelButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        callInsteadButton.setVisibility(View.GONE);
    }

    /**
     * Hide the cancel/try another sms/call instead buttons until after 30seconds the verification has started.
     */
    private void setupCommands() {
        this.cancelButton = (Button) findViewById(R.id.cancel_btn);
        this.tryAgainButton = (Button) findViewById(R.id.try_again_btn);
        this.callInsteadButton = (Button) findViewById(R.id.call_instead_btn);
        this.verifyClientBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // command timer finished
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(VerifyClient.MESSAGE_KEY_TIMER_STATE_DONE)) {
                    cancelButton.setVisibility(View.VISIBLE);
                    callInsteadButton.setVisibility(View.VISIBLE);
                    tryAgainButton.setVisibility(View.VISIBLE);
                }
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(this.verifyClientBroadcastReceiver, new IntentFilter(VerifyClient.ACTION_BROADCAST_COMMAND_TIMER));
    }

    private String getCodeInput() {
        return this.codeEditText.getText().toString();
    }

    private void triggerCheck(){
        this.activity_indicator.setVisibility(View.VISIBLE);
        synchronized(this) {
            this.verifyRequest.setPinCode(getCodeInput());
        }
        this.checkServiceListener = new CheckServiceListener(this);
        CheckService checkService = CheckService.getInstance();
        checkService.init(this.verifyRequest);
        checkService.start(this.nexmoClient, this.checkServiceListener);
    }

}
