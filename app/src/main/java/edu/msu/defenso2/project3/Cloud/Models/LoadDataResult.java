package edu.msu.defenso2.project3.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="user")
public class LoadDataResult {
    @Attribute(name = "msg", required = false)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Attribute(name = "status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Attribute(name="coins", required = false)
    private int coins;

    public int getCoins() { return coins; }

    public void setCoins(int coins) { this.coins = coins; }


    public LoadDataResult() {}

    public LoadDataResult(String status, String msg) {
        this.status = status;
        this.message = msg;
    }
}
