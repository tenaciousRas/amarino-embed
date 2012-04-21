ANNOUNCEMENT
------------
Major refactoring to this project is coming!  This project is originally a fork of the Amarino project, which itself is a wrapper around the Abraxas Bluetooth API for Android.
After many hours of work and advanced usage it's morphed into a fully-embeddable Android service.

Amarino is a tool intended for those just getting introducced to Arduino.  Unlike Amarino, this tool is
built by and for Android developers - first and foremost.  It is therefore intended for serious beginners,
intermeditate, and advanced programmers in the Android and embedded development 'worlds'.

Honestly - if you're just getting started with Arduino and want to build things with bluetooth - you should check out the
kickstarter project Ard'Vark.  http://www.kickstarter.com/projects/694850905/the-ard-vark  It will probably be less
confusing for you than Amarino, and you might get more done.

In 2012 I plan to launch an Android application that's easily searchable in Google Play for easy Android connectivity.
It will be based on a completely refactored codebase - and the point of the app is that it'll be easily searchable.

The project will be renamed Android Bluetooth Connectivity and this one will either be phased-out or replaced.

Stay tuned!


Overview
--------
This is an embeddable version of the Amarino library (http://www.amarino-toolkit.net/).  Amarino is originally built to be installed as an Android application which other Android apps depend on.  While this is an acceptable practice (for ex:  Google Navigator does it), this isn't always a pattern that works well for application developers.

Amarino is a natural concept that evolves from the Intent based platform in Android.  It has a simple RPC-like frame-buffer implementation that allows it to execute callbacks on an Arduino connected to a bluetooth module (such as SparkFun's BlueSmirf).  It's easy to use too.

This API decouples the Amarino Service architecture from its application, allowing you to embed Amarino in your own applications.  To keep things simple, and in cases where you intend to use Amarino as a shared application, the old behavior is supported.

Another important change in the API is support for custom MessageBuilder implementations.  This allows you to implement new protocols to talk to Arduino.  Maybe you want to use Google's Protocol Buffers to talk to Arudino.  Maybe you want to send binary data, or BSON over the wireless wire.  No problem�write a custom MessageBuilder implementation and set that in your embedded service.

This project is considered relatively stable, but so many changes from the original project were needed that it can only be considered relatively stable.  For example, the Amarino APK is known to crash, and that hasn't been fixed here (yet).  Please excuse any issues and help contribute to the project -- if you have time to help write code let me know I'd love some help!

Several of the examples in this project are from the original Amarino codebase, modified for the Amarino Library project.  Two examples are provided that use the embedded service:  HelloAmarinoWorld and RGBLEDPickers.  HelloAmarinoWorld
provides a simple working example that only requires an Arduino with an Amarino-compatible bluetooth module.  The RGBLEDPickers example is similar
but requires a small working circuit built around the Arduino.  RGBLEDPickers works with an embedded Amarino service and
uses the Android compatibility library (fragments).

Branches
--------
vendor => original Amarino branch.  The jump-point for merging changes from the Google Code source branch.
dev-* => dev branch
trunk => latest stable branch (coming when top priority bugs resolved and backwards-compat tested)

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
