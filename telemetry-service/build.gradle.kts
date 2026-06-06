plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.10.0"
}

group = "org.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation(enforcedPlatform("io.grpc:grpc-bom:1.81.0"))
    implementation("io.grpc:grpc-protobuf:1.81.0")
    implementation("io.grpc:grpc-stub:1.81.0")
    implementation("com.google.protobuf:protobuf-java:4.34.1")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.34.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.81.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/sources/proto/main/java",
                "build/generated/sources/proto/main/grpc"
            )
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}
