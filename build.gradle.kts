import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"

    id("com.github.johnrengelman.shadow") version "6.1.0"

    signing
    maven

    id("io.codearte.nexus-staging") version "0.22.0"
}

group = "com.zp4rker"
version = "2.4.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("net.dv8tion:JDA:4.2.0_240")

    implementation("com.zp4rker:log4kt:1.1.8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
    implementation("com.charleskorn.kaml:kaml:0.46.0")

    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("io.leego:banana:2.1.0")

    implementation("org.reflections:reflections:0.10.2")

    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("org.jline:jline-reader:3.19.0")
}

tasks.create<Sync>("filterSources") {
    from("src/main/kotlin") {
        include("com/zp4rker/discore/Constants.kt")
    }
    into("$buildDir/generated-sources")
    filter(ReplaceTokens::class, mapOf("tokens" to mapOf("VERSION" to project.version)))
    kotlin.sourceSets["main"].kotlin.exclude("com/zp4rker/discore/Constants.kt")
}

tasks.compileKotlin {
    dependsOn("filterSources")
    source(listOf("$buildDir/generated-sources"))
}

tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.getByName("javadoc"))
}

tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts {
    add("archives", tasks.getByName("javadocJar"))
    add("archives", tasks.getByName("sourcesJar"))
}

signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
    }
    sign(configurations["archives"])
}

tasks.named<Upload>("uploadArchives") {
    repositories {
        withConvention(MavenRepositoryHandlerConvention::class) {
            mavenDeployer {
                beforeDeployment {
                    signing.signPom(this)
                }

                withGroovyBuilder {
                    "repository"("url" to "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                        "authentication"("userName" to properties["ossrhUsername"], "password" to properties["ossrhPassword"])
                    }
                    "snapshotRepository"("url" to "https://oss.sonatype.org/content/repositories/snapshots/") {
                        "authentication"("userName" to properties["ossrhUsername"], "password" to properties["ossrhPassword"])
                    }
                }

                pom.project {
                    withGroovyBuilder {
                        "name"("Discore")
                        "artifactId"("discore")
                        "packaging"("jar")
                        "description"("A Discord bot core built upon JDA, written in Kotlin.")
                        "url"("https://github.com/zp4rker/discore")

                        "scm" {
                            "connection"("scm:git:git://github.com/zp4rker/discore.git")
                            "url"("https://github.com/zp4rker/discore")
                        }

                        "licenses" {
                            "license" {
                                "name"("The Apache License, Version 2.0")
                                "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }

                        "developers" {
                            "developer" {
                                "id"("zp4rker")
                                "name"("Zaeem Parker")
                                "email"("iam@zp4rker.com")
                            }
                        }
                    }
                }
            }
        }
    }
}