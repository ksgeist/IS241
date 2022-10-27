package com.turtleshelldevelopment.utils;

import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.utils.permissions.PermissionType;
import org.json.JSONObject;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModelUtil {

    private static final String headerFile;

    static {
        try {
            headerFile = IOUtils.toString(Objects.requireNonNull(BackendServer.class.getResourceAsStream("/header.html")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final JSONObject modelData = new JSONObject();

    public ModelUtil() {
        modelData.put("header", headerFile);
    }

    public ModelUtil addPermissions(HashMap<PermissionType, Boolean> perms) {
        if(perms == null) return this;
        for(Map.Entry<PermissionType, Boolean> permission : perms.entrySet()) {
            modelData.put(permission.getKey().getVal().toLowerCase(), permission.getValue());
        }
        return this;
    }
    public ModelUtil add(String key, Object value) {
        modelData.put(key, value);
        return this;
    }

    public ModelUtil addError(int errorCode, String errorMessage) {
        modelData.put("error_code", errorCode);
        modelData.put("error_message", errorMessage);
        return this;
    }

    public ModelUtil addMFAError(boolean success, String message, boolean shouldRetry) {
        modelData.put("success", success);
        modelData.put("messager", message);
        modelData.put("retry", shouldRetry);
        return this;
    }

    public JSONObject build() {
        return this.modelData;
    }
}
