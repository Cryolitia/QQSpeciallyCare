package me.singleNeuron.QQSpeciallyCare;

import android.app.AndroidAppHelper;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tencent.mobileqq")) return;
        XposedBridge.hookAllMethods(NotificationManager.class, "notify", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    super.beforeHookedMethod(param);
                    Notification notification = (Notification) param.args[param.args.length - 1];
                    String title = notification.extras.get(Notification.EXTRA_TITLE).toString();
                    String text = notification.extras.get(Notification.EXTRA_TEXT).toString();
                    if (BuildConfig.DEBUG) XposedBridge.log("QQ特别关心：" + title + text);
                    if (title.contains("[特别关心]")) {
                        XposedBridge.log("QQ特别关心：" + title + text + "true");
                        XposedBridge.log("QQ特别关心Channel：" + XposedHelpers.getObjectField(notification, "mChannelId").toString());
                        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID_SPECIALLY_CARE","特别关心", NotificationManager.IMPORTANCE_HIGH);
                        ((NotificationManager) AndroidAppHelper.currentApplication().getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                        XposedHelpers.setObjectField(notification,"mChannelId","CHANNEL_ID_SPECIALLY_CARE");
                        param.args[param.args.length - 1] = notification;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
