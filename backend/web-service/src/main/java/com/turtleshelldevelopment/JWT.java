package com.turtleshelldevelopment;

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

public class JWT {
    File privateKey = new File("store/private.key");
    File publicKey = new File("store/key.pub");

    /***
     * Loads or generate public and private key for JWT Authentication
     */
    public KeyPair loadJwt() {
        if(!createKeyStoreLocation()) {
            BackendServer.serverLogger.error("Could not create key store location!");
            return null;
        }
        if (privateKey.exists() && publicKey.exists()) {
            return loadKeys();
        } else {
            return createKeys();
        }
    }
    private boolean createKeyStoreLocation() {
        File storeFolder = new File("store");
        if(!storeFolder.exists()) {
            boolean created = storeFolder.mkdirs();
            if(!created) {
                BackendServer.serverLogger.error("Failed to create store folder");
                return false;
            }
        }
        return true;
    }

    private KeyPair createKeys() {
        BackendServer.serverLogger.info("Generating keys for JWT tokens...");
        KeyPair kp;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
            FileOutputStream privateKeyFile = new FileOutputStream(privateKey);
            FileOutputStream publicKeyFile = new FileOutputStream(publicKey);
            privateKeyFile.write(kp.getPrivate().getEncoded());
            publicKeyFile.write(kp.getPublic().getEncoded());
            privateKeyFile.close();
            publicKeyFile.close();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        BackendServer.serverLogger.info("Done!");
        return kp;
    }

    private KeyPair loadKeys() {
        try {
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
        } catch (IOException e) {
            BackendServer.serverLogger.error("Failed to get keys!");
            return null;
        } catch (InvalidKeySpecException e) {
            BackendServer.serverLogger.error("Invalid Key Spec for private or public key!");
            return null;
        } catch (NoSuchAlgorithmException e) {
            BackendServer.serverLogger.error("RSA Algorithm is not available!");
            return null;
        }
    }
}
