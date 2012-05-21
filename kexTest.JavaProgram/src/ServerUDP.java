import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLException;

/**
 * 
 * @author Alfred Andersson & Ivan Pedersen
 *
 */
public class ServerUDP {
	public static void main(String[] args) throws IOException, SQLException {
		
		int numberOfThreads = 4;
		if(args.length > 0){
			numberOfThreads = Integer.parseInt(args[0]);
		}
		System.out.println("Server started with: " +  numberOfThreads + " worker threads.");
		PoolOfTasks poolOfTasks = new PoolOfTasks();
		
		/*
		 * Creates a new socket that will receive the UDP messages.
		 */ 
		DatagramSocket serverSocket = new DatagramSocket(50000);
		/*
		 * Entering the loop that will run for ever.
		 */
		
		for (int i = 0; i < numberOfThreads; i++) {
			new Thread(new DatabaseController(poolOfTasks)).start();
		}
		
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
			
			/*
			 * Creates a new thread that will decrypt the message and at the data to the databae.
			 */
			poolOfTasks.addTask(receivePacket.getData());
		}
	}
}
