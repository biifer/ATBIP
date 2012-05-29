import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.*;
import java.text.*;
import javax.crypto.*;
import java.util.Date;
import javax.crypto.spec.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import net.sf.json.JSONObject;

public class DatabaseController implements Runnable {

	String gateway_id, sensor_id, type, time, url = "jdbc:mysql://biifer.mine.nu:3306/development3";
	float value;
	long t;
	Connection connection;
	ResultSet resultSet;
	Statement statement;
	byte[] encryptedMessage;
	PreparedStatement preparedStatement;
	Exception badGateway = new Exception("Not a valid gateway!");
	Exception badMessageDigestException = new Exception("Bad MessageDigest!");
	Exception wrongNumberOfAttributesException = new Exception(
			"Wrong number of attributes in message!");
	PoolOfTasks poolOfTasks;
	String decryptedMessage = null;
	URL fayeUrl = new URL("http://biifer.mine.nu:9292/faye");
	HttpURLConnection fayeConnection;
	OutputStreamWriter outputWriter;

	public DatabaseController(PoolOfTasks poolOfTasks) throws SQLException, IOException{
		this.poolOfTasks = poolOfTasks;
		this.resultSet = null;
		this.statement = null;
		this.preparedStatement = null;
		connection = DriverManager.getConnection(url, "ivan",
				"eKufsfrQMNrSyB4K");
		
		initializeFayeConnection();
	}
	
	public void initializeFayeConnection() throws IOException{
		fayeConnection = (HttpURLConnection) fayeUrl.openConnection();
		fayeConnection.setDoOutput(true);
		fayeConnection.setRequestProperty("Content-Type", "application/json");
		fayeConnection.setRequestMethod("POST");
		fayeConnection.connect();
	}

	@Override
	public void run() {

		while (true) {
			try {
				encryptedMessage = poolOfTasks.getTask();
				t = new Date().getTime();
				System.out.println("Thread: " + Thread.currentThread().getId()
						+ " Received encoded message: " + encryptedMessage
						+ "\nAt: " + t);

				decryptedMessage = decryptAES(encryptedMessage);
				System.out.println(Thread.currentThread().getId() + " Decrypt done after: "
						+ (new Date().getTime() - t));

				// System.out.println("Decrypted message: " +
				// decryptedMessage.trim() );

				// Creates a DOM-object from the message received and extracts
				// sensor_reading elements
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(
						new StringReader(decryptedMessage.trim())));
				NodeList sensor_readings = document
						.getElementsByTagName("sensor_reading");

				// For each sensor_reading element in the DOM-object do the
				// necessary operations
				for (int i = 0; i < sensor_readings.getLength(); i++) {
					try {
						// Extracts attributes from the element
						Element node = (Element) sensor_readings.item(i);
						NamedNodeMap attr = node.getAttributes();
						if (attr.getLength() < 5) {
							throw wrongNumberOfAttributesException;
						}
						gateway_id = node.getAttribute("gateway_id");
						sensor_id = node.getAttribute("sensor_id");
						value = Float
								.valueOf(node.getAttribute("value").trim())
								.floatValue();
						type = node.getAttribute("type");
						time = node.getAttribute("time");

						Class.forName("com.mysql.jdbc.Driver");
						System.out.println(Thread.currentThread().getId() + " Creating connection after: "
								+ (new Date().getTime() - t));
						if (connection.isClosed()) {
							connection = DriverManager.getConnection(url,
									"ivan", "eKufsfrQMNrSyB4K");
							
						}
						long time2 = new Date().getTime();
						System.out.println(Thread.currentThread().getId() + " Connection done after: "
								+ (time2 - t) + "ms");

						statement = connection.createStatement();
						resultSet = statement
								.executeQuery("SELECT id AS gateway_id, "
										+ "(SELECT sensor_id FROM sensors WHERE sensor_id = '"
										+ sensor_id
										+ "' AND gateway_id = "
										+ gateway_id
										+ ") AS valid, "
										+ "(SELECT created_at FROM "
										+ "(SELECT id, created_at FROM sensor_readings WHERE sensor_id = '"
										+ sensor_id
										+ "' AND gateway_id ="
										+ gateway_id
										+ " ORDER BY id DESC LIMIT 1) AS timetable)"
										+ " AS timestamp FROM gateways WHERE id = "
										+ gateway_id);
						System.out.println(Thread.currentThread().getId() + " 'Checking' query done after: "
								+ (new Date().getTime() - t));

						// Checks if the gateway exists. If NOT throw badGateway
						// exception
						if (!resultSet.next()) {
							throw badGateway;
						} else {
							// If the sensor already exists, add sensor data to
							// the existing
							// sensor
							if (resultSet.getString(2) != null) {

								// System.out.println("Adding data to sensor with id: "
								// + sensor_id + "\n");

								addSensorReadings(resultSet.getString(3));

							}
							// If the sensor does NOT exist, create a new sensor
							// first and
							// then add the sensor readings
							else {

								System.out
										.println(Thread.currentThread().getId() + " Adding new sensor to gateway: "
												+ gateway_id
												+ " and data to sensor with id: "
												+ sensor_id + "\n");

								addSensor();
								addSensorReadings(resultSet.getString(3));

							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// connection.close();
				System.out.println(Thread.currentThread().getId() + " Completely done after: "
						+ (new Date().getTime() - t) + "ms");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addSensorReadings(String timestamp) throws SQLException,
			ParseException, IOException {
		System.out.println(Thread.currentThread().getId() + " Adding sensor reading after: "
				+ (new Date().getTime() - t));
		if (timestamp != null) {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long timestampLong = sdf.parse(timestamp).getTime();
			long timeLong = sdf.parse(time).getTime();
			// if(timeLong > (timestampLong + 2000)){
			if (true) {
				String gateway = "INSERT INTO sensor_readings (sensor_id, gateway_id, value, created_at, updated_at) VALUES( ?, ?, ?, ?, ?)";
				preparedStatement = connection.prepareStatement(gateway);
				preparedStatement.setString(1, sensor_id);
				preparedStatement.setString(2, gateway_id);
				preparedStatement.setString(3, "" + value);
				preparedStatement.setString(4, time);
				preparedStatement.setString(5, time);
				System.out.println(Thread.currentThread().getId() + " executing Update: "
						+ (new Date().getTime() - t));
				preparedStatement.executeUpdate();
				System.out.println(Thread.currentThread().getId() + " executeUpdate done after: "
						+ (new Date().getTime() - t));
				preparedStatement.close();
				
				System.out.println(Thread.currentThread().getId() + " closed prepared statement after: "
						+ (new Date().getTime() - t));
				pushToFaye();
				System.out.println(Thread.currentThread().getId() +  " pushed to faye after: "
						+ (new Date().getTime() - t));
			}
		} else {
			String gateway = "INSERT INTO sensor_readings (sensor_id, gateway_id, value, created_at, updated_at) VALUES( ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(gateway);
			preparedStatement.setString(1, sensor_id);
			preparedStatement.setString(2, gateway_id);
			preparedStatement.setString(3, "" + value);
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

	private String decryptAES(byte[] message) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec("PK80111q''eto0z<"
				.getBytes(), "AES");
		Cipher cipher;

		/**
		 * Extracts the encrypted message and the message digest from the
		 * message received.
		 */
		byte[] original = null;
		byte[] digest = new byte[16];
		byte[] digestedMessage = new byte[1008];
		byte[] iv = new byte[16];
		byte[] encryptedMessage = new byte[992];

		// Extracts the message that the digest was calculated from
		for (int i = 0; i < 1008; i++) {
			digestedMessage[i] = message[i];
		}
		// Extracts the digest
		for (int i = 0; i < 16; i++) {
			digest[i] = message[1008 + i];
		}

		// Calculates the digest from the message
		Mac md = Mac.getInstance("HmacMD5");
		md.init(skeySpec);
		md.update(digestedMessage);
		if (!MessageDigest.isEqual(digest, md.doFinal())) {
			throw badMessageDigestException;
		}
		// Extracts the encrypted messsage
		for (int i = 0; i < 992; i++) {
			encryptedMessage[i] = digestedMessage[i];
		}
		// Extracts the initialization vector
		for (int i = 0; i < 16; i++) {
			iv[i] = digestedMessage[i + 992];
		}
		// Decrypts the message using the extracted IV
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		cipher = Cipher.getInstance("AES/CBC/Nopadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, paramSpec);
		original = cipher.doFinal(encryptedMessage);

		return new String(original);

	}

	public void pushToFaye() throws IOException {
		JSONObject arrayMessageJSON = new JSONObject();
		JSONObject elementJSON = new JSONObject();
		elementJSON.put("value", value);
		elementJSON.put("created_at", time);
		arrayMessageJSON.put("channel", "/sensor/" + sensor_id + "/new");
		arrayMessageJSON.put("data", elementJSON.toString());
		
		try {
			outputWriter = new OutputStreamWriter(new BufferedOutputStream(fayeConnection.getOutputStream()));
			outputWriter.write(arrayMessageJSON.toString());
		} catch (IOException e) {
			fayeConnection.disconnect();
			initializeFayeConnection();
			outputWriter = new OutputStreamWriter(new BufferedOutputStream(fayeConnection.getOutputStream()));
			outputWriter.write(arrayMessageJSON.toString());
		}finally{

			outputWriter.close();
		}
		fayeConnection.getInputStream();

		// conn.disconnect();
	}

}