package edu.bupt.jetdeepl.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import edu.bupt.jetdeepl.model.MainViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewModelScope
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import edu.bupt.jetdeepl.R
import edu.bupt.jetdeepl.data.allLangs
import edu.bupt.jetdeepl.model.SelectMode
import edu.bupt.jetdeepl.ui.theme.inputHint
import edu.bupt.jetdeepl.ui.theme.searchLanguage
import edu.bupt.jetdeepl.ui.theme.toggleLangBackground
import edu.bupt.jetdeepl.ui.theme.translateColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@ExperimentalMaterialApi
@Composable
fun ChangeStatusBarColor(sheetState: ModalBottomSheetState) {
    var systemUiController = rememberSystemUiController()
    if (sheetState.isVisible) {
        systemUiController.setStatusBarColor(Color(0xffa8a8a8))
    } else {
        systemUiController.setStatusBarColor(Color(0xfff8f8f8))
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TranslateLayout(viewModel: MainViewModel, scaffoldState: ScaffoldState) {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    ChangeStatusBarColor(sheetState)
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            SelectLanguageSheet(sheetState, viewModel)
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SelectLanguageBar(sheetState, viewModel)
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    InputBlock(viewModel, scaffoldState)
                    Divider(color = translateColor, thickness = 2.dp)
                    OutputBlock(viewModel, scaffoldState)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ColumnScope.OutputBlock(viewModel: MainViewModel, scaffoldState: ScaffoldState) {
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
            if(viewModel.displayOutput.isNotEmpty() && viewModel.isTranslatSuccess) {
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
                                    viewModel.viewModelScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("待复制内容为空")
                                    }
                                } else {
                                    viewModel.viewModelScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("已复制到剪贴板")
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
fun ColumnScope.InputBlock(viewModel: MainViewModel, scaffoldState: ScaffoldState) {
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
                                viewModel.viewModelScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("输入内容不能为空哦～")
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
@ExperimentalMaterialApi
@Composable
fun SelectLanguageBar(sheetState: ModalBottomSheetState, viewModel: MainViewModel) {
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
    val scope = rememberCoroutineScope()
    var systemUiController = rememberSystemUiController()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp)
    ) {
        Button(
            onClick = {
                viewModel.changeSelectMode(SelectMode.SOURCE)
                scope.launch {
                    launch {
                        systemUiController.setStatusBarColor(Color(0xffa8a8a8))
                    }
                    sheetState.show()
                }
            },
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
            onClick = {
                viewModel.changeSelectMode(SelectMode.TARGET)
                scope.launch {
                    launch {
                        systemUiController.setStatusBarColor(Color(0xffa8a8a8))
                    }
                    sheetState.show()
                }
            },
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

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SelectLanguageSheet(sheetState: ModalBottomSheetState, viewModel: MainViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(top = 25.dp, start = 15.dp, end = 15.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            SearchLanguageField(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                Modifier.fillMaxWidth(),
            ) {
                stickyHeader { 
                    Text(text = "全部${viewModel.displayLanguageList.size}种语言", modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 10.dp))
                }
                for(language in viewModel.displayLanguageList) {
                    if (viewModel.currentSelectMode == SelectMode.TARGET && language == "自动检测") {
                        continue
                    }
                    item { 
                        SelectLanguageItem(sheetState, viewModel, language)
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SelectLanguageItem(sheetState: ModalBottomSheetState, viewModel: MainViewModel, language: String) {
    var scope = rememberCoroutineScope()
    var systemUiController = rememberSystemUiController()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                viewModel.selectLanguage(language)
                scope.launch {
                    launch {
                        systemUiController.setStatusBarColor(Color(0xfff8f8f8))
                    }
                    sheetState.hide()
                }
            }
    ) {
        Text(
            text = language,
            color = if (viewModel.isSelectedLanguage(language)) translateColor else Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
        )
        if (viewModel.isSelectedLanguage(language)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_selected), contentDescription = "selected",
                tint = translateColor,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            )
        }
    }
}

@Composable
fun SearchLanguageField(viewModel: MainViewModel) {
    var displayLanguageInput by remember {mutableStateOf("")}
    TextField(
        value = displayLanguageInput,
        onValueChange = { value ->
            displayLanguageInput = value
            viewModel.displayLanguageList = if (value.isEmpty()) {
                allLangs.keys.toList()
            } else{
                allLangs.keys.filter {
                    it.contains(value)
                }
            }
        },
        leadingIcon = {
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "search")
        },
        trailingIcon = {
            if (viewModel.focusOnSearch) {
                IconButton(
                    onClick = {
                        displayLanguageInput = ""
                        viewModel.displayLanguageList = allLangs.keys.toList()
                    }
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = "cancel",
                    )
                }
            }
        },
        placeholder = {
            Text(text = "搜索")
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = searchLanguage,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .onFocusChanged {
                viewModel.focusOnSearch = it.isFocused
            }
    )
}

