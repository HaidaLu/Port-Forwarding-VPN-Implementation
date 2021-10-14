import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SessionEncrypter {
	SecretKey key ;
	byte[] IVbytes;
	byte[] Keybytes;
	SessionKey Mykey;
	Cipher cipher ;
	CipherOutputStream cos ;



	public SessionEncrypter(Integer keylength)  {
		try {
			cipher = Cipher.getInstance("AES/CTR/NoPadding");
			Mykey = new SessionKey(keylength);
			key=Mykey.getSecretKey();
			try {
				cipher.init(Cipher.ENCRYPT_MODE,key);
				IVbytes= cipher.getIV();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SessionEncrypter(byte[] keybytes, byte[] ivbytes){

		try {
			cipher = Cipher.getInstance("AES/CTR/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Mykey = new SessionKey(keybytes);
		key = Mykey.getSecretKey();

		IVbytes = ivbytes;
		IvParameterSpec ivSpec = new IvParameterSpec(IVbytes);

		try {
			cipher.init(Cipher.ENCRYPT_MODE,key,ivSpec);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public byte[] getKeyBytes() {
		Keybytes = Mykey.getKeyBytes();
		return Keybytes;
	}


	public byte[] getIVBytes() {
		return IVbytes;
	}

	public CipherOutputStream openCipherOutputStream(OutputStream output) {
		cos = new CipherOutputStream(output,cipher);
		return cos;

	}
}
