import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Date;


public class UDPSendStressTester {

	public static void main(String[] args) throws Exception {
		String address = args[0];
		int UDPPacketSize = Integer.parseInt(args[1]);
		int iterations = Integer.parseInt(args[2]);
		float totalPacketSize = Integer.parseInt(args[3]);
		int socket = Integer.parseInt(args[4]);
		
		DatagramSocket clientSocket;
		clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

		IPAddress = InetAddress.getByName(address);
		byte[] randomBytes = new byte[UDPPacketSize];
		random.nextBytes(randomBytes);

		long startTime = new Date().getTime();
		
		for(int i = 0; i < iterations; i++){	
			DatagramPacket sendPacket = new DatagramPacket(randomBytes, randomBytes.length, IPAddress, socket);
			clientSocket.send(sendPacket);
		}
		float floatDate = ( new Date().getTime() - startTime) / (float) 1000;
		System.out.println("Time to send: " + floatDate + "s");
		float throughput =  ((totalPacketSize / 10) / floatDate);
		System.out.println("Throughput: " + throughput + "Mbps");
		clientSocket.close();
	}
}
