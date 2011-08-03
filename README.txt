Overview
--------
This is an embeddable version of the Amarino library (http://www.amarino-toolkit.net/).  Amarino is originally built to be installed as an Android application which other Android apps depend on.  While this is an acceptable practice (for ex:  Google Navigator does it), this isn't always a pattern that works well for application developers.

Amarino is nice.  It has a simple RPC-like frame-buffer implementation that allows it to execute callbacks on an Arduino connected to a bluetooth module (such as SparkFun's BlueSmirf).  It's easy to use too.

This API decouples the Amarino Service architecture from its application, allowing you to embed Amarino in your own applications.  To keep things simple, and in cases where you intend to use Amarino as a shared application, the old behavior is supported.

Another important change in the API is support for custom MessageBuilder implementations.  This allows you to implement new protocols to talk to Arduino.  Maybe you want to use Google's Protocol Buffers to talk to Arudino.  Maybe you want to send binary data, or BSON over the wireless wire.  No problemÉwrite a custom MessageBuilder implementation and set that in your embedded service.

This is an early project commit.  Please excuse any issues and help contribute to the project -- if you have time to help write code let me know I'd love some help!

Branches
--------
vendor => original Amarino branch.  The jumping point for merging changes from the Google Code source branch.
dev-* => dev branch
trunk => latest stable branch

Known issues
------------
* Some bugs in Amarino -- something with the way the service is bound -- top priority
* Testing of Amarino APK as a shared app -- old behavior fully supported?  -- top priority
* No way to set a custom icon for service notifications
* I think service notifications needs to be more configurable -- haven't looked at it
 