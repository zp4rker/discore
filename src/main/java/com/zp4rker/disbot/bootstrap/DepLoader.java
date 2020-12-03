package com.zp4rker.disbot.bootstrap;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author zp4rker
 */
public class DepLoader {

    private static final ExecutorService async = Executors.newCachedThreadPool();

    static void loadDeps(Runnable onComplete) throws URISyntaxException {
        File root = new File(DepLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();

        List<String> defaultDeps = loadList("/default.dep");
        List<String> botDeps = loadList("/bot.dep");

        List<String> allDeps = new ArrayList<>();
        if (defaultDeps != null) allDeps.addAll(defaultDeps);
        if (botDeps != null) allDeps.addAll(botDeps);

        DepCounter counter = new DepCounter(allDeps.size(), onComplete);

        for (String dep : allDeps) {
            async.submit(() -> {
                try {
                    downloadFile(root, dep, counter);
                } catch (IOException | URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static List<String> loadList(String resource) {
        InputStream is = null;
        BufferedReader rd = null;
        List<String> lines = new ArrayList<>();

        try {
            is = DepLoader.class.getResourceAsStream(resource);
            if (is == null) return null;
            rd = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = rd.readLine()) != null) {
                if (line.startsWith("#")) continue;
                lines.add(line);
            }
        } catch (IOException ignored) {
        } finally {
            try {
                Objects.requireNonNull(is).close();
            } catch (Exception ignored) {
            }
            try {
                Objects.requireNonNull(rd).close();
            } catch (Exception ignored) {
            }
        }

        return lines;
    }

    private static void downloadFile(File root, String path, DepCounter counter) throws IOException, URISyntaxException, InterruptedException {
        File file = new File(root, "lib" + path.substring(path.lastIndexOf("/")));

        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (file.exists()) {
            extractFile(root, file, counter);
            return;
        } else file.createNewFile();

        URL url = new URL(path);
        Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        extractFile(root, file, counter);
    }

    private static void extractFile(File root, File file, DepCounter counter) throws IOException {
        File libDir = new File(root, "lib");

        byte[] buffer = new byte[256 * 1024];
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                File f = new File(libDir, entry.getName());

                if (entry.getName().startsWith("META-INF")) continue;

                if (entry.isDirectory()) {
                    f.mkdirs();
                    continue;
                }

                if (f.exists()) continue;

                try (InputStream is = jar.getInputStream(entry); FileOutputStream os = new FileOutputStream(f)) {
                    for (int r; (r = is.read(buffer)) > 0; ) {
                        os.write(buffer, 0, r);
                    }
                }
            }
        }
        counter.increment();
    }

}
