package com.turtleshelldevelopment;

public class Config {
    //Authentication Configurations
    public static final int AUTHENTICATION_EXPIRE = 10*60;

    //MFA Configuration
    public static final boolean REQUIRE_MFA = true;
    public static final int MFA_TOKEN_TIMEOUT = 180;
}
