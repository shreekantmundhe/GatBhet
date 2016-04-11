package com.gatbhet.services;

import android.provider.SyncStateContract;

import com.gatbhet.config.Constants;
import com.gatbhet.config.Util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by a514192 on 10-04-2016.
 */
public class WebServiceHelper {

    public String makeWebServiceCall(String queryParam, String data) {
        try {
            URLConnection urlConnection = getURLConnection(queryParam);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            return Util.convertInputStreamToString(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String makeWebServiceCall(String queryParam) {
        try {
            URLConnection urlConnection = getURLConnection(queryParam);
            return Util.convertInputStreamToString(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String makeWebServiceCall(IWebService webService) {
        try {
            URLConnection urlConnection = getURLConnection(webService);
            return Util.convertInputStreamToString(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private URLConnection getURLConnection(IWebService webService) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(Constants.WEB_SERVICE_URL + webService.getURLParams() + "?" + webService.getKeyValueQueryParam());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(webService.getMethodType());
            for(String key : webService.getHeader().keySet()){
                urlConnection.setRequestProperty(key,webService.getHeader().get(key));
            }
//            urlConnection.setRequestProperty();
            //urlConnection.setRequestProperty(Constants.WS_CONTENT_TYPE, Constants.JSON);
            urlConnection.setReadTimeout(Constants.TIME_OUT);
            urlConnection.setRequestProperty(Constants.ACCEPT, "*/*");
            urlConnection.setDoOutput(true);
            urlConnection.setDoOutput(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }

    public URLConnection getURLConnection(String queryParam) {
        try {
            URL url = new URL(Constants.WEB_SERVICE_URL + queryParam);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(Constants.WS_REQUEST_TYPE_GET);
            //urlConnection.setRequestProperty(Constants.WS_CONTENT_TYPE, Constants.JSON);
            urlConnection.setReadTimeout(Constants.TIME_OUT);
            urlConnection.setRequestProperty(Constants.ACCEPT, "*/*");
            urlConnection.setDoOutput(true);
            urlConnection.setDoOutput(true);
            return urlConnection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
