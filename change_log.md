Verify 3.0.0
========
9 September 2016

Fixes:
Deleted getPhoneNumber() from DeviceUtil
Deleted isSimAvailable() from DeviceUtil
Deleted isNumberMatchingSimCard() from DeviceUtil
Deleted prefillInput()  method from VerifyPhoneNumberActivity
Deleted depreciated method getVerifiedUser() in VerifyClient


Verify 2.0.0
========
4 August 2016

Integration changes: permissions have to be added in the host app rather than the SDK itself.
Verify SDK requires special permissions in order to access the device's id, internet and network connection.
For integrating Verify SDK you have to edit your application's Manifest and add the items listed below:
java
<!-- Automatically granted permissions PROTECTION_NORMAL -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<!-- Permission has to be GRANTED -->
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```

The SDK handles runtime permissions, so applications running Marshmallow will failover gracefully if the end user denies permissions.

Verify 1.2.0
========
3 August 2016

Added support for Marshmallow: runtime permission checks.


Stable release: Verify 1.1.0
========
25 July 2016

Fixes:
Upgraded build tools.
Newer versions of play services will not cause conflict anymore.

VerifyBeta
========
5 May 2015

Initial release


VerifyBeta 0.2
========
6 July 2015

- getUserStatus is added to retrieve real-time user status. It enables you to get the user status even if a verification is still in progress.
Usage:
```java
myVerifyClient.isUserVerified(myCountryCode, myPhoneNo, new SearchListener() {
@Override
public void onUserStatus(final UserStatus userStatus) {
// Update the application UI here if needed.
}
@Override
public void onError(final com.nexmo.sdk.verify.event.VerifyError errorCode, final String errorMessage) {
// Update the application UI here if needed.
}
@Override
public void onException(IOException exception) {
// Update the application UI here if needed. Most probably there is a network connectivity exception.
}
}
```

- commands are implemented: CANCEL/LOGOUT/TRIGGER_NEXT_EVENT
Usage:
```java
myVerifyClient.command(myCountryCode, myPhoneNo, Command.LOGOUT, new CommandListener() {
@Override
public void onSuccess(Command command) {
// Update the application UI here if needed.
}
@Override
public void onError(final com.nexmo.sdk.verify.event.VerifyError errorCode, final String errorMessage) {
// Update the application UI here if needed.
}
@Override
public void onException(IOException exception) {
// Update the application UI here if needed. Most probably there is a network connectivity exception.
}
}
```

- Sample application provided: VerifySample is available.

- added IP address support for cellular networks as well, unless it's the loopback address.

- deprecated VerifyClient#getVerifiedUser() that allowed automatic phone number retrieve, as TelephonyManager is not always reliable in geeting a valid phone number.

- added network exception callback for each request. Callback: onException(IOException exception)

- phone number supplied is not checked anymore against the SIM's card phone number, as the method is not reliable, especially on CDMA phones or ported numbers. Therefore, the SDK can now be used even when the user does not supply the same phone number as the one on the device/handset.


Verify Beta 0.3
========
19 August 2015

- added push notification integration for pin codes via GCM. The GCM registration token is set via the NexmoClient builder and a separate setter.
The SDK does a silent check for the push, parses the notifications and automatically triggers a check request with the received pin code.
Usage:
```java
nexmoClient.setGcmRegistrationToken("YourGcmRegistrationToken");
```

- updated error codes.


Verify Beta 0.4
========
11 September 2015

- updated JSon service response formatting to allow indentation.

- updated the gcm dependency from 'com.google.android.gms:play-services-gcm:7.5.+' to: 'com.google.android.gms:play-services-gcm:7.8.0'

- split up the documentation on layers.


Verify Beta 0.5
========
24 September 2015

- Use maven dependency for gson instead of local lib.
- Update gradle wrapper and plugin to newest versions.
- Retrieve a reliable DeviceProperties.getDeviceId even for devices with no Telephony services.


Verify Beta 0.6
========
8 October 2015

- Published verify:1.0.0-SNAPSHOT to maven central.
- Updated the samples to point to the external dependency, and moved away from using internal aar in flatDir.


Verify 1.0
========
11 November 2015

- Published verify:1.0.1-SNAPSHOT
- Created a new sample VerifyQuickSample that demonstrates the new VerifyClient.getgetVerifiedUserFromDefaultManagedUI() feature.
- Added standalone vertifications via VerifyClient.verifyStandalone
