import java.io.IOException;
import java.net.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.*;
import java.text.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import net.sf.json.JSONObject;

public class DatabaseController implements Runnable {

	String gateway_id, sensor_id, value, type, time, url;
	Connection connection;
	ResultSet resultSet;
	Statement statement;
	byte[] encryptedMessage;
	PreparedStatement preparedStatement;
	Exception badGateway = new Exception("Not a valid gateway!");
	Exception badMessageDigestException = new Exception("Bad MessageDigest!");
	PoolOfTasks poolOfTasks;
	String decryptedMessage = null;

	public DatabaseController(PoolOfTasks poolOfTasks) throws SQLException {
		this.poolOfTasks = poolOfTasks;
		this.url = "jdbc:mysql://biifer.mine.nu:3306/development3";
		this.connection = DriverManager.getConnection(url, "ivan",
				"eKufsfrQMNrSyB4K");
		this.resultSet = null;
		this.statement = null;
		this.preparedStatement = null;
	}

	@Override
	public void run() {

		while(true){
			try {
				encryptedMessage = poolOfTasks.getTask();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("Received encoded message: " + encryptedMessage);
			try {
				decryptedMessage = decryptAES(encryptedMessage);

				System.out.println("Decrypted message: " + decryptedMessage.trim() );
				String[] splitSentence = decryptedMessage.split(",");
				gateway_id = splitSentence[0];
				sensor_id = splitSentence[1];
				value = splitSentence[2];
				type = splitSentence[3];
				time = splitSentence[4];
	
				Class.forName("com.mysql.jdbc.Driver");

				statement = connection.createStatement();
				resultSet = statement.executeQuery(				"SELECT id AS gateway_id, " +
						"(SELECT sensor_id FROM sensors WHERE sensor_id = '" + sensor_id + "' AND gateway_id = " + gateway_id + ") AS valid, " +
						"(SELECT created_at FROM " +
						"(SELECT id, created_at FROM sensor_readings WHERE sensor_id = '"+ sensor_id +"' AND gateway_id =" + gateway_id + " ORDER BY id DESC LIMIT 1) AS timetable)" +
						" AS timestamp FROM gateways WHERE id = "+ gateway_id);

				// Checks if the gateway exists. If NOT throw badGateway exception
				if (!resultSet.next()) {
					throw badGateway;
				} else {
					// If the sensor already exists, add sensor data to the existing
					// sensor
					if (resultSet.getString(2) != null) {

						System.out.println("Adding data to sensor with id: "
								+ sensor_id + "\n");

						addSensorReadings(resultSet.getString(3));

					}
					// If the sensor does NOT exist, create a new sensor first and
					// then add the sensor readings
					else {

						System.out.println("Adding new sensor to gateway: "
								+ gateway_id + " and data to sensor with id: "
								+ sensor_id + "\n");

						addSensor();
						addSensorReadings(resultSet.getString(3));

					}
				}

			}

			catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addSensorReadings(String timestamp) throws SQLException, ParseException, IOException {
		if(timestamp != null){
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long timestampLong = sdf.parse(timestamp).getTime();
			long timeLong = sdf.parse(time).getTime();
			if(timeLong > (timestampLong + 2000)){
				String gateway = "INSERT INTO sensor_readings (sensor_id, gateway_id, value, created_at, updated_at) VALUES( ?, ?, ?, ?, ?)";
				preparedStatement = connection.prepareStatement(gateway);
				preparedStatement.setString(1, sensor_id);
				preparedStatement.setString(2, gateway_id);
				preparedStatement.setString(3, value);
				preparedStatement.setString(4, time);
				preparedStatement.setString(5, time);
				preparedStatement.executeUpdate();

				preparedStatement.close();
				pushToFaye();
			}
		}else{
			String gateway = "INSERT INTO sensor_readings (sensor_id, gateway_id, value, created_at, updated_at) VALUES( ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(gateway);
			preparedStatement.setString(1, sensor_id);
			preparedStatement.setString(2, gateway_id);
			preparedStatement.setString(3, value);
			preparedStatement.setString(4, time);
			preparedStatement.setString(5, time);
			preparedStatement.executeUpdate();

			preparedStatement.close();
			pushToFaye();
		}
	}

	public void addSensor() throws SQLException {

		String sensor = "INSERT INTO sensors (sensor_type, sensor_id, created_at, updated_at, gateway_id) VALUES( ?, ?, ?, ?, ?)";
		preparedStatement = connection.prepareStatement(sensor);
		preparedStatement.setString(1, type);
		preparedStatement.setString(2, sensor_id);
		preparedStatement.setString(3, time);
		preparedStatement.setString(4, time);
		preparedStatement.setString(5, gateway_id);
		preparedStatement.executeUpdate();

		preparedStatement.close();
	}

	private String decryptAES(byte[] message){
		SecretKeySpec skeySpec = new SecretKeySpec("PK80Â‰®q''eto0z<".getBytes(), "AES");
		Cipher cipher;
		byte[] original = null;

		/**
		 * Extracts the encrypted message and the message digest from the message received.
		 */
		byte[] digest = new byte[16];
		byte[] encryptedMessage = new byte[1008];
		for(int i = 0; i < 1008; i++){
			encryptedMessage[i] = message[i];
		}
		for(int i = 0; i < 16; i++){
			digest[i] = message[1008+i];
		}

		try {

			Mac md = Mac.getInstance("HmacMD5");
			md.init(skeySpec);
			md.update(encryptedMessage);		
			if(!MessageDigest.isEqual(digest, md.doFinal())){
				throw badMessageDigestException;
			}
			AlgorithmParameterSpec paramSpec = new IvParameterSpec("IvAnQQ-piece*pie".getBytes());
			cipher = Cipher.getInstance("AES/CBC/Nopadding");

			cipher.init(Cipher.DECRYPT_MODE, skeySpec, paramSpec);
			original = cipher.doFinal(encryptedMessage);


			return new String(original);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


	}

	public void pushToFaye() throws IOException{
		URL url = new URL("http://biifer.mine.nu:9292/faye");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestMethod("POST");
		conn.connect();
		JSONObject arrayMessageJSON = new JSONObject();
		JSONObject elementJSON = new JSONObject();
		elementJSON.put("value", value);
		elementJSON.put("created_at", time);
		arrayMessageJSON.put("channel", "/sensor/" + sensor_id + "/new");
		arrayMessageJSON.put("data", elementJSON.toString());
		conn.getOutputStream().write(arrayMessageJSON.toString().getBytes("UTF-8"));
		conn.disconnect();
	}

}
