import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.sentry)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "au.com.shiftyjelly.pocketcasts"

    defaultConfig {
        applicationId = project.property("applicationId").toString()
        multiDexEnabled = true

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDir(files("$rootDir/modules/services/model/schemas"))
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    buildTypes {
        named("debug") {
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_radioactive"
        }

        named("debugProd") {
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_3"
        }

        named("release") {
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"

            if (!file("${project.rootDir}/sentry.properties").exists()) {
                println("WARNING: Sentry configuration file 'sentry.properties' not found. The ProGuard mapping files won't be uploaded.")
            }
        }
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                forEach {
                    println(it.name)
                }
                id("java") {
                    option("lite")
                }
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    debugProdImplementation(libs.compose.ui.tooling)
    implementation("io.branch.engage:conduit-source:0.2.3-pocketcasts.9@aar") {
        isTransitive = true
    }
    implementation(libs.aboutlibraries.compose)
    implementation(libs.aboutlibraries.core)
    implementation(libs.accessibility.test.framework)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.car)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.mediarouter)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.webkit)
    implementation(libs.automattic.crashlogging)
    implementation(libs.automattic.explat)
    implementation(libs.automattic.tracks)
    implementation(libs.billing.ktx)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.compose.activity)
    implementation(libs.compose.animation)
    implementation(libs.compose.constraintlayout)
    implementation(libs.compose.livedata)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window.size)
    implementation(libs.compose.rxjava2)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.webview)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.play.services)
    implementation(libs.coroutines.reactive)
    implementation(libs.coroutines.rx2)
    implementation(libs.coroutines.test)
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.core)
    implementation(libs.device.names)
    implementation(libs.encryptedlogging)
    implementation(libs.engage)
    implementation(libs.ffmpeg)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.flexbox)
    implementation(libs.fragment.compose)
    implementation(libs.fragment.ktx)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.guava)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    implementation(libs.horologist.auth.data.phone)
    implementation(libs.horologist.datalayer)
    implementation(libs.junit)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.reactivestreams.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lottie)
    implementation(libs.lottie.compose)
    implementation(libs.material)
    implementation(libs.material.dialogs)
    implementation(libs.material.progressbar)
    implementation(libs.media3.cast)
    implementation(libs.media3.common)
    implementation(libs.media3.datasource)
    implementation(libs.media3.datasource.okhttp)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.extractor)
    implementation(libs.media3.ui)
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.navigation.compose)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.okHttp.logging)
    implementation(libs.okhttp)
    implementation(libs.play.auth)
    implementation(libs.play.cast)
    implementation(libs.play.review)
    implementation(libs.play.wearable)
    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlinlite)
    implementation(libs.reorderable)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit.protobuf) {
        exclude(
            group = "com.google.protobuf",
            module = "protobuf-java"
        )
    }
    implementation(libs.retrofit.rx2)
    implementation(libs.room)
    implementation(libs.room.ktx)
    implementation(libs.room.rx2)
    implementation(libs.rx2.android)
    implementation(libs.rx2.extensions)
    implementation(libs.rx2.java)
    implementation(libs.rx2.kotlin)
    implementation(libs.rx2.relay)
    implementation(libs.showkase)
    implementation(libs.tasker)
    implementation(libs.timber)
    implementation(libs.work.runtime)
    implementation(libs.work.rx2)
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.moshi.kotlin.codegen)
    ksp(libs.room.compiler)
    ksp(libs.showkase.processor)
} 
