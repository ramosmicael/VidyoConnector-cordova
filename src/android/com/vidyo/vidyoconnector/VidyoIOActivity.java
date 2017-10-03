package com.vidyo.vidyoconnector;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.vidyo.VidyoClient.Connector.VidyoConnector;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Device.VidyoDevice;
import com.vidyo.VidyoClient.Device.VidyoLocalCamera;
import com.vidyo.VidyoClient.Endpoint.VidyoLogRecord;

public class VidyoIOActivity extends Activity implements
        VidyoConnector.IConnect,
        VidyoConnector.IRegisterLogEventListener,VidyoConnector.IRegisterLocalCameraEventListener {

    enum VIDYO_CONNECTOR_STATE {
        VC_CONNECTED,
        VC_DISCONNECTED,
        VC_DISCONNECTED_UNEXPECTED,
        VC_CONNECTION_FAILURE
    }

    private VIDYO_CONNECTOR_STATE mVidyoConnectorState = VIDYO_CONNECTOR_STATE.VC_DISCONNECTED;
    private boolean mVidyoConnectorConstructed = false;
    private boolean mVidyoClientInitialized = false;
    private Logger mLogger = Logger.getInstance();
    private VidyoConnector mVidyoConnector = null;
    private ToggleButton mToggleConnectButton;
    private ProgressBar mConnectionSpinner;
    private LinearLayout mControlsLayout;
    private LinearLayout mToolbarLayout;
    private EditText mHost;
    private EditText mDisplayName;
    private EditText mToken;
    private EditText mResourceId;
    private TextView mToolbarStatus;
    private FrameLayout mVideoFrame;
    private FrameLayout mToggleToolbarFrame;
    private boolean mHideConfig = false;
    private boolean mAutoJoin = false;
    private boolean mAllowReconnect = true;
    private String mReturnURL = null;
    private VidyoIOActivity mSelf;

    /*
     *  Operating System Events
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.Log("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize the member variables
        mToggleConnectButton = (ToggleButton) findViewById(R.id.toggleConnectButton);
        mControlsLayout = (LinearLayout) findViewById(R.id.controlsLayout);
        mToolbarLayout = (LinearLayout) findViewById(R.id.toolbarLayout);
        mVideoFrame = (FrameLayout) findViewById(R.id.videoFrame);
        mToggleToolbarFrame = (FrameLayout) findViewById(R.id.toggleToolbarFrame);
        mHost = (EditText) findViewById(R.id.hostTextBox);
        mDisplayName = (EditText) findViewById(R.id.displayNameTextBox);
        mToken = (EditText) findViewById(R.id.tokenTextBox);
        mResourceId = (EditText) findViewById(R.id.resourceIdTextBox);
        mToolbarStatus = (TextView) findViewById(R.id.toolbarStatusText);
        mConnectionSpinner = (ProgressBar) findViewById(R.id.connectionSpinner);
        mSelf = this;

        // Suppress keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize the VidyoClient
        Connector.SetApplicationUIContext(this);
        mVidyoClientInitialized = Connector.Initialize();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mLogger.Log("onNewIntent");
        super.onNewIntent(intent);

        // New intent was received so set it to use in onStart()
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        mLogger.Log("onStart");
        super.onStart();

        // If the app was launched by a different app, then get any parameters; otherwise use default settings
        Intent intent = getIntent();
        mHost.setText(intent.hasExtra("host") ? intent.getStringExtra("host") : "prod.vidyo.io");
        mToken.setText(intent.hasExtra("token") ? intent.getStringExtra("token") : "cHJvdmlzaW9uAHN1bWl0QGRkYTg0MC52aWR5by5pbwA2MzY1ODg4MTAyOAAAMTYyNGJmM2IwMGQ3ODdjNmFkNGIyNzE3YTgxNTlkMGUzMDA3NzQxZTZkOWQzYjEwMTY4ZTMxMWZhZDE4MmMxNmVmOWQwNmQyZDQxMGZjMGUzMzUyZDg5ZWQ5Mzk5NDc3");
        mDisplayName.setText(intent.hasExtra("displayName") ? intent.getStringExtra("displayName") : "");
        mResourceId.setText(intent.hasExtra("resourceId") ? intent.getStringExtra("resourceId") : "demoRoom");
        mReturnURL = intent.hasExtra("returnURL") ? intent.getStringExtra("returnURL") : null;
        mHideConfig = intent.getBooleanExtra("hideConfig", false);
        mAutoJoin = intent.getBooleanExtra("autoJoin", false);
        mAllowReconnect = intent.getBooleanExtra("allowReconnect", true);

        mLogger.Log("onStart: hideConfig = " + mHideConfig + ", autoJoin = " + mAutoJoin + ", allowReconnect = " + mAllowReconnect);

        // Enable toggle connect button
        mToggleConnectButton.setEnabled(true);

        // Hide the controls if hideConfig enabled
        if (mHideConfig) {
            mControlsLayout.setVisibility(View.GONE);
        }

    }


    public void OnLocalCameraAdded(VidyoLocalCamera localCamera)    { /* New camera is available */



    }
    public void OnLocalCameraRemoved(VidyoLocalCamera localCamera)  { /* Existing camera became unavailable */          }
    public void OnLocalCameraSelected(VidyoLocalCamera localCamera) {
       
    /* Camera was selected by user or automatically */ }
    public void OnLocalCameraStateUpdated(VidyoLocalCamera localCamera, VidyoDevice.VidyoDeviceState state) { /* Camera state was updated */ }



    @Override
    protected void onResume() {
        mLogger.Log("onResume");
        super.onResume();

        ViewTreeObserver viewTreeObserver = mVideoFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mVideoFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // If the vidyo connector was not previously successfully constructed then construct it

                    if (!mVidyoConnectorConstructed) {

                        if (mVidyoClientInitialized) {

                            mVidyoConnector = new VidyoConnector(mVideoFrame,
                                    VidyoConnector.VidyoConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default,
                                    16,
                                    "debug@VidyoClient info@VidyoConnector warning ",
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/VidyoIOAndroid.log",
                                    0);
                            mLogger.Log("Version is "+ mVidyoConnector.GetVersion());
                            mVidyoConnector.EnableDebug(0,"");
                            if (mVidyoConnector != null) {
                                mVidyoConnectorConstructed = true;

								// Set initial position
                                RefreshUI();

                                // Register for log callbacks
                                if (!mVidyoConnector.RegisterLogEventListener(mSelf, "info@VidyoClient info@VidyoConnector warning")) {
                                    mLogger.Log("VidyoConnector RegisterLogEventListener failed");
                                }
                                if(!mVidyoConnector.RegisterLocalCameraEventListener(mSelf)){
                                    mLogger.Log("VidyoConnector RegisterLocalCameraEventListener failed");
                                }
                            } else {
                                mLogger.Log("VidyoConnector Construction failed - cannot connect...");
                            }
                        } else {
                            mLogger.Log("ERROR: VidyoClientInitialize failed - not constructing VidyoConnector ...");
                        }

                        Logger.getInstance().Log("onResume: mVidyoConnectorConstructed => " + (mVidyoConnectorConstructed ? "success" : "failed"));
                    }

                    // If configured to auto-join, then simulate a click of the toggle connect button
                    if (mVidyoConnectorConstructed && mAutoJoin) {
                        mToggleConnectButton.performClick();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        mLogger.Log("onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        mLogger.Log("onRestart");
        super.onRestart();
        mVidyoConnector.SetMode(VidyoConnector.VidyoConnectorMode.VIDYO_CONNECTORMODE_Foreground);

    }

    @Override
    protected void onStop() {
        mLogger.Log("onStop");
        if(mVidyoConnector != null)
            mVidyoConnector.SetMode(VidyoConnector.VidyoConnectorMode.VIDYO_CONNECTORMODE_Background);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLogger.Log("onDestroy");
        Connector.Uninitialize();
        super.onDestroy();
    }

    // The device interface orientation has changed
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.Log("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);

        // Refresh the video size after it is painted
        ViewTreeObserver viewTreeObserver = mVideoFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mVideoFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // Width/height values of views not updated at this point so need to wait
                    // before refreshing UI

                    RefreshUI();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Private Utility Functions
     */

    // Refresh the UI
    private void RefreshUI() {
        // Refresh the rendering of the video
        mVidyoConnector.ShowViewAt(mVideoFrame, 0, 0, mVideoFrame.getWidth(), mVideoFrame.getHeight());
        mLogger.Log("VidyoConnectorShowViewAt: x = 0, y = 0, w = " + mVideoFrame.getWidth() + ", h = " + mVideoFrame.getHeight());
    }

    // The state of the VidyoConnector connection changed, reconfigure the UI.
    // If connected, dismiss the controls layout
    private void ConnectorStateUpdated(VIDYO_CONNECTOR_STATE state, final String statusText) {
        mLogger.Log("ConnectorStateUpdated, state = " + state.toString());

        mVidyoConnectorState = state;

        // Execute this code on the main thread since it is updating the UI layout

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Update the toggle connect button to either start call or end call image
                mToggleConnectButton.setChecked(mVidyoConnectorState == VIDYO_CONNECTOR_STATE.VC_CONNECTED);

                // Set the status text in the toolbar
                mToolbarStatus.setText(statusText);

                if (mVidyoConnectorState == VIDYO_CONNECTOR_STATE.VC_CONNECTED) {
                    // Enable the toggle toolbar control
                    mToggleToolbarFrame.setVisibility(View.VISIBLE);

                    if (!mHideConfig) {
                        // Update the view to hide the controls
                        mControlsLayout.setVisibility(View.GONE);
                    }
                } else {
                    // VidyoConnector is disconnected

                    // Disable the toggle toolbar control
                    mToggleToolbarFrame.setVisibility(View.GONE);

                    // If a return URL was provided as an input parameter, then return to that application
                    if (mReturnURL != null) {
                        // Provide a callstate of either 0 or 1, depending on whether the call was successful
                        Intent returnApp = getPackageManager().getLaunchIntentForPackage(mReturnURL);
                        returnApp.putExtra("callstate", (mVidyoConnectorState == VIDYO_CONNECTOR_STATE.VC_DISCONNECTED) ? 1 : 0);
                        startActivity(returnApp);
                    }

                    // If the allow-reconnect flag is set to false and a normal (non-failure) disconnect occurred,
                    // then disable the toggle connect button, in order to prevent reconnection.
                    if (!mAllowReconnect && (mVidyoConnectorState == VIDYO_CONNECTOR_STATE.VC_DISCONNECTED)) {
                        mToggleConnectButton.setEnabled(false);
                        mToolbarStatus.setText("Call ended");
                    }

                    if (!mHideConfig ) {
                        // Update the view to display the controls
                        mControlsLayout.setVisibility(View.VISIBLE);
                    }
                }

                // Hide the spinner animation
                mConnectionSpinner.setVisibility(View.INVISIBLE);
            }
        });
    }

    /*
     * Button Event Callbacks
     */

    // The Connect button was pressed.
    // If not in a call, attempt to connect to the backend service.
    // If in a call, disconnect.
    public void ToggleConnectButtonPressed(View v) {
        if (mToggleConnectButton.isChecked()) {
            mToolbarStatus.setText("Connecting...");

            // Display the spinner animation
            mConnectionSpinner.setVisibility(View.VISIBLE);

            final boolean status = mVidyoConnector.Connect(
                    mHost.getText().toString(),
                    mToken.getText().toString(),
                    mDisplayName.getText().toString(),
                    mResourceId.getText().toString(),
                    this);
            if (!status) {
                // Hide the spinner animation
                mConnectionSpinner.setVisibility(View.INVISIBLE);

                ConnectorStateUpdated(VIDYO_CONNECTOR_STATE.VC_CONNECTION_FAILURE, "Connection failed");
            }
            mLogger.Log("VidyoConnectorConnect status = " + status);
        } else {
            // The button just switched to the callStart image: The user is either connected to a resource
            // or is in the process of connecting to a resource; call VidyoConnectorDisconnect to either disconnect
            // or abort the connection attempt.
            // Change the button back to the callEnd image because do not want to assume that the Disconnect
            // call will actually end the call. Need to wait for the callback to be received
            // before swapping to the callStart image.
            mToggleConnectButton.setChecked(true);

            mToolbarStatus.setText("Disconnecting...");

            mVidyoConnector.Disconnect();
        }
    }

    // Toggle the microphone privacy
    public void MicrophonePrivacyButtonPressed(View v) {
        mVidyoConnector.SetMicrophonePrivacy(((ToggleButton) v).isChecked());
    }

    // Toggle the camera privacy
    public void CameraPrivacyButtonPressed(View v) {
        mVidyoConnector.SetCameraPrivacy(((ToggleButton) v).isChecked());
    }

    // Handle the camera swap button being pressed. Cycle the camera.
    public void CameraSwapButtonPressed(View v) {
        mVidyoConnector.CycleCamera();
    }

    // Toggle visibility of the toolbar
    public void ToggleToolbarVisibility(View v) {
        if (mVidyoConnectorState == VIDYO_CONNECTOR_STATE.VC_CONNECTED) {
            if (mToolbarLayout.getVisibility() == View.VISIBLE) {
                mToolbarLayout.setVisibility(View.INVISIBLE);
            } else {
                mToolbarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     *  Connector Events
     */

    // Handle successful connection.
    public void OnSuccess() {
        mLogger.Log("OnSuccess: successfully connected.");
        ConnectorStateUpdated(VIDYO_CONNECTOR_STATE.VC_CONNECTED, "Connected");
    }

    // Handle attempted connection failure.
    public void OnFailure(VidyoConnector.VidyoConnectorFailReason reason) {
        mLogger.Log("OnFailure: connection attempt failed, reason = " + reason.toString());

        // Update UI to reflect connection failed
        ConnectorStateUpdated(VIDYO_CONNECTOR_STATE.VC_CONNECTION_FAILURE, "Connection failed");
    }

    // Handle an existing session being disconnected.
    public void OnDisconnected(VidyoConnector.VidyoConnectorDisconnectReason reason) {
        if (reason == VidyoConnector.VidyoConnectorDisconnectReason.VIDYO_CONNECTORDISCONNECTREASON_Disconnected) {
            mLogger.Log("OnDisconnected: successfully disconnected, reason = " + reason.toString());
            ConnectorStateUpdated(VIDYO_CONNECTOR_STATE.VC_DISCONNECTED, "Disconnected");
        } else {
            mLogger.Log("OnDisconnected: unexpected disconnection, reason = " + reason.toString());
            ConnectorStateUpdated(VIDYO_CONNECTOR_STATE.VC_DISCONNECTED_UNEXPECTED, "Unexpected disconnection");
        }
    }

    // Handle a message being logged.
    public void OnLog(VidyoLogRecord logRecord) {
        mLogger.LogClientLib(logRecord.message);
    }
}
