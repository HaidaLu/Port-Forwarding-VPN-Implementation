import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SessionDecrypter {
	Cipher cipher;
	byte[] key;
	byte[] IVbytes;
	public SessionDecrypter(byte[] keybytes, byte[] ivbytes){
		try {
			cipher = Cipher.getInstance("AES/CTR/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		key = keybytes;
		IVbytes = ivbytes;
		Key keySpec= new SecretKeySpec(key,"AES");
		IvParameterSpec ivSpec = new IvParameterSpec(IVbytes);
		try {
			cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public CipherInputStream openCipherInputStream(InputStream input) {
		CipherInputStream cis = new CipherInputStream(input,cipher);
		try {
			cis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cis ;}
}
