package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Este método é chamado quando o app recebe uma nova mensagem do FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            // Pega a URL dos dados da notificação (vamos configurar isso no Firebase)
            val url = remoteMessage.data["url"]
            Log.d("FCM", "Message Notification Body: ${it.body}, URL: $url")
            sendNotification(it.title, it.body, url)
        }
    }

    // Este método é chamado quando um novo token de dispositivo é gerado
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // No futuro, você enviaria este token para seus servidores
    }

    private fun sendNotification(title: String?, messageBody: String?, url: String?) {
        // Cria uma Intent para abrir a MainActivity quando a notificação for tocada
        val intent = Intent(this, MainActivity::class.java).apply {
            // Adiciona a URL como um "dado extra" para a MainActivity saber qual página abrir
            putExtra("url", url)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "default_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_splash)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // <-- Define a ação de clique
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Para Android 8.0 (Oreo) e superior, é obrigatório criar um Canal de Notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Notificações Gerais",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID da notificação */, notificationBuilder.build())
    }
}