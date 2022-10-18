package com.turtleshelldevelopment.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.PermissionType;
import com.turtleshelldevelopment.WebServer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TokenUtils {

    private String token;
    private DecodedJWT decodedJWT;
    private boolean invalid;
    private String errorReason = "";

    public TokenUtils(String token, String requiredIssuer) {
        if(token == null || token.isEmpty()) {
            invalid = true;
            return;
        }
        decodedJWT = JWT.decode(token);
        JWTVerifier validator = JWT.require(WebServer.JWT_ALGO).withIssuer(requiredIssuer).build();
        try {
            validator.verify(decodedJWT);
        } catch (TokenExpiredException e) {
            this.invalid = true;
            this.errorReason = "Expired Token";
        } catch (JWTVerificationException e) {
            this.invalid = true;
            this.errorReason = "Invalid Token";
        }
    }

    public HashMap<PermissionType, Boolean> getPermissions() {
        HashMap<PermissionType, Boolean> permissionData = new HashMap<>();
        List<String> allowedPerms = Arrays.asList(decodedJWT.getClaim("perms").asArray(String.class));
        for(PermissionType type : PermissionType.values()) {
            permissionData.put(type, !invalid && allowedPerms.contains(type.getVal()));
        }
        return permissionData;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public String getErrorReason() {
        return this.errorReason;
    }

    public String getToken() {
        return token;
    }
}
