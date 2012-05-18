import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;


public class SendUDP {

	/**
	 * @param args IP address
	 * @throws Exception 
	 */
	public static void main(String[] args) {

		DatagramSocket clientSocket;
		try {
			clientSocket = new DatagramSocket();
			
			Random rnd = new Random();
			int randomValue = (rnd.nextInt(10000) % 50) -20;
			
			InetAddress IPAddress = InetAddress.getByName(args[0]);
			byte[] cryptoData = new byte[1024];
			String sentence = "27,3," + randomValue + ",Temperature," + getTime() + ",";
			cryptoData = encryptAES(sentence);
			DatagramPacket sendPacket = new DatagramPacket(cryptoData, cryptoData.length, IPAddress, 50000);
			System.out.println("Sending encrypted message: " + cryptoData.toString() + "to: " + args[0]);
			clientSocket.send(sendPacket);
			clientSocket.close();

		}			 
		catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Not a valid message!");
			e.printStackTrace();
		}

	}
	private static byte[] encryptAES(String message)  {
		byte[] encryptedMessage = new byte[1024];
		final byte[] temp = new byte[1008];
		final byte[] encryptedMessageToReturn = new byte[1024];
		
		try {
			SecretKeySpec skeySpec = new SecretKeySpec("PK80111q''eto0z<".getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/Nopadding");
			
			AlgorithmParameterSpec paramSpec = new IvParameterSpec("IvAnQQ-piece*pie".getBytes());
			
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, paramSpec);

			final byte[] plainTextBytes = message.getBytes("UTF-8");
			// Appends the plain message to a byte string with the size 1008 to be encrypted.
			for(int i = 0; i < plainTextBytes.length; i++){
				temp[i] = plainTextBytes[i];
			}
			encryptedMessage = cipher.doFinal(temp);
			
			// Creates a message digest for the encrypted message.
			Mac md = Mac.getInstance("HmacMD5");
			md.init(skeySpec);
			md.update(encryptedMessage);
			byte[] digest = md.doFinal();
			
			// Adds the encrypted message and the digest to the byte string to be sent.
			for(int i = 0; i < 1008; i++){
				encryptedMessageToReturn[i] = encryptedMessage[i];
			}
			for(int i = 0; i < 16; i++){
				encryptedMessageToReturn[1008+i] = digest[i];
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedMessageToReturn;
	}
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(Calendar.getInstance().getTime());

		return time;

	}


}
