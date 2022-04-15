package com.example.firebasertcandroid.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firebasertcandroid.R
import com.example.firebasertcandroid.other.WebViewChat
import com.example.firebasertcandroid.components.SharedPref
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.collections.ArrayList

const val CHAT_URI = "com.example.firebasertcandroid.ui.CHAT_URI"
const val CHAT_URI_ERROR = "com.example.firebasertcandroid.ui.CHAT_URI_ERROR"
const val REQUEST_CODE_PERMISSION = 6042022

@AndroidEntryPoint
class ChatActivity : AppCompatActivity()
{


    lateinit var webViewChat:WebViewChat

    private val observerPermission=androidx.lifecycle.Observer<PermissionRequest>
    { permissionsRequest ->

        handlePermissionsRequest(permissionsRequest)

    }

    private val observerOnFinishLoadPage=androidx.lifecycle.Observer<Boolean>
    { isFinishing ->

        when (isFinishing)
        {
            null -> {}
            true -> {

                webViewChat.evaluateJavascript(
                    presenter.getJavaScriptIsValidWebChat()
                ) { resultJavaScript ->

                    handleResultJavaScriptIsValidWebChat(
                        resultJavaScript
                    )

                }

            }
            false -> {}
        }

    }

    private val observerJsLog=androidx.lifecycle.Observer<String>
    { message ->

        Toast.makeText(this,message,Toast.LENGTH_LONG).show()

        when {

            message.contains("failed") ->{

                roomIsBad(getString(android.R.string.httpErrorBadUrl))

            }

        }
    }

    @Inject lateinit var presenter:
            ChatActivityViewPresenter

    @Inject lateinit var sharedPref:
            SharedPref



    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_chat)

        webViewChat=findViewById(R.id.zz_chat_activity)

        when(val chatUri=intent.extras?.getString(CHAT_URI))
        {
            null->
            {

                roomIsBad()

            }

            else->
            {

                try
                {

                    webViewChat.loadUrl(chatUri)

                }
                catch (e:Exception)
                {

                    roomIsBad(e.toString())

                }

            }
        }

    }

    override fun onStart()
    {
        super.onStart()

        webViewChat.onPermissionRequest.observe(
            this,
            observerPermission
        )

        webViewChat.onFinishLoadPage.observe(
            this,
            observerOnFinishLoadPage
        )
        webViewChat.jsLog.observe(
            this,
            observerJsLog
        )

    }

    override fun onStop()
    {
        super.onStop()
        webViewChat.onPermissionRequest.removeObserver(
            observerPermission
        )

        webViewChat.onFinishLoadPage.removeObserver(
            observerOnFinishLoadPage
        )

        webViewChat.jsLog.removeObserver(
            observerJsLog
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== REQUEST_CODE_PERMISSION)
        {
            val grantedAll=ArrayList<Int>()
            grantResults.toCollection(grantedAll)
            if (grantedAll.contains(PackageManager.PERMISSION_DENIED))
            {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Need permission")
                    .setPositiveButton("exit") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    .setNegativeButton("set permission") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", this.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .show()
            }
            else
            {
                intent.addFlags(
                    FLAG_ACTIVITY_CLEAR_TOP
                )
                startActivity(intent)
            }
        }

    }

    override fun checkSelfPermission(permission: String): Int
    {

        return ContextCompat.checkSelfPermission(this,permission)
    }

    fun requestPermissions(permissions: Array<String>)
    {

        ActivityCompat.requestPermissions(
            this,
            permissions,
            REQUEST_CODE_PERMISSION
        )

    }

    private fun roomIsBad(strError: String?=null)
    {
        if (strError==null)
        {
            intent.putExtra(CHAT_URI_ERROR,getString(android.R.string.httpErrorBadUrl))
        }
        else
        {
            intent.putExtra(CHAT_URI_ERROR,strError)
        }
        setResult(RESULT_CANCELED,intent)
        finish()
    }

    fun handleResultJavaScriptIsValidWebChat(resultJavaScript: String?)
    {
        when(resultJavaScript)
        {
            "true"->{

            }
            else ->{

                roomIsBad(getString(android.R.string.httpErrorBadUrl))

            }
        }
    }
    fun handlePermissionsRequest(permissionsRequest: PermissionRequest)
    {

        val permissionAndroid = ArrayList<String>()

        permissionsRequest.resources
            .forEach { permissionWebView ->

                when (permissionWebView)
                {

                    PermissionRequest.RESOURCE_VIDEO_CAPTURE ->
                    {

                        Manifest.permission.CAMERA.let {
                            if (
                                checkSelfPermission(it)
                                !=
                                PackageManager.PERMISSION_GRANTED
                            )
                            {
                                permissionAndroid.add(it)
                            }
                        }

                    }

                    PermissionRequest.RESOURCE_AUDIO_CAPTURE ->
                    {

                        Manifest.permission.RECORD_AUDIO.let {

                            if (
                                checkSelfPermission(it)
                                !=
                                PackageManager.PERMISSION_GRANTED
                            )
                            {
                                permissionAndroid.add(it)
                            }

                        }

                    }

                }

            }

        if(permissionAndroid.size==0)
        {
            permissionsRequest.grant(permissionsRequest.resources)
        }
        else
        {

            requestPermissions(permissionAndroid.toTypedArray())

        }

    }

}

class ChatActivityViewPresenter @Inject constructor()
{

    fun getJavaScriptIsValidWebChat(): String
    {
        return "isValidChatPage();"
    }


}

