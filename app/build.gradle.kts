// 🛑 [已移除] 不再需要 kotlin-kapt 插件，因为 Room 代码是 Java
// plugins {
//     alias(libs.plugins.android.application)
//     alias(libs.plugins.kotlin.android)
//     kotlin("kapt")
// }
// ✅ [新配置] 使用标准的 java-library 和 android application 插件
plugins {
    alias(libs.plugins.android.application)
    // 如果你的项目完全没有 Kotlin 代码，下面这行也可以删除。
    // 如果有任何 Kotlin 文件（比如 MainActivity），则必须保留。
    alias(libs.plugins.kotlin.android)
}


android {
    namespace = "com.example.leicameasurement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.leicameasurement"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 如果保留了 kotlin.android 插件，就保留这个 block
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // 基础 Android 库
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 测试库
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ViewModel 和 LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Room 数据库
    implementation(libs.room.runtime)
    // ✅✅✅ [核心修复] 使用 annotationProcessor 因为你的实体类是 Java
    annotationProcessor(libs.room.compiler)
    // 🛑 [已移除] kapt(libs.room.compiler)
    implementation(libs.room.ktx) // room-ktx 依然可以和 Java 代码一起使用，无需改动

    // 权限请求
    implementation(libs.accompanist.permissions)

    // 日志工具
    implementation(libs.timber)

    implementation("com.google.code.gson:gson:2.10.1")

}
