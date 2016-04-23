package com.gatbhet.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gatbhet.model.TokenRequest;
import com.gatbhet.model.TokenResponse;
import com.gatbhet.services.LoginWebService;
import com.gatbhet.services.TokenWebService;
import com.gatbhet.R;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Created by tinuzz on 04/08/2016.
 */
public class WebServiceAsyncTask extends AsyncTask {

    public interface WebServiceResponseListener{
        public void onResponseReceived(String response);
    }
    private WebServiceResponseListener webServiceResponseListener;

    public WebServiceResponseListener getWebServiceResponseListener() {
        return webServiceResponseListener;
    }

    public void setWebServiceResponseListener(WebServiceResponseListener webServiceResponseListener) {
        this.webServiceResponseListener = webServiceResponseListener;
    }

    private Context context;
    private ProgressDialog progressDialog;

    public WebServiceAsyncTask(Context context) {
        this.context = context;

    }

    public WebServiceAsyncTask() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(context != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.loading_text));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(context != null) {
            progressDialog.dismiss();
        }
        TokenResponse tokenResponse = (TokenResponse)o;
        String request_token = tokenResponse.getData().getRequest_token();
        Util.log("Login","Request Token : " + tokenResponse.getData().getRequest_token());
        if(webServiceResponseListener != null) {
            webServiceResponseListener.onResponseReceived(tokenResponse.getData().getRequest_token());
        }
//        LoginWebService loginWebService = new LoginWebService(request_token);
//        GenericWebServiceAsyncTask genericWebServiceAsyncTask = new GenericWebServiceAsyncTask(loginWebService,context);
//        genericWebServiceAsyncTask.execute();


//        Toast.makeText(context,"Request Sent Successfully",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        TokenWebService bookAppointmentWebService = new TokenWebService();

        TokenRequest tokenRequest = new TokenRequest();
        return bookAppointmentWebService.getTokenResponse(tokenRequest);

    }
}
