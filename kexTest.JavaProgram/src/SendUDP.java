import java.io.IOException;
import java.net.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SendUDP {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


		DatagramSocket clientSocket;
		try {
			SendUDP test = new SendUDP();
			clientSocket = new DatagramSocket();
			
			Random rnd = new Random();
			int randomValue = rnd.nextInt(10000) % 9;
			
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendCryptoData = new byte[1024];
			String sentence = "27,1," + randomValue + ",Temperature," + getTime() + ",";
			sendCryptoData = test.encrypt(sentence);
			DatagramPacket sendPacket = new DatagramPacket(sendCryptoData, sendCryptoData.length, IPAddress, 9876);
			System.out.println(sendCryptoData);
			System.out.println(sendCryptoData.length);
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
	private byte[] encrypt(String message) throws Exception {
		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest("HG58YZ3CR7"
				.getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		final SecretKey key = new SecretKeySpec(keyBytes, "TripleDES");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher cipher = Cipher.getInstance("DESede/CBC/Nopadding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		final byte[] temp = new byte[1024];
		final byte[] plainTextBytes = message.getBytes("utf-8");
		for(int i = 0; i < plainTextBytes.length; i++){
			temp[i] = plainTextBytes[i];
		}
		final byte[] cipherText = cipher.doFinal(temp);

		return cipherText;
	}
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(Calendar.getInstance().getTime());

		return time;

	}


}
