package com.turtleshelldevelopment.utils;

import com.turtleshelldevelopment.PermissionType;
import org.json.simple.JSONObject;

import java.util.List;

public class PermissionUtils {
    public static JSONObject getPermissionValues(List<String> permissionClaims) {
        JSONObject permissions = new JSONObject();
        for(PermissionType type : PermissionType.values()) {
            permissions.put(type.getVal().toLowerCase(), permissionClaims.contains(type.getVal()));
        }
        return permissions;
    }
}
