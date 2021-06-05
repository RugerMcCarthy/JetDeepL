package edu.bupt.jetdeepl.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.bupt.jetdeepl.data.allLangs
import kotlinx.coroutines.Dispatchers
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
import java.util.concurrent.Flow

class MainViewModel: ViewModel() {
    var displayOutput by mutableStateOf("")
    var displayInput by mutableStateOf("")
    var sourceLanguage by mutableStateOf(allLangs["英语"])
    var targetLanguage by mutableStateOf(allLangs["中文"])
    private val client = OkHttpClient()

    private fun translateByCrawler(originWord: String, translateFlow: MutableSharedFlow<String>){
        viewModelScope.launch(Dispatchers.IO){
            val body = "{\"jsonrpc\":\"2.0\",\"method\": \"LMT_handle_jobs\",\"params\":{\"jobs\":[{\"kind\":\"default\",\"raw_en_sentence\":\"$originWord\",\"raw_en_context_before\":[],\"raw_en_context_after\":[],\"preferred_num_beams\":4,\"quality\":\"fast\"}],\"lang\":{\"user_preferred_langs\":[\"PL\",\"RU\",\"FR\",\"SL\",\"DE\",\"JA\",\"HU\",\"IT\",\"EN\",\"ZH\",\"ES\"],\"source_lang_user_selected\":\"${sourceLanguage}\",\"target_lang\":\"${targetLanguage}\"},\"priority\":-1,\"commonJobParams\":{\"formality\":null},\"timestamp\":1621181157844},\"id\":54450008}"
            val mediaType = "application/json; charset=utf-8".toMediaType()

            val request = Request.Builder()
                .url("https://www2.deepl.com/jsonrpc")
                .post(body.toRequestBody(mediaType))
                .build()

            try{
                client.newCall(request).execute().use { response ->
                    if(response.code == 200){
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val jsonObject = response.body?.let { Json.parseToJsonElement(it.string()) }
                        val results = jsonObject?.jsonObject?.get("result")
                        val translations = results?.jsonObject?.get("translations")

                        val newObject = translations?.jsonArray?.get(0)
                        val beams = newObject?.jsonObject?.get("beams")

                        val resultArray = beams?.jsonArray

                        translateFlow.emit(resultArray!!.get(0).jsonObject["postprocessed_sentence"].toString().replace("\"", ""))
                    } else translateFlow.emit("出现了错误")
                }
            }catch(e: IOException){

            }
        }
    }

    // API 方式
    private fun translateByAPI(originWord: String, translateFlow: MutableSharedFlow<String>){
        viewModelScope.launch(Dispatchers.IO){

            val url: String = if(sourceLanguage == "") "https://api-free.deepl.com/v2/translate?auth_key=&text=$originWord&detected_source_language=auto&target_lang=${targetLanguage}"
            else "https://api-free.deepl.com/v2/translate?auth_key=&text=$originWord&source_lang=${sourceLanguage}&target_lang=${targetLanguage}"

            val request = Request.Builder()
                .url(url)
                .build()

            try{
                client.newCall(request).execute().use { response ->
                    if(response.code == 200){
                        val jsonObject = response.body?.let { Json.parseToJsonElement(it.string()) }
                        val text = jsonObject?.jsonObject?.get("translations")?.jsonArray?.get(0)?.jsonObject?.get("text")

                        val source_lang = jsonObject?.jsonObject?.get("translations")?.jsonArray?.get(0)?.jsonObject?.get("detected_source_language").toString().replace("\"", "")

                        if(sourceLanguage == ""){
                            sourceLanguage = allLangs.values.first {
                                it == source_lang
                            }
                        }
                        translateFlow.emit(text.toString().replace("\"", ""))
                    } else{
                        translateByCrawler(originWord, translateFlow)
                    }
                }
            }catch(e: IOException){

            }
        }
    }

    fun translate() {
        var translateFlow = MutableSharedFlow<String>()
        translateByAPI(displayInput, translateFlow)

        viewModelScope.launch {
            var waitingJob = launch {
                while (true) {
                    if (displayOutput == ".....") {
                        displayOutput = "."
                    } else {
                        displayOutput += "."
                    }
                    delay(300)
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
}

