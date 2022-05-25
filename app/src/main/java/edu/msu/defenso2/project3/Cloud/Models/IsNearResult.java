package edu.msu.defenso2.project3.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "coin")
public class IsNearResult {
    @Attribute(name = "id", required = false)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Attribute(name = "status", required = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Attribute(name = "coins", required = false)
    private int coins;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }


    public IsNearResult() {
    }

    public IsNearResult(String status, int id, int coins) {
        this.status = status;
        this.id = id;
        this.coins = coins;
    }
}