package com.jokeserver.server;

import java.io.Serializable;

public class Response implements Serializable {
    private final String version = "1.0";
    private final String status;
    private final Object data;

    public Response(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getVersion() {
        return version;
    }
}