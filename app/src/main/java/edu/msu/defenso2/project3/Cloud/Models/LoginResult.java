package edu.msu.defenso2.project3.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "user")
public class LoginResult {
    @Attribute(name = "id", required = false)
    private int id;

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }


    @Attribute(name = "msg", required = false)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Attribute(name = "status", required = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LoginResult() {}

    public LoginResult(String status, int id) {
        this.status = status;
        this.id = id;
    }
}