package me.rowkey.pje.security;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bryant.Hang on 2017/8/9.
 */
public class JavaCiperExample {

    public static void md5() throws NoSuchAlgorithmException {
        byte[] data = "plainText".getBytes(); //明文数据
        MessageDigest md5 = MessageDigest.getInstance("md5");
        md5.update(data);  //加密后的数据
    }

    public static void sha() throws NoSuchAlgorithmException {
        byte[] data = "plainText".getBytes(); //明文数据
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        sha.update(data);
    }

    public static void hmac() throws NoSuchAlgorithmException, InvalidKeyException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");

        SecretKey secretKey = keyGenerator.generateKey();
        byte[] secret = secretKey.getEncoded();

        SecretKey secretKey1 = new SecretKeySpec(secret, "HmacMD5");
        Mac mac = Mac.getInstance(secretKey1.getAlgorithm());
        mac.init(secretKey);

        byte[] data = "plainText".getBytes(); //明文数据
        mac.doFinal(data);
    }

    public void testDes() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        byte[] secret = "!@#34566".getBytes();//密钥
        DESKeySpec keySpec = new DESKeySpec(secret);
        SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = "plainText".getBytes(); //明文数据
        byte[] encryData = cipher.doFinal(data); //加密数据

        cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        cipher.doFinal(encryData); //解密数据
    }

    public void testAes() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] secret = "!@#34566".getBytes();//密钥
        SecretKey key = new SecretKeySpec(secret, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] data = "plainText".getBytes(); //明文数据
        byte[] encryData = cipher.doFinal(data); //加密数据

        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        cipher.doFinal(encryData); //解密数据
    }

    public void testPBE() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] salt = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        String password = "!@#56744";//用户口令
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 200); //迭代200次
        Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

        byte[] data = "plainText".getBytes(); //明文数据
        byte[] encryData = cipher.doFinal(data);

        cipher = Cipher.getInstance("PBEWITHMD5andDES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

        cipher.doFinal(encryData);
    }

    public void testDH() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(1024); //密钥字节数
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        byte[] aPublicKey = keyPair.getPublic().getEncoded(); //A的公钥
        byte[] aPrivateKey = keyPair.getPrivate().getEncoded();  //A的私钥

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(aPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        PublicKey aPubKey = keyFactory.generatePublic(x509KeySpec);

        DHParameterSpec dhParamSpec = ((DHPublicKey) aPubKey).getParams(); //由A的公钥构建B的密钥对

        keyPairGenerator.initialize(dhParamSpec);

        keyPair = keyPairGenerator.generateKeyPair();

        byte[] bPublicKey = keyPair.getPublic().getEncoded(); //B的公钥
        byte[] bPrivateKey = keyPair.getPrivate().getEncoded();  //B的私钥

        String plainText = "你好";

        keyFactory = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(aPublicKey);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(bPrivateKey);
        Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);

        SecretKey secretKey = keyAgree.generateSecret("DES");   //本地密钥
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryData = cipher.doFinal(plainText.getBytes());

        x509KeySpec = new X509EncodedKeySpec(bPublicKey);
        pubKey = keyFactory.generatePublic(x509KeySpec);

        pkcs8KeySpec = new PKCS8EncodedKeySpec(aPrivateKey);
        priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);

        secretKey = keyAgree.generateSecret("DES");   //本地密钥

        cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        cipher.doFinal(encryData);
    }

    public static void testRSA() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance("RSA");
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();

        // 私钥
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String plainText = "plain text.";
        byte[] encryData = cipher.doFinal(plainText.getBytes());

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        cipher.doFinal(encryData);
    }
}
