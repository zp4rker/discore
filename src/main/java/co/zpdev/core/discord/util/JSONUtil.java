package co.zpdev.core.discord.util;

import co.zpdev.core.discord.exception.ExceptionHandler;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Util for reading and writing JSON files, both from file and URL.
 *
 * @author ZP4RKER
 * @version 1.0
 */
public class JSONUtil {

    /**
     * Read JSON data from a URL.
     *
     * @param url the url to read from
     * @return the parsed JSONObject
     */
    public static JSONObject fromUrl(String url) {
        String data = "";
        HttpURLConnection con;

        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.connect();

            int response = con.getResponseCode();
            switch (response) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    data = sb.toString().replaceAll("\\\\([\"/])", "$1");
            }
        } catch (IOException e) {
            ExceptionHandler.handleException("Reading file", e);
        }

        return data.isEmpty() ? new JSONObject() : new JSONObject(data);
    }

    /**
     * Reads JSON data from a file.
     *
     * @param file the file to read
     * @return the parsed JSONObject
     */
    public static JSONObject fromFile(File file) {
        String data = "";
        try {
            FileReader rd = new FileReader(file);
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = rd.read()) != -1) {
                sb.append((char) c);
            }
            data = sb.toString();
        } catch (IOException e) {
            ExceptionHandler.handleException("Reading file", e);
        }
        return data.isEmpty() ? new JSONObject() : new JSONObject(data);
    }

    /**
     * Writes data to a file.
     *
     * @param data the JSON to write
     * @param file the file to write to
     */
    public static void toFile(JSONObject data, File file) {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdir();
            if (!file.exists()) file.createNewFile();

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            wr.write(data.toString(2));
            wr.flush();
            wr.close();
        } catch (IOException e) {
            ExceptionHandler.handleException("Writing file", e);
        }
    }

}
