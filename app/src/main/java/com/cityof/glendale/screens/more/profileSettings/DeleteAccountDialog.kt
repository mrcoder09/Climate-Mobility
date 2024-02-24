package com.cityof.glendale.screens.more.profileSettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.ButtonWithLeadingIcon
import com.cityof.glendale.composables.components.PADDING_AROUND
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.theme.PROFILE_RED_BUTTON
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

const val HORIZONTAL_PADDING = 14

@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun DeleteAccountDialog(
    show: Boolean = true, onDelete: () -> Unit = {}, onDismiss: () -> Unit = {}
) {

    if (show) Dialog(properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PADDING_AROUND.sdp
                ), shape = RoundedCornerShape(10.sdp), color = Color.White
        ) {
            Spacer(modifier = Modifier.height(10.sdp))
            Row(
                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x), contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.sdp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.sdp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.sdp))
                Image(
                    painter = painterResource(id = R.drawable.ic_circle_delete),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(20.sdp))
                TitleWithDesc(
                    title = R.string.title_are_you_sure,
                    desc = R.string.msg_are_you_sure,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    titleStyle = baseStyleLarge().copy(
                        color = Color.Black
                    ), descStyle = baseStyle2().copy(
                        fontSize = 14.ssp, lineHeight = 20.ssp
                    )
                )
                Spacer(modifier = Modifier.height(32.sdp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 5.sdp
                        ), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ButtonWithLeadingIcon(
                        modifier = Modifier
                            .height(36.sdp)
                            .weight(1f),
//                        style = StyleButton().copy(fontSize = 11.ssp),
                        title = stringResource(R.string.delete),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White, containerColor = PROFILE_RED_BUTTON
                        ),
                        onClick = onDelete
                    )
                    Spacer(modifier = Modifier.width(12.sdp))
                    AppButton(
                        modifier = Modifier
                            .height(36.sdp)
                            .weight(1f), title = stringResource(id = R.string.cancel), onClick = onDismiss
                    )
                }
                Spacer(modifier = Modifier.height(24.sdp))
            }
        }
    }
}