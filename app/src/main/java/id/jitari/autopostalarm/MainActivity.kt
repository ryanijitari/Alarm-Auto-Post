package id.jitari.autopostalarm

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Button
import android.widget.Toast
import java.time.OffsetDateTime

class MainActivity : android.app.Activity() {
    private lateinit var web: WebView
    private val prefs by lazy { getSharedPreferences("jitari", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel(); requestNotificationPermission()
        val base = prefs.getString("base_url", "") ?: ""
        if (base.isBlank()) showSetup() else showWeb(intent.getStringExtra("open_url") ?: base)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent); setIntent(intent)
        val url = intent.getStringExtra("open_url")
        if (::web.isInitialized && !url.isNullOrBlank()) web.loadUrl(url)
    }

    private fun showSetup() {
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(40,80,40,40)}
        val input=EditText(this).apply{hint="https://domainmu.com/autopost/";inputType=android.text.InputType.TYPE_TEXT_VARIATION_URI}
        val btn=Button(this).apply{text="Simpan & Buka Jitari Auto Post"}
        root.addView(input);root.addView(btn);setContentView(root)
        btn.setOnClickListener { var u=input.text.toString().trim(); if(!u.startsWith("https://")){Toast.makeText(this,"Gunakan URL HTTPS",Toast.LENGTH_SHORT).show();return@setOnClickListener}; if(!u.endsWith("/"))u+="/";prefs.edit().putString("base_url",u).apply();showWeb(u) }
    }

    private fun showWeb(url:String){
        web=WebView(this);setContentView(web)
        web.settings.javaScriptEnabled=true;web.settings.domStorageEnabled=true;web.settings.mediaPlaybackRequiresUserGesture=false
        web.webChromeClient=WebChromeClient();web.webViewClient=WebViewClient();web.addJavascriptInterface(AlarmBridge(this),"JitariAlarm")
        web.loadUrl(url)
    }

    override fun onBackPressed(){ if(::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed() }

    private fun createChannel(){ if(Build.VERSION.SDK_INT>=26){val ch=NotificationChannel("draft_alarm",getString(R.string.channel_name),NotificationManager.IMPORTANCE_HIGH).apply{description="Pengingat untuk posting draft";enableVibration(true);lockscreenVisibility=1};getSystemService(NotificationManager::class.java).createNotificationChannel(ch)} }
    private fun requestNotificationPermission(){if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),33)}

    inner class AlarmBridge(private val ctx:Context){
        @JavascriptInterface fun schedule(id:String, iso:String, title:String, url:String):Boolean = try {
            val whenMs=OffsetDateTime.parse(iso).toInstant().toEpochMilli(); if(whenMs<=System.currentTimeMillis())return false
            val am=getSystemService(AlarmManager::class.java)
            if(Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms()){ startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")));return false }
            val i=Intent(ctx,AlarmReceiver::class.java).putExtra("id",id).putExtra("title",title).putExtra("url",url)
            val pi=PendingIntent.getBroadcast(ctx,id.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMs,pi)
            runOnUiThread{Toast.makeText(ctx,"Alarm draft tersimpan di HP",Toast.LENGTH_SHORT).show()};true
        } catch(e:Exception){false}
        @JavascriptInterface fun cancel(id:String){val am=getSystemService(AlarmManager::class.java);val i=Intent(ctx,AlarmReceiver::class.java);val pi=PendingIntent.getBroadcast(ctx,id.hashCode(),i,PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE);if(pi!=null)am.cancel(pi)}
    }
}
