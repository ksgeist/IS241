package com.turtleshelldevelopment;

import com.turtleshelldevelopment.endpoints.*;
import com.turtleshelldevelopment.pages.*;
import com.turtleshelldevelopment.utils.Issuers;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.TokenUtils;
import com.turtleshelldevelopment.utils.permissions.PermissionType;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import static com.turtleshelldevelopment.EndpointFilters.verifyCredentials;
import static spark.Service.ignite;
import static spark.Spark.*;

public class Routing {

    public Routing() {
        port(8091);
        exception(Exception.class, (exception, request, response) -> BackendServer.serverLogger.error(exception.getMessage()));
        BackendServer.serverLogger.info("Routing /login");
        staticFileLocation("/frontend");
        before("/dashboard", EndpointFilters::verifyCredentials);
        before("/api/logout", EndpointFilters::verifyCredentials);
        before("/user/add", (req, resp) -> verifyCredentials(req, resp, PermissionType.ADD_USER));
        before("/api/login/mfa", (req, res) -> {
            TokenUtils tokenUtils = new TokenUtils(req.cookie("token"), Issuers.MFA_LOGIN.getIssuer());
            if (tokenUtils.isInvalid()) {
                //Invalid token, Remove it
                res.cookie("/", "token", null, 0, true, true);
                halt(401, new ModelUtil().addMFAError(false, "Invalid Token", false).build().toString());
            }
        });
        before("/site/create", (req, res) -> verifyCredentials(req, res, PermissionType.ADD_SITE));
        before("/record/add", (req, resp) -> verifyCredentials(req, resp, PermissionType.WRITE_PATIENT));
        path("/", () -> {
            get("/", (req, resp) -> new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().build(), "/frontend/index.vm")));
            get("/dashboard", new DashboardPage());
            path("/site", () -> {
                get("/create", new SiteCreatePage());
            });
            path("/user", () -> {
                get("/add", new UserCreatePage());
                post("/add", new NewAccountEndpoint());
            });
            post("/print_record/print", new PrintInfo());
            path("/record", () -> {
                get("/add", new AddRecordPage());
                post("/add", new AddEntryEndpoint());
                get("/edit", new EditRecordPage());
                patch("/edit", new UpdateRecordEndpoint());
            });
        });
        path("/api", () -> {
            path("/login", () -> post("/mfa", new MfaEndpoint()));
            post("/login", new LoginEndpoint());
            get("/logout", new LogoutEndpoint());
            BackendServer.serverLogger.info("Routing /account");
            path("/account", () -> {
                BackendServer.serverLogger.info("Routing /account/new");
                post("/new", new NewAccountEndpoint());
            });
            post("/site/add", new NewSiteEndpoint());
            get("/lookupAddress", new GeocodingEndpoint());
        });
        BackendServer.serverLogger.info("Ready to Fire");
    }
    public void fire() {
        ignite();
        BackendServer.serverLogger.info("We have Lift off!");
    }
}
