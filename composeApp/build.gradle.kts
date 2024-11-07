import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.kotlin.serialization)
}

// Carregar a chave de API do local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY") ?: ""

kotlin {

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("GoogleMaps") {
            moduleName = "GoogleMaps"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("Google-Maps-iOS-Utils") {
            moduleName = "GoogleMapsUtils"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("GooglePlaces") {
            moduleName = "GooglePlaces"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    sourceSets {

        val generatedDir = buildDir.resolve("generated/buildkonfig")

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.serialization.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.napier.logger)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        commonMain.kotlin.srcDir(generatedDir)

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.maps.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.kotlinx.coroutines.play.services)
            implementation(libs.androidx.core.ktx.v1101)
            implementation(libs.places)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.ktor.client.darwin)
            implementation(libs.kotlinx.coroutines.core)
        }

    }
}

tasks.register("generateBuildKonfig") {
    val outputDir = buildDir.resolve("generated/buildkonfig/com/dmribeiro/cmpmapview")
    val outputFile = outputDir.resolve("BuildKonfig.kt")

    inputs.property("apiKey", mapsApiKey)
    outputs.file(outputFile)

    doLast {
        outputDir.mkdirs()
        outputFile.writeText("""
            package com.seu.pacote

            object BuildKonfig {
                const val MAPS_API_KEY = "$mapsApiKey"
            }
        """.trimIndent())
    }
}

// Assegurar que a tarefa de geração seja executada antes de compilar
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
    dependsOn("generateBuildKonfig")
}

android {
    namespace = "com.dmribeiro.currencyapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        applicationId = "com.dmribeiro.currencyapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        // Outras configurações
        val configProps = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }
        buildConfigField("String", "MAPS_API_KEY", "\"${configProps["MAPS_API_KEY"]}\"")
        manifestPlaceholders["MAPS_API_KEY"] = configProps["MAPS_API_KEY"] as String
    }
}

dependencies {
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.places)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.tasks)
    debugImplementation(compose.uiTooling)
}

