package com.turtleshelldevelopment.pages;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.utils.Issuers;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.TokenUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

public interface IS241Page extends Route {
    boolean checkPermissions(DecodedJWT jwtToken);
    Object handleRequest(Request request, Response response);

    @Override
    default Object handle(Request request, Response response) {
        TokenUtils tokenVerifier = new TokenUtils(request.cookie("token"), Issuers.AUTHENTICATION.getIssuer());
        if(checkPermissions(tokenVerifier.getDecodedJWT())) {
            return this.handle(request, response);
        } else {
            return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().build(), "error.vm"));
        }
    }
}
