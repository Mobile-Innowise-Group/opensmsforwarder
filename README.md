# Open SMS Forwarder
An <b> Open Source </b> app that allows to forward your phone messages to friends and coworkers forwarding it through phone sms or email. You can create rule if will be applied to message, will redirect it to chosen phone number or email (you choose sender/receiver emails).

# Demo
<img height="500" src="https://github.com/dmorskoi/smsforwarder/assets/133751754/d45a319d-35e2-4748-a659-19602496e15b" />

<h1>Google play <img width="20" height="20" src="https://github.com/dmorskoi/smsforwarder/assets/133751754/864d9767-df82-4151-8d60-0863af526301"/> </h1> 

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

You will require Android Studio Jellyfish | 2023.3.1, Java version 17

Steps to succesfully build the app:
- Download the SmsForwarder source code from https://github.com/dmorskoi/smsforwarder ( git clone https://github.com/dmorskoi/smsforwarder.git )
- Sync gradle and download needed dependencies
- Build the project (gradle job generate_apikey_properties.gradle will generate \smsforwarder\apikey.properties)
- Go to \smsforwarder\apikey.properties and initiate fields CLIENT_ID, CLIENT_SECRET, REDIRECT_URI with your values 

```
#Replace apikey variables with your actual values. Don't commit this file to source control.
#Tue Jun 25 13:12:22 CEST 2024
CLIENT_ID=YOUR_CLIENT_ID
CLIENT_SECRET=YOUR_CLIENT_SECRET
REDIRECT_URI=YOUR_REDIRECT_URI
```

- Build and launch the project :)

<h1>Google and firebase console settings <img src="https://github.com/dmorskoi/smsforwarder/assets/133751754/643d4b0b-be6a-473d-82cc-bd6024705957" height="20" width="20" /></h1

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
