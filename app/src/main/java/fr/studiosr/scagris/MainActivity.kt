package fr.studiosr.scagris

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Garder l'écran allumé au comptoir et mode plein écran immersif
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        webView = WebView(this)
        setContentView(webView)

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true // Indispensable pour sauvegarder les comptes en dur dans la tablette (localStorage)
        settings.databaseEnabled = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true // Permet d'appeler le script Google Sheets depuis le fichier local
        settings.mediaPlaybackRequiresUserGesture = false // Permet les bips sonores vert/rouge instantanés
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }
        }

        // Support natif des boîtes de dialogue (ex: création d'un nouveau joueur)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("SC Agris")
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ -> result?.confirm() }
                    .setCancelable(false)
                    .show()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Confirmation")
                    .setMessage(message)
                    .setPositiveButton("Oui") { _, _ -> result?.confirm() }
                    .setNegativeButton("Annuler") { _, _ -> result?.cancel() }
                    .setCancelable(false)
                    .show()
                return true
            }

            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                val input = EditText(this@MainActivity)
                input.setText(defaultValue ?: "")
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(message)
                    .setView(input)
                    .setPositiveButton("Valider") { _, _ -> result?.confirm(input.text.toString()) }
                    .setNegativeButton("Annuler") { _, _ -> result?.cancel() }
                    .setCancelable(false)
                    .show()
                return true
            }
        }

        // Chargement 100 % en dur depuis la mémoire interne de l'APK (Zéro serveur web, Zéro Netlify)
        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            // Empêche de quitter l'application par erreur au comptoir
        }
    }
}
