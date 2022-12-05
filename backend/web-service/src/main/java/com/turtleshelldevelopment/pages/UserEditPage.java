package com.turtleshelldevelopment.pages;

import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.db.Account;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

public class UserEditPage implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        ModelUtil model = new ModelUtil(request);
        model.add("user", Account.getAccountInfo(request.params("requested_user")));
        return new VelocityTemplateEngine().render(new ModelAndView(model ,"/frontend/edit_user.vm"));
    }
}
