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
