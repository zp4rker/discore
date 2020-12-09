package com.zp4rker.dsc.disbot.bootstrap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author zp4rker
 */
public class PomParser {

    private static final List<String> repos = new ArrayList<>();

    public static List<Dependency> parsePom(URL location) throws InterruptedException, IOException, SAXException, ParserConfigurationException, ExecutionException {
        if (!repos.contains("https://repo1.maven.org/maven2")) repos.add("https://repo1.maven.org/maven2");

        List<Callable<Boolean>> taskList = new ArrayList<>();

        List<Dependency> depList = new ArrayList<>();

        if (location == null) return depList;

        InputStream is = location.openStream();
        if (is.available() < 1) throw new IOException("Empty stream!");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(is);

        Element root = doc.getDocumentElement();

        NodeList repoList = root.getElementsByTagName("repository");
        for (int i = 0; i < repoList.getLength(); i++) {
            Node repoNode = repoList.item(i);
            NodeList children = repoNode.getChildNodes();

            String url = "";
            for (int j = 0; j < children.getLength(); j++) {
                Node attr = children.item(j);
                if (attr.getNodeName().equalsIgnoreCase("url")) {
                    url = attr.getFirstChild().getNodeValue();
                }
            }

            if (url.equals("")) continue;
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);

            if (url.contains("oss.sonatype.org/service/local/staging/deploy/maven2")) continue;
            if (!url.startsWith("https://") && !url.startsWith("http://")) continue;
            if (!repos.contains(url)) repos.add(url);
        }

        Node depsNode = getChildByTag(root, "dependencies");
        if (depsNode != null) {
            Element depsElement = (Element) depsNode;
            NodeList deps = depsElement.getElementsByTagName("dependency");
            for (int i = 0; i < deps.getLength(); i++) {
                Node depNode = deps.item(i);
                NodeList children = depNode.getChildNodes();

                String groupId = "";
                String artifactId = "";
                String version = "";
                String scope = "compile";
                String optional = "false";
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
                            break;
                        case "scope":
                            scope = attr.getFirstChild().getNodeValue();
                            break;
                        case "optional":
                            optional = attr.getFirstChild().getNodeValue();
                    }
                }

                if (optional.equals("true")) continue;
                if (!scope.equals("compile") && !scope.equals("runtime")) continue;
                if (version.equals("")) version = searchDepManagement(groupId, artifactId, root.getElementsByTagName("parent").item(0).getChildNodes());
                if (version == null) continue;

                if (version.equals("${project.version}")) {
                    version = Objects.requireNonNull(getChildByTag(root, "version")).getFirstChild().getNodeValue();
                } else if (version.matches("\\$\\{.*}")) {
                    version = version.substring(2, version.length() - 1);
                    NodeList varSearch = root.getElementsByTagName("version");
                    if (varSearch.getLength() > 0) version = varSearch.item(0).getFirstChild().getNodeValue();
                    else version = searchParents(version, root.getElementsByTagName("parent").item(0).getChildNodes(), false);
                }

                Dependency dep = new Dependency(groupId, artifactId, version, scope);
                depList.add(dep);

                taskList.add(new ParseTask(repos, dep));
            }
        }

        if (!taskList.isEmpty()) DependencyLoader.async.invokeAll(taskList);

        return depList;
    }

    public static void downloadDep(Dependency dep, DownloadCounter counter, File root) {
        for (int i = 0; i < repos.size(); i++) {
            try {
                DependencyLoader.downloadFile(root, dep.getUrl(repos.get(i)), counter);
                break;
            } catch (Exception e) {
                if ((i + 1) == repos.size()) {
                    if (e instanceof FileNotFoundException) counter.increment();
                    else e.printStackTrace();
                }
            }
        }
    }

    public static List<String> getRepos() {
        return repos;
    }

    public static void addRepo(String repo) {
        if (!repos.contains(repo)) repos.add(repo);
    }

    private static String searchParents(String key, NodeList parent, boolean searchRoot) throws ParserConfigurationException {
        Element root = getParent(parent);
        if (root == null) return "";

        String match;

        if (!searchRoot) {
            NodeList matches = root.getElementsByTagName(key);

            if (matches.getLength() < 1) return searchParents(key, root.getElementsByTagName("parent").item(0).getChildNodes(), searchRoot);

            match = matches.item(0).getFirstChild().getNodeValue();
        } else {
            Node nodeMatch = getChildByTag(root, key);

            if (nodeMatch == null) return searchParents(key, root.getElementsByTagName("parent").item(0).getChildNodes(), searchRoot);

            match = nodeMatch.getFirstChild().getNodeValue();
        }

        if (match.matches("\\$\\{.*}")) {
            match = match.substring(2, match.length() - 1);
            if (match.equalsIgnoreCase("project.version")) {
                match = Objects.requireNonNull(getChildByTag(root, "version")).getFirstChild().getNodeValue();
            } else {
                NodeList matchSearch = root.getElementsByTagName(match);
                if (matchSearch.getLength() > 0) match = matchSearch.item(0).getFirstChild().getNodeValue();
                else match = searchParents(match, root.getElementsByTagName("parent").item(0).getChildNodes(), searchRoot);
            }
        }

        return match;
    }

    private static String searchDepManagement(String groupId, String artifactId, NodeList parent) throws ParserConfigurationException {
        Element root = getParent(parent);
        if (root == null) return "";

        String version = null;

        NodeList depManagement = root.getElementsByTagName("dependencyManagement");
        if (depManagement.getLength() > 0) {
            Element element = (Element) depManagement.item(0);
            NodeList deps = element.getElementsByTagName("dependency");
            for (int i = 0; i < deps.getLength(); i++) {
                Element dep = (Element) deps.item(i);

                NodeList gSearch = dep.getElementsByTagName("groupId");
                if (gSearch.getLength() > 0) {
                    String result = gSearch.item(0).getFirstChild().getNodeValue();
                    if (!result.equals(groupId)) continue;
                }

                NodeList aSearch = dep.getElementsByTagName("artifactId");
                if (aSearch.getLength() > 0) {
                    String result = aSearch.item(0).getFirstChild().getNodeValue();
                    if (!result.equals(artifactId)) continue;
                }

                NodeList vSearch = dep.getElementsByTagName("version");
                if (vSearch.getLength() > 0) {
                    version = vSearch.item(0).getFirstChild().getNodeValue();
                    if (version.matches("\\$\\{.*}")) {
                        version = version.substring(2, version.length() - 1);
                        if (version.equals("project.version")) {
                            version = Objects.requireNonNull(getChildByTag(root, "version")).getFirstChild().getNodeValue();
                        } else {
                            NodeList varSearch = root.getElementsByTagName(version);
                            if (varSearch.getLength() > 0) version = varSearch.item(0).getFirstChild().getNodeValue();
                            else version = searchParents(version, root.getElementsByTagName("parent").item(0).getChildNodes(), false);
                        }
                    }
                }
            }
        } else {
            return searchDepManagement(groupId, artifactId, root.getElementsByTagName("parent").item(0).getChildNodes());
        }

        return version;
    }

    private static Node getChildByTag(Node parent, String tag) {
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child.getNodeName().equalsIgnoreCase(tag)) return child;
        }
        return null;
    }

    private static Element getParent(NodeList parent) throws ParserConfigurationException {
        String groupId = "";
        String artifactId = "";
        String version = "";

        for (int i = 0; i < parent.getLength(); i++) {
            Node attr = parent.item(i);

            switch (attr.getNodeName()) {
                case "groupId":
                    groupId = attr.getFirstChild().getNodeValue();
                    break;
                case "artifactId":
                    artifactId = attr.getFirstChild().getNodeValue();
                    break;
                case "version":
                    version = attr.getFirstChild().getNodeValue();
            }
        }

        Dependency parentModule = new Dependency(groupId, artifactId, version, "compile");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = null;

        for (int i = 0; i < repos.size(); i++) {
            try {
                doc = builder.parse(new URL(parentModule.getUrl(repos.get(i)).replace(".jar", ".pom")).openStream());
                break;
            } catch (Exception e) {
                if ((i + 1) == repos.size()) e.printStackTrace();
            }
        }

        if (doc == null) return null;

        return doc.getDocumentElement();
    }

}