package com.zp4rker.discore.bootstrap;

import com.sun.tools.classfile.Dependency;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author zp4rker
 */
public class DependencyLoader {

    public static final ExecutorService async = Executors.newCachedThreadPool();

    static void loadDeps(Runnable onComplete) throws URISyntaxException, InterruptedException, ParserConfigurationException, SAXException, IOException {
        File root = new File(DependencyLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();

        List<Dependency> allDeps = checkCache();
        if (allDeps.isEmpty()) {
            List<Dependency> defaultDeps = PomParser.parsePom(Main.class.getResource("/defaultdeps.xml"), "core");
            List<Dependency> botDeps = PomParser.parsePom(Main.class.getResource("/botdeps.xml"), "bot");
            allDeps = recurseDeps(defaultDeps, botDeps);
        }
        writeCache(allDeps);

        DownloadCounter counter = new DownloadCounter(allDeps.size(), onComplete);
        counter.start();

        for (Dependency dep : allDeps) {
            async.submit(() -> PomParser.downloadDep(dep, counter, root));
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (Main.starting) {
                    System.out.println("\nDependencies took too long to load!");
                    System.exit(0);
                }
            }
        }, TimeUnit.MINUTES.toMillis(2));
    }

    @SafeVarargs
    private static List<Dependency> recurseDeps(List<Dependency>... depLists) {
        List<Dependency> all = new ArrayList<>();
        for (List<Dependency> depList : depLists) {
            for (Dependency dep : depList) {
                all.add(dep);
                if (!dep.subDeps.isEmpty()) all.addAll(recurseDeps(dep.subDeps));
            }
        }
        return filterDupes(all);
    }

    private static List<Dependency> checkCache() throws URISyntaxException, FileNotFoundException {
        List<Dependency> list = new ArrayList<>();

        File root = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        File cache = new File(root, "lib/dep.cache");

        if (!cache.exists() || cache.length() < 1) return list;

        List<String> lines = readLines(new FileInputStream(cache));

        int defaultHash = readLines(Main.class.getResourceAsStream("/defaultdeps.xml")).hashCode();
        if (!lines.get(0).equals(defaultHash + "")) return list;

        int extraHash = readLines(Main.class.getResourceAsStream("/botdeps.xml")).hashCode();
        if (!lines.get(1).equals(extraHash + "")) return list;

        for (int i = 2; i < lines.size(); i++) {
            if (lines.get(i).startsWith("http://") || lines.get(i).startsWith("https://")) {
                PomParser.addRepo(lines.get(i));
            } else {
                String[] params = lines.get(i).split(":");
                String groupId = params[0];
                String artifactId = params[1];
                String version = params[2];
                String scope = params[3];
                list.add(new Dependency(groupId, artifactId, version, scope));
            }
        }

        return list;
    }

    private static void writeCache(List<Dependency> deps) throws URISyntaxException, IOException {
        File root = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        File cache = new File(root, "lib/dep.cache");

        if (cache.exists() && cache.length() > 0) return;

        if (!cache.getParentFile().exists()) cache.getParentFile().mkdirs();
        if (!cache.exists()) cache.createNewFile();

        int defaultHash = readLines(Main.class.getResourceAsStream("/defaultdeps.xml")).hashCode();
        int extraHash = readLines(Main.class.getResourceAsStream("/botdeps.xml")).hashCode();

        BufferedWriter wr = new BufferedWriter(new FileWriter(cache));
        wr.write(defaultHash + "\n");
        wr.write(extraHash + "\n");

        for (Dependency dep : deps) {
            String out = String.format("%s:%s:%s:%s\n", dep.groupId, dep.artifactId, dep.version, dep.scope);
            wr.write(out);
        }

        for (String repo : PomParser.getRepos()) {
            wr.write(repo + "\n");
        }

        wr.flush();
        wr.close();
    }

    private static List<String> readLines(InputStream is) {
        List<String> list = new ArrayList<>();

        if (is == null) return list;

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = rd.readLine()) != null) {
                list.add(line);
            }

            rd.close();
        } catch (IOException ignored) {
        }

        return list;
    }


    private static List<Dependency> filterDupes(List<Dependency> raw) {
        List<Dependency> filtered = new ArrayList<>();
        for (Dependency dep : raw) {
            if (filtered.stream().anyMatch(dep2 -> dep.groupId.equals(dep2.groupId) && dep.artifactId.equals(dep2.artifactId))) continue;
            filtered.add(dep);
        }
        return filtered;
    }

    public static void downloadFile(File root, String path, DownloadCounter counter) throws IOException {
        File file = new File(root, "lib" + path.substring(path.lastIndexOf("/")));

        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (file.length() != 0) {
            extractFile(root, file, counter);
            return;
        } else file.createNewFile();

        URL url = new URL(path);
        Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        extractFile(root, file, counter);
    }

    private static void extractFile(File root, File file, DownloadCounter counter) throws IOException {
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
