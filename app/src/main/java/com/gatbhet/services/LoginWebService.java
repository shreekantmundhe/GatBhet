package com.gatbhet.services;

import com.gatbhet.config.Constants;

import java.util.HashMap;

/**
 * Created by a514192 on 10-04-2016.
 */
public class LoginWebService implements IWebService {

    HashMap<String,String> requestParams;
    HashMap<String,String> headers;

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void getLoginResponse(){
    WebServiceHelper webServiceHelper = new WebServiceHelper();

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
