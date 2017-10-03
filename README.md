# VidyoIOCordovaPlugin

This a Vidyo.io Android plugin for Cordova. This plugin is built using the Vidyo.io Android sample available @ https://vidyo.io. If you would like to build the plugin from scratch against a newer version of Vidyo.io sample, follow the procedure give below.

## How to use

      cordova plugin add <path-to-plugin-folder>
      
 OR,
 
      cordova plugin add https://github.com/Vidyo/VidyoIOCordovaPlugin.git
      
## How to create this plugin from scratch

This section explains how you can build your own Cordova plugin for Vidyo.io using the Android sample application.

### Prerequisites
- Download the Android SDK from https://vidyo.io. This SDK contains a sample application.
- npm - Node.js package manager. I have used NodeJS version 6.9.1. Download from - https://nodejs.org/en/

#### Install Cordova Plugin manager
>$ npm install -g plugman
 
#### Create plugin project
>$ plugman create --name VidyoIOPlugin --plugin_id com.vidyo.plugin.VidyoIOPlugin --plugin_version 0.0.1

#### Add Android platform to the plugin by typing the following command
>$ cd VidyoIOPlugin

>$ plugman platform add --platform_name android

#### Now we have to copy a few files from Vidyo.IO sample application to the plugin folder.

- copy the res folder from the sample to VidyoIOPlugin/src/android
- copy the lib folder from the sample to VidyoIOPlugin/src/android
- copy the com folder from VidyoConnector\android\app\src\main\java to VidyoIOPlugin/src/android
- We have to refactor the MainActivity.Java file included in the sample to VidyoIOActivity.java and change the class name also to reflect the change. We have to do this because, when we install this plugin in to the Cordova project, Cordova project also has a MainActivity so there will be a conflict.
