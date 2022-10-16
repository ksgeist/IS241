package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.Issuers;
import com.turtleshelldevelopment.WebServer;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

//*****************************************************
//*                                                   *
//* Created By: Colin Kinzel                          *
//* Created On: 10/6/2022, 4:28:35 PM                 *
//* Last Modified By: Colin Kinzel                    *
//* Last Modified On: 10/6/2022, 4:28:35 PM           *
//* Description: New Account Endpoint Handler Route   *
//*                                                   *
//*****************************************************
@SuppressWarnings("unchecked")
public class NewAccountEndpoint implements Route {

    @Override
    public Object handle(Request request, Response response) {
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
            //WebServer.serverLogger.info("Username is Good");
            //Take in the password and hash it
            byte[] salt = getSalt();
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = skf.generateSecret(spec).getEncoded();
            //WebServer.serverLogger.info("Password is Good");
            //Put Permissions
            int userType = Integer.parseInt(request.queryParams("userType"));
            //WebServer.serverLogger.info("Permissions is Good");
            try {
                //Insert new account
                CallableStatement insertUser = WebServer.database.getConnection().prepareCall("CALL ADD_USER(?,?,?,?,?)");
                JSONObject mfa = generateTOTPMultiFactor(username);
                insertUser.setString(1, username);
                insertUser.setBytes(2, hash);
                insertUser.setBytes(3, salt);
                insertUser.setString(4, (String) mfa.get("secret"));
                insertUser.setInt(5, userType);
            if (insertUser.executeUpdate() == 1) {
                body.put("error", "200");
                body.put("2fa", mfa.get("qr"));
                System.out.println("User created: " + username + ", " + password + ", salt=" + Arrays.toString(salt));
                WebServer.serverLogger.info("Success!");
                return "<html><img src=" + body.get("2fa") +" /></html>";
            } else {
                body.put("error", "500");
                body.put("message", "Failed to update database");
                WebServer.serverLogger.info("Failed to update");
            }
            insertUser.close();
            WebServer.serverLogger.info("Successfully put in database");
            } catch (QrGenerationException e) {
                WebServer.serverLogger.error("Error creating QR");
                body.put("error", "500");
                body.put("message", "Failed to Create QR Code for 2FA");
            }
            return body;
        } catch (SQLException e) {
            WebServer.serverLogger.info(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            WebServer.serverLogger.error(e.getMessage());
        }
        return "<html><img src=" + body.get("2fa") +" /></html>";
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public JSONObject generateTOTPMultiFactor(String username) throws QrGenerationException {
        JSONObject mfa = new JSONObject();
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();

        QrData qr = new QrData.Builder()
                .label("Covid-19 Dashboard: " + username)
                .secret(secret)
                .issuer(Issuers.AUTHENTICATION.getIssuer())
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = generator.generate(qr);

        String mimeType = generator.getImageMimeType();
        String dataUri = getDataUriForImage(imageData, mimeType);
        mfa.put("secret", secret);
        mfa.put("qr", dataUri);
        return mfa;
    }

}
