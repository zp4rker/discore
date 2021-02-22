package com.zp4rker.discore.bootstrap;

import com.sun.tools.classfile.Dependency;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author zp4rker
 */
public class ParseTask implements Callable<Boolean> {

    private final List<String> repos;
    private final Dependency dep;
    private ParseProgress prog;

    public ParseTask(List<String> repos, Dependency dep, ParseProgress prog) {
        this.repos = repos;
        this.dep = dep;
        this.prog = prog;
    }

    @Override
    public Boolean call() {
        for (int i = 0; i < repos.size(); i++) {
            try {
                dep.subDeps.addAll(PomParser.parsePom(new URL(dep.getUrl(repos.get(i)).replace(".jar", ".pom")), "sub"));
                if (prog != null) prog.increment();
                return true;
            } catch (Exception e) {
                if ((i + 1) == repos.size()) {
                    if (prog != null) prog.increment();
                    return false;
                }
            }
        }
        if (prog != null) prog.increment();
        return false;
    }
}
