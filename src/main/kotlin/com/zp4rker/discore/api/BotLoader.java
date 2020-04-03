package com.zp4rker.discore.api;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BotLoader {

    private static BotInfo bot;

    public static void main(String[] args) {
        System.out.println("Loading libraries, please wait...");
        loadLibraries();

        bot.setup();
    }

    private static void loadLibraries() {
        //setup kotlin
        List<String> kotlinLibs = Arrays.asList("kotlin-stdlib", "kotlin-stdlib-jdk8");
        kotlinLibs.forEach(it -> {
            File lib = Library.getFile(it + "-1.3.70.jar");
            if (!lib.exists()) {
                lib = Library.downloadFromCentral("org.jetbrains.kotlin", it, "1.3.70");
            }
            Library.add(lib);
        });

        // setup logback
        File slfLib = Library.getFile("slf4j-api-2.0.0-alpha1.jar");
        if (!slfLib.exists()) {
            slfLib = Library.downloadFromCentral("org.slf4j", "slf4j-api", "2.0.0-alpha1");
        }
        File logbackCore = Library.getFile("logback-core-1.3.0-alpha5.jar");
        if (!logbackCore.exists()) {
            logbackCore = Library.downloadFromCentral("ch.qos.logback", "logback-core", "1.3.0-alpha5");
        }
        File logbackLib = Library.getFile("logback-classic-1.3.0-alpha5.jar");
        if (!logbackLib.exists()) {
            logbackLib = Library.downloadFromCentral("ch.qos.logback", "logback-classic", "1.3.0-alpha5");
        }
        Library.addAll(slfLib, logbackCore, logbackLib);

        // setup json
        File jsonLib = Library.getFile("json-20190722.jar");
        if (!jsonLib.exists()) {
            jsonLib = Library.downloadFromCentral("org.json", "json", "20190722");
        }
        Library.add(jsonLib);

        // setup jda
        bot = new BotInfo();
        File jdaLib = Library.getFile("JDA-${info.jdaVersion}.jar");
        if (!jdaLib.exists()) {
            String build = bot.getJdaVersion().split("_")[1];
            String url = "https://ci.dv8tion.net/job/JDA/" + build + "/artifact/build/libs/JDA-" + bot.getJdaVersion() + "-withDependencies-no-opus.jar";
            jdaLib = Library.download(url, jdaLib.getName());
        }
        assert jdaLib != null;
        Library.add(jdaLib);
    }

}
