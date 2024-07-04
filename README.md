# Open SMS Forwarder 
<h1><img width="20" height="20" src="https://github.com/dmorskoi/smsforwarder/assets/133751754/864d9767-df82-4151-8d60-0863af526301"/> </h1> 
The application for forwarding incoming SMS messages by email or via SMS to your colleagues and friends.
You can create a set of forwarding rules that will act as a filter for all incoming messages. All SMS that will meet the forwarding rules will be redirected to a chosen reciever's phone or email.

# The idea 

We want to make it clear for users (who are developers in fact) to have the source code and see how the app works and there is no other play behind the scene. Also no Ads, no Fee:)


# Demo
<img height="500" src="https://github.com/dmorskoi/smsforwarder/assets/133751754/d45a319d-35e2-4748-a659-19602496e15b" />

<h1>Tech stack <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg" height="20" width="20" /> </h1>

App tech stack includes: 
- Language - 100% [Kotlin](https://kotlinlang.org/) <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" height="15" width="15" />
- Environment - [Android Studio](https://developer.android.com/studio) <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/androidstudio/androidstudio-original.svg" height="15" width="15" />
- Navigation - [Cicerone](https://github.com/terrakok/Cicerone)
- Database - [RoomDB](https://developer.android.com/training/data-storage/room) <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/9ce8ef5d-449b-4e32-9c41-35e78beb6594" height="15" width="15" />
- Network - [Retrofit](https://github.com/square/retrofit)
- Dependency injection - [Hilt](https://dagger.dev/hilt/) <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/db6d99a1-36a3-4f8b-979f-b4b513070d80" height="15" width="15" />
- Testing - [Junit](https://junit.org/junit5/)/[Espresso](https://developer.android.com/training/testing/espresso) <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/d4df3028-7bdd-48cd-a4e9-a1c973032938" height="15" width="15" />
- Other - [Google OAuth](https://developers.google.com/identity/protocols/oauth2), [Firebase](https://firebase.google.com/) <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/3bda77d3-b4ca-4a4e-a575-4316392d8653" height="15" width="15" /> <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/e05d9da6-5520-48a5-9866-02be4d2f86b0" height="15" width="15" />

<h1>Project Setup <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/b88da3f7-c34f-4552-acc4-97614e505098" height="20" width="20" /></h1>

- Download the OpenSmsForwarder source code

  by HTTPS
  ```
  git clone https://github.com/Mobile-Innowise-Group/opensmsforwarder.git
  ```
  or by SSH
  ```
  git clone git@github.com:Mobile-Innowise-Group/opensmsforwarder.git
  ```
- Create and setup the Firebase project for Android app (https://firebase.google.com/docs/android/setup, https://youtu.be/jbHfJpoOzkI?t=187).
- Enable Google as a sign-in method in the Firebase console (https://firebase.google.com/docs/auth/android/google-signin#before_you_begin):
  -- Open the Auth section
  -- Click on the **Sign in method** tab
  -- Enable the Google sign-in method
  -- Click Save
- Download the updated Firebase config file (google-services.json), which now contains the OAuth client information required for Google sign-in and put it in app/google-services.json.
- Generate debug key and put it in app/keystore/debug/ directory (https://developer.android.com/studio/publish/app-signing#generate-key)
- Sync and build the project (Don't bother if you gave any errors while building on this step:).
- Open app/keystore/debug/debug.properties file (was generated on previous step) and replace DEBUG_KEYSTORE_ALIAS, DEBUG_KEYSTORE_PASSWORD, DEBUG_KEY_PASSWORD with your actual values.
```
DEBUG_KEYSTORE_ALIAS=YOUR_VALUE
DEBUG_KEYSTORE_PASSWORD=YOUR_VALUE
DEBUG_KEY_PASSWORD=YOUR_VALUE
```
- run ./gradlew signingReport in terminal task and copy SHA-1 value of your debug key.
- Add the SHA-1 debug key to the projects setting in the Firebase Console (https://support.google.com/firebase/answer/9137403?hl=en)
- Enable **Gmail API** in the Google Cloud Console (https://cloud.google.com/endpoints/docs/openapi/enable-api#console)
- Setup WebClient for Google Authentication and fill configure your OAuth Consent Screen **Note!: The Web Client is automatically generated throug the Firabase project - Web client (auto created by Google Service). No need to create a new one** (https://developers.google.com/identity/gsi/web/guides/get-google-api-clientid)
- 
- Open apikey.properties file and replace CLIENT_ID, CLIENT_SECRET, REDIRECT_URI values with your ones. Get this values from **Web client (auto created by Google Service)**
```
CLIENT_ID=YOUR_CLIENT_ID
CLIENT_SECRET=YOUR_CLIENT_SECRET
REDIRECT_URI=YOUR_REDIRECT_URI
```

- Build and launch the app :)

<h1>Licence <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/d716eb12-9573-4809-88d5-ad0fd66f3c1c" height="20" width="20" /></h1>

```
MIT License

Copyright (c) 2019 Innowise Group

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.


THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
