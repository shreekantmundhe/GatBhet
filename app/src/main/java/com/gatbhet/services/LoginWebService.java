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
            requestParams.put("request_for","profile");//alerts,audio,profile
            requestParams.put("caller_ref_id","9766363775");
           // requestParams.put("long","50");
           // requestParams.put("lat","100");
            Util.log("Login","Security Token : " + Util.createSecurityToken(request_token,timeStamp,requestParams));
            String security_token= Util.createSecurityToken(request_token,timeStamp,requestParams);
            headers.put("Content-Type","text/xml;charset=windows-1250");
            headers.put("Security-Token",security_token);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public LoginWebService(String request_token,String request_for,String caller_ref_id,String latitude,String longitude){
        try {
            String timeStamp = Util.getTimeStamp();
            Util.log("Login","Time Stamp : " + timeStamp);
            requestParams = new HashMap<String, String>();
            headers = new HashMap<String, String>();
            requestParams.put("timestamp",timeStamp);
            requestParams.put("request_token", request_token);
            requestParams.put("request_for",request_for);//alerts,audio,profile
            requestParams.put("caller_ref_id",caller_ref_id);
            requestParams.put("long",latitude);
            requestParams.put("lat",longitude);
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

        return Util.getKeyValueQueryParam(requestParams);
    }
}
