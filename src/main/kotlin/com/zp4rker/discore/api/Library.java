package com.zp4rker.discore.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.util.Arrays;

public class Library {

    public static void add(File file) {
        try {
            URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(cl, file.toURI().toURL());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            System.out.println("Failed to load library: " + file.getName());
        }
    }

    public static void addAll(File... files) {
        Arrays.stream(files).forEach(Library::add);
    }

    public static File download(String sUrl, String name) {
        File file = getFile(name);

        try {
            URL url = new URL(sUrl);

            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (file.exists()) return file;
            else file.createNewFile();

            FileOutputStream os = new FileOutputStream(file);
            os.getChannel().transferFrom(Channels.newChannel(url.openStream()), 0, Long.MAX_VALUE);
            os.close();

            return file;
        } catch (IOException e) {
            System.out.println("Unable to download library from: " + sUrl);
        }

        return null;
    }

    public static File downloadFromCentral(String groupId, String artifactId, String version) {
        String url = String.format("https://repo1.maven.org/maven2/%s/%s/%s/%s-%s.jar", groupId.replace(".", "/"), artifactId, version, artifactId, version);
        return download(url, artifactId + "-" + version + ".jar");
    }

    public static File getFile(String name) {
        File root = new File(Library.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
        return new File(root, "libraries/" + name);
    }

}
