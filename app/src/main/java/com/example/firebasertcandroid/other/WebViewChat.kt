package com.example.firebasertcandroid.other

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.webkit.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import kotlinx.coroutines.launch


class WebViewChat:WebView
{

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {
                init()
            }
    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs) {
        init()
            }
    constructor(context: Context)
            : super(context) {
        init()
            }

    private val _onFinishLoadPage=MutableLiveData<Boolean>()
    internal val onFinishLoadPage:MutableLiveData<Boolean> =_onFinishLoadPage

    private val _onRequestPermission=MutableLiveData<PermissionRequest>()
    internal val onPermissionRequest:MutableLiveData<PermissionRequest> =_onRequestPermission

    private val _jsLog=MutableLiveData<String>()
    internal val jsLog:MutableLiveData<String> = _jsLog

    @SuppressLint("SetJavaScriptEnabled")
    private fun init()
    {

        webChromeClient = object : WebChromeClient()
        {

            override fun onPermissionRequest(request: PermissionRequest)
            {

                onPermissionRequest.value=request

            }

        }
        webViewClient = object :WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                _onFinishLoadPage.value=false
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                _onFinishLoadPage.value=true
            }
        }
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.builtInZoomControls = false
        settings.setSupportZoom(false)
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false

        addJavascriptInterface(JSInterface(_jsLog,this), "JSInterface")
    }

    override fun onDetachedFromWindow()
    {
        super.onDetachedFromWindow()

        evaluateJavascript("iAmLeaving()") {}

    }

}

open class JSInterface(private val _jsLog: MutableLiveData<String>, private val webViewChat: WebViewChat)
{
    @JavascriptInterface
    fun receiveLog(message: String)
    {
        webViewChat.findViewTreeLifecycleOwner()?.let {

            it.lifecycle.coroutineScope.launch {

                _jsLog.value=message

            }

        }
    }
    @JavascriptInterface
    open fun copyToClipboard(text: String?)
    {
        val clipboard: ClipboardManager? =
            webViewChat.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("room", text)
        clipboard?.setPrimaryClip(clip)
    }
}