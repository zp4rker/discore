package com.zp4rker.disbot.bootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zp4rker
 */
public class Dependency {
    String groupId;
    String artifactId;
    String version;
    String scope;

    List<Dependency> subDeps = new ArrayList<>();

    public Dependency(String groupId, String artifactId, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
    }

    public String getUrl(String baseUrl) {
        return String.format("%s/%s/%s/%s/%s-%s.jar", baseUrl, groupId.replace(".", "/"), artifactId, version, artifactId, version);
    }

    /*public void download(File root, DepCounter counter) {
        try {
            DepLoader.downloadFile(root, getUrl(central), counter);
        } catch (Exception e) {
            try {
                DepLoader.downloadFile(root, getUrl(jcenter), counter);
            } catch (Exception ex) {
                counter.increment();
                ex.printStackTrace();
            }
        }
    }*/
}
