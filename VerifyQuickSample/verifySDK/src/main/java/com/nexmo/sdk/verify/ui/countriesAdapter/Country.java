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

package com.nexmo.sdk.verify.ui.countriesAdapter;

/**
 * Country information.
 */
public class Country {

    private final String name;
    private final String code;
    private final String prefix;

    public Country(final String name, final String code, final String prefix) {
        this.name = name;
        this.code = code;
        this.prefix = prefix;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

}
