package Utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class Crypto {

    public static KeyPair generateKeypair(String algorithm) {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(algorithm);
            keygen.initialize(2048);
            return keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static Key generateSessionKey(String algorithm) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
            keygen.init(128);
            Key sessionKey = keygen.generateKey();
            return sessionKey;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] cypher(byte[] data, Key cypherKey) {
        try {
            Cipher cypher = Cipher.getInstance(cypherKey.getAlgorithm());
            cypher.init(Cipher.ENCRYPT_MODE, cypherKey);
            return cypher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
        }
        return data;
    }

    public static byte[] decypher(byte[] data, Key decypherKey) {
        try {
            Cipher cypher = Cipher.getInstance(decypherKey.getAlgorithm());
            cypher.init(Cipher.DECRYPT_MODE, decypherKey);
            return cypher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
        }
        return data;
    }

    // Generate signature of file
    public static byte[] signFile(byte[] data, Key privKey) {
        try {
            Signature sign = Signature.getInstance("MD5WithRSA");
            sign.initSign((PrivateKey) privKey);
            sign.update(data);
            return sign.sign();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    // Checks if signature provided is correct
    public static boolean checkSign(byte[] data, Key pubKey, byte[] sig) {
        try {
            Signature sign = Signature.getInstance("MD5WithRSA");
            sign.initVerify((PublicKey) pubKey);
            sign.update(data);
            return sign.verify(sig);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
