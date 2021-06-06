package edu.bupt.jetdeepl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import edu.bupt.jetdeepl.model.MainViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import edu.bupt.jetdeepl.R
import edu.bupt.jetdeepl.ui.theme.inputHint
import edu.bupt.jetdeepl.ui.theme.toggleLangBackground
import edu.bupt.jetdeepl.ui.theme.translateColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TranslateLayout(viewModel: MainViewModel, scaffoldState: ScaffoldState) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = state,
        sheetElevation = 0.dp,
        sheetBackgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.32f),
        sheetShape = RoundedCornerShape(20.dp),
        sheetContent = {
            Text("Hello World")
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SelectLanguageBar(viewModel)
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    InputBlock(viewModel, scaffoldState, scope)
                    Divider(color = translateColor, thickness = 2.dp)
                    OutputBlock(viewModel, scaffoldState, scope)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ColumnScope.OutputBlock(viewModel: MainViewModel, scaffoldState: ScaffoldState, scope: CoroutineScope) {
    Box(
        modifier = Modifier
            .padding(top = 30.dp)
            .weight(0.5f)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = viewModel.displayOutput,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .fillMaxHeight()
                    .padding(start = 10.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.W500
            )
            if(viewModel.displayOutput.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_volumn),
                                contentDescription = "volumn",
                                tint = translateColor)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.clearOutputDisplay()
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_undo),
                                contentDescription = "undo",
                                tint = translateColor
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        IconButton(
                            onClick = {
                                if (viewModel.displayOutput.isEmpty()) {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("待复制内容为空")
                                    }
                                } else {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("内部已复制到剪贴板")
                                    }
                                    viewModel.requestCopyToClipboard()
                                }
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clipboard),
                                contentDescription = "clipboard",
                                tint = translateColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.InputBlock(viewModel: MainViewModel, scaffoldState: ScaffoldState, scope: CoroutineScope) {
    Box(
        modifier = Modifier
            .weight(0.5f)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = viewModel.displayInput,
                onValueChange = {
                    viewModel.displayInput = it
                },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                ),
                placeholder = {
                    Text (
                        text = "随便输入点什么吧~",
                        color = inputHint,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W900
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .fillMaxHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_volumn),
                            contentDescription = "volumn",
                            tint = translateColor)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.clearInputDisplay()
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.3f)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo),
                            contentDescription = "undo",
                            tint = translateColor
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = {
                            if (viewModel.displayInput.isEmpty()) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("输入内容为空")
                                }
                            } else {
                                viewModel.clearOutputDisplay()
                                viewModel.translate()
                            }
                        },
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxHeight()
                            .weight(0.7f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = translateColor, contentColor = Color.White)
                    ) {
                        Text(
                            text = "翻译",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W900
                        )
                    }
                }
            }
        }
    }
}

var isFirst = true
@Composable
fun SelectLanguageBar(viewModel: MainViewModel) {
    var rotateAngle = remember { Animatable(0f) }
    var textAlpha = remember { Animatable(1f) }
    LaunchedEffect(viewModel.flipToggle) {
        if (isFirst){
            isFirst = false
            return@LaunchedEffect
        }
        launch {
            rotateAngle.animateTo(if (viewModel.flipToggle) 180f else 0f, tween(1000))
        }
        launch {
            textAlpha.animateTo(0f, tween(500))
            var tempLanguage = viewModel.displaySourceLanguage
            viewModel.displaySourceLanguage = viewModel.displayTargetLanguage
            viewModel.displayTargetLanguage = tempLanguage
            textAlpha.animateTo(1f, tween(500))
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp)
    ) {
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onBackground),
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))

        ) {
            Text(
                text = viewModel.displaySourceLanguage,
                fontSize = 18.sp,
                fontWeight = FontWeight.W900,
                modifier = Modifier.alpha(textAlpha.value)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(painter = painterResource(id = R.drawable.ic_down_arrow), contentDescription = "down_arrow", Modifier.size(15.dp))
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .width(40.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(50))
                .background(toggleLangBackground)
                .clickable {
                    viewModel.flipLanguage()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_swap),
                contentDescription = "swap",
                tint = Color.White,
                modifier = Modifier
                    .padding(5.dp)
                    .rotate(rotateAngle.value)
            )
        }
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onBackground),
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Text(
                text = viewModel.displayTargetLanguage,
                fontSize = 18.sp,
                fontWeight = FontWeight.W900,
                modifier = Modifier.alpha(textAlpha.value)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(painter = painterResource(id = R.drawable.ic_down_arrow), contentDescription = "down_arrow", Modifier.size(15.dp))
        }
    }
}
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Composable
fun TranslateLayoutPreview() {
    TranslateLayout(MainViewModel(), rememberScaffoldState())
}

