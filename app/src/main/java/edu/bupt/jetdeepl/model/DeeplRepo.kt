package edu.bupt.jetdeepl.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class DeeplRepo(private var okHttpClient: OkHttpClient) {
    // Crawler 方式
    suspend fun translateByCrawler(originWord: String, sourceLanguageCode: String?, targetLanguageCode: String?, block: suspend (response: Response)->Unit){
        val body = "{\"jsonrpc\":\"2.0\",\"method\": \"LMT_handle_jobs\",\"params\":{\"jobs\":[{\"kind\":\"default\",\"raw_en_sentence\":\"$originWord\",\"raw_en_context_before\":[],\"raw_en_context_after\":[],\"preferred_num_beams\":4,\"quality\":\"fast\"}],\"lang\":{\"user_preferred_langs\":[\"PL\",\"RU\",\"FR\",\"SL\",\"DE\",\"JA\",\"HU\",\"IT\",\"EN\",\"ZH\",\"ES\"],\"source_lang_user_selected\":\"${sourceLanguageCode}\",\"target_lang\":\"${targetLanguageCode}\"},\"priority\":-1,\"commonJobParams\":{\"formality\":null},\"timestamp\":1621181157844},\"id\":54450008}"
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val request = Request.Builder()
            .url("https://www2.deepl.com/jsonrpc")
            .post(body.toRequestBody(mediaType))
            .build()
        okHttpClient.newCall(request).execute().use {
            block(it)
        }
    }

    // API 方式
    suspend fun translateByAPI(originWord: String, sourceLanguageCode: String?, targetLanguageCode: String?, block: suspend (response: Response) -> Unit){
        val url: String = if(sourceLanguageCode == "") "https://api-free.deepl.com/v2/translate?auth_key=&text=$originWord&detected_source_language=auto&target_lang=${targetLanguageCode}"
        else "https://api-free.deepl.com/v2/translate?auth_key=&text=$originWord&source_lang=${sourceLanguageCode}&target_lang=${targetLanguageCode}"
        val request = Request.Builder()
            .url(url)
            .build()
        okHttpClient.newCall(request).execute().use {
            block(it)
        }
    }
}