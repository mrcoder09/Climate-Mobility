
import Versions.ACTIVITY_COMPOSE
import Versions.COIL
import Versions.CONSTRAINT_LAYOUT
import Versions.COROUTINES_CORE
import Versions.DATASTORE
import Versions.ESPRESSO
import Versions.EXT_JUNIT
import Versions.JUNIT
import Versions.KOTLIN_REFLECT
import Versions.LIFECYCLE_RUNTIME_KTX
import Versions.MATERIAL_M3
import Versions.SDP_COMPOSE
import Versions.SYSTEM_UI_CONTROLLER
import Versions.VANPRA_DATE_PICKER
import Versions.VIEWMODEL_COMPOSE
import Versions.VIEWMODEL_KTX
import Versions.composeVersion
import Versions.navVersion

object Deps {




    //COMPOSE
    const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$LIFECYCLE_RUNTIME_KTX"
    const val activityCompose = "androidx.activity:activity-compose:$ACTIVITY_COMPOSE"
    const val uiCompose = "androidx.compose.ui:ui:$composeVersion"
    const val materialM2 = "androidx.compose.material:material:$composeVersion"
    const val materialM3 = "androidx.compose.material3:material3:$MATERIAL_M3"
    const val toolingPreview = "androidx.compose.ui:ui-tooling-preview:$composeVersion"
//    const val permission = "com.google.accompanist:accompanist-permissions:$permissionVersion"

    const val constraintLayoutCompose =
        "androidx.constraintlayout:constraintlayout-compose:$CONSTRAINT_LAYOUT"
    const val KotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:$KOTLIN_REFLECT"
    const val DataStore = "androidx.datastore:datastore-preferences:$DATASTORE"


    const val systemUiController =
        "com.google.accompanist:accompanist-systemuicontroller:$SYSTEM_UI_CONTROLLER"
    const val navigationCompose = "androidx.navigation:navigation-compose:$navVersion"
    const val sdpCompose = "com.github.Kaaveh:sdp-compose:$SDP_COMPOSE"
    const val dateTimePicker =
        "io.github.vanpra.compose-material-dialogs:datetime:$VANPRA_DATE_PICKER"
    const val coil = "io.coil-kt:coil-compose:$COIL"

    //LifeCycles
    const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$VIEWMODEL_COMPOSE"
    const val runtimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:$VIEWMODEL_COMPOSE"

    // Coroutines
    const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_CORE"
    const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_CORE"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VIEWMODEL_KTX"


    //SQUARE
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.gsonConverter}"
    const val httpLoggingInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.httpLoggingInterceptor}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val chuck = "com.readystatesoftware.chuck:library:${Versions.CHUCK}"
    const val chuckNoOp = "com.readystatesoftware.chuck:library-no-op:${Versions.CHUCK}"

    //Misc
    const val biometric = "androidx.biometric:biometric-ktx:${Versions.biometric}"
    const val easyCropper = "io.github.mr0xf00:easycrop:${Versions.EASY_CROPPER}"
    const val zelory = "id.zelory:compressor:${Versions.Zelory}"

    //DAGGER-HILT
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hiltAndroid}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hiltAndroid}"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavCompose}"

}

object TestDeps {
    const val testjUnit = "androidx.compose.ui:ui-test-junit4:$composeVersion"
    const val jUnit = "junit:junit:$JUNIT"
    const val extjUnit = "androidx.test.ext:junit:$EXT_JUNIT"
    const val espressoCore = "androidx.test.espresso:espresso-core:$ESPRESSO"
    const val composeUITestjUnit = "androidx.compose.ui:ui-test-junit4:$composeVersion"
    const val uiTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
    const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$composeVersion"
}