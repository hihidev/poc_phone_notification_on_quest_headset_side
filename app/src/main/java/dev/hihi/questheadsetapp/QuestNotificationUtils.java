package dev.hihi.questheadsetapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class QuestNotificationUtils {

    private static void sendInternalNotification(Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, boolean vrshell_aui_persist, String vrshell_aui_badge_route, boolean vrshell_aui_badge_auto_dismiss, boolean vrshell_aui_badge_badge_parent, String oculus_category, boolean oculus_ignore_missing_title) {

        Bundle bundle = new Bundle();
        bundle.putBoolean("vrshell_aui_persist", vrshell_aui_persist);
        bundle.putString("aui_notif_duration", "LONG");
        bundle.putString("vrshell_aui_badge_route", vrshell_aui_badge_route);
        bundle.putBoolean("vrshell_aui_badge_auto_dismiss", vrshell_aui_badge_auto_dismiss);
        bundle.putBoolean("vrshell_aui_badge_badge_parent", vrshell_aui_badge_badge_parent);
        bundle.putString("oculus_category", oculus_category);
        bundle.putBoolean("oculus_ignore_missing_title", oculus_ignore_missing_title);
        NotificationCompat.Builder builder = (new NotificationCompat.Builder(paramContext)).setContentTitle(paramString1).setContentText(paramString2).setPriority(paramInt2).setSmallIcon(paramInt3).setAutoCancel(true).addExtras(bundle);
        if (paramPendingIntent1 != null)
            builder.setContentIntent(paramPendingIntent1);
//        if (paramPendingIntent2 != null)
//            builder.addAction(-1, "accept", paramPendingIntent2);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(paramContext);
        notificationManager.notify("tag", (int) System.currentTimeMillis(), builder.build());
    }

    public static void sendNotification(Context paramContext, String title, String text) {
        int id = (int) System.currentTimeMillis();
        int priority = 2;
        boolean vrshell_aui_persist = true;
        String vrshell_aui_badge_route = null;
        boolean vrshell_aui_badge_auto_dismiss = true;
        boolean vrshell_aui_badge_badge_parent = true;
        String oculus_category = "";
        boolean oculus_ignore_missing_title = false;
        String vrshell_test_notif_tag = null;

        Intent intent = new Intent("com.oculus.vrshell.intent.action.LAUNCH");
        intent.setPackage("com.oculus.vrshell");
        intent.putExtra("intent_data", (Parcelable) Uri.parse("systemux://browser"));
        intent.putExtra("blackscreen", false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(paramContext, 0, intent, 0);
        intent = new Intent("com.oculus.vrshell.intent.action.LAUNCH");
        intent.setPackage("com.oculus.vrshell");
        intent.putExtra("intent_data", (Parcelable)Uri.parse("systemux://browser"));
        intent.putExtra("uri", "");
        intent.putExtra("blackscreen", false);
        sendInternalNotification(paramContext, title, text, vrshell_test_notif_tag, id, priority, R.drawable.ic_launcher_foreground, pendingIntent, PendingIntent.getBroadcast(paramContext, 989, intent, 0), vrshell_aui_persist, vrshell_aui_badge_route, vrshell_aui_badge_auto_dismiss, vrshell_aui_badge_badge_parent, oculus_category, oculus_ignore_missing_title);
    }
}
