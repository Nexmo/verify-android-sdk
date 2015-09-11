
# Advanced use of the Nexmo Verify SDK

The following sections explain how to use the advanced features in the Nexmo Verify SDK:

* <a href="#search">Search User Status</a>
* <a href="#cancel">Cancel verification</a>
* <a href="#trigger">Trigger the next verification event</a>
* <a href="#logout">Logout</a>

## Search User Status<a name="search"></a>

You use *getUserStatus* to retrieve a user's current verification status. *getUserStatus* takes into account the device it is being executed on. Only query the status of the user using the current (device ID, phone number) pair.

To search for status:

1. Add the following to your code:
  ```java
        verifyClient.getUserStatus("CountryPrefix", "PhoneNumber", new SearchListener() {
            @Override
            public void onUserStatus(UserStatus userStatus) {
                switch(userStatus){
                    case USER_PENDING:{
                        // Handle each userStatus accordingly.
                    }
                }
		// other user statuses can be found in the UserStatus class
            }

            @Override
            public void onError(VerifyError verifyError, String s) {
		// unable to get user status for given device + phone number pair
            }

            @Override
            public void onException(IOException e) {
            }
        });
  ```

## Cancel verification<a name="cancel"></a>

You can cancel an ongoing verification. For example, if a user no longer wishes to perform the verification. A cancelled verification will not send any further verification SMS or TTS requests to the device.

To cancel a verification request:

1. Call the `command` function  with `Command.TRIGGER_NEXT_EVENT` action:
  ```java
        verifyClient.command("CountryPrefix", "PhoneNumber", Command.LOGOUT, new CommandListener() {
            @Override
            public void onSuccess(Command command) {
		// verification request successfully cancelled
            }

            @Override
            public void onError(Command command, com.nexmo.sdk.verify.event.VerifyError verifyError, String s) {
		// something went wrong whilst attempting to cancel the current verification request
            }

            @Override
            public void onException(IOException e) {
            }
        });
```

## Trigger the next verification event<a name="trigger"></a>

Speed up the verification workflow by triggering the next workflow event early. For instance in an *SMS -> TTS -> TTS* workflow, at the SMS stage, call the *command* function with *Command.TRIGGER_NEXT_EVENT* action to trigger the *TTS* event.

To trigger the next event:

1. Add the following to your code:
  ```java
        verifyClient.command("CountryPrefix", "PhoneNumber", Command.TRIGGER_NEXT_EVENT, new CommandListener() {
            @Override
            public void onSuccess(Command command) {
		// successfully triggered next event
            }

            @Override
            public void onError(Command command, com.nexmo.sdk.verify.event.VerifyError verifyError, String s) {
		// unable to trigger next event
            }

            @Override
            public void onException(IOException e) {
            }
        });
  ```

### Logout<a name="logout"></a>

You can logout a user in order to reset their verification status. Subsequent verification requests will no longer return a *verified* response directly, they will execute the entire verification workflow again.

To logout a user
 
1. Call the `command` function with `Command.LOGOUT` action:
  ```java
        verifyClient.command("CountryPrefix", "PhoneNumber", Command.LOGOUT, new CommandListener() {
            @Override
            public void onSuccess(Command command) {
		 // successfully logged out user
            }

            @Override
            public void onError(Command command, com.nexmo.sdk.verify.event.VerifyError verifyError, String s) {
		// unable to logout user
            }

            @Override
            public void onException(IOException e) {
            }
        });
  ```
