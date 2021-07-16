package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.Order;

import java.util.List;

public interface ILoadOrderCallBackListener {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFailed(String message);
}
