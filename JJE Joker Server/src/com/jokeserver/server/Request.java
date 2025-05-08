package com.jokeserver.server;

import java.io.Serializable;

public class Request implements Serializable {
    private final String version = "1.0";
    private final String command;
    private final Object payload;
    private final String authToken; // For role-based authentication

    public Request(String command, Object payload, String authToken) {
        this.command = command;
        this.payload = payload;
        this.authToken = authToken;
    }

    public String getCommand() {
        return command;
    }

    public Object getPayload() {
        return payload;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getVersion() {
        return version;
    }
}