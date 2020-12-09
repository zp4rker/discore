package com.zp4rker.dsc.disbot.bootstrap;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author zp4rker
 */
public class ParseTask implements Callable<Boolean> {

    private final List<String> repos;
    private final Dependency dep;

    public ParseTask(List<String> repos, Dependency dep) {
        this.repos = repos;
        this.dep = dep;
    }

    @Override
    public Boolean call() throws Exception {
        for (int i = 0; i < repos.size(); i++) {
            try {
                dep.subDeps.addAll(PomParser.parsePom(new URL(dep.getUrl(repos.get(i)).replace(".jar", ".pom"))));
                return true;
            } catch (Exception e) {
                if ((i + 1) == repos.size()) return false;
            }
        }
        return false;
    }
}
