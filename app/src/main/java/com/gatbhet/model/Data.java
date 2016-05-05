package com.gatbhet.model;

import java.util.ArrayList;

/**
 * Created by a514192 on 10-04-2016.
 */
public class Data {
    String request_token;
    User user;
    ArrayList<Alert> alerts;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(ArrayList<Alert> alerts) {
        this.alerts = alerts;
    }

    public String getRequest_token() {
        return request_token;
    }

    public void setRequest_token(String request_token) {
        this.request_token = request_token;
    }
}
