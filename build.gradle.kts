plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.34"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

val libVersion: String = "6.0.7-44"

taboolib {
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
    install("module-kether")
    install("module-nms")
    install("module-nms-util")
    install("module-database")
    install("platform-bukkit")
    install("expansion-command-helper")
    description {
        contributors {
            name("寒雨")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
        }
    }
    classifier = null
    version = libVersion
}

repositories {
    mavenCentral()
//    maven { url = uri("https://repo.tabooproject.org/repository/releases") }
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.9.0")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11800:11800-minimize:api")
    compileOnly("ink.ptms.core:v11800:11800-minimize:mapped")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    implementation(kotlin("reflect"))

//    implementation("io.izzel:taboolib:$libVersion:common")
//    implementation("io.izzel:taboolib:$libVersion:common-5")
//    implementation("io.izzel:taboolib:$libVersion:module-chat")
//    implementation("io.izzel:taboolib:$libVersion:module-configuration")
//    implementation("io.izzel:taboolib:$libVersion:module-kether")
//    implementation("io.izzel:taboolib:$libVersion:module-nms")
//    implementation("io.izzel:taboolib:$libVersion:module-nms-util")
//    implementation("io.izzel:taboolib:$libVersion:module-database")
//    implementation("io.izzel:taboolib:$libVersion:platform-bukkit")
//    implementation("io.izzel:taboolib:$libVersion:expansion-command-helper")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}