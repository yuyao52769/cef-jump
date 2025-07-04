plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.17.1"
}

group = "cn.yuyao"
version = "1.5-release"

repositories {
    mavenCentral()
}

dependencies {
    api("org.thymeleaf:thymeleaf:3.1.2.RELEASE")
    api("cn.hutool:hutool-all:5.8.22")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
    // 确保将依赖打包到插件中
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("243.*")
    }


    buildPlugin {
        // 设置重复处理策略为排除重复项
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        // 将依赖复制到插件的 lib 目录，使用 runtimeClasspath 替代 api
        from(configurations.runtimeClasspath) {
            into("lib")
        }
    }


    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
