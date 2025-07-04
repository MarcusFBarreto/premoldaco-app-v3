// app/src/main/java/com/pesquisapromo/premoldaco.premoldacoapp.v1/MainActivity.kt
package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface // <-- IMPORTANTE: Nova importação
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity()    {

    private lateinit var webView: WebView
    private var isWebViewReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { !isWebViewReady }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                webView.goBack()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        webView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        // --- NOVO CÓDIGO: Conectando a ponte entre JS e Kotlin ---
        // A linha abaixo "injeta" nossa classe WebAppInterface no JavaScript da WebView.
        // Dentro do JavaScript, ela será acessível como o objeto "Android".
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        // ---------------------------------------------------------

        webView.webViewClient = CustomWebViewClient(onBackPressedCallback)
        webView.webChromeClient = CustomWebChromeClient()


        // Chama a função para pedir permissão
        askNotificationPermission()

        // --- CÓDIGO NOVO: Verifica se a activity foi aberta por uma notificação ---
        val urlFromNotification = intent.getStringExtra("url")
        val initialUrl = if (urlFromNotification != null) {
            // Se veio de uma notificação, usa a URL da notificação
            urlFromNotification
        } else {
            // Senão, usa a URL padrão da calculadora
            "https://premoldaco.com.br/calculadora"
        }
        webView.loadUrl("https://premoldaco.com.br/calculadora.html")
    }



    // --- NOVA CLASSE: A "ponte" que contém as funções que o JS pode chamar ---
    private inner class WebAppInterface(private val context: Context) {

        // A anotação @JavascriptInterface é OBRIGATÓRIA por segurança.
        // Apenas métodos com ela podem ser chamados pelo JavaScript.
        @JavascriptInterface
        fun playSound() {
            // Este é o mesmo código que usamos para o som da splash screen,
            // garantindo que ele só toque se o celular não estiver no silencioso.
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.dim3)
                    mediaPlayer.start()
                    mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // ----------------------------------------------------------------------


    private inner class CustomWebViewClient(private val callback: OnBackPressedCallback) : WebViewClient() {
        // ... seu código do WebViewClient continua o mesmo ...
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            callback.isEnabled = view?.canGoBack() ?: false
            isWebViewReady = true
        }

        override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
            super.onReceivedError(view, request, error)
            isWebViewReady = true
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
            val url = request?.url.toString()
            val allowedDomain = "premoldaco.com.br"

            if (Uri.parse(url).host == allowedDomain) {
                return false
            }

            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Não foi possível abrir o link.", Toast.LENGTH_LONG).show()
            }
            return true
        }
    }

    private inner class CustomWebChromeClient : WebChromeClient() {
        // ... seu código do WebChromeClient continua o mesmo ...
        override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message?): Boolean {
            val newWebView = WebView(this@MainActivity)
            newWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(newView: WebView, url: String): Boolean {
                    webView.loadUrl(url)
                    return true
                }
            }
            val transport = resultMsg?.obj as? WebView.WebViewTransport
            transport?.webView = newWebView
            resultMsg?.sendToTarget()
            return true
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Aqui você pode tratar se a permissão foi concedida ou não
    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU = Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                // Pede a permissão
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


}