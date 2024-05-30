import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.lanut"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {

    // 下面是Compose桌面应用的依赖
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3-desktop:1.6.10-rc02")
    implementation("org.jetbrains.compose.material3:material3:1.6.10")
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    implementation("org.jetbrains.compose.material:material-icons-extended:1.6.10")

    // 下面是课程设计所用到的依赖
    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.50")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("net.sourceforge.plantuml:plantuml:1.2024.5")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CompilationUIDesign"
            packageVersion = "1.0.0"
        }
    }
}
