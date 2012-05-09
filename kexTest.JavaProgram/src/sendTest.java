import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import net.sf.json.JSONObject;

public class sendTest {

	public static void main(String[] args) throws UnknownHostException, IOException {

		URL url = new URL("http://localhost:9292/faye");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestMethod("POST");
		conn.connect();
		JSONObject json = new JSONObject();
		JSONObject js2 = new JSONObject();
		js2.put("value", 15);
		json.put("channel", "/messages/new");
		json.put("data", js2.toString());
		json.put("value", 15);
		conn.getOutputStream().write(json.toString().getBytes("UTF-8"));
		conn.getInputStream();
		conn.disconnect();

	}

}
