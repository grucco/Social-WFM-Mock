package it.acea.android.socialwfm.utils;

import java.util.Locale;

public enum HttpMethod {
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    CONNECT("CONNECT"),
    PATCH("PATCH");

    private final String description;

    HttpMethod(String description) {
        this.description = description;
    }

    public final String toString() {
        return description;
    }

    public static HttpMethod fromString(String method) {
        method = method.toUpperCase(Locale.ROOT);
        if (method.equals(GET.toString())) {
            return GET;
        } else if (method.equals(HEAD.toString())) {
            return HEAD;
        } else if (method.equals(POST.toString())) {
            return POST;
        } else if (method.equals(PUT.toString())) {
            return PUT;
        } else if (method.equals(DELETE.toString())) {
            return DELETE;
        } else if (method.equals(OPTIONS.toString())) {
            return OPTIONS;
        } else if (method.equals(TRACE.toString())) {
            return TRACE;
        } else if (method.equals(CONNECT.toString())) {
            return CONNECT;
        } else if (method.equals(PATCH.toString())) {
            return PATCH;
        }
        throw new IllegalArgumentException("Unrecognized HTTP method " + method + ".");
    }
}
