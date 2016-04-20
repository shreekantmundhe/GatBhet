package com.gatbhet.config;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.gatbhet.R;
import com.gatbhet.config.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by tinuzz on 04/08/2016.
 */
public class Util {

    public static String convertInputStreamToString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String read;
        try {
            while ((read = br.readLine()) != null) {
                //System.out.println(read);
                sb.append(read);
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void log(String tag,String message){
        if(Constants.IS_LOGGING_ENABLED){
            Log.d(tag,message);
        }
    }


    public static String getMD5(String input) throws NoSuchAlgorithmException {
        String md5="MD5";
        MessageDigest digest = java.security.MessageDigest
                .getInstance(md5);
        digest.update(input.getBytes());
        byte messageDigest[] = digest.digest();
        Util.log("Util","MD5 : " + convertToHex(messageDigest));
        return convertToHex(messageDigest);
    }

    public static String getSHA256(String input) throws NoSuchAlgorithmException {
        String sha256="SHA256";
        MessageDigest digest = java.security.MessageDigest
                .getInstance(sha256);
        digest.update(input.getBytes());
        byte messageDigest[] = digest.digest();
        Util.log("Util","SHA256 : " + convertToHex(messageDigest));
        return convertToHex(messageDigest);
    }

    public static String convertToHex(byte[] input){
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : input)
        {
            String h = Integer.toHexString(0xFF & aMessageDigest);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
    }

    public static String getTimeStamp(){

        Date todayDate = new Date();
        long  time = todayDate.getTime();
        Util.log("Util","timestamp : " + String.valueOf(time));
        return String.valueOf(time) ;

    }

    public  static  String createSecurityToken(String request_token, String timeStamp,HashMap<String,String> params) throws NoSuchAlgorithmException {
        Random random = new Random();
        int offset = random.nextInt(6) + 4;
//        Util.log("","Offset Vaue : "+offset);
        int length = random.nextInt(2) + 8;
//        Util.log("","Length Vaue : "+length);

        String prefix = String.valueOf(offset +""+ length);
//        Util.log("","prefix Vaue : "+prefix);

//        Util.log("","MD5 for user_pass : "+ getMD5(Constants.user_pass));
//        Util.log("","Timestamp Vaue : "+timeStamp);
//        Util.log("","request token Vaue : "+request_token);
        String security_token = getSHA256(request_token + getMD5(Constants.user_pass)+timeStamp);
        String sufix = security_token.substring(offset,offset+length);
//        Util.log("","sufix Vaue : "+sufix);


        security_token = security_token.substring(0,offset) + security_token.substring(offset+length,security_token.length());
//        Util.log("","Security token after applying offset and lenght substring replace : "+security_token);
        security_token = prefix+security_token+sufix;
//        Util.log("","Security token after concating offset and lenght : "+security_token);
        security_token += getMD5(getValuesOfParam(params));  // Final security token, this will change in each request
//        Util.log("","param : "+params);
//        Util.log("","Security token after MD5 of params : "+security_token);
        return  security_token;

    }

    public static String getKeyValueQueryParam(HashMap<String,String> requestParams) {
        StringBuffer keyValuePairRequest = new StringBuffer();
        for (String key : requestParams.keySet()) {
            keyValuePairRequest.append(key + "=" + requestParams.get(key) + "&");
        }

        if(keyValuePairRequest.length()>0){
            return keyValuePairRequest.substring(0,keyValuePairRequest.length()-1);
        }
        return null;
    }

    public static String getValuesOfParam(HashMap<String,String> requestParams) {
        StringBuffer keyValuePairRequest = new StringBuffer();
        for (String key : requestParams.keySet()) {
            keyValuePairRequest.append(requestParams.get(key));
        }


        return keyValuePairRequest.toString();
    }

    public static void displayNotification(Context context){
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Gatbhet")
                        .setContentText("This is sample notification");
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(Constants.MID, mBuilder.build());

    }
}
