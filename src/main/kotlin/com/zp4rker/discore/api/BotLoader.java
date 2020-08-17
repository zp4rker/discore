package com.zp4rker.discore.api;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BotLoader {

    private static BotInfo bot;

    public static void main(String[] args) {
        System.out.println("Fetching libraries...");
        fetchLibraries();

        fetchKotlin();
        fetchJDA();

        bot.setup();
    }

    private static void fetchKotlin() {
        //setup kotlin
        List<String> kotlinLibs = Arrays.asList("kotlin-stdlib", "kotlin-stdlib-jdk8");
        kotlinLibs.forEach(it -> {
            File lib = Library.getFile(it + "-1.3.72.jar");
            if (!lib.exists()) {
                lib = Library.downloadFromCentral("org.jetbrains.kotlin", it, "1.3.72");
            }
            Library.add(lib);
        });
    }

    private static void fetchJDA() {
        // setup jda
        bot = new BotInfo();
        File jdaLib = Library.getFile("JDA-" + bot.getJdaVersion() + ".jar");
        if (!jdaLib.exists()) {
            String build = bot.getJdaVersion().split("_")[1];
            String url = "https://ci.dv8tion.net/job/JDA/" + build + "/artifact/build/libs/JDA-" + bot.getJdaVersion() + "-withDependencies-no-opus.jar";
            jdaLib = Library.download(url, jdaLib.getName());
        }
        assert jdaLib != null;
        Library.add(jdaLib);
    }

    private static void fetchLibraries() {
        // setup logback
        File slfLib = Library.getFile("slf4j-api-1.7.30.jar");
        if (!slfLib.exists()) {
            slfLib = Library.downloadFromCentral("org.slf4j", "slf4j-api", "1.7.30");
        }
        File logbackCore = Library.getFile("logback-core-1.2.3.jar");
        if (!logbackCore.exists()) {
            logbackCore = Library.downloadFromCentral("ch.qos.logback", "logback-core", "1.2.3");
        }
        File logbackLib = Library.getFile("logback-classic-1.2.3.jar");
        if (!logbackLib.exists()) {
            logbackLib = Library.downloadFromCentral("ch.qos.logback", "logback-classic", "1.2.3");
        }
        Library.addAll(slfLib, logbackCore, logbackLib);

        // setup json
        File jsonLib = Library.getFile("json-20190722.jar");
        if (!jsonLib.exists()) {
            jsonLib = Library.downloadFromCentral("org.json", "json", "20190722");
        }
        Library.add(jsonLib);
    }

}
