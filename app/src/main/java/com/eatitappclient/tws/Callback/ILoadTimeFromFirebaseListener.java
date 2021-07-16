package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.Order;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(Order order , long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
