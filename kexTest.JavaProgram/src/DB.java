import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;


public class DB {

	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        
		Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement pstmt = null;
		Statement stmt = null;
		Connection con = null;
		ResultSet rs = null;
		String url = "jdbc:mysql://biifer.mine.nu:3306/development3";


		con = DriverManager.getConnection(url,"ivan", "eKufsfrQMNrSyB4K");
		

		System.out.println("URL: " + url);
		System.out.println("Connection: " + con + "\n");


		System.out.println("View gateway entries:\t[g]\nAdd new gateway:\t[ng]");
		Scanner scan = new Scanner(System.in);
		while(true){
			String input = scan.nextLine();
			if(input.equals("g")) {
				stmt = con.createStatement();
				rs = stmt.executeQuery("SELECT * " + "from gateways");
				System.out.println("Namn\tID");
				while(rs.next()){
					String name= rs.getString("name");
					String id = rs.getString("id");
					System.out.println(name
							+ "\t\t" + id);
					input = null;
				}
				rs.close();
				stmt.close();
			}
			else if(input.equals("ng")) {
				System.out.println("Enter the gateway name:\n");
				String name = scan.nextLine();
				System.out.println("Enter the gateway id:\n");
				String id = scan.nextLine();
				String gateway = "INSERT INTO gateways VALUES( ?, ?)";
				pstmt = con.prepareStatement(gateway);
				pstmt.setString(1, name);
				pstmt.setString(2, id);
				pstmt.executeUpdate();
				System.out.println("You added one new entry into the gateway table\nName:\t" + name + "\nID:\t" + id + "\n");

				pstmt.close();
			}
			else if (input.equals("nga")){
				System.out.println("För att lägga till en ny guide skriv: <Personnummer:Namn:Adress:Telefonnummer>\nPersonnummer ska vara i formen (YYMMDD-XXXX) Adress och Tele är valfria" );
				String nyGuide = scan.nextLine();
				String[] guide = nyGuide.split(":");
				String[] pn = guide[0].split("-");
				int aInt = 0;
				int bInt = 0;
				boolean check = true;
				try {
					aInt = Integer.parseInt(pn[0]);
					bInt = Integer.parseInt(pn[1]);
				}catch (NumberFormatException e){
					System.out.println("Du har inte angivet ett giltigt personnummer!");
					check = false;}
				String pnr = Integer.toString(aInt) + Integer.toString(bInt);

				String sGuide = "INSERT INTO Guide VALUES( ?, ?, ?, ?)";				
				pstmt = con.prepareStatement(sGuide);
				pstmt.setString(1,pnr);
				pstmt.setString(2, guide[1]);

				if(guide.length == 2 && check == true){
					pstmt.setString(3, null);
					pstmt.setString(4, null);
				}
				else if(guide.length == 3 && check == true){
					pstmt.setString(3, guide[2]);
					pstmt.setString(4, null);				}
				else if(guide.length == 4 && check == true){
					pstmt.setString(3, guide[2]);
					pstmt.setString(4, guide[3]);
				}else{
					System.out.println("Texten du skrev kan inte behandlas!");
				}
				try {
					pstmt.executeUpdate();
					System.out.println("Du har lagt till guiden den nya guide: <" +  pnr + ", " + guide[1] + ">");
				}catch(java.lang.ArrayIndexOutOfBoundsException e){
					System.out.println("LOEL");
				}
				rs.close();
				pstmt.close();
			}
		}
	}


}

