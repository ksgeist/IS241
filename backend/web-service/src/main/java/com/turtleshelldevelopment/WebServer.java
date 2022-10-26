package com.turtleshelldevelopment;

import com.auth0.jwt.algorithms.Algorithm;
import com.turtleshelldevelopment.endpoints.*;
import com.turtleshelldevelopment.pages.AddRecordPage;
import com.turtleshelldevelopment.pages.DashboardPage;
import com.turtleshelldevelopment.pages.SiteCreatePage;
import com.turtleshelldevelopment.pages.UserCreatePage;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.TokenUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import static com.turtleshelldevelopment.EndpointFilters.verifyCredentials;
import static spark.Service.ignite;
import static spark.Spark.*;

@CommandLine.Command(name = "WebServer", mixinStandardHelpOptions = true)
public class WebServer implements Runnable {
    public static Dotenv env;
    public static final Logger serverLogger = LoggerFactory.getLogger("Dashboard-Backend");
    public static Algorithm JWT_ALGO;
    public static Database database;

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Valid values: ${COMPLETION-CANDIDATES}")
    public static EnvironmentType environment = EnvironmentType.PROD;

    @CommandLine.Option(names = {"-db", "--database-type"}, description = "Should use test Database")
    boolean enableTestDb = false;

    @Override
    public void run() {
        serverLogger.info("Starting backend in " + environment.name());
        serverLogger.info("Loading .env...");
        env = Dotenv.load();
        serverLogger.info("Loaded .env");
        serverLogger.info("Connecting to Database...");
        if(enableTestDb || environment.equals(EnvironmentType.DEVEL)) {
            database = new Database(env.get("TEST_DB_URL"), env.get("TEST_DB_USERNAME"), env.get("TEST_DB_PASSWORD"));
        } else {
            database = new Database();
        }
        serverLogger.info("Successfully connected to Database!");
        serverLogger.info("Setting up JWT...");
        KeyPair jwtPair;
        try {
            jwtPair = loadOrGenerate();
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        if(jwtPair != null) {
            JWT_ALGO = Algorithm.RSA512((RSAPublicKey) jwtPair.getPublic(), (RSAPrivateKey) jwtPair.getPrivate());
        } else {
            throw new RuntimeException("Failed to generate JWT folder");
        }
        serverLogger.info("Successfully Setup JWT Provider!");
        serverLogger.info("Setting up Endpoints");
        startWebService();
        awaitStop();
    }

    /***
     * Loads or generate public and private key for JWT Authentication
     */
    private static KeyPair loadOrGenerate() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        File storeFolder = new File("store");
        if(!storeFolder.exists()) {
            boolean created = storeFolder.mkdirs();
            if(!created) {
                WebServer.serverLogger.error("Failed to create store folder");
                return null;
            }
        }
        File privateKey = new File("store/private.key");
        File publicKey = new File("store/key.pub");
        if (privateKey.exists() && publicKey.exists()) {
            //Load Files
            byte[] privateKeyData = Files.readAllBytes(privateKey.toPath());
            byte[] pubKey = Files.readAllBytes(publicKey.toPath());

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
            X509EncodedKeySpec pub = new X509EncodedKeySpec(pubKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey loadPrivateKey = keyFactory.generatePrivate(privateKeySpec);
            PublicKey loadPublicKey = keyFactory.generatePublic(pub);
            if (!(loadPublicKey instanceof RSAPublicKey)) {
                throw new IllegalArgumentException("Public Key is not an RSA Public Key");
            } else if (!(loadPrivateKey instanceof RSAPrivateKey)) {
                throw new IllegalArgumentException("Private Key is not an RSA Private Key");
            }
            return new KeyPair(loadPublicKey, loadPrivateKey);
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            FileOutputStream privateKeyFile = new FileOutputStream(privateKey);
            FileOutputStream publicKeyFile = new FileOutputStream(publicKey);
            privateKeyFile.write(kp.getPrivate().getEncoded());
            publicKeyFile.write(kp.getPublic().getEncoded());
            privateKeyFile.close();
            publicKeyFile.close();
            return kp;
        }
    }

    public static void startWebService() {
            port(8091);
            exception(Exception.class, (exception, request, response) -> serverLogger.error(exception.getMessage()));
            serverLogger.info("Routing /login");
            staticFileLocation("/frontend");
            before("/dashboard", EndpointFilters::verifyCredentials);
            before("/api/logout", EndpointFilters::verifyCredentials);
            before("/user/add", (req, resp) -> verifyCredentials(req, resp, PermissionType.ADD_USER));
            before("/api/login/mfa", (req, res) -> {
                TokenUtils tokenUtils = new TokenUtils(req.cookie("token"), Issuers.MFA_LOGIN.getIssuer());
                if (tokenUtils.isInvalid()) {
                    //Invalid token, Remove it
                    res.cookie("/", "token", null, 0, true, true);
                    halt(401, new ModelUtil().addMFAError(false, "Invalid Token", false).build().toJSONString());
                }
            });
            before("/site/create", (req, res) -> verifyCredentials(req, res, PermissionType.ADD_SITE));
            before("/addRecord", (req, resp) -> verifyCredentials(req, resp, PermissionType.WRITE_PATIENT));
            path("/", () -> {
                get("/", (req, resp) -> new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().build(), "/frontend/index.vm")));
                get("/dashboard", new DashboardPage());
                get("/AddRecord", new AddRecordPage());
                post("/AddRecord", new AddEntryEndpoint());
                get("/site/create", new SiteCreatePage());
                path("/user", () -> {
                    get("/add", new UserCreatePage());
                    post("/add", new NewAccountEndpoint());
                });
            });
            path("/api", () -> {
                path("/login", () -> post("/mfa", new MfaEndpoint()));
                post("/login", new LoginEndpoint());
                get("/logout", new LogoutEndpoint());
                serverLogger.info("Routing /account");
                path("/account", () -> {
                    serverLogger.info("Routing /account/new");
                    post("/new", new NewAccountEndpoint());
                });
                post("/site/add", new NewSiteEndpoint());
                get("/lookupAddress", new GeocodingEndpoint());
            });
            serverLogger.info("Ready to Fire");
            ignite();
            serverLogger.info("We have Lift off!");
    }


    /***
     * Created By: Colin Kinzel
     * Modified On: Colin (9/27/22)
     */
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting Down Backend!");
            stop();
            awaitStop();
        }));
        new CommandLine(new WebServer()).execute(args);
    }
}
