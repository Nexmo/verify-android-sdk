Verify
========
	Nexmo Verify SDK(beta version) for Android.
Nexmo Verify enables you to verify whether one of your end users has access to a specific phone number by challenging them with
a PIN code to enter into your application or website. This PIN code is delivered by Nexmo via SMS and/or TTS (an automated Text
To Speech) call. Your end users then enter this code into your application and subsequently, you can check through Verify if 
the code entered matches the one which was sent to the user. This completes a phone verification successfully.

The Nexmo Verify SDK for Android enables you to build Verify into your Android Application by simplifying this integration.
If you import this library into your application, you simply need the user's phone number and the SDK will take care of the
various steps required to verify your users.

Download
========

Clone this repository and reference the local aar file.
In a file explorer (not Android Studio), drag the verify-1.0.0-beta.aar file into the /app/libs directory
in your project’s root directory.

In Android Studio, edit the build.gradle file in the app directory (it can also be labelled as Module: app)
and change its contents to the following:
```java
repositories {
    mavenCentral()
    flatDir {
        dirs 'libs'
    }
}
```
Then reference the library in the dependency section:
```java
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])

compile 'com.nexmo.sdk:verify-beta@aar'
```
Don’t forget to add the flatDir entry to the repositories section. Otherwise Gradle will not be able to locate the aar.

Verify Android SDK requires at minimum Android 2.1.

Getting Started
==============

1. You need a Nexmo account to use the SDK. Register for one (if you don't have it already) at:
   https://dashboard.nexmo.com/register
2. In order to safeguard your Nexmo credentials, all interaction between your application and Nexmo services requires an 
   Application Key and Shared Secret to be configured per application that you are building the SDK into.
   If you don't have one yet, please send us an email at: productfeedback@nexmo.com and we will set up an application ID and 
   a Shared Secret Key for your application.
   We're working to allow you to setup a new Application using the Dashboard where you can obtain an ApplicationId and 
   SharedSecretKey - Watch out for it in an upcoming update.
3. Download the Nexmo Verify SDK (as instructed above).

Creating a new Nexmo Client:
```java
import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.verify.client.VerifyClient;

try {
	NexmoClient nexmoClient = new NexmoClient.NexmoClientBuilder()
                    .context(applicationContext)
                    .applicationId(APPLICATION_ID) //your application key
                    .sharedSecretKey(SHARED_KEY) //your application secret
                    .build();
} catch (ClientBuilderException e) {
	e.printStackTrace();
}
```
Note: For the security of your Nexmo account, you should not use your Nexmo Account ID or Nexmo Account Secret anywhere in the 
code.

Now let's aquire a new Verify Client object that does all the verification magic.
```java
verifyClient = new VerifyClient(nexmoClient);
```

Remember to register for receiving verify status events, in case you want to update you application's UI accordingly.
```java
verifyClient.addVerifyListener(new VerifyClientListener() {
        @Override
        public void onVerifyInProgress(VerifyClient verifyClient) {
        }

        @Override
        public void onUserVerified(VerifyClient verifyClient) {
        }

        @Override
        public void onError(VerifyClient verifyClient, com.nexmo.sdk.verify.event.VerifyError errorCode) {
        }
    });
```
A new verification is initiated using a supplied country code and phone number.
```java
verifyClient.getVerifiedUser("GB", "07000000000");
```
Even if a user enters the phone number with the country code, the library will determine the correct internationalised 
phone number for use.

If you would like to have the library read the phone number from the SIM card in the phone, you can also use:
```java
verifyClient.getVerifiedUser();
```
However, please note that if the phone number cannot be read from the SIM, this method will result in an error. 
In a future iteration, you would be able to pass an international number directly as well (as opposed to country and local no.)

When the verification is successfully started VerifyClientListener.onVerifyInProgress(VerifyClient verifyClient) is invoked.

If the verification cannot be started VerifyClientListener.onError(VerifyClient verifyClient, VerifyError errorCode); is invoked describing the error.

Anytime the PIN code has been received by the end user, it should be supplied to the verify client:
```java
verifyClient.checkPinCode("1234");
```

A successful verification will be completed once the VerifyClientListener.onUserVerified(VerifyClient verifyClient) event is 
invoked.

Verify SDK maintains states of Verified users and will generate an SMS to verify a user if the user is unverified. By default,
users remain verified for 30 days. This will be customisable in a future version of the library to suit your needs - if a user 
should be reverified everytime, never, or a custom duration (other than 30 days) in between.

Please note that sensitive user details, like the phone number will NOT be stored locally at any time.

License
=======

Copyright (c) 2015 Nexmo, Inc.
All rights reserved.
Licensed only under the Nexmo Verify SDK License Agreement (the "License") located at

	https://www.nexmo.com/terms-use/verify-sdk/

By downloading or otherwise using our software or services, you acknowledge
that you have read, understand and agree to be bound by the 
[Nexmo Verify SDK License Agreement][1] and [Privacy Policy][2].
    
You may not use, exercise any rights with respect to or exploit this SDK,
or any modifications or derivative works thereof, except in accordance with the License.

 [1]: https://www.nexmo.com/terms-use/verify-sdk/
 [2]: https://www.nexmo.com/privacy-policy/
