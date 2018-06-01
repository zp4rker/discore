package co.zpdev.core.discord.util;

import co.zpdev.core.discord.exception.ExceptionHandler;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Util for posting a string to hastebin/pushbullet and getting a link in return.
 *
 * @author zpdev
 * @version 1.0
 */
public class PostUtil {

    /**
     * Pastes a provided string to hastebin and returns the URL.
     *
     * @param string the string to paste
     * @return the url to access the paste
     */
    public static String paste(String string) {
        String result = null;
        HttpsURLConnection con = null;

        try {
            URL url = new URL("https://hastebin.com/documents");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.addRequestProperty("User-Agent", "Mozilla/5.0");
            con.connect();

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(string);
            wr.flush();
            wr.close();

            BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = "https://hastebin.com/" + new JSONObject(rd.readLine()).getString("key");
        } catch (IOException e) {
            ExceptionHandler.handleException("Pasting to hastebin", e);
        } finally {
            if (con != null) con.disconnect();
        }

        return result;
    }

    public static void push(String token, String title, String body) {
        HttpsURLConnection con = null;

        try {
            con = (HttpsURLConnection) new URL("https://api.pushbullet.com/v2/pushes").openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Access-Token", token);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.connect();

            JSONObject data = new JSONObject();
            data.put("type", "note");
            data.put("title", title);
            data.put("body", body);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data.toString());
            wr.flush();
            wr.close();

            con.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect();
        }
    }

}
