package com.eatitappclient.tws.Services;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eatitappclient.tws.Common.Common;
import com.eatitappclient.tws.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMServices  extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String,String> dataRecv = remoteMessage.getData();
        if (dataRecv != null)
        {
            if (dataRecv.get(Common.IS_SEND_IMAGE) != null &&
                    dataRecv.get(Common.IS_SEND_IMAGE).equals("true"))
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Common.IS_OPEN_ACTIVITY_NORMAL_NOTIFY, true);
                Glide.with(this)
                        .asBitmap()
                        .load(dataRecv.get(Common.IMAGE_URL))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                Common.showNotificationBigStyle(MyFCMServices.this,new Random().nextInt(),
                                        dataRecv.get(Common.NOTI_TITLE),
                                        dataRecv.get(Common.NOTI_CONTENT),
                                        resource,
                                        intent);
                                Log.d("notifiNews",Common.NOTI_CONTENT);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

            }
            else {
                if (dataRecv.get(Common.NOTI_TITLE).equals("تم تحديث حالة طلبك") || dataRecv.get(Common.NOTI_TITLE).contains("Your order")) { //Notification from Order Update
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER, true);

                    Common.showNotification(this, new Random().nextInt(),
                            dataRecv.get(Common.NOTI_TITLE),
                            dataRecv.get(Common.NOTI_CONTENT),
                            intent);
                } else { //Notification From news
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(Common.IS_OPEN_ACTIVITY_NORMAL_NOTIFY, true);
                    Common.showNotification(this, new Random().nextInt(),
                            dataRecv.get(Common.NOTI_TITLE),
                            dataRecv.get(Common.NOTI_CONTENT),
                            intent);
                }
            }
        }
    }



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Common.updateToken(this,s);
    }
}
