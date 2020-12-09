package com.zp4rker.dsc.core.bootstrap;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes;

/**
 * @author zp4rker
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException, ParserConfigurationException, SAXException, IOException, InterruptedException, ExecutionException {
        DependencyLoader.loadDeps(() -> {
            try {
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

    /*private static void parsePom(InputStream is) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();
            NodeList deps = root.getElementsByTagName("dependency");

            System.out.println(root.getElementsByTagName("artifactId").item(0).getFirstChild().getNodeValue() + " ->");
            for (int i = 0; i < deps.getLength(); i++) {
                Node depNode = deps.item(i);
                NodeList children = depNode.getChildNodes();

                String groupId = "";
                String artifactId = "";
                String version = "";
                String scope = "compile";
                for (int j = 0; j < children.getLength(); j++) {
                    Node attr = children.item(j);
                    switch (attr.getNodeName()) {
                        case "groupId":
                            groupId = attr.getFirstChild().getNodeValue();
                            break;
                        case "artifactId":
                            artifactId = attr.getFirstChild().getNodeValue();
                            break;
                        case "version":
                            version = attr.getFirstChild().getNodeValue();
                            if (version.contains("project.version")) {
                                version = root.getAttribute("version");
                            }
                            break;
                        case "scope":
                            scope = attr.getFirstChild().getNodeValue();
                            break;
                    }
                }

                if (version.equals("")) continue;
                if (scope.equals("test") || scope.equals("provided")) continue;

                System.out.println(scope + " - " + artifactId);

                try {
                    downloadDep(groupId, artifactId, version, central);
                } catch (IOException e) {
                    try {
                        downloadDep(groupId, artifactId, version, jcenter);
                    } catch (IOException ignored) {
                    }
                }
            }

            System.out.println(root.getElementsByTagName("artifactId").item(0).getFirstChild().getNodeValue() + " <-");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }*/

    /*private static void downloadDep(String groupId, String artifactId, String version, String repoBase) throws IOException {
        String path = String.format("%s/%s/%s/%s/%s-%s.jar", repoBase, groupId.replace(".", "/"), artifactId, version, artifactId, version);
        File dest = new File("lib" + path.substring(path.lastIndexOf("/")));

        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();

        if (!dest.exists()) dest.createNewFile();

        if (Arrays.stream(dest.getParentFile().listFiles()).noneMatch(file -> file.getName().contains(artifactId + "-"))) {
            Files.copy(new URL(path).openStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            parsePom(new URL(path.replace(".jar", ".pom")).openStream());
        } catch (Exception ignored) {}
    }*/

    private static Attributes getManifest() throws IOException {
        String path = "jar:" + Main.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm() + "!/";
        URL url = new URL(path);
        JarURLConnection con = (JarURLConnection) url.openConnection();
        return con.getMainAttributes();
    }

}
