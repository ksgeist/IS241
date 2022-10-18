package com.turtleshelldevelopment.pages;

import com.turtleshelldevelopment.utils.ModelUtil;
import org.json.simple.JSONObject;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

public class SiteCreatePage implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        JSONObject modelInfo = new ModelUtil().build();
        return new VelocityTemplateEngine().render(new ModelAndView(modelInfo, "/frontend/create_site.vm"));
    }
}
