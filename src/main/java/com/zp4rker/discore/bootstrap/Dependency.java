package com.zp4rker.discore.bootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zp4rker
 */
public class Dependency {
    final String groupId;
    final String artifactId;
    final String version;
    final String scope;

    final List<Dependency> subDeps = new ArrayList<>();

    public Dependency(String groupId, String artifactId, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
    }

    public String getUrl(String baseUrl) {
        return String.format("%s/%s/%s/%s/%s-%s.jar", baseUrl, groupId.replace(".", "/"), artifactId, version, artifactId, version);
    }

}
