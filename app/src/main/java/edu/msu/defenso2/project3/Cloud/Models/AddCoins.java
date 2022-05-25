package edu.msu.defenso2.project3.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="user")
public class AddCoins {
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


    public AddCoins() {}

    public AddCoins(String status, String msg) {
        this.status = status;
        this.message = msg;
    }
}
