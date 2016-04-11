package com.gatbhet.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gatbhet.model.TokenRequest;
import com.gatbhet.model.TokenResponse;
import com.gatbhet.services.TokenWebService;
import com.gatbhet.R;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Created by tinuzz on 04/08/2016.
 */
public class WebServiceAsyncTask extends AsyncTask {

    private Context context;
    private ProgressDialog progressDialog;

    public WebServiceAsyncTask(Context context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading_text));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressDialog.dismiss();
        TokenResponse tokenResponse = (TokenResponse)o;
        Util.log("Login","Request Token : " + tokenResponse.getData().getRequest_token());
        String timeStamp = Util.getTimeStamp();
        Util.log("Login","Time Stamp : " + timeStamp);
        HashMap<String,String> params = new HashMap<String, String>();

        params.put("timestamp",timeStamp);
        params.put("request_token", tokenResponse.getData().getRequest_token());
        params.put("request_for","alerts");//alerts,audio,profile
        params.put("caller_ref_id","9766363775");
        params.put("long","50");
        params.put("lat","100");


        try {
            Util.log("Login","Security Token : " + Util.createSecurityToken(tokenResponse.getData().getRequest_token(),timeStamp,params));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Toast.makeText(context,"Request Sent Successfully",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        TokenWebService bookAppointmentWebService = new TokenWebService();

        TokenRequest tokenRequest = new TokenRequest();
        return bookAppointmentWebService.getTokenResponse(tokenRequest);

    }
}
