# Android Storage Use cases

An android app that demonstrates some of the important storage use cases in android and how to handle these cases for different versions of Android

## Why?

I have always had few doubts on these basic storage use cases like writing a file to a shared storage 
or create an app-specific file, etc. So I thought of implementing the storage use cases which we generally 
use in our day-to-day development work and build it as an simple app. So that in future we can refer to them. 
**The main objective is these should work even if the scoped storage is enabled or not**.

## Storage Use cases

* App specific Internal Storage
  * CRUD File Operations
  * CRUD Media File Operations
* Shared Storage Media file operations (Other apps can access, and it will stay in the device even if we uninstall the app)
  * Creating a Media file in Shared Storage
  * Accessing Media Files created by other apps
  * Modifying Media files created by other apps
  * Delete Media files created by other apps
* Shared storage Non-Media file operations (Storage Access Framework)
  * CRUD Operations in files owned by our apps
  * CRUD Operations in files owned by other apps


> It's highly recommend to read the [android official documentation](https://developer.android.com/training/data-storage) once
> before referring this project.

All these will work in devices in which scoped storage is enabled or not.

## How to build and run this app?

### Prerequisites

* [Java - 17 or higher](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Android Studio](https://developer.android.com/studio)

Once you have the these prerequisites you can build the app

``./gradlew assembleDebug``

and use android studio to launch the app to emulator or physical device
