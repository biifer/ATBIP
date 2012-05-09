import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.*;
import java.sql.*;
import java.util.Arrays;
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
	Exception badGateway = null;

	public DatabaseController(byte[] encryptedMessage) throws SQLException {
		this.encryptedMessage = encryptedMessage;
		this.url = "jdbc:mysql://biifer.mine.nu:3306/development3";
		this.connection = DriverManager.getConnection(url, "ivan",
				"eKufsfrQMNrSyB4K");
		this.resultSet = null;
		this.statement = null;
		this.preparedStatement = null;
	}

	@Override
	public void run() {

		System.out.println("Received encoded message: " + encryptedMessage);
		String sentence = new String(decrypt(encryptedMessage));
		System.out.println("Decrypted message: " + sentence.trim() + "\n");
		String[] splitSentence = sentence.split(",");
		gateway_id = splitSentence[0];
		sensor_id = splitSentence[1];
		value = splitSentence[2];
		type = splitSentence[3];
		time = splitSentence[4];

		System.out.println("DatabaseController received: \nGateway: "
				+ gateway_id + "\nSensor: " + sensor_id + "\nValue: " + value
				+ "\nType: " + type + "\nTime: " + time + "\n");
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// Checks if the gateway exists. If NOT throw badGateway exception
			if (!validGateway(gateway_id)) {
				throw badGateway;
			} else {
				// If the sensor already exists, add sensor data to the existing
				// sensor
				if (validSensor(sensor_id)) {

					System.out.println("Adding data to sensor with id: "
							+ sensor_id);

					addSensorReadings();

				}
				// If the sensor does NOT exist, create a new sensor first and
				// then add the sensor readings
				else {

					System.out.println("Adding new sensor to gateway: "
							+ gateway_id + " and data to sensor with id: "
							+ sensor_id);

					addSensor();
					addSensorReadings();

				}
				pushToFaye();
			}

		}

		catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Not a valid gateway!");
		}
	}

	public boolean validGateway(String gateway) throws SQLException {

		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * " + "from gateways "
				+ "where id=" + gateway);
		if (resultSet.next()) {
			System.out.println("A gateway with id: " + gateway + " exists!");
			return true;
		} else {
			System.out.println("A gateway with id: " + gateway
					+ " does NOT exists!");
			return false;
		}

	}

	public boolean validSensor(String sensor) throws SQLException {

		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * " + "from sensors "
				+ "where sensor_id=" + "'" + sensor + "'" + " AND gateway_id="
				+ gateway_id);
		if (resultSet.next()) {
			System.out.println("A sensor with id: " + sensor + " exists!");
			return true;
		} else {
			System.out.println("A sensor with id: " + sensor
					+ " does NOT exists!");
			return false;
		}

	}

	public void addSensorReadings() throws SQLException {

		String gateway = "INSERT INTO sensor_readings (sensor_id, gateway_id, value, created_at, updated_at) VALUES( ?, ?, ?, ?, ?)";
		preparedStatement = connection.prepareStatement(gateway);
		preparedStatement.setString(1, sensor_id);
		preparedStatement.setString(2, gateway_id);
		preparedStatement.setString(3, value);
		preparedStatement.setString(4, time);
		preparedStatement.setString(5, time);
		preparedStatement.executeUpdate();

		preparedStatement.close();
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

	private static String decrypt(byte[] message) {

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
			final Cipher decipher = Cipher
					.getInstance("TripleDES/CBC/Nopadding");
			decipher.init(Cipher.DECRYPT_MODE, key, iv);

			// final byte[] encData = new
			// sun.misc.BASE64Decoder().decodeBuffer(message);
			final byte[] plainText = decipher.doFinal(message);
			return new String(plainText, "UTF-8");

		} catch (NoSuchAlgorithmException e) {

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
	public void pushToFaye() throws IOException{
		URL url = new URL("http://localhost:9292/faye");
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
		conn.getInputStream();
		conn.disconnect();
	}

}
