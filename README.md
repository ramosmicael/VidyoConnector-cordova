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

Next, edit VidyoIOPlugin/src/android/VidyoIOPlugin.java and make sure it looks like the following. Here we are mapping the method "launchVidyoIO" to invoke Vidyo.io activity. We also pass the required parameters to join a Vidyo room.

```
package com.vidyo.plugin;
 
import android.content.Context;
import android.content.Intent;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vidyo.vidyoconnector.VidyoIOActivity;
 
/**
 * This class echoes a string called from JavaScript.
 */
public class VidyoIOPlugin extends CordovaPlugin {
 
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        if(action.equals("launchVidyoIO")) {
            this.openNewActivity(context,args);
            return true;
        }
        return false;
    }
 
    private void openNewActivity(Context context,JSONArray args)  throws JSONException {
        Intent intent = new Intent(context, VidyoIOActivity.class);
        intent.putExtra("token", args.getString(0));
        intent.putExtra("host", args.getString(1));
        intent.putExtra("displayName", args.getString(2));
        intent.putExtra("resourceId", args.getString(3));
        intent.putExtra("hideConfig", true);
        intent.putExtra("autoJoin", true);
 
        this.cordova.getActivity().startActivity(intent);
    }
}

```
Now edit VidyoIOPlugin/www/VidyoIOPlugin.js and create a binding to the native method created in previous step

```
var exec = require('cordova/exec');
function plugin() {
}
plugin.prototype.launchVidyoIO = function(args) { exec(function(res){}, function(err){}, "VidyoIOPlugin", "launchVidyoIO", args); }
module.exports = new plugin();

```

Next, edit the Cordova plugin configuration file - VidyoIOPlugin/plugin.xml. Here we add rules to correctly merge the permissions, resources, native libraries and Vidyo.IO android activity file. Be sure to change the <clobbers> tag value to VidyoIOPlugin
      
```
<?xml version='1.0' encoding='utf-8'?>
<plugin id="com.vidyo.plugin" version="0.0.1" xmlns="http://apache.org/cordova/ns/plugins/1.0" xmlns:android="http://schemas.android.com/apk/res/android">
<name>VidyoIOPlugin</name>
<js-module name="VidyoIOPlugin" src="www/VidyoIOPlugin.js">
<clobbers target="VidyoIOPlugin" />
</js-module>
<platform name="android">
<config-file parent="/*" target="res/xml/config.xml">
<feature name="VidyoIOPlugin"><param name="android-package" value="com.vidyo.plugin.VidyoIOPlugin" />
</feature>
</config-file>
<config-file parent="/manifest" target="AndroidManifest.xml">
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.CAPTURE_AUDIO_OUTPUT" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-feature android:name="android.hardware.camera" />
</config-file>
<config-file parent="/manifest/application" target="AndroidManifest.xml">
<activity
android:name="com.vidyo.vidyoconnector.VidyoIOActivity"
android:launchMode="singleTop"
android:configChanges="orientation|screenSize"
android:label="@string/app_name" >
<intent-filter>
<category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
</activity>
</config-file>
<source-file src="src/android/VidyoIOPlugin.java" target-dir="src/com/vidyo/plugin/VidyoIOPlugin" />
<source-file src="src/android/com/vidyo/vidyoconnector/VidyoIOActivity.java" target-dir="src/com/vidyo/vidyoconnector" />
<source-file src="src/android/com/vidyo/vidyoconnector/VidyoConnector.java" target-dir="src/com/vidyo/vidyoconnector" />
<source-file src="src/android/com/vidyo/vidyoconnector/Logger.java" target-dir="src/com/vidyo/vidyoconnector" />
<source-file src="src/android/lib/android/vidyoclient.jar" target-dir="libs" />
<source-file src="src/android/lib/android/arm64-v8a/libVidyoClient.so" target-dir="libs/arm64-v8a" />
<source-file src="src/android/lib/android/armeabi-v7a/libVidyoClient.so" target-dir="libs/armeabi-v7a" />
<source-file src="src/android/lib/android/x86/libVidyoClient.so" target-dir="libs/x86" />
<source-file src="src/android/lib/android/x86_64/libVidyoClient.so" target-dir="libs/x86_64" />
<source-file src="src/android/res/drawable/callend.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/callstart.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/camera_off.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/camera_switch.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/cameraonwhite.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/microphoneoff.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/microphoneonwhite.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/rounded_border_edittext.xml" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/toggle_camera_privacy.xml" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/toggle_connect.xml" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/toggle_mic_privacy.xml" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/vidyo_io_icon.png" target-dir="res/drawable" />
<source-file src="src/android/res/drawable/vidyo_io_vertical_dark_at_2x.png" target-dir="res/drawable" />
<source-file src="src/android/res/layout/activity_main.xml" target-dir="res/layout" />
<source-file src="src/android/res/menu/menu_main.xml" target-dir="res/menu" />
<source-file src="src/android/res/mipmap-hdpi/ic_launcher.png" target-dir="res/mipmap-hdpi" />
<source-file src="src/android/res/mipmap-mdpi/ic_launcher.png" target-dir="res/mipmap-mdpi" />
<source-file src="src/android/res/mipmap-xhdpi/ic_launcher.png" target-dir="res/mipmap-xhdpi" />
<source-file src="src/android/res/mipmap-xxhdpi/ic_launcher.png" target-dir="res/mipmap-xxhdpi" />
<source-file src="src/android/res/values/dimens.xml" target-dir="res/values" />
<source-file src="src/android/res/values/strings.xml" target-dir="res/values" />
<source-file src="src/android/res/values/styles.xml" target-dir="res/values" />
<source-file src="src/android/res/values-w820dp/dimens.xml" target-dir="res/values-w820dp" />
</platform>
</plugin>

```

Finally create the plugin package file so that it can be imported in to a Cordova project

> plugman createpackagejson /path/to/your/pluginfolder
> Example: plugman createpackagejson C:\CordovaPlugins\VidyoIOPlugin

Your plugin is Ready!
