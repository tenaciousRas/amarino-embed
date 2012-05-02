Overview
--------
This is an embeddable version of the Amarino library (http://www.amarino-toolkit.net/).  Amarino is originally built to be installed as an Android application which other Android apps depend on.  While this is an acceptable practice (for ex:  Google Navigator does it), this isn't always a pattern that works well for application developers.

Amarino is a natural concept that evolves from the Intent based platform in Android.  It has a simple RPC-like frame-buffer implementation that allows it to execute callbacks on an Arduino connected to a bluetooth module (such as SparkFun's BlueSmirf).  It's easy to use too.

This API decouples the Amarino Service architecture from its application, allowing you to embed Amarino in your own applications.  To keep things simple, and in cases where you intend to use Amarino as a shared application, the old behavior is supported.

Another important change in the API is support for custom MessageBuilder implementations.  This allows you to implement new protocols to talk to Arduino.  Maybe you want to use Google's Protocol Buffers to talk to Arudino.  Maybe you want to send binary data, or BSON over the wireless wire.  For that, write a custom MessageBuilder implementation and set that in your embedded service (example needed).

This project is considered relatively stable, but so many changes from the original project were needed that it can only be considered relatively stable.  For example, the Amarino APK is known to crash, and that hasn't been fixed here (yet).  Please excuse any issues and help contribute to the project -- if you have time to help write code let me know I'd love some help!

Several of the examples in this project are from the original Amarino codebase, modified for the Amarino Library project.  Two examples are provided that use the embedded service:  HelloAmarinoWorld and RGBLEDPickers.  HelloAmarinoWorld provides a simple working example that only requires an Arduino with an Amarino-compatible bluetooth module.  The RGBLEDPickers example is similar but requires a small working circuit built around the Arduino.  RGBLEDPickers works with an embedded Amarino service and uses the Android compatibility library (fragments).

ANNOUNCEMENT
------------
Major refactoring to this project is coming!  This project is originally a fork of the Amarino project, which itself is a wrapper around the 'Gerdavax' Bluetooth API for Android.  After many hours of work and fairly serious usage it's morphed into a fully-embeddable Android service.

Let's face it.  Amarino isn't exactly easy to use.  Honestly, if you're just getting started with Arduino and want to build things with bluetooth you should check out the kickstarter project Ard'Vark.  http://www.kickstarter.com/projects/694850905/the-ard-vark  It will probably be less confusing for you than Amarino, and you might get more done.

That's why I've decided to rebuild this code.  The concept of using an Android OpenIntent approach to expose bluetooth connectivity will stay, and the concept of having a side-by-side app for this will remain.  Just about everything else will change.  After evaluating the codebase fairly thoroughly the following roadmap is planned:
1)  Remove the Gerdavax library and rebuid on the native (SDK) API.  Gerdavax, the base API for Amarino, is unfinished and no longer active, and has more robust cousins in the official SDK.  The prime motivation to choose this library may be to support Android OS 1.1-1.5.  That motivation has since evatporated.  Thus, no support is planned for 1.1 and 1.5; 1.6 and higher will be supported.
2)  Rewrite the Amarino service and give it a new name.   Observe this project's differences with Amarino today - the desired pattern is already established - just needs to be refined and cleaned.  The code has already deviated significantly and is backwards compatible, as it should be.
3)  Build a new Arduino sketch based on (either JSON or BSON) as a protocol.  Yes, the current protocol is so-very-thin, but it's brittle and breaks easily.
3a)  Support non-standard pin assignment.
3b)  Non-blocking reads.
3c)  Investigate a roadmap that would deliver support for BT modules connected to Arduino in master mode.
4)  Port Arduino sketch for PIC support.

All of that is planned for 2012 - with no announced dates - because right now it's just me taking it on.  Would you like to help?  Any serious volunteers are welcome, even if you only have a few hours per year!

Branches
--------
- vendor => original Amarino branch.  The jump-point for merging changes from the Google Code source branch.
- dev-* => dev branch
- trunk => latest stable branch (coming when top priority bugs resolved and backwards-compat tested)

Getting Started
---------------
This Github Wiki provides (basic) documentation, and the HelloAmarinoWorld and RGBLEDPickers projects provide working examples.  Also, there's a source JAR in Amarino/dist, and a "src-dist" ANT target in the Amarino project that builds the sources JAR.  The basic steps are as follows:
- You'll need to reference the Amarino project as a library project.  In Eclipse this means following the usual steps to import an Android Library project.
- You'll need to add a the AndroidBluetooth.JAR file found in Amarino/libs as a JAR on the build path of your project.
- The next step is to create an implementation for the AmarinoServiceIntentConfig interface, providing (Intent) namespaces that work for your app.
- The next step is to subclass AmarinoService with your implementation that injects your AmarinoServiceIntentConfig implementation (above step).
- The next step is to setup an intent receiver in your Activity that handles the broadcast Amarino service intents (defined in AmarinoServiceIntentConfig).  See the HelloAmarinoWorld or RGBLEDPickers examples.
- You'll need to modify your AndroidManifest to define your service class and allow for Bluetooth permissions.
- The final step is to connect and communicate with the Arduino hardware in your Activity.
- Having problems.  Try the Google Group for support at https://groups.google.com/forum/#!forum/amarino-embed.

More Info
---------
A presentation about Amarino Embed and RGBLEDPickers can be found at https://docs.google.com/present/view?id=df3bs8md_57g8mr447x&interval=5&autoStart=true.  This presentation was given to the Denver and Boulder Android Meetup groups in 2011.

Known issues
------------
See the issues page in this project.
