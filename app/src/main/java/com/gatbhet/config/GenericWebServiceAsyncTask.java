package com.gatbhet.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.gatbhet.R;
import com.gatbhet.services.IWebService;
import com.gatbhet.services.LoginWebService;

/**
 * Created by 801 on 4/15/2016.
 */
public class GenericWebServiceAsyncTask extends AsyncTask<Void,Void,String> {

    private Context context;
    private ProgressDialog progressDialog;

    public GenericWebServiceAsyncTask(IWebService service) {
        this.service = service;
    }

    public interface GenericWebServiceResponseListener{
        public void onGenericResponseReceived(String response);
    }
    private GenericWebServiceResponseListener webServiceResponseListener;

    public GenericWebServiceResponseListener getWebServiceResponseListener() {
        return webServiceResponseListener;
    }

    public void setWebServiceResponseListener(GenericWebServiceResponseListener webServiceResponseListener) {
        this.webServiceResponseListener = webServiceResponseListener;
    }

    IWebService service;
    public GenericWebServiceAsyncTask(IWebService service,Context context) {
        this.service = service;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(context!=null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.loading_text));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        return service.getResponse();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(context!= null) {
            progressDialog.dismiss();
        }
        if(service instanceof LoginWebService){
            Util.log("LoginWebService", "Response : " + response );
            webServiceResponseListener.onGenericResponseReceived(response);
        }

    }
}
