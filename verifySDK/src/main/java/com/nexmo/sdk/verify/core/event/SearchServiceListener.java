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

import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.core.event.ServiceListener;

import com.nexmo.sdk.verify.core.response.SearchResponse;
import com.nexmo.sdk.verify.core.service.SearchService;

import com.nexmo.sdk.verify.event.*;

import java.io.IOException;

/**
 * SDK internal network response callback for Search responses.
 * Encapsulates the public listener {@link com.nexmo.sdk.core.event.ServiceListener} as well.
 */
public class SearchServiceListener extends ServiceListener<SearchResponse> {

    private final SearchListener searchListener;

    public SearchServiceListener(final SearchListener listener,
                                 final BaseClientListener clientListener) {
        super(clientListener);
        this.searchListener = listener;
    }

    /**
     * Get the public listener attached to {@link com.nexmo.sdk.verify.client.VerifyClient#getUserStatus}
     * @return The listener.
     */
    public SearchListener getSearchListener(){
        return this.searchListener;
    }

    @Override
    public void onResponse(final SearchResponse response) {
        switch (response.getResultCode()) {
            case ResultCodes.RESULT_CODE_OK: {
                searchListener.onUserStatus(response.getUserStatus());
                break;
            }
            case ResultCodes.INVALID_TOKEN: {
                // Restart search and issue a new token request.
                SearchService.getInstance().start(SearchService.getInstance().getNexmoClient(), this);
                break;
            }
            default: {
                searchListener.onError(formatResultCode(response.getResultCode()),
                                                        response.getResultMessage());
                break;
            }
        }
    }

    @Override
    public void onFail(final com.nexmo.sdk.verify.event.VerifyError errorCode,
                       final String reasonMessage) {
        searchListener.onError(errorCode, reasonMessage);
    }

    /**
     * A request was timed out because of network connectivity exception.
     * Triggered in case of network error, such as UnknownHostException or SocketTimeout exception.
     *
     * @param exception The exception.
     */
    @Override
    public void onException(final IOException exception) {
        searchListener.onException(exception);
    }

}
