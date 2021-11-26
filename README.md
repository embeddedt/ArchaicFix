# anatawa12's ForgeGradle 1.2 fork for Gradle 4.4.1+ - example project

This is an example mod using the [fork of ForgeGradle-1.2 made by anatawa12](https://github.com/anatawa12/ForgeGradle-1.2).
This fork supports Gradle 4.4.1 and later. This example project uses Gradle 5.6.4.

## How to use this example project

You can download this example project from [here](https://github.com/anatawa12/ForgeGradle-example/archive/master.zip), or use it as a template on Github.
This project can be used as a replacement for Forge's 1.7.10 MDK.

## How to replace ForgeGradle 1.2. with anatawa12's fork
Although this example project has some differences to Forge's 1.7.10 MDK, anatawa12's fork of ForgeGradle 1.2 can be used by most projects with only minimal changes to their Gradle build script.

Here is a list of changes to Forge's 1.7.10 MDK Gradle build script, to replace the official ForgeGradle 1.2 plugin with the fork. These changes are likely to work with most projects based on Forge's 1.7.10 MDK.

In the repositories block of the buildscript section, add jcenter, and switch the Forge maven to use HTTPS instead of HTTP:
```diff
     repositories {
         mavenCentral()
         maven {
             name = "forge"
-            url = "http://files.minecraftforge.net/maven"
+            url = "https://files.minecraftforge.net/maven"
         }
```

Also in the dependencies block of the buildscript section, change the dependency on Forge's official ForgeGradle 1.2 to the fork:
```diff
     dependencies {
-        classpath 'net.minecraftforge.gradle:ForgeGradle:1.2-SNAPSHOT'
+        classpath ('com.anatawa12.forge:ForgeGradle:1.2-1.0.+') {
+            changing = true
+        }
     }
```

The Gradle wrapper should also be changed to use Gradle 4.4.1 or higher. Currently, the plugin [does not support Gradle 6.x](https://github.com/anatawa12/ForgeGradle-1.2/issues/9), although this may change in the future. As such, the latest version of Gradle this plugin supports is Gradle 5.6.4.
