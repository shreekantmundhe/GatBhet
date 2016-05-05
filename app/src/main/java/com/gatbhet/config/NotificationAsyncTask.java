package com.gatbhet.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.gatbhet.model.Alert;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ADMINIBM on 5/6/2016.
 */
public class NotificationAsyncTask extends AsyncTask<Alert,Void,Bitmap> {
    int index=-1;
    String content;
    String title;
    Context context;

    public NotificationAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Alert... params) {
        InputStream in;

        try {
            index++;
            title = params[index].getAtitle();
            content = params[index].getAmsg();
            Util.log("Notification","URL : " + params[index].getAicon().replaceAll("/","").trim());
            in = new URL(params[index].getAicon().replaceAll("/","").trim()).openStream();
            Bitmap bmp = BitmapFactory.decodeStream(in);
            return bmp;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Util.displayNotification(context,title,content,bitmap);
    }
}
