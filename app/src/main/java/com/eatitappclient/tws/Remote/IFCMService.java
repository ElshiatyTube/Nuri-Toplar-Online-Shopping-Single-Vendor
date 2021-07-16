package com.eatitappclient.tws.Remote;


import com.eatitappclient.tws.Model.FCMResponse;
import com.eatitappclient.tws.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA0ui3tN8:APA91bFXuRN7r2E0fT3opkHtbbfwPP7CHO1auShn6WmjgdsHE-G4xo-EVYizOta4f_VsG6LaHueX1X3QXEuMhfRAwR8CQSyL7IxQ47qSMpw-tT1kOCJmsweZTtW8wEiYNIZ1mcEzfirb"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
