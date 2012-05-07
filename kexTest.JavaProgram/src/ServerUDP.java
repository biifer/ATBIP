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
/**
 * 
 * @author Alfred Andersson & Ivan Pedersen
 *
 */
public class ServerUDP {
	public static void main(String[] args) throws IOException {
		/*
		 * Creates a new socket that will receive the UDP messages.
		 */
		DatagramSocket serverSocket = new DatagramSocket(9876);
		/*
		 * Entering the loop that will run for ever.
		 */
		while (true) {
			/*
			 * Creates a byte array of the fixed size 1024 Bytes.
			 */
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			/*
			 * The socket waits for packages.
			 */
			serverSocket.receive(receivePacket);
			/*
			 * Saves the received package in a byte array. 
			 */
			byte[] encryptedMessage = receivePacket.getData();
			
			System.out
					.println("Spawning new thread to handle incoming UDP package\n");
			try {
				/*
				 * Creates a new thread that will decrypt the message and at the data to the databae.
				 */
				new Thread(new DatabaseController(encryptedMessage)).start();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
