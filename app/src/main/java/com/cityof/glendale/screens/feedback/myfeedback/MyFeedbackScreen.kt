package com.cityof.glendale.screens.feedback.myfeedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppDropDown2
import com.cityof.glendale.composables.components.DIALOG_BUTTON_HEIGHT
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.DropDownText
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TextFieldColor
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.ratingbar.RatingBar
import com.cityof.glendale.composables.components.ratingbar.model.GestureStrategy
import com.cityof.glendale.composables.components.ratingbar.model.RatingInterval
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.feedback.Survey
import com.cityof.glendale.screens.feedback.SurveyOption
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.InputValidatorImpl
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
fun FeedBackFormPreview() {
    MyFeedbackScreen(
        viewModel = MyFeedbackViewModel(
            AppRepository(MockApiService()), InputValidatorImpl()
        )
    )
}


@Composable
fun MyFeedbackScreen(
    navHostController: NavHostController? = null,
    viewModel: MyFeedbackViewModel = hiltViewModel()
) {


    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUi(AppConstants.myFeedbackIn)
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            MyFeedbackContract.NavActions.NavFeedbackList -> navHostController?.popBackStack()
            MyFeedbackContract.NavActions.NavDashboard -> {
                navHostController?.navigate(Routes.Dashboard.name) {
                    popUpTo(Routes.Dashboard.name) {
                        inclusive = false
                    }
                }
            }
            null -> {}
        }
    })


    ToastApp(state.toastMsg)
    ProgressDialogApp(state.isLoading)
    SuccessAlert(state.isSuccess, onDismiss = {
        viewModel.dispatch(MyFeedbackContract.Intent.ShowSuccess(false))
    }, onOkay = {
        viewModel.dispatch(MyFeedbackContract.Intent.ShowSuccess(false))
        viewModel.dispatch(MyFeedbackContract.Intent.NavFeedbackList)
    })
    DoUnauthorization(
        state.isAuthErr, navHostController
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        AppBarWithBack(
            title = stringResource(R.string.my_feedback)
        ) {
            navHostController?.popBackStack()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SingleChoiceAnswerWithSpinner(state.safetyQuestion,
                state.safety,
                state.isReasonForSafety,
                state.reasonSelected,
                state.reasonIndex,
                onClick = {
                    viewModel.dispatch(MyFeedbackContract.Intent.SafetyChanged(it))
                },
                onItemSelected = { index, surveyOption ->
                    viewModel.dispatch(
                        MyFeedbackContract.Intent.ReasonChanged(
//                            surveyOption.option
                                    surveyOption.value
                        )
                    )
                })
            SingleChoiceAnswer(state.servicePerformanceQuestion, state.servicePerformance) {
                viewModel.dispatch(MyFeedbackContract.Intent.ServicePerformanceChanged(it))
            }
            SingleChoiceAnswer(state.driverConductQuestion, state.driverBehaviour) {
                viewModel.dispatch(MyFeedbackContract.Intent.DriverBehaviourChanged(it))
            }
//            SingleChoiceAnswer(state.vehicleMaintQuestion, state.vehicleMaintenance) {
//                viewModel.dispatch(MyFeedbackContract.Intent.VehicleMaintenanceChanged(it))
//            }
            SingleChoiceAnswerWithSpinner(survey = state.vehicleMaintQuestion,
                selectedAnswer = state.vehicleMaintenance,
                isReason = state.isReasonForDirty,
                selectedReason = state.dirtyReason,
                reasonIndex = state.dirtyReasonIndex,
                onClick = {
                    viewModel.dispatch(MyFeedbackContract.Intent.VehicleMaintenanceChanged(it))
                },
                onItemSelected = { index, sureyOption ->
                    viewModel.dispatch(
                        MyFeedbackContract.Intent.DirtyReasonChanged(
//                            sureyOption.option
                                    sureyOption.value
                        )
                    )
                })
            RatingQuestion(state.ratingQuestion, state.rating) {
                viewModel.dispatch(MyFeedbackContract.Intent.RatingChanged(it))
            }
            TextualAnswer(state.comment) {
                viewModel.dispatch(MyFeedbackContract.Intent.CommentChanged(it))
            }
            Box(
                modifier = Modifier
                    .background(FFF9F9F9)
                    .padding(
                        start = 22.sdp, end = 22.sdp, bottom = 22.sdp
                    )
            ) {
                AppButton(title = stringResource(R.string.submit_feedback)) {
                    viewModel.dispatch(MyFeedbackContract.Intent.CreateFeedback)
                }
            }

            Spacer(modifier = Modifier.height(32.sdp))
        }

    }
}

@Composable
fun TextualAnswer(comment: String, onValueChange: (String) -> Unit) {

    SurveyLayoutComposable(
        isDivider = false
    ) {
        Spacer(modifier = Modifier.height(12.sdp))
        TextField(
            value = comment,
            onValueChange = onValueChange,
            Modifier
                .fillMaxWidth()
                .height(140.sdp)
                .padding(bottom = 16.sdp)
                .background(
                    color = Color(0xFFF3F3F3), shape = RoundedCornerShape(size = 10.dp)
                ),
            placeholder = {
                Text(
                    text = stringResource(R.string.write_your_feedback), style = baseStyle2().copy(
                        color = FF777C80
                    )
                )
            },
            textStyle = baseStyle2(),
            colors = TextFieldColor()
        )
    }

}

@Composable
fun RatingQuestion(survey: Survey, rating: Float, onRatingChange: (Float) -> Unit) {
    SurveyLayoutComposable(survey = survey) {
        RatingBar(
            rating = rating,
            imageVectorEmpty = ImageVector.vectorResource(R.drawable.ic_star_outlined),
            imageVectorFilled = ImageVector.vectorResource(R.drawable.ic_start_filled),
            space = 4.sdp,
            gestureStrategy = GestureStrategy.DragAndPress,
            itemSize = 32.dp,
            modifier = Modifier.padding(top = 8.sdp),
            onRatingChange = onRatingChange,
            ratingInterval = RatingInterval.Half
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceAnswerWithSpinner(
    survey: Survey,
    selectedAnswer: String,
    isReason: Boolean = false,
    selectedReason: String,
    reasonIndex: Int = 0,
    onClick: (String) -> Unit = {},
    onItemSelected: (Int, SurveyOption) -> Unit
) {
    SurveyLayoutComposable(survey = survey) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(), maxItemsInEachRow = 2
        ) {
            survey.answers.forEachIndexed { index, answer ->
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                vertical = 4.sdp
                            )
                    ) {
                        RadioButton(selected = (selectedAnswer == answer.value), onClick = {
                            onClick(answer.value)
                        })
                        Spacer(modifier = Modifier.width(4.sdp))
                        Text(text = answer.option.toStr(),
                            style = baseStyle2(),
                            modifier = Modifier.noRippleClickable {
                                onClick(answer.value)
                            })
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(8.sdp))
        if (isReason) AppDropDown2(
            list = survey.moreOption,
            selectedValue = selectedReason,
            selectedIndex = reasonIndex,
            onItemSelected = onItemSelected
        ) {
            DropDownText(text = it.option.toStr())
        }
    }
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SingleChoiceAnswer(
    survey: Survey = Survey(), selectedAnswer: String = "", onClick: (String) -> Unit = {}
) {
    SurveyLayoutComposable(survey = survey) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(), maxItemsInEachRow = 2
        ) {
            survey.answers.forEachIndexed { index, answer ->
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                vertical = 4.sdp
                            )
                    ) {
                        RadioButton(selected = (selectedAnswer == answer.value), onClick = {
                            onClick(answer.value)
                        })
                        Spacer(modifier = Modifier.width(4.sdp))
                        Text(text = answer.option.toStr(), style = baseStyle2())
                    }
                }
            }
        }
    }
}


@Composable
fun SurveyLayoutComposable(
    survey: Survey? = null, isDivider: Boolean = true, content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 22.sdp, end = 22.sdp
            )
    ) {
        survey?.let {
            Text(
                text = it.question.toStr(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.sdp, bottom = 6.sdp),
                style = baseStyle().copy(
                    fontSize = 15.ssp
                )
            )
        }
        content()
        Spacer(modifier = Modifier.height(16.sdp))
        if (isDivider) HorizontalDivider()
    }
}


@Composable
@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
fun SuccessAlert(
    value: Boolean = true, onDismiss: () -> Unit = {}, onOkay: () -> Unit = {}
) {

    if (value) {
        DialogApp(onDismiss = onDismiss) {


            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(
                    horizontal = 14.sdp
                )
            ) {

                Spacer(modifier = Modifier.height(8.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = null,
                        modifier = Modifier.noRippleClickable(
                            onDismiss
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.sdp))
                Text(
                    text = stringResource(R.string.feedback_submitted), style = baseStyle().copy(
                        fontSize = 16.ssp
                    )
                )
                Spacer(modifier = Modifier.height(8.sdp))
                Text(
                    text = stringResource(R.string.thanks_for_your_feedback),
                    style = baseStyle2().copy(

                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.sdp))
                AppButton(
                    modifier = Modifier
                        .width(100.sdp)
                        .height(DIALOG_BUTTON_HEIGHT.sdp)
                        .padding(
                            horizontal = 2.sdp
                        ), title = stringResource(id = R.string.ok), onClick = onOkay
                )
                Spacer(modifier = Modifier.height(26.sdp))
            }
        }
    }

}