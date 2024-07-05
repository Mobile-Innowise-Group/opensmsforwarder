# Open SMS Forwarder 
<h1><img width="20" height="20" src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/3ba6195c-4f30-4134-840b-8d736074b132"/> </h1> 
The application for forwarding incoming SMS messages by email or via SMS to your colleagues and friends.
You can create a set of forwarding rules that will act as a filter for all incoming messages. All SMS that will meet the forwarding rules will be redirected to a chosen reciever's phone or email.

# The idea 

We want to make it clear for users (who are here developers in fact) having the source code to see how the app works and there is no other things behind the scenes. Also no ads, no paid content:)

# Demo
<img height="500" src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/da1993c5-6f02-4404-a07b-5b75b35f66ef" />

<h1>Tech stack <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg" height="20" width="20" /> </h1>

App tech stack includes: 
- Language - 100% [Kotlin](https://kotlinlang.org/) <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" height="15" width="15" />
- Environment - [Android Studio](https://developer.android.com/studio) <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/androidstudio/androidstudio-original.svg" height="15" width="15" />
- Navigation - [Cicerone](https://github.com/terrakok/Cicerone)
- Database - [RoomDB](https://developer.android.com/training/data-storage/room) <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/8a25c569-e3d6-4d02-a5ca-d6399299c322" height="15" width="15" />
- Network - [Retrofit](https://github.com/square/retrofit)
- Dependency injection - [Hilt](https://dagger.dev/hilt/) <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/b5ef3f8a-0c11-4003-ae8e-e103a97cc640" height="15" width="15" />
- Testing - [Junit](https://junit.org/junit5/)/[Espresso](https://developer.android.com/training/testing/espresso) <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/323dd5d3-f5f2-4406-86fa-4488a1d95939" height="15" width="15" />
- Other - [Google OAuth](https://developers.google.com/identity/protocols/oauth2), [Firebase](https://firebase.google.com/) <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/9afadfb3-3b15-4559-a861-276f7c40e1d7" height="15" width="15" /> <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/d7eb8341-dd27-4405-9b6e-337f23282f82" height="15" width="15" />
<h1>Project Setup <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/cca63857-6b65-4999-854e-b14fcb2c14f6" height="20" width="20" /></h1>

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
- Generate debug key, name it "debug.jks" and put it in app/keystore/debug/ directory (https://developer.android.com/studio/publish/app-signing#generate-key)
- Sync and build the project (Don't bother if you gave any errors while building on this step:).
- Open app/keystore/debug/debug.properties file (was generated on previous step) and replace DEBUG_KEYSTORE_ALIAS, DEBUG_KEYSTORE_PASSWORD, DEBUG_KEY_PASSWORD with your actual values.
```
DEBUG_KEYSTORE_ALIAS=YOUR_VALUE
DEBUG_KEYSTORE_PASSWORD=YOUR_VALUE
DEBUG_KEY_PASSWORD=YOUR_VALUE
```
- run ./gradlew signingReport in terminal task and copy SHA-1 value of your debug key.
- Add the SHA-1 debug key to the projects setting in the Firebase Console (https://support.google.com/firebase/answer/9137403?hl=en)
- Go to Google Cloud Console, choose your project and enable **Gmail API**. (https://cloud.google.com/endpoints/docs/openapi/enable-api#console)
- Setup WebClient for Google Authentication and fill configure your OAuth Consent Screen **Note!: The Web Client is automatically generated throug the Firabase project - Web client (auto created by Google Service). No need to create a new one** (https://developers.google.com/identity/gsi/web/guides/get-google-api-clientid)
- 
- Open apikey.properties file and replace CLIENT_ID, CLIENT_SECRET, REDIRECT_URI values with your ones. Get this values from **Web client (auto created by Google Service)**
```
CLIENT_ID=YOUR_CLIENT_ID
CLIENT_SECRET=YOUR_CLIENT_SECRET
REDIRECT_URI=YOUR_REDIRECT_URI
```

- Build and launch the app :)

<h1>Licence <img src="https://github.com/Mobile-Innowise-Group/opensmsforwarder/assets/79689735/7485e9fb-1152-4918-955c-033f7cc949a5" height="20" width="20" /></h1>

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
