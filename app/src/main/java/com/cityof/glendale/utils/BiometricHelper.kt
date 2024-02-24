package com.cityof.glendale.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppOutlinedButton
import com.cityof.glendale.composables.components.DIALOG_BUTTON_HEIGHT
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyleLarge
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


private const val TAG = "BiometricHelper"

data class BiometricCredentials(
    var email: String? = null,
    var password: String? = null,
    var isBiometricEnabled: Boolean = false
)

@HiltViewModel
class BiometricViewModel @Inject constructor(private val preferenceManager: AppPreferenceManager) :
    ViewModel() {

    private val _biometricCredentials = MutableSharedFlow<BiometricCredentials>(replay = 0)
    val biometricCredentials: Flow<BiometricCredentials> = _biometricCredentials

    init {
        doData()
    }

    fun doData() {
        Timber.d("doData")
        viewModelScope.launch {
            Timber.d("launch")
            preferenceManager.biometricDetails.firstOrNull()?.let {
                Timber.d("$it")
                _biometricCredentials.emit(
                    BiometricCredentials(it.first, it.second, it.third ?: false)
                )
            }
        }
    }
}


@Composable
fun LaunchBiometricFlow(
    launchFlow: Boolean = false,
    credentials: BiometricCredentials? = null,
//    viewModel: BiometricViewModel = hiltViewModel(),
    success: (Pair<String, String>) -> Unit,
    failed: (Pair<Int, CharSequence>?) -> Unit
) {

    //TODO:
    //1. CHECK CREDENTIALS ARE THERE IN DATA STORE
    //2. CHECK IF BIOMETRIC IS ENABLED WHEN USER GETS REGISTERED
    //3. IF BOTH CONDITION MATCHED, LAUNCH THE FLOW

    var isRunAlready by rememberSaveable {
        mutableStateOf(true)
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isSetting by rememberSaveable { mutableStateOf(false) }
    var msg by rememberSaveable { mutableStateOf("") }

//    val biometricCredentials by viewModel.biometricCredentials.collectAsState(initial = null)
    Timber.d("$credentials")

//    val triple by viewModel.biometricCredentials.collectAsState(initial = null)

//    LaunchedEffect(key1 = Unit, block = {
//        Timber.d("inside launchedeffect")
//        viewModel.doData()
//    })

    BiometricErrDialog(showDialog = showDialog, isSetting = isSetting, msg = msg, onDismiss = {
        showDialog = false
        failed(null)
    })

    if (launchFlow && AppConstants.isBiometricAfterLogout.not()) {

//        Timber.d("$credentials $triple")
//        if ((credentials.first.isNotBlank() && credentials.second.isNotEmpty()) &&
//            !(credentials.first == triple?.first && credentials.second == triple?.second)) {
//            msg =
//                stringResource(R.string.er_biometric_saved_credentials)
//            showDialog = true
//            isSetting = false
//            return
//        }


        Timber.d("biometricCredentials == null = ${credentials == null}")
        Timber.d("${credentials?.xtJson()}")
        if (credentials == null && isRunAlready.not()) {
            msg = stringResource(R.string.err_biometric_you_need_to_access)
            showDialog = true
            isSetting = false
            isRunAlready = true
            return
        }

        if (credentials?.isBiometricEnabled?.not() == true) {
            msg = stringResource(
                R.string.err_biometric_not_enabled, credentials?.email ?: ""
            )
            showDialog = true
            isSetting = false
            return
        }

        val context = LocalContext.current
        val promptInfo =
            BiometricPrompt.PromptInfo.Builder().setAllowedAuthenticators(BIOMETRIC_STRONG)
                .setTitle(stringResource(R.string.biometric_authentication))
                .setSubtitle(stringResource(R.string.msg_login_biometric))
                .setNegativeButtonText(stringResource(id = R.string.use_account_password)).build()

        val prompt = BiometricPrompt(
            context as FragmentActivity,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    success(
                        Pair(
                            credentials?.email ?: "",
                            credentials?.password ?: ""
                        )
                    )
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_CANCELED, BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
//                                msg = context.getString(R.string.err_biometric_operation_cancelled)
//                                showDialog = true
//                                isSetting = false
                        }

                        BiometricPrompt.ERROR_LOCKOUT -> {
                            msg = context.getString(R.string.biometric_too_many_attempt)
                            showDialog = true
                            isSetting = false
                        }

                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            msg = context.getString(R.string.msg_biometric_no_enrollment)
                            showDialog = true
                            isSetting = true
                        }

                        else -> {
                            msg = errString.toString()
                            showDialog = true
                            isSetting = false
                        }
                    }
                    failed(null)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    failed(null)
                }
            })

        prompt.authenticate(promptInfo)
    }
}


@Composable
@Preview
fun BiometricErrDialog(
    showDialog: Boolean = false,
    isSetting: Boolean = false,
    msg: String = "",
    onDismiss: () -> Unit = {}
) {
    if (showDialog) {
        val context = LocalContext.current

        DialogApp(onDismiss = onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.sdp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.sdp))
                TitleWithDesc(
                    title = stringResource(id = R.string.biometrics),
                    titleStyle = baseStyleLarge().copy(
                        color = Color.Black, fontSize = 16.ssp
                    ),
                    desc = msg,
                    height = 6
                )
                Spacer(modifier = Modifier.height(20.sdp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.sdp
                        ), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isSetting) AppButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(DIALOG_BUTTON_HEIGHT.sdp),
                        title = stringResource(id = R.string.go_to_settings),
                        style = StyleButton().copy(
                            fontSize = 11.ssp
                        )
                    ) {
                        onDismiss()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            context.startActivity(Intent(Settings.ACTION_BIOMETRIC_ENROLL))
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            context.startActivity(Intent(Settings.ACTION_FINGERPRINT_ENROLL))
                        } else {
                            context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        }
                    }
                    Spacer(modifier = Modifier.width(4.sdp))
                    AppOutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(DIALOG_BUTTON_HEIGHT.sdp)
                            .padding(
                                horizontal = if (isSetting.not()) 42.sdp else 0.sdp
                            ),
                        title = stringResource(id = R.string.cancel),
//                        style = StyleButton().copy(
//                            fontSize = 11.ssp
//                        )
                    ) {
                        onDismiss()
                    }
                }
                Spacer(modifier = Modifier.height(20.sdp))
            }
        }
    }
}


object BiometricHelper {


    @Composable
    fun launchFlow() {

        val context = LocalContext.current

        Log.d(TAG, "launchFlow: ")
        var msg by remember {
            mutableIntStateOf(R.string.err_biometric)
        }

        var showDialog by remember {
            mutableStateOf(false)
        }

//        BiometricErrDialog(showDialog = showDialog,
//            msg = msg, onDismiss = {
//                showDialog = false
//            })

        when (checkCapability(context)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Biometric features are available
//                authenticate(context)
                Log.d(TAG, "launchFlow: BIOMETRIC_SUCCESS")
                showDialog = true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // No biometric features available on this device
                Log.d(TAG, "launchFlow: BIOMETRIC_ERROR_NO_HARDWARE")
                showDialog = true
                msg = R.string.err_biometric
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Biometric features are currently unavailable.
                Log.d(TAG, "launchFlow: BIOMETRIC_ERROR_HW_UNAVAILABLE")
                showDialog = true
                msg = R.string.err_biometric_unavailable
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                // Biometric features available but a security vulnerability has been discovered
                Log.d(TAG, "launchFlow: BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
                showDialog = true
                msg = R.string.err_biometric_security_valunerablity
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                // Biometric features are currently unavailable because the specified options are incompatible with the current Android version..
                Log.d(TAG, "launchFlow: BIOMETRIC_ERROR_UNSUPPORTED")
                showDialog = true
                msg = R.string.err_biometric_incompatiblity
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                // Unable to determine whether the user can authenticate using biometrics
                Log.d(TAG, "launchFlow: BIOMETRIC_STATUS_UNKNOWN")
                showDialog = true
                msg = R.string.err_biometric_auth

            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // The user can't authenticate because no biometric or device credential is enrolled.
                Log.d(TAG, "launchFlow: BIOMETRIC_ERROR_NONE_ENROLLED")
                showDialog = true
                msg = R.string.msg_biometric_no_enrollment
            }

            else -> {
                Log.d(TAG, "launchFlow: ELSE")
            }
        }
    }


//    @Composable
//    fun authenticate(
//        onSuccess: () -> Unit, onFailure: () -> Unit
//    ): BiometricPrompt {
//
//        val context = LocalContext.current
//        var msg by remember {
//            mutableIntStateOf(R.string.err_biometric)
//        }
//
//        var showDialog by remember {
//            mutableStateOf(false)
//        }
//
////        if (showDialog)
////        BiometricErrDialog(showDialog = showDialog,
////            msg = msg, onDismiss = {
////                showDialog = false
////            })
//
//
//        val prompt = BiometricPrompt(context as ComposeMainActivity,
//            object : BiometricPrompt.AuthenticationCallback() {
//
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    super.onAuthenticationSucceeded(result)
//
//                    Log.d(TAG, "onAuthenticationSucceeded: ")
//                    onSuccess()
//                }
//
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    super.onAuthenticationError(errorCode, errString)
//
//
//
//                    Log.d(TAG, "onAuthenticationError: $errorCode $errString")
//
//                    when (errorCode) {
//                        BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_CANCELED, BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
//                            msg = R.string.err_biometric_operation
//                            showDialog = true
//
//                        }
//
//                        else -> {
//                            showDialog = true
//                        }
//                    }
//                    onFailure()
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    onFailure()
//                    Log.d(TAG, "onAuthenticationFailed: ")
//                }
//            })
//
//
//        return prompt
//    }


    fun checkCapability(context: Context): Int {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
    }

}


object BiometricUtils {
    val isBiometricPromptEnabled: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
//    val isSdkVersionSupported: Boolean
//        /*
//                      * Condition I: Check if the android version in device is greater than
//                      * Marshmallow, since fingerprint authentication is only supported
//                      * from Android 6.0.
//                      * Note: If your project's minSdkversion is 23 or higher,
//                      * then you won't need to perform this check.
//                      *
//                      * */
//        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /*
       * Condition II: Check if the device has fingerprint sensors.
       * Note: If you marked android.hardware.fingerprint as something that
       * your app requires (android:required="true"), then you don't need
       * to perform this check.
       *
       * */
    fun isHardwareSupported(context: Context): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.isHardwareDetected
    }


    /*
     * Condition III: Fingerprint authentication can be matched with a
     * registered fingerprint of the user. So we need to perform this check
     * in order to enable fingerprint authentication
     *
     * */
    fun isFingerprintAvailable(context: Context?): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context!!)
        return fingerprintManager.hasEnrolledFingerprints()
    }

    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    fun isPermissionGranted(context: Context?): Boolean {
        return ActivityCompat.checkSelfPermission(
            context!!, Manifest.permission.USE_FINGERPRINT
        ) == PackageManager.PERMISSION_GRANTED
    }
}