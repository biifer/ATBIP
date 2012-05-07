import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseController implements Runnable{

	String gateway_id, sensor_id, value, type, time, url;
	Connection connection;
	ResultSet resultSet;
	Statement statement;
	PreparedStatement preparedStatement;
	Exception badGateway = null;
	public DatabaseController(String gateway_id, String sensor_id, String value, String type, String time) throws SQLException{
		this.gateway_id = gateway_id;
		this.sensor_id = sensor_id;
		this.value = value;
		this.type = type;
		this.time = time;
		this.url = "jdbc:mysql://biifer.mine.nu:3306/development3";
		this.connection = DriverManager.getConnection(url,"ivan", "eKufsfrQMNrSyB4K");
		this.resultSet = null;
		this.statement = null;
		this.preparedStatement = null;
	}
	
	@Override
	public void run() {
		System.out.println("DatabaseController received: \nGateway: " + gateway_id + "\nSensor: " + sensor_id + "\nValue: " + value + "\nType: " + type + "\nTime: " + time + "\n");
		try {
			Class.forName("com.mysql.jdbc.Driver");

		// Checks if the gateway exists. If NOT throw badGateway exception
		if(!validGateway(gateway_id)){
			throw badGateway;
		}else{
			//If the sensor already exists, add sensor data to the existing sensor
			if(validSensor(sensor_id)){
				
				System.out.println("Adding data to sensor with id: " + sensor_id);
				
				addSensorData();
				
			}
			//If the sensor does NOT exist, create a new sensor first and then add the sensor data
			else{
				
				System.out.println("Adding new sensor to gateway: " + gateway_id + " and data to sensor with id: " + sensor_id);
				
				addSensor();
				addSensorData();
				
			}
			
		}
		
		
		}
		
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Not a valid gateway!");
		}
		finally{
		}
	}
	public boolean validGateway(String gateway) throws SQLException{
		
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * " + "from gateways " + "where id=" + gateway);
		if(resultSet.next()){
			System.out.println("A gateway with id: " + gateway + " exists!");
			return true;
		}else{
			System.out.println("A gateway with id: " + gateway + " does NOT exists!");
			return false;
		}
		
	}
	public boolean validSensor(String sensor) throws SQLException{
		
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * " + "from sensors " + "where sensor_id=" + "'" +sensor + "'" + " AND gateway_id=" + gateway_id);
		if(resultSet.next()){
			System.out.println("A sensor with id: " + sensor + " exists!");
			return true;
		}else{
			System.out.println("A sensor with id: " + sensor + " does NOT exists!");
			return false;
		}
		
	}
	public void addSensorData() throws SQLException{
		
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
	public void addSensor() throws SQLException{
		
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
	
	
}
