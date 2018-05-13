import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.com.gilbord"
version = "1.0"

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.2.30"
    var springBootVersion = "1.5.9.RELEASE"

    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlinVersion))
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-noarg:${kotlinVersion}")
    }
    
}

apply {
    plugin("kotlin")
    plugin("org.springframework.boot")
    plugin("kotlin-spring")
    plugin("application")
    plugin("kotlin-jpa")
}

configure<ApplicationPluginConvention> {
    mainClassName = "com.gilbord.Application"
}

val kotlinVersion: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlinVersion))
    compile("org.postgresql:postgresql:42.2.2")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.0")
    compile("com.h2database:h2")
    compile("com.itextpdf:itextpdf:5.0.6")
    // https://mvnrepository.com/artifact/org.knowm.xchart/xchart
    compile("org.knowm.xchart:xchart:3.5.1")
    compile("org.springframework.boot:spring-boot-starter-security")


}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

