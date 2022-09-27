package com.turtleshelldevelopment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.endpoints.LoginEndpoint;
import com.turtleshelldevelopment.endpoints.LogoutEndpoint;
import com.turtleshelldevelopment.endpoints.MfaEndpoint;
import com.turtleshelldevelopment.endpoints.NewAccountEndpoint;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

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
import java.util.Date;
import java.util.Map;

import static spark.Spark.*;

public class WebServer {
    public static Dotenv env;
    public static final Logger serverLogger = LoggerFactory.getLogger("Dashboard-Backend");
    public static Algorithm JWT_ALGO;
    public static Database database;


    /***
     * Created By: Colin Kinzel
     * Modified On: Colin (9/27/22)
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        serverLogger.info("In: " + System.getProperty("user.dir"));
        serverLogger.info("Loading .env...");
        env = Dotenv.load();
        serverLogger.info("Connecting to Database...");
        if(args.length >= 1 && args[0].equals("use-test-db")) {
            database = new Database(env.get("TEST_DB_URL"), env.get("TEST_DB_USERNAME"), env.get("TEST_DB_PASSWORD"));
        } else {
            database = new Database();
        }
        serverLogger.info("Successfully connected to Database!");
        serverLogger.info("Setting up JWT...");
        KeyPair jwtPair = loadOrGenerate();
        if(jwtPair != null) {
            JWT_ALGO = Algorithm.RSA512((RSAPublicKey) jwtPair.getPublic(), (RSAPrivateKey) jwtPair.getPrivate());
        } else {
            throw new RuntimeException("Failed to generate JWT folder");
        }
        serverLogger.info("Successfully Setup JWT Provider!");
        serverLogger.info("Setting up Endpoints");
        startWebService();
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
        File privateKey = new File("store/priv.key");
        File publicKey = new File("store/key.pub");
        if (privateKey.exists() && publicKey.exists()) {
            //Load Files
            byte[] privKey = Files.readAllBytes(privateKey.toPath());
            byte[] pubKey = Files.readAllBytes(publicKey.toPath());

            PKCS8EncodedKeySpec priv = new PKCS8EncodedKeySpec(privKey);
            X509EncodedKeySpec pub = new X509EncodedKeySpec(pubKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey loadPrivateKey = keyFactory.generatePrivate(priv);
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
        serverLogger.info("Routing /login");
        staticFileLocation("/frontend");
        before("/dashboard.html", WebServer::verifyCredentials);
        before("/api/logout", WebServer::verifyCredentials);
        before("/api/login/mfa", (req, res) -> {
            try {
                Map<String, String> cookies = req.cookies();
                if (cookies.containsKey("token")) {
                    String token = cookies.get("token");
                    serverLogger.info("Token received: " + token);
                    DecodedJWT jwt = JWT.decode(token);
                    WebServer.JWT_ALGO.verify(jwt);
                    System.out.println(jwt.getIssuer() + " vs. " + Issuers.MFA_LOGIN.getIssuer());
                    System.out.println(jwt.getExpiresAt().after(new Date()));
                    if (jwt.getExpiresAt().before(new Date()) || !jwt.getIssuer().equals(Issuers.MFA_LOGIN.getIssuer())) {
                        res.cookie("/", "token", null, 0, true, true);
                        JSONObject err = new JSONObject();
                        err.put("success", false);
                        err.put("message", "Token is no longer valid");
                        err.put("retry", false);
                        halt(401, err.toJSONString());
                    }
                }
            } catch (NullPointerException e) {
                serverLogger.error("Null on Token");
                halt(401, "Attempted to call multi-factor without logging in");
            } catch (SignatureVerificationException e) {
                halt(401, "Invalid Token");
            }
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
        });
        serverLogger.info("Ready to Fire");
        serverLogger.info("We have Lift off!");
    }

    public static void verifyCredentials(Request req, Response res) {
        try {
            String token = req.cookie("token");
            if(token == null) {
                System.out.println("Token did not exist");
                res.redirect("/");
                return;
            }
            serverLogger.info("Token received: " + token);
            DecodedJWT jwt = JWT.decode(token);
            WebServer.JWT_ALGO.verify(jwt);
            System.out.println("JWT is " + jwt);
            System.out.println("JWT expires at is: " + jwt.getExpiresAt());
            System.out.println("JWT issuer is: " + jwt.getIssuer());
            if (jwt.getExpiresAt().before(new Date()) || !jwt.getIssuer().equals(Issuers.AUTHENTICATION.getIssuer())) {
                res.cookie("/", "token", null, 0, true, true);
                JSONObject err = new JSONObject();
                err.put("success", false);
                err.put("message", "Invalid Token");
                err.put("expired", jwt.getExpiresAt().before(new Date()));
                err.put("bad-issuer", jwt.getIssuer().equals(Issuers.AUTHENTICATION.getIssuer()));
                halt(401, err.toJSONString());
            }
        } catch (SignatureVerificationException | JWTDecodeException e) {
            JSONObject err = new JSONObject();
            err.put("success", false);
            err.put("message", "Invalid Token");
            halt(401, err.toJSONString());
        }
    }
}
