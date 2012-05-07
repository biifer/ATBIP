import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class sendTest {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket socket;
		PrintWriter out;
		BufferedReader in;
		String name = "data", receivedData;
		
		socket = new Socket("localhost", 8080);
		out = new PrintWriter(socket.getOutputStream(), true);
		out.println(name); 
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		receivedData = in.readLine();
		
		if(receivedData == "OK"){
			System.out.println("Receeived OK from server");
		}
		else{
			System.out.println("Did not receive OK message from server, Resend?");
		}
		/*
		 * FACKING VERSION UNO!
		 */
		

		
	}

}
