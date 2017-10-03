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
