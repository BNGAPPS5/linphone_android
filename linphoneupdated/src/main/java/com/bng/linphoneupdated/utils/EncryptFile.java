package com.bng.linphoneupdated.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptFile {

    private static final String KEY_ALIAS = "MySecretKey";

    public static void encryptRootCAFile() {
        try {
            // Path to the original file
            File inputFile = new File("certificates/rootcaa.pem");
            // Path where the encrypted file will be saved
            File encryptedFile = new File("src/main/assets/rootcaa.pem.enc");

            SecretKey secretKey = generateKey();

            encryptFile(secretKey, inputFile, encryptedFile);

            // Save the secret key securely (in Android Keystore)
            saveKey(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  /*  public static void main(String[] args) throws Exception {
        // Path to the original file
        File inputFile = new File("certificates/rootcaa.pem");
        // Path where the encrypted file will be saved
        File encryptedFile = new File("src/main/assets/rootcaa.pem.enc");

        SecretKey secretKey = generateKey();

        encryptFile(secretKey, inputFile, encryptedFile);

        // Save the secret key securely (in Android Keystore)
        saveKey(secretKey);
    }*/

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
        keyGenerator.init(keyGenParameterSpec);
        return keyGenerator.generateKey();
    }

    public static void encryptFile(SecretKey key, File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void saveKey(SecretKey key) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(null);
        keyStore.setEntry(KEY_ALIAS, secretKeyEntry, protectionParameter);
    }

    public static SecretKey getKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
    }
}


