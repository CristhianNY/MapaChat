package com.optimusfly.cali1.mapa.Services;

/**
 * Created by cali1 on 15/11/2017.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.optimusfly.cali1.mapa.MainActivity;
import com.optimusfly.cali1.mapa.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cali1 on 14/11/2017.
 */

public class myfirebasemensajes extends FirebaseMessagingService {


    String type ="";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()>0){
            type= "json";
            showNotification(remoteMessage.getData().toString());
        }
        if(remoteMessage.getNotification() != null){

            type="message";
            showNotification(remoteMessage.getNotification().getBody());
        }

    }

    private void showNotification(String messageBody) {

        String id= "",mensaje= "",title="";

        if(type.equals("json")){


            try {
                JSONObject jsonObject = new JSONObject(messageBody);
                id = jsonObject.getString("id");
                mensaje = jsonObject.getString("message");
                title = jsonObject.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            mensaje = messageBody;
        }

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM Cristhian bonilla")
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        Uri sonundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        builder.setSound(sonundUri);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        manager.notify(0,builder.build());
    }
}
