package Util;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Prefs {

    private static final String PREF_NOTIFICATION_LIST = "pref_notification_list";

    private SharedPreferences mPrefs;

    public void clearNotifCache() {
        getEditor().remove(PREF_NOTIFICATION_LIST).apply();
        //clear the notification tray - mNotifManager.cancelAll();
    }

    public void addToNotifCache(NotificationUtil.NotificationContent content) {

        List<NotificationUtil.NotificationContent> notifList = getCachedNotifObject();

        if (notifList != null) {
            notifList.add(content);
            saveNotifToCache(notifList);
        }
    }

    public void removeNotifFromCache(int notifId) {

        List<NotificationUtil.NotificationContent> notifList = getCachedNotifObject();

        if (notifList.isEmpty() || notifId < 0)
            return;

        for (NotificationUtil.NotificationContent content : notifList) {
            if (content.getNotifId() == notifId) {
                notifList.remove(content);
                break;
            }
        }

        saveNotifToCache(notifList);
    }

    public List<NotificationUtil.NotificationContent> getCachedNotifObject() {

        String jsonString = mPrefs.getString(PREF_NOTIFICATION_LIST, "");

        if (TextUtils.isEmpty(jsonString))
            return new ArrayList<>();
        else
            return new Gson().fromJson(jsonString, new TypeToken<List<NotificationUtil.NotificationContent>>() {
            }.getType());
    }

    private void saveNotifToCache(List<NotificationUtil.NotificationContent> notifList) {

        String jsonString;

        if (notifList == null || notifList.isEmpty())
            jsonString = "";
        else
            jsonString = new Gson().toJson(notifList, new TypeToken<List<NotificationUtil.NotificationContent>>() {
            }.getType());

        getEditor().putString(PREF_NOTIFICATION_LIST, jsonString).apply();
    }

    public int getNotifCacheCount() {
        return getCachedNotifObject().size();
    }

    public SharedPreferences.Editor getEditor() {
        return mPrefs.edit();
    }
    /*Create class body*/
}
