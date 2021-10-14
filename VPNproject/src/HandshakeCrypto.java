import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

public class HandshakeCrypto {

//Encrypt: takes a plaintext as a byte array ,and returns the corresponding cipher text as a byte array
    public static byte[] encrypt(byte[] plaintext, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] ciphertext = cipher.doFinal(plaintext);
        return ciphertext;
    }
    public static byte[] decrypt(byte[] ciphertext, Key key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,key);
        byte[] plaintext = cipher.doFinal(ciphertext);
        return plaintext;
    }
//The getPublicKeyFromCertFile method extracts a public key from a certificate file (in PKCS#1 PEM format, see below).
    public static PublicKey getPublicKeyFromCertFile(String certfile) throws Exception{
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream Input = new FileInputStream(certfile);
        X509Certificate cert = (X509Certificate)cf.generateCertificate(Input);
        PublicKey publicKey =cert.getPublicKey();

        return publicKey;
    }
//The getPrivateKeyFromKeyFile method extracts a private key from a key file (the file is in PKCS#1 PEM format or PKCS#8 DER format, see below).
    public static PrivateKey getPrivateKeyFromKeyFile(String keyfile) throws Exception{
        Path path = Paths.get(keyfile);
        byte[] privKeyByteArray = Files.readAllBytes(path);
        KeySpec keySpec = new PKCS8EncodedKeySpec(privKeyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privatekey =keyFactory.generatePrivate(keySpec);
        return privatekey;
    }

}
