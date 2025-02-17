plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose)

    alias(libs.plugins.detekt)
}

android {
    namespace = "com.nasdroid.auth.ui"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        sarifReport = true
        htmlReport = false
    }
}

kotlin {
    jvmToolchain(21)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
    basePath = rootDir.absolutePath
}

dependencies {
    implementation(projects.core.design)
    implementation(projects.core.navigation)
    implementation(projects.features.auth.logic)

    implementation(libs.androidx.window)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Compose
    implementation(libs.androidx.navigation)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.bundles.compose)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.bundles.compose.tooling)

    implementation(libs.koin.android)
    implementation(libs.koin.android.compose)
}
