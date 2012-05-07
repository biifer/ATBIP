import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class ServerUDP {


	public static void main(String[] args) throws IOException {
		
		while(true){
		DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        //String sentence = new String( receivePacket.getData());
        byte[] encryptedMessage =receivePacket.getData();

        System.out.println("Received encoded message: " + encryptedMessage);
        String sentence = new String(decrypt(encryptedMessage));
        System.out.println("Decrypted message: " + sentence.trim() + "\n");
        String[] splitSentence = sentence.split(",");
        String gw = splitSentence[0];
        String sensor = splitSentence[1];
        String value = splitSentence[2];
        String type = splitSentence[3];
        String time = splitSentence[4];
        System.out.println("Spawning new thread to handle incoming UDP package\n");
        try {
			new Thread(new DatabaseController(gw, sensor, value, type, time)).start();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
        
        serverSocket.close();
		}

	}
    private static String decrypt(byte[] message)  {
        
		try {
	        final MessageDigest md = MessageDigest.getInstance("md5");
	        final byte[] digestOfPassword = md.digest("HG58YZ3CR7"
	                        .getBytes("utf-8"));
	        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
	        for (int j = 0, k = 16; j < 8;) {
	                keyBytes[k++] = keyBytes[j++];
	        }

	        final SecretKey key = new SecretKeySpec(keyBytes, "TripleDES");
	        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
	        final Cipher decipher = Cipher.getInstance("DESede/CBC/Nopadding");
	        decipher.init(Cipher.DECRYPT_MODE, key, iv);

	        // final byte[] encData = new
	        // sun.misc.BASE64Decoder().decodeBuffer(message);
	        final byte[] plainText = decipher.doFinal(message);
        return new String(plainText, "UTF-8");
        
		}
        catch (NoSuchAlgorithmException e) {

			return e.toString();
		} catch (UnsupportedEncodingException e) {

			return e.toString();
		} catch (NoSuchPaddingException e) {

			return e.toString();
		} catch (InvalidKeyException e) {

			return e.toString();
		} catch (InvalidAlgorithmParameterException e) {

			return e.toString();
		} catch (IllegalBlockSizeException e) {

			return e.toString();
		} catch (BadPaddingException e) {

			return e.toString() + "\nBad message!";
		}
		

        
    }

}
