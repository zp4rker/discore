package com.zp4rker.disbot.bootstrap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.Attributes;

/**
 * @author zp4rker
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException {
        System.out.print("Loading libraries... ");
        DepLoader.loadDeps(() -> {
            try {
                System.out.println("Succesfully loaded libraries.");

                Attributes mf = getManifest();
                String botMain = mf.getValue("Bot-Main");

                Class<?> mainClass = Class.forName(botMain);
                Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);

                mainMethod.invoke(null, (Object) args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static Attributes getManifest() throws IOException {
        String path = "jar:" + Main.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm() + "!/";
        URL url = new URL(path);
        JarURLConnection con = (JarURLConnection) url.openConnection();
        return con.getMainAttributes();
    }

}
