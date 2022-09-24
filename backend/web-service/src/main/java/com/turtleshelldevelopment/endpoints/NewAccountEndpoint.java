package com.turtleshelldevelopment.endpoints;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.turtleshelldevelopment.WebServer;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Base64;

@SuppressWarnings("unchecked")
public class NewAccountEndpoint implements Route {
    private final String issuer = "Covid-19-MO-IIS";

    @Override
    public Object handle(Request request, Response response) throws IOException {
        WebServer.serverLogger.info("Handling New Account!");
        JSONObject body = new JSONObject();
        try {
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            System.out.println("Username: " + username + ", password: " + password);

            //Validate Username
            PreparedStatement statement = WebServer.database.getConnection().prepareStatement("CALL CHECK_USERNAME(?);");
            statement.setString(1, username);
            if (statement.executeQuery().next()) {
                body.put("error", "Username already exists");
                return body;
            }
            WebServer.serverLogger.info("Username is Good");
            //Take in the password and hash it
            byte[] salt = getSalt();
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = skf.generateSecret(spec).getEncoded();
            WebServer.serverLogger.info("Password is Good");
            //Put Permissions

            boolean readPatient = Boolean.parseBoolean(request.queryParams("readPatient"));
            boolean reports = Boolean.parseBoolean(request.queryParams("reports"));
            boolean addUsers = Boolean.parseBoolean(request.queryParams("addUsers"));
            boolean editUsers = Boolean.parseBoolean(request.queryParams("editUsers"));
            boolean writePatient = Boolean.parseBoolean(request.queryParams("writePatient"));
            boolean editPatient = Boolean.parseBoolean(request.queryParams("editPatient"));

            CallableStatement permissions = WebServer.database.getConnection().prepareCall("call CREATE_PERMISSIONS(?,?,?,?,?,?, ?)");
            permissions.registerOutParameter(1, Types.INTEGER);
            permissions.setBoolean(2, readPatient);
            permissions.setBoolean(3, reports);
            permissions.setBoolean(4, addUsers);
            permissions.setBoolean(5, editUsers);
            permissions.setBoolean(6, writePatient);
            permissions.setBoolean(7, editPatient);

            permissions.executeUpdate();

            int permissionId = permissions.getInt(1);

            permissions.close();
            WebServer.serverLogger.info("Permissions is Good");

            //Insert new account
            CallableStatement insertUser = WebServer.database.getConnection().prepareCall("CALL ADD_USER(?,?,?,?,?)");
            JSONObject mfa = generateMultiFactor(username);
            insertUser.setString(1, username);
            insertUser.setBytes(2, hash);
            insertUser.setBytes(3, salt);
            insertUser.setString(4, (String) mfa.get("secret"));
            insertUser.setInt(5, permissionId);

            if (insertUser.executeUpdate() == 1) {
                body.put("error", "200");
                body.put("2fa", mfa.get("qr"));
                System.out.println("User created: " + username + ", " + password + ", salt=" + Arrays.toString(salt));
                WebServer.serverLogger.info("Success!");
            } else {
                body.put("error", "500");
                body.put("message", "Failed to update database");
                WebServer.serverLogger.info("Failed to update");
            }
            WebServer.serverLogger.info("Successfully put in database");
            insertUser.close();
        } catch (SQLException e) {
            WebServer.serverLogger.info(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | URISyntaxException | WriterException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public JSONObject generateMultiFactor(String username) throws URISyntaxException, WriterException, IOException {
        JSONObject multifactor = new JSONObject();
        GoogleAuthenticatorKey googleAuthkey = WebServer.gAuth.createCredentials();
        String key = googleAuthkey.getKey();
        //String secret = generateRawSecret(64);

        URI uri = new URI("otpauth", "totp", "/"+ issuer + ":" + username, "secret=" + key + "&issuer=" + issuer);
        BitMatrix matrix = new MultiFormatWriter().encode(String.valueOf(uri), BarcodeFormat.QR_CODE,
                64, 64);
        multifactor.put("secret", key);

        File createTemp = File.createTempFile("temp", null);
        MatrixToImageWriter.writeToPath(matrix, "temp",  createTemp.toPath());
        Base64.Encoder encoder = Base64.getEncoder();
        String base64Image = encoder.encodeToString(Files.readAllBytes(createTemp.toPath()));
        multifactor.put("qr", "data:image/png;base64," + base64Image);
        return multifactor;
    }

    public static String generateRawSecret(int length) {
        byte[] buf = new byte[length];
        new SecureRandom().nextBytes(buf);
        String rawSecret = Base64.getEncoder().encodeToString(buf);
        return rawSecret.substring(1, length + 1);
    }


}
