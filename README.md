# gradle-plugin-delegator
###### *A delegator plugin for the new Gradle plugin DSL*

This plugin delegates its apply() calls to a configurable list of available project plugins.

The [```plugin-delegator```](https://plugins.gradle.org/plugin/name.yount.steffen.plugin-delegator) plugin is intended to serve as a test harness allowing developers to build, debug, apply, and test their custom plugin implementations within the new *Gradle plugins {} DSL*'s early apply phase while not requiring that the latest targeted plugins' implementations are first uploaded to the Gradle plugin portal before each test iteration.

It has been built and tested using Gradle 2.4.

To setup your custom plugin as a delegate plugin just add a ```META-INF/plugin-delegator.properties``` file on your project's classpath and include a property named ```plugins``` therein to specify the pluginId of your custom plugin. Multiple pluginIds may be specified in a comma separated list and/or by including multiple ```META-INF/plugin-delegator.properties``` files on your project's classpath.
