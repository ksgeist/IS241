package com.turtleshelldevelopment.utils;

import com.turtleshelldevelopment.PermissionType;

import java.util.HashMap;

public class DevelopmentTokenUtils {

    public HashMap<PermissionType, Boolean> getPermissions() {
        HashMap<PermissionType, Boolean> permissionData = new HashMap<>();
        for(PermissionType type : PermissionType.values()) {
            permissionData.put(type, true);
        }
        return permissionData;
    }

    public boolean isInvalid() {
        return false;
    }
}
