Corinna
=======

Corinna is an open source software implementation of the Java Bindlet technology.

The Java Bindlet is similar to Java Servlet platform, but allows the implementation of software components to handle requests in various protocols (binary or text based protocols), not just those based on HTTP. The framework supports the implementation of both TCP and UDP bindlets.

This framework is suitable for the construction of application servers or stantard client/server applications. The bindlet model allow to easily handle various protocols. You can also extend the Corinna framework to handle other protocols in addition to those that already provided: the current implementation provides the HTTP, REST and SOAP bindlet interfaces, ready to use!

This implementation use the Netty framework for network communication over IP (http://www.jboss.org/netty) and LogBack framework for logging (http://logback.qos.ch/).
