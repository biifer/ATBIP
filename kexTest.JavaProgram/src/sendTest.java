import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.text.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class sendTest {

	public static void main(String[] args) throws UnknownHostException, IOException, ParseException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchProviderException {	
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey key = keyGen.generateKey();
		System.out.println(key.getEncoded().length + " - " + key.getEncoded());

		byte[] temp = ENC("Tja fö faAn", key);
		DEC(temp, key);

	}
	private static byte[] ENC(String message, SecretKey key) throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, UnsupportedEncodingException, NoSuchProviderException {	    
		//		To use the generate key:
		//		SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(), "AES");
		SecretKeySpec skeySpec = new SecretKeySpec("PK80åä¨q''eto0z<".getBytes(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encryptedMessage = cipher.doFinal(message.getBytes());
		System.out.println("Encrypted message: " + new String(encryptedMessage));
		
		

		return encryptedMessage;
	}
	private static void DEC(byte[] message, SecretKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		//		To use the generated key:
		//		SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(), "AES");
		SecretKeySpec skeySpec = new SecretKeySpec("PK80åä¨q''eto0z<".getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original =
				cipher.doFinal(message);
		System.out.println("Decrypted message: " + new String(original));

	}

}
