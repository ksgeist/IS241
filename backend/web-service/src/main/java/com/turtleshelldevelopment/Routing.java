package com.turtleshelldevelopment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.endpoints.*;
import com.turtleshelldevelopment.pages.*;
import com.turtleshelldevelopment.utils.Issuers;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.TokenUtils;
import com.turtleshelldevelopment.utils.permissions.PermissionType;
import com.turtleshelldevelopment.utils.permissions.Permissions;
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
        after((req, resp) -> {
            //Refresh Token
            if(resp.status() == 200) {
                DecodedJWT jwt = JWT.decode(req.cookie("token"));
                JWTAuthentication.generateAuthToken(jwt.getSubject(), new Permissions(jwt.getSubject()).getPermissionsAsString(), resp);
            }
        });
        path("/", () -> {
            get("/", (req, resp) -> {
                if(req.cookie("token") != null) {
                    resp.redirect("/dashboard");
                    return "";
                }
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().build(), "/frontend/index.vm"));
            });
            get("/dashboard", new DashboardPage());
            path("/site", () -> {
                get("/add", new SiteCreatePage());
                post("/add", new NewSiteEndpoint());
            });
            path("/user", () -> {
                get("/add", new UserCreatePage());
                post("/add", new NewAccountEndpoint());
            });
            path("/patient", () -> {
                get("/view/:id", new ViewPatientPage());
                get("/print/:id", new PrintRecordPage());
            });
            post("/print_record/print", new PrintInfoPage());
            path("/record", () -> {
                get("/add", new AddRecordPage());
                post("/add", new AddRecordEndpoint());
                patch("/edit", new UpdateRecordEndpoint());
                get("/search", new SearchRecordPage());
                post("/search", new SearchPatientsEndpoint());
            });
            path("/vaccine", () -> {
                get("/add/:id", new NewDosePage());
                post("/add/:id", new AddVaccineEndpoint());
            });
            path("/contact", () -> {
                //Move to delete later
                get("/remove/:id", new DeleteContactInformationEndpoint());
                get("/add/:id", new AddContactPage());
            });
            path("/insurance", () -> {
                get("/remove/:id", new DeleteInsuranceInformationEndpoint());
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
