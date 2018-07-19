package com.google.firebase.udacity.friendlychat;

/**
 * Created by Momen on 6/29/2018.
 */

public class UserPro {
    private String userName;
    private String userMail;


    public UserPro() {
    }

    public UserPro(String userName, String userMail) {
        this.userName = userName;
        this.userMail = userMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String text) {
        this.userName = text;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String name) { this.userMail = name; }


}
