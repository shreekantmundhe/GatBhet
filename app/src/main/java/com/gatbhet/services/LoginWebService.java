package com.gatbhet.services;

import com.gatbhet.config.Constants;
import com.gatbhet.config.Util;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by a514192 on 10-04-2016.
 */
public class LoginWebService implements IWebService {

    HashMap<String,String> requestParams;
    HashMap<String,String> headers;

    public LoginWebService(String request_token){
        try {
            String timeStamp = Util.getTimeStamp();
            Util.log("Login","Time Stamp : " + timeStamp);
            requestParams = new HashMap<String, String>();
            headers = new HashMap<String, String>();
            requestParams.put("timestamp",timeStamp);
            requestParams.put("request_token", request_token);
            requestParams.put("request_for","alerts");//alerts,audio,profile
            requestParams.put("caller_ref_id","9766363775");
            requestParams.put("long","50");
            requestParams.put("lat","100");
            Util.log("Login","Security Token : " + Util.createSecurityToken(request_token,timeStamp,requestParams));
            String security_token= Util.createSecurityToken(request_token,timeStamp,requestParams);
            headers.put("Content-Type","text/xml;charset=windows-1250");
            headers.put("Security-Token",security_token);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getResponse(){
        WebServiceHelper webServiceHelper = new WebServiceHelper();
        return webServiceHelper.makeWebServiceCall(this);
    }

    public String getURL(){
        return "http://dev.mulikainfotech.com/gathbhet.com/app/info/";
    }

    @Override
    public HashMap<String, String> getHeader() {
        return headers;
    }


    @Override
    public String getMethodType() {
        return Constants.WS_REQUEST_TYPE_GET;
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public String getURLParams() {
        return "SPT-28";
    }

    @Override
    public String getKeyValueQueryParam() {
        StringBuffer keyValuePairRequest = new StringBuffer();
        for (String key : requestParams.keySet()) {
            keyValuePairRequest.append(key + "=" + requestParams.get(key) + "&");
        }

        if(keyValuePairRequest.length()>0){
            return keyValuePairRequest.substring(0,keyValuePairRequest.length()-1);
        }
        return null;
    }
}
