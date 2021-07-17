package edu.bupt.jetdeepl.model

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.bupt.jetdeepl.data.allLangs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

sealed class SelectMode {
    object SOURCE: SelectMode()
    object TARGET: SelectMode()
}
@HiltViewModel
class MainViewModel @Inject constructor(var deeplRepo: DeeplRepo): ViewModel() {
    var displayOutput by mutableStateOf("")
    var displayInput by mutableStateOf("")
    var isTranslatSuccess by mutableStateOf(false)
    var displaySourceLanguage by mutableStateOf("英语")
    var displayTargetLanguage by mutableStateOf("中文")
    var flipToggle by mutableStateOf(false)
    var currentSelectMode:SelectMode by mutableStateOf(SelectMode.SOURCE)
        private set
    var focusOnSearch by mutableStateOf(false)

    var displayLanguageList by mutableStateOf<List<String>>(
        allLangs.keys.toList()
    )

    var requestCopyToClipboardData = MutableLiveData<String>()
        private set
    private val sourceLanguageCode
        get() = allLangs[displaySourceLanguage]
    private val targetLanguageCode
        get() = allLangs[displayTargetLanguage]
    private val client = OkHttpClient()

    // Crawler 方式
    private fun translateByCrawler(originWord: String, translateFlow: MutableSharedFlow<String>){
        viewModelScope.launch(Dispatchers.IO){
            try{
                deeplRepo.translateByCrawler(originWord, sourceLanguageCode, targetLanguageCode) { response ->
                        if (response.code == 200) {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val jsonObject =
                                response.body?.let { Json.parseToJsonElement(it.string()) }
                            val results = jsonObject?.jsonObject?.get("result")
                            val translations = results?.jsonObject?.get("translations")

                            val newObject = translations?.jsonArray?.get(0)
                            val beams = newObject?.jsonObject?.get("beams")

                            val resultArray = beams?.jsonArray
                            isTranslatSuccess = true
                            translateFlow.emit(
                                resultArray!!.get(0).jsonObject["postprocessed_sentence"].toString()
                                    .replace("\"", "")
                            )
                        } else translateFlow.emit("似乎网络出现了点问题～")
                }
            }catch(e: IOException){

            }
        }
    }

    // API 方式
    private fun translateByAPI(originWord: String, translateFlow: MutableSharedFlow<String>){
        viewModelScope.launch(Dispatchers.IO){
            try{
                deeplRepo.translateByAPI(originWord, sourceLanguageCode, targetLanguageCode) { response ->
                        if(response.code == 200){
                            val jsonObject = response.body?.let { Json.parseToJsonElement(it.string()) }
                            val text = jsonObject?.jsonObject?.get("translations")?.jsonArray?.get(0)?.jsonObject?.get("text")

                            val source_lang = jsonObject?.jsonObject?.get("translations")?.jsonArray?.get(0)?.jsonObject?.get("detected_source_language").toString().replace("\"", "")

//                            if(sourceLanguageCode == ""){
//                                sourceLanguageCode = allLangs.values.first {
//                                    it == source_lang
//                                }
//                            }
                            translateFlow.emit(text.toString().replace("\"", ""))
                            isTranslatSuccess = true
                        } else{
                            translateByCrawler(originWord, translateFlow)
                        }
                }
            }catch(e: IOException){

            }
        }
    }

    fun translate() {
        isTranslatSuccess = false
        var translateFlow = MutableSharedFlow<String>()
        translateByAPI(displayInput, translateFlow)
        viewModelScope.launch {
            var waitingJob = launch {
                var count = 0
                while (true) {
                    if (displayOutput == ".....") {
                        displayOutput = "."
                    } else {
                        displayOutput += "."
                    }
                    delay(500)
                    count++
                    if (count == 60) {
                        translateFlow.emit("似乎网络出现了点问题～")
                    }
                }
            }
            launch {
                translateFlow.collect {
                    waitingJob.cancel()
                    displayOutput = it
                }
            }
        }
    }

    fun clearOutputDisplay() {
        displayOutput = ""
    }

    fun clearInputDisplay() {
        displayInput = ""
    }

    fun flipLanguage() {
        flipToggle = !flipToggle
    }

    fun requestCopyToClipboard() {
        requestCopyToClipboardData.value = displayOutput
    }

    fun changeSelectMode(selectMode: SelectMode) {
        currentSelectMode = selectMode
    }

    fun isSelectedLanguage(languageName: String): Boolean {
        return when (currentSelectMode) {
            SelectMode.SOURCE -> {
                displaySourceLanguage == languageName
            }
            SelectMode.TARGET -> {
                displayTargetLanguage == languageName
            }
        }
    }
    fun selectLanguage(languageName: String) {
        when (currentSelectMode) {
            SelectMode.SOURCE -> {
                displaySourceLanguage = languageName
            }
            SelectMode.TARGET -> {
                displayTargetLanguage = languageName
            }
        }
    }
}

