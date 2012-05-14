import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sf.json.JSONObject;

public class sendTest {

	public static void main(String[] args) throws UnknownHostException, IOException, ParseException {
		String timestamp = "2013-05-12 14:12:13"; 
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		java.util.Date date = sdf.parse(timestamp);  
		System.out.println(sdf.parse(timestamp).getTime());

	}

}
