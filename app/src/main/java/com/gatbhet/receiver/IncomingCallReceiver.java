package com.gatbhet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gatbhet.R;

public class IncomingCallReceiver extends BroadcastReceiver {
	private Context context;
	private WindowManager wm;
    private static LinearLayout ly1;
    private WindowManager.LayoutParams params1;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			this.context = context;
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			// TELEPHONY MANAGER class object to register one listner
			TelephonyManager tmgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// Create Listner
			MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
			// Register listener for LISTEN_CALL_STATE
			tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
			if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)||state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
				System.out.println("RETAIL : Inside the if block");
                wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                params1 = new WindowManager.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT, 
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSPARENT);
                params1.gravity = Gravity.TOP;		
                params1.height = 75;
                params1.width = 512;
                params1.x = 265; 
                params1.y = 200;
                params1.format = PixelFormat.TRANSLUCENT;
                if(ly1==null){
                	//wm.removeView(ly1);
                
                ly1 = new LinearLayout(context);
                ly1.setBackgroundColor(Color.BLACK);
                ly1.setOrientation(LinearLayout.VERTICAL);

                View hiddenInfo = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.calling_layout, ly1, false);
                ly1.addView(hiddenInfo);
                System.out.println("RETAIL : Adding the layout...");
                wm.addView(ly1, params1);
                }
            }

            // To remove the view once the dialer app is closed.
            if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
                state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    if(ly1!=null)
                    {
                    	System.out.println("RETAIL : Removing the layout...");
                        wm.removeView(ly1);
                        ly1 = null;
                    }
                }
            }
		} catch (Exception e) {
			Log.e("Phone Receive Error", " " + e);
		}
//		Toast.makeText(context, "Call Received", Toast.LENGTH_SHORT).show();
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		public void onCallStateChanged(int state, String incomingNumber) {
			Log.d("MyPhoneListener", state + "   incoming no:" + incomingNumber);
			if (state == 1) {

				String msg = "New Phone Call Event. Incomming Number : "
						+ incomingNumber;
				//Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

			}
		}
	}
}
