package com.turtleshelldevelopment;

import com.turtleshelldevelopment.utils.EnvironmentType;
import com.turtleshelldevelopment.utils.Issuers;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.permissions.PermissionType;
import com.turtleshelldevelopment.utils.TokenUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.halt;

public class EndpointFilters {

    public static void verifyCredentials(Request req, Response resp, PermissionType requiredEntitlement) {
        if(BackendServer.environment.equals(EnvironmentType.FRONT_DEVEL)) return;
        TokenUtils tokenUtils = new TokenUtils(req.cookie("token"), Issuers.AUTHENTICATION.getIssuer());
        if(!tokenUtils.isInvalid()) {
            if(requiredEntitlement == null) return;
            if(!tokenUtils.getPermissions().get(requiredEntitlement)) {
                ModelUtil error = new ModelUtil()
                        .addError(401, "You do not have permission to view this page");
                halt(401, new VelocityTemplateEngine().render(new ModelAndView(error.build(), "/frontend/error.vm")));
            }
        } else {
            resp.redirect("/");
            ModelUtil error = new ModelUtil()
                    .addError(401, tokenUtils.getErrorReason());
            halt(401, new VelocityTemplateEngine().render(new ModelAndView(error.build(), "/frontend/error.vm")));
        }
    }
    public static void verifyCredentials(Request req, Response res) {
        verifyCredentials(req, res, null);
    }
}
