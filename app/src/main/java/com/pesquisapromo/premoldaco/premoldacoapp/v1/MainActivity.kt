package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var isWebViewReady = false
    private var isProcessing: Boolean = false
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var prefillEmail: String? = null
    // No futuro, podemos adicionar estes também
    // private var prefillName: String? = null
    // private var prefillPhone: String? = null

    private var isPageReadyForPrefill = false // Nova flag de controle

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { !isWebViewReady }
        }
        setTheme(R.style.Theme_PremoldaçoApp) // Para evitar crashes de tema
        super.onCreate(savedInstanceState)

        prefillEmail = intent.getStringExtra("USER_EMAIL")

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
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.webViewClient = CustomWebViewClient(onBackPressedCallback, prefillEmail)
        webView.webChromeClient = CustomWebChromeClient()

        askNotificationPermission()

        val urlFromNotification = intent.getStringExtra("url")
        val initialUrl = urlFromNotification ?: "https://premoldaco.com.br/calculadora.html"
        webView.loadUrl(initialUrl)
    }

    private fun tryToPrefillData() {
        runOnUiThread {
            if (prefillEmail != null && isPageReadyForPrefill) {
                val escapedEmail = prefillEmail!!.replace("'", "\\'")
                val script = "javascript:window.preencherDadosUsuario('$escapedEmail', '', '')"
                webView.evaluateJavascript(script, null)
                Log.d("Prefill", "Script de pré-preenchimento executado: $script")

                prefillEmail = null
            }
        }
    }

    private fun processQuote(jsonData: String) {
        val currentUser = auth.currentUser ?: return

        isProcessing = true
        val quoteMap = Gson().fromJson(jsonData, Map::class.java) as MutableMap<String, Any>
        quoteMap["userId"] = currentUser.uid
        quoteMap["dataCriacao"] = FieldValue.serverTimestamp()

        db.collection("orcamentos")
            .add(quoteMap)
            .addOnSuccessListener {
                isProcessing = false
                runOnUiThread {
                    Toast.makeText(this, "Orçamento enviado com sucesso!", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                isProcessing = false
                runOnUiThread {
                    Toast.makeText(this, "Erro ao enviar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("FCM", "Erro ao salvar no Firestore", e)
            }
    }

    private inner class WebAppInterface(private val context: Context) {

        @JavascriptInterface
        fun pageLoaded() {
            Log.d("WebAppInterface", "JS avisou que a página carregou.")
            isPageReadyForPrefill = true
            tryToPrefillData()
        }

        @JavascriptInterface
        fun playSound() {
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

        @JavascriptInterface
        fun submitQuote(jsonData: String) {
            if (isProcessing) {
                runOnUiThread { Toast.makeText(context, "Um envio já está em andamento.", Toast.LENGTH_SHORT).show() }
                return
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                processQuote(jsonData)
            } else {
                isProcessing = true
                Log.d("AUTH", "Nenhum usuário. Iniciando login anônimo sob demanda...")
                auth.signInAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AUTH", "Login anônimo sob demanda bem-sucedido. Processando orçamento.")
                        processQuote(jsonData)
                    } else {
                        isProcessing = false
                        runOnUiThread { Toast.makeText(context, "Falha na autenticação inicial. Tente novamente.", Toast.LENGTH_LONG).show() }
                        Log.w("AUTH", "Falha no login anônimo sob demanda", task.exception)
                    }
                }
            }
        }
    }

    private inner class CustomWebViewClient(
        private val callback: OnBackPressedCallback,
        private var emailToPrefill: String?
    ) : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            callback.isEnabled = view?.canGoBack() ?: false
            isWebViewReady = true

            tryToPrefillData()
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
    ) { /* Não fazemos nada com o resultado por enquanto */ }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}