package com.zp4rker.core.discord.config;

import com.zp4rker.core.discord.exception.ExceptionHandler;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author ZP4RKER
 */
public class Config {

    private File file;
    public JSONObject data;

    /**
     * Default constructor.
     *
     * @param file name of the config file
     * @throws URISyntaxException when unable to get directory
     */
    public Config(String file) throws URISyntaxException {
        File dir = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        this.file = new File(dir, file);
        reload();
    }

    /**
     * Reloads the config file.
     */
    private void reload() {
        try {
            if (!file.exists()) file.createNewFile();

            FileReader rd = new FileReader(file);
            int c; StringBuilder sb = new StringBuilder();
            while ((c = rd.read()) != -1) {
                sb.append((char) c);
            }
            String result = sb.toString();

            this.data = result.isEmpty() ? new JSONObject() : new JSONObject(result);
        } catch (IOException e) {
            ExceptionHandler.handleException("reloading data from config file", e);
        }
    }

    /**
     * Saves the config file.
     */
    public void save() {
        try {
            FileWriter wr = new FileWriter(file);
            wr.write(data.toString(2));
            wr.flush();
            wr.close();
        } catch (IOException e) {
            ExceptionHandler.handleException("saving data to config file", e);
        }
    }

}
