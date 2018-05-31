package co.zpdev.bots.core.util;

import co.zpdev.bots.core.logger.ZLogger;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A utility for pasting a string to hastebin and getting a link in return.
 *
 * @author zpdev
 * @version 0.9_BETA
 * @deprecated until future update.
 */
public class PasteUtil {

    public static String paste(String string) {
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
            return "https://hastebin.com/" + new JSONObject(rd.readLine()).getString("key");
        } catch (Exception e) {
            ZLogger.warn("Could not paste string!");
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) con.disconnect();
        }
    }

}
