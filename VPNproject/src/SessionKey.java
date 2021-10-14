import javax.crypto.KeyGenerator; 
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
//import java.security.Key;
import java.security.NoSuchAlgorithmException;
public class SessionKey {
	 KeyGenerator kg ;
	 SecretKey sk;
	 SecureRandom sr = new SecureRandom();
	 byte[] bytearray;  
	 
	public SessionKey(Integer keylength) {
		try{ 
		kg = KeyGenerator.getInstance("AES");
		kg.init(keylength); 
		sk = kg.generateKey();
		}
		catch(NoSuchAlgorithmException e) {
		      e.printStackTrace();
		}
	} 
	
	
	public SessionKey(byte[] keybytes) {
		bytearray = new byte[keybytes.length];
		for(int i =0;i<keybytes.length;i++) {
			bytearray[i] = keybytes[i];
		}
		sk = new SecretKeySpec(bytearray,"AES");
		}
	
	public SecretKey getSecretKey() {

		return sk;
		}
	
	public byte[] getKeyBytes() {
		getSecretKey();
		byte[] key = sk.getEncoded();
		return key ;
	}
}

