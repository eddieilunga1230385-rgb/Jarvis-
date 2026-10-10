package com.ngm.jarvis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var status: TextView
    private val VOICE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32,48,32,32)
        }
        val title = TextView(this).apply { text="JARVIS NGM"; textSize=32f }
        status = TextView(this).apply { text="Prêt."; textSize=18f }
        val input = EditText(this).apply { hint="Écris une commande..." }
        val send = Button(this).apply { text="Exécuter" }
        val voice = Button(this).apply { text="🎙️ Parler à JARVIS" }
        layout.addView(title); layout.addView(status); layout.addView(input); layout.addView(send); layout.addView(voice)
        setContentView(layout)

        send.setOnClickListener { execute(input.text.toString()) }
        voice.setOnClickListener { startVoice() }
        requestPermissions()
    }

    private fun requestPermissions() {
        val p = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)
        ActivityCompat.requestPermissions(this, p, 20)
    }

    private fun startVoice() {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH)
        startActivityForResult(i, VOICE)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if(requestCode==VOICE && resultCode==RESULT_OK) {
            val text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: return
            execute(text)
        }
    }

    private fun execute(command:String) {
        val c=command.lowercase(Locale.FRENCH)
        status.text="Commande : $command"
        when {
            c.contains("ouvre whatsapp") -> openPackage("com.whatsapp")
            c.contains("ouvre facebook") -> openPackage("com.facebook.katana")
            c.contains("ouvre messenger") -> openPackage("com.facebook.orca")
            c.contains("ouvre youtube") -> openPackage("com.google.android.youtube")
            c.contains("ouvre chrome") -> openPackage("com.android.chrome")
            c.contains("ouvre paramètres") || c.contains("ouvre les paramètres") ->
                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            c.startsWith("appelle ") -> {
                val number=c.removePrefix("appelle ").trim()
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
            }
            else -> Toast.makeText(this,"Cette commande devra être reliée à ChatGPT/Claude.",Toast.LENGTH_LONG).show()
        }
    }

    private fun openPackage(pkg:String) {
        val i=packageManager.getLaunchIntentForPackage(pkg)
        if(i!=null) startActivity(i)
        else Toast.makeText(this,"Application non installée.",Toast.LENGTH_SHORT).show()
    }
}
