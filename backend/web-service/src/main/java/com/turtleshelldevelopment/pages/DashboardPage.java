package com.turtleshelldevelopment.pages;

import com.turtleshelldevelopment.Issuers;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.TokenUtils;
import org.json.simple.JSONObject;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

public class DashboardPage implements Route {
    @Override
    public Object handle(Request request, Response response) {
        TokenUtils tokenVerifier = new TokenUtils(request.cookie("token"), Issuers.AUTHENTICATION.getIssuer());
        if(!tokenVerifier.isInvalid()) {
            JSONObject modelData = new ModelUtil().addPermissions(tokenVerifier.getPermissions()).build();
            return new VelocityTemplateEngine().render(new ModelAndView(modelData, "/frontend/dashboard.vm"));
        } else {
            System.out.println("Token is invalid: " + tokenVerifier.getErrorReason());
            response.redirect("/");
        }
        return "";
    }
}
