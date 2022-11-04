package com.turtleshelldevelopment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.endpoints.*;
import com.turtleshelldevelopment.pages.*;
import com.turtleshelldevelopment.endpoints.AddInsuranceInformationEndpoint;
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

    /**
     * Sets up routing for system REST API
     */
    public Routing() {
        port(8091);
        exception(Exception.class, (exception, request, response) -> BackendServer.serverLogger.error(exception.getMessage()));
        staticFileLocation("/frontend");
        BackendServer.serverLogger.info("Setting up API Server routes...");
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
            before("/dashboard", EndpointFilters::verifyCredentials);
            get("/dashboard", new DashboardPage());
            post("/print_record/print", new PrintInfoPage());
            createAPIRoutes();
            createContactRoutes();
            createInsuranceRoutes();
            createRecordRoutes();
            createPatientRoutes();
            createSiteRoutes();
            createUserRoutes();
            createVaccineRoutes();
        });
        BackendServer.serverLogger.info("Ready to Fire");
    }

    public void createAPIRoutes() {
        path("/api", () -> get("/lookupAddress", new GeocodingEndpoint()));
    }

    public void createSiteRoutes() {
        path("/site", () -> {
            before("/add", (req, res) -> verifyCredentials(req, res, PermissionType.ADD_SITE));
            get("/add", new SiteCreatePage());
            post("/add", new NewSiteEndpoint());
        });
    }

    public void createInsuranceRoutes() {
        path("/insurance", () -> {
            post("/add/:user_id", new AddInsuranceInformationEndpoint());
            get("/add/:user_id", new AddInsuranceInformationPage());
            get("/remove/:id", new DeleteInsuranceInformationEndpoint());
        });
    }

    public void createUserRoutes() {
        path("/user", () -> {
            before("/add", (req, resp) -> verifyCredentials(req, resp, PermissionType.ADD_USER));
            get("/add", new UserCreatePage());
            post("/add", new NewAccountEndpoint());
            path("/login", () -> {
                before("/mfa", (req, res) -> {
                    TokenUtils tokenUtils = new TokenUtils(req.cookie("token"), Issuers.MFA_LOGIN.getIssuer());
                    if (tokenUtils.isInvalid()) {
                        //Invalid token, Remove it
                        res.cookie("/", "token", null, 0, true, true);
                        halt(401, new ModelUtil().addMFAError(false, "Invalid Token", false).build().toString());
                    }
                });
                post("/mfa", new MfaEndpoint());
            });
            post("/login", new LoginEndpoint());
            before("/logout", EndpointFilters::verifyCredentials);
            get("/logout", new LogoutEndpoint());
        });
    }

    public void createPatientRoutes() {
        path("/patient", () -> {
            get("/view/:id", new ViewPatientPage());
            get("/print/:id", new PrintRecordPage());
        });
    }

    public void createVaccineRoutes() {
        path("/vaccine", () -> {
           get("/add/:id", new NewDosePage());
           post("/add/:id", new AddVaccineEndpoint());
        });
    }

    public void createContactRoutes() {
        path("/contact", () -> {
            //Move to delete later
            get("/remove/:id", new DeleteContactInformationEndpoint());
            get("/add/:id", new AddContactPage());
        });
    }

    public void createRecordRoutes() {
        path("/record", () -> {
            before("/add", (req, resp) -> verifyCredentials(req, resp, PermissionType.WRITE_PATIENT));
            get("/add", new AddRecordPage());
            post("/add", new AddRecordEndpoint());
            patch("/edit", new UpdateRecordEndpoint());
            get("/search", new SearchRecordPage());
            post("/search", new SearchPatientsEndpoint());
        });
    }

    public void fire() {
        ignite();
        BackendServer.serverLogger.info("We have Lift off!");
    }
}
