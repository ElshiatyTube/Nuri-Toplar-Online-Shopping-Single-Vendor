package com.eatitappclient.tws.EventBus;

public class GoToOrdersEvent {
    private boolean success;

    public GoToOrdersEvent(boolean success) {
        this.success = success;
    }

    public GoToOrdersEvent() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
