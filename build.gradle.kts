plugins {
    war
    id("com.bmuschko.cargo") version "2.8.0"
    id("com.diffplug.gradle.spotless") version "4.5.1"
}

group = "com.github.stiemannkj1.example"
version = "1.0.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

val tomcat by configurations.creating
val junitVersion = "5.3.1"

dependencies {
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")
    tomcat("org.apache.tomcat:tomcat:9.0.41@zip")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.test {
    useJUnitPlatform()
}

cargo {
    containerId = "tomcat9x"
    withGroovyBuilder {
        "deployable" {
            "setContext"("${project.name}")
        }
    }
    local.withGroovyBuilder {
        "installer" {
            val tomcatFiles = tomcat.resolve()
            assert(tomcatFiles.size == 1)
            "setInstallConfiguration"(files(tomcatFiles))
            "setDownloadDir"(tomcatFiles.first().parentFile)
            "setExtractDir"(file("${project.buildDir}/tomcat"))
        }
        project.findProperty("cargo.start.jvmargs").also {
            "setJvmArgs"(it)
        }
    }
}

spotless {
    java {
        googleJavaFormat()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
