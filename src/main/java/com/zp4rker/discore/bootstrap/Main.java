package com.zp4rker.discore.bootstrap;

import com.zp4rker.discore.util.FileUtilsKt;
import org.fusesource.jansi.AnsiConsole;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.Attributes;

/**
 * @author zp4rker
 */
public class Main {

    public static boolean starting = true;
    public static boolean debug = false;

    public static void main(String[] args) throws URISyntaxException, ParserConfigurationException, SAXException, IOException, InterruptedException {

        if (Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("-debug"))) {
            debug = true;
        }
        DependencyLoader.loadDeps(() -> {
            try {
                starting = false;

                Attributes mf = getManifest();
                String botMain = mf.getValue("Bot-Main");

                Class<?> mainClass = Class.forName(botMain);
                Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);

                if (AnsiConsole.isInstalled()) AnsiConsole.systemInstall();
                FileUtilsKt.datedArchive(new File("logs/log.txt"), true);

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
