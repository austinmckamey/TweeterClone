package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {
    /**
     * Value of the auth token.
     */
    public String token;
    /**
     * String representation of date/time at which the auth token was created.
     */
    public long datetime;

    public AuthToken(String token) {
        this.token = token;
    }

    public AuthToken() {
        UUID uuid = UUID.randomUUID();
        this.token = uuid.toString();

        Date date = new Date();
        this.datetime = date.getTime();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getDatetime() {
        return datetime;
    }
}