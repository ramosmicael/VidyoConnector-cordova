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
