package com.cityof.glendale.screens.video

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.utils.AppConstants

private const val TAG = "VideoScreen"

class FullScreenPlayer(
    context: Context, val assetFile: AssetFileDescriptor, val listener: OnCompletionListener
) : TextureView(context), SurfaceTextureListener {

    private var mSurface: Surface? = null
    private var mPlayer: MediaPlayer? = null

    init {
        surfaceTextureListener = this
        isOpaque = false
        Log.d(TAG, ": init")
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, p1: Int, p2: Int) {

        Log.d(TAG, "onSurfaceTextureAvailable: ")

        if (mPlayer == null) {
            try {
                mSurface = Surface(surface)
                mPlayer = MediaPlayer()
                mPlayer?.setSurface(mSurface)
                playVideo(assetFile, listener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {}

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
        releasePlayer() // Release the MediaPlayer
        mSurface?.release() // Release the Surface
        mSurface = null
        return true
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {}

    fun playVideo(
        assetFile: AssetFileDescriptor, listener: OnCompletionListener
    ) {
        Log.d(TAG, "playVideo: $mPlayer")
        mPlayer?.setDataSource(
            assetFile.fileDescriptor, assetFile.startOffset, assetFile.length
        )
        mPlayer?.setSurface(mSurface)
        mPlayer?.prepareAsync()
        mPlayer?.setOnCompletionListener(listener)
        mPlayer?.setOnPreparedListener {
            it.start()
        }
    }

    fun releasePlayer() {
        when {
            mPlayer != null -> {
                mPlayer?.release()
                mPlayer = null
            }
        }
    }

    /**
     * helper method to stop media mPlayer
     */
    fun togglePlayer() {
        when {
            mPlayer != null && (mPlayer?.isPlaying == true) -> mPlayer?.pause()
            else -> {
                mPlayer?.start()
            }
        }
    }
}


@Composable
@Preview
fun VideoScreen(
    navHostController: NavHostController? = null,
    viewModel: VideoViewModel = hiltViewModel()
) {
//    LockOrientation()
    val context = LocalContext.current
    val assetFile = context.assets.openFd(AppConstants.ANIMATION_FILE)

    AndroidView(factory = {
        FullScreenPlayer(context, assetFile) {
            try {
                if (viewModel.isLanguageShown.not()) {
                    navHostController?.navigate(Routes.Languages.name)
                } else {
                    if (viewModel.isLoggedIn) {
                        navHostController?.navigate(Routes.Dashboard.name) {
                            popUpTo(Routes.Video.name) {
                                inclusive = true
                            }
                        }
                    } else navHostController?.navigate(Routes.Landing.name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    })
}