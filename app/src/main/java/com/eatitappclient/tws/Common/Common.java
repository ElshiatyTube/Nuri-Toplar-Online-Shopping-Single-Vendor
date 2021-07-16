package com.eatitappclient.tws.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.eatitappclient.tws.HomeActivity;
import com.eatitappclient.tws.Model.AddonModel;
import com.eatitappclient.tws.Model.BestDealsModel;
import com.eatitappclient.tws.Model.CategoryModel;
import com.eatitappclient.tws.Model.FoodModel;
import com.eatitappclient.tws.Model.ShippingOrderModel;
import com.eatitappclient.tws.Model.SizeModel;
import com.eatitappclient.tws.Model.TokenModel;
import com.eatitappclient.tws.Model.UserModel;
import com.eatitappclient.tws.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String USER_REFFERENCES="Users";
    public static final String POPULAR_CATEGORY_REF = "MostPopular";
    public static final String BEST_DEAL_REF = "BestDeals";
    public static final int DEFAULT_CLUMN_COUNT = 0;
    public static final int FULL_WIDTH_CLOUMN = 1;
    public static final String CATEGORY_REF = "Category";
    public static final String COMMENT_REF = "Comments";
    public static final String ORDER_REF = "Orders";
    public static final String SHIPPING_ORDER_REF = "ShippingOrder";
    public static final String OPEN_HOURS_REF = "OpenHours";
    public static final String BEST_DEALS = "BestDeals";

    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "content";
    public static final String BRANCHES_REF = "Branches";
    public static final String FOOD_REF = "foods";
    public static  String SELECTED_BRANCH ;
    public static  String DELVPRICE ;
    public static  String RESTURANT_STATU = null;
    public static  Boolean OFFERS_STATU = null;

    public static  double PLACE_LNG =0;
    public static double PLACE_LAT =0;
    private static final String TOKEN_REF = "Tokens";
    public static final String IS_OPEN_ACTIVITY_NEW_ORDER = "IsOpenActivityNewOrder";
    public static final String IS_OPEN_ACTIVITY_NORMAL_NOTIFY = "IsOpenNormalNotify";

    public static final String IS_SUBSCRIBE_NEWS = "IS_SUBSCRIBE_NEWS";
    public static final String NEWS_TOPIC = "news";

    public static final String IS_SEND_IMAGE = "IS_SEND_IMAGE"; //Sme of Server app
    public static final String IMAGE_URL = "IMAGE_URL";

    public static UserModel currentUser;
    public static CategoryModel categroySelected;
    public static FoodModel selectedFood;
    public static BestDealsModel selectedBestDeals;

    public static ShippingOrderModel currentShippingOrder;
    //  public static void currentToken;
//"#,##0.00"
    public static String formatPrice(double displayPrice) {
        if (displayPrice!=0)
        {
            DecimalFormat df=new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice=new StringBuilder(df.format(displayPrice)).toString();
            return finalPrice.replace(".",".");
        }
        else
          return "0,00";
    }


       public static void showNotificationBigStyle(Context context, int id, String title, String content, Bitmap bitmap, Intent intent) {
        PendingIntent pendingIntent =null;
           Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           AudioAttributes attributes = new AudioAttributes.Builder()
                   .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                   .build();
           if (intent!=null)
            pendingIntent=PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID ="Nuri_Toplar_Notification";
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Nuri Toplar Notification",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Nuri_Toplar_Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setSound(uri,attributes);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setSound(uri)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));

        if (pendingIntent !=null)
            builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        notificationManager.notify(id,notification);


    }


    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent =null;
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        if (intent!=null)
            pendingIntent=PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID ="Nuri_Toplar_Notification";
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Nuri Toplar Notification",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Nuri Toplar Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setSound(uri,attributes);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setSound(uri)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_restaurant_menu_black_24dp));
        if (pendingIntent !=null)
            builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        notificationManager.notify(id,notification);


    }


    public static void getOffersStatue(){

        FirebaseDatabase.getInstance()
                .getReference(Common.OPEN_HOURS_REF)
                .child("offers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            Common.OFFERS_STATU=dataSnapshot.getValue(Boolean.class);
                            if (dataSnapshot.getValue(Boolean.class).equals(false)){
                                HomeActivity.hideItem();
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void getOpenStatue(Context context){

        FirebaseDatabase.getInstance()
                .getReference(Common.OPEN_HOURS_REF)
                .child("statue")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            Common.RESTURANT_STATU=dataSnapshot.getValue(String.class);
                            if (dataSnapshot.getValue(String.class).equals("close")){
                                CloseDialog(context);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void CloseDialog(Context context){
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.restaurantclosed))
                .setMessage(context.getResources().getString(R.string.restaurantnow))
                .setCancelable(false)

                .setPositiveButton(context.getResources().getString(R.string.OkOk), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_do_not_disturb_black_24dp)
                .show();
    }


    public static Double calculateExtraPrice(SizeModel userSelectedSize, List<AddonModel> userSelectedAddon) {
        Double result=0.0;
        if (userSelectedSize == null && userSelectedAddon == null)
            return 0.0;
        else if (userSelectedSize == null)
        {
            //if
            for (AddonModel addonModel : userSelectedAddon)
                result+=addonModel.getPrice();
            return result;
        }
        else if (userSelectedAddon == null)
        {
            return userSelectedSize.getPrice()*1.0;
        }
        else
        {
            result = userSelectedSize.getPrice()*1.0;
            for (AddonModel addonModel : userSelectedAddon)
                result+=addonModel.getPrice();
            return result;
        }
    }

    public static void setSpanString(String s, String name, TextView txt_user) {
        SpannableStringBuilder builder=new SpannableStringBuilder();
        builder.append(s);
        SpannableString spannableString=new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        txt_user.setText(builder,TextView.BufferType.SPANNABLE);
    }

    public static String createOrderNumber() {
        return new StringBuilder()
                .append(System.currentTimeMillis())
                .append(Math.abs(new Random().nextInt()))
                .toString();
    }

    public static String getDateOfWeek(int i) {
       switch (i)
       {
           case 1:
               return "Monday";
           case 2:
               return "Tuesday";
           case 3:
               return "Wednesday";
           case 4:
               return "Thursday";
           case 5:
               return "Friday";
           case 6:
               return "Saturday";
           case 7:
               return "Sunday";
               default:
                   return "Unk";
       }
    }



    public static String convertStatusToText(int orderStatus,Context context) {
        switch (orderStatus)
        {
            case 0:
                return context.getResources().getString(R.string.Placed);
            case 1:
                return context.getResources().getString(R.string.Preparing);
            case 2:
                return context.getResources().getString(R.string.Shipping);
            case 3:
                return context.getResources().getString(R.string.Shipped);
            case -1:
                return context.getResources().getString(R.string.Cancelled);
            default:
                return "Error";
        }
    }


    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while (index < len)
        {
            int b,shift=0,result=0;
            do {
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~ (result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while (b >= 0x20);
            int dlng =((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat= Math.abs(begin.latitude-end.latitude);
        double lng= Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng/lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng/lat)))+90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng/lat))+180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng/lat)))+270);

        return -1;

    }

    public static void updateToken(Context context, String newToken) {
        try {
            if (Common.currentUser != null)
                FirebaseDatabase.getInstance()
                        .getReference(Common.TOKEN_REF)
                        .child(Common.currentUser.getUid())
                        .setValue(new TokenModel(Common.currentUser.getPhone(),newToken))
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        });
        }
      catch (Exception e){}



    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }

    public static String getListAddon(List<AddonModel> addonModels) {
        StringBuilder result = new StringBuilder();
        for (AddonModel addonModel:addonModels)
        {
            result.append(addonModel.getName()).append(",");
        }
        return result.substring(0,result.length()-1); //Remove last ","
    }

    public static FoodModel findFoodInListById(CategoryModel categoryModel, String foodId) {
        if (categoryModel.getFoods() != null && categoryModel.getFoods().size() > 0)
        {
            for (FoodModel foodModel:categoryModel.getFoods())
                if (foodModel.getId().equals(foodId))
                    return foodModel;

                return null;
        }
        else
            return null;
    }
}
