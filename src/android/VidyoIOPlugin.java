package com.vidyo.plugin;

import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.apache.cordova.PluginResult;/*NOESIS 2019.08.01*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.WindowManager;/*NOESIS 2019.08.01*/

import com.vidyo.vidyoconnector.VidyoIOActivity;

import android.Manifest;

import android.content.pm.PackageManager;/*NOESIS 2019.08.01*/

import android.widget.Toast;

/**
 * This class echoes a string called from JavaScript.
 */
public class VidyoIOPlugin extends CordovaPlugin {

    private static final int PERMISSION_REQ_CODE = 0x7b;
    /*NOESIS 2019.08.01*/
    private static final String ACTION_KEEP_AWAKE = "keepAwake";
    private static final String ACTION_ALLOW_SLEEP_AGAIN = "allowSleepAgain";

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //Manifest.permission.WAKE_LOCK /*NOESIS 2019.08.01*/
    };

    private JSONArray launchVidyoIOArguments;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("launchVidyoIO")) {
            this.openNewActivity(args);
            
            // ACTION_KEEP_AWAKE
            /*NOESIS 2019.08.01 REMOVE COMMENT TO TEST THE KEEP SCREEN*/ 
            /*cordova.getActivity().runOnUiThread(
            new Runnable() {
              public void run() {
                cordova.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
              }
            });*/
            
            return true;
        }
        /*
        cordova.getActivity().runOnUiThread(
            new Runnable() {
              public void run() {
                cordova.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
              }
            });*/
        return false;
    }

    private void openNewActivity(JSONArray args) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();

        /* Check for required permissions */
        if (!hasAllPermissions()) {
            this.launchVidyoIOArguments = args;
            this.cordova.requestPermissions(this, PERMISSION_REQ_CODE, PERMISSIONS);
            return;
        }

        Intent intent = new Intent(context, VidyoIOActivity.class);
        intent.putExtra("token", args.getString(0));
        intent.putExtra("host", args.getString(1));
        intent.putExtra("displayName", args.getString(2));
        intent.putExtra("resourceId", args.getString(3));
        intent.putExtra("hideConfig", true);
        intent.putExtra("autoJoin", true);

        this.cordova.getActivity().startActivity(intent);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        /*NOESIS 2019.08.01 REMOVE COMMENT TO TEST THE KEEP SCREEN*/ 
            /*
        cordova.getActivity().runOnUiThread(
            new Runnable() {
              public void run() {
                cordova.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
              }
            });*/
        
        
        if (requestCode == PERMISSION_REQ_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(cordova.getActivity(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                    return; /* quit */
                }
            }

            /* Success */
            if (launchVidyoIOArguments != null) {
                this.openNewActivity(launchVidyoIOArguments);
                this.launchVidyoIOArguments = null;
            }
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : PERMISSIONS) {
            if (!this.cordova.hasPermission(permission)) return false;
        }

        return true;
    }
}
