package id.jitari.autopostalarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  val id=intent.getStringExtra("id")?:return
  val title=intent.getStringExtra("title")?.ifBlank{"Draft Posting"}?:"Draft Posting"
  val url=intent.getStringExtra("url")?:return
  val open=Intent(context,MainActivity::class.java).putExtra("open_url",url).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
  val pi=PendingIntent.getActivity(context,id.hashCode(),open,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val n=Notification.Builder(context,"draft_alarm").setSmallIcon(R.drawable.ic_alarm).setContentTitle("Waktunya posting").setContentText(title).setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX).setAutoCancel(true).setContentIntent(pi).setFullScreenIntent(pi,true).setDefaults(Notification.DEFAULT_ALL).build()
  context.getSystemService(NotificationManager::class.java).notify(id.hashCode(),n)
 }
}
