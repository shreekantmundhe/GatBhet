package com.gatbhet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gatbhet.R;
import com.gatbhet.config.GenericWebServiceAsyncTask;
import com.gatbhet.config.Util;
import com.gatbhet.config.WebServiceAsyncTask;
import com.gatbhet.model.TokenResponse;
import com.gatbhet.services.LoginWebService;
import com.google.gson.Gson;

import java.io.IOException;

public class IncomingCallReceiver extends BroadcastReceiver implements WebServiceAsyncTask.WebServiceResponseListener, GenericWebServiceAsyncTask.GenericWebServiceResponseListener {
    private Context context;
    private WindowManager windowManager;
    private static LinearLayout customLayout;
    private WindowManager.LayoutParams windowParams;
    private boolean stateIsMute;
    private AudioManager audioManager;
    private MediaPlayer player;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Util.log("Call Receiver", "Incoming call event");
            this.context = context;
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            // TELEPHONY MANAGER class object to register one listner
            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            // Create Listner
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            // Register listener for LISTEN_CALL_STATE
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) || state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Util.log("Incoming Call","State Ringing");
                windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowParams = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSPARENT);
                windowParams.gravity = Gravity.TOP;
                windowParams.height = 120;
                windowParams.width = 120;
                windowParams.x = 265;
                windowParams.y = 200;
                windowParams.format = PixelFormat.TRANSLUCENT;
                if (customLayout == null) {
                    customLayout = new LinearLayout(context);
                    customLayout.setBackgroundColor(Color.TRANSPARENT);
                    customLayout.setOrientation(LinearLayout.VERTICAL);
                    WebServiceAsyncTask webServiceAsyncTask = new WebServiceAsyncTask();
                    webServiceAsyncTask.setWebServiceResponseListener(this);
                    webServiceAsyncTask.execute();
                    View callingLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.calling_layout, customLayout, false);
                    callingLayout.findViewById(R.id.mute).setTag(false);
                    callingLayout.findViewById(R.id.mute).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isFocused = (boolean) v.getTag();
                            if(isFocused){
                                ((ImageView)v).setImageResource(R.drawable.mic_white);
                            }else{
                                ((ImageView)v).setImageResource(R.drawable.mic_black);
                            }
                            v.setTag(!isFocused);
                            Util.log("Call Receiver", "Mute button clicked!");
                            playAudio();
                        }
                    });
                    customLayout.addView(callingLayout);
                    Util.log("Call Receiver","Adding calling layout");
                    windowManager.addView(customLayout, windowParams);
                }
            }

            // To remove the view once the dialer app is closed.
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if(windowManager == null)
                        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    if (customLayout != null) {
                        unMute();
                        Util.log("Call Receiver","Removing calling layout");
                        windowManager.removeView(customLayout);
                        customLayout = null;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    @Override
    public void onResponseReceived(String response) {
        Util.log("Call Receiver", "Token WS Response : " + response);
        LoginWebService loginWebService = new LoginWebService(response);
        GenericWebServiceAsyncTask genericWebServiceAsyncTask = new GenericWebServiceAsyncTask(loginWebService);
        genericWebServiceAsyncTask.setWebServiceResponseListener(this);

        genericWebServiceAsyncTask.execute();
    }

    @Override
    public void onGenericResponseReceived(String response) {
        Gson gson = new Gson();
        TokenResponse data = gson.fromJson(response, TokenResponse.class);
        Util.log("Call Receiver", "WS Output : " + data);
    }

    private void playAudio() {
        AssetFileDescriptor afd = null;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // get original mode
        final int originalMode = audioManager.getMode();
        Util.log("Call Receiver", "Current Mode : " + originalMode);
        // change mute
        stateIsMute = !audioManager.isMicrophoneMute();
        if (stateIsMute) {
            try {
                Util.log("Call Receiver","Phone state is not mute.");
                //audio
                afd = IncomingCallReceiver.this.context.getAssets().openFd("gbaudio.mp3");
                if(player == null)
                    player = new MediaPlayer();
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Util.log("Call Receiver","Phone state changed to mute.");
                        audioManager.setSpeakerphoneOn(false);
                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                        audioManager.setMicrophoneMute(stateIsMute);
                    }
                });
                if (player.isPlaying()) {
                    Util.log("Call Receiver","Stopping audio");
                    player.stop();
                } else {
                    Util.log("Call Receiver","Playing audio");
                    audioManager.setSpeakerphoneOn(true);
                    player.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Util.log("Call Receiver","Phone state is mute.");
            Util.log("Call Receiver", "Phone state changed to unmute.");
           unMute();
        }
        
    }

    private void unMute(){
        boolean stateIsMute = audioManager.isMicrophoneMute();
        Util.log("Call Receiver","Mute : " + stateIsMute);
        Util.log("Call Receiver","Un-mute done.");
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentMode = audioManager.getMode();
        Util.log("Call Receiver","Current Mode : " + currentMode);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(stateIsMute)
            audioManager.setMicrophoneMute(!stateIsMute);

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {
                Util.log("MyPhoneListener","Incoming no : " + incomingNumber);
            Util.log("MyPhoneListener","State : " + state);
            if (state == 1) {
                String msg = "New Phone Call Event. Incomming Number : "
                        + incomingNumber;
                Util.log("MyPhoneListener","Msg : " + msg);
            }
        }
    }
}
