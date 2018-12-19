package Util;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.eemf.sirgoingfar.notificationstyling.R;

import java.util.List;

public class NotificationUtil {

    private static final String NOTIF_CHANNEL_ID = "channel_general_01";
    private static final String NOTIF_CHANNEL_NAME = "General";
    private static final String APP_GROUP_KEY = "com.notificationstyling.android";

    private Prefs prefs;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private Context mContext;
    private int notifId = 0;
    private String notifContent;

    public NotificationUtil(@NonNull Context context) {
        mContext = context;
    }

    public void notifyUser() {

        prefs = null; //initialize as appropriate

        //clear any notification from the app
        removeNotification();

        //send the incoming notification
        sendCustomNotification();

        //send pending notifications
        sendPendingNotifications();
    }

    private void removeNotification() {
        notificationManager.cancelAll();
    }

    private void sendCustomNotification() {

        Intent notificationIntent = new Intent(); /*Set the Value as appropriate*/

        pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIF_CHANNEL_ID);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Set Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(
                    NOTIF_CHANNEL_ID, NOTIF_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            );

            //create the channel for the Notification Manager
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notifChannel);
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        String notifContent = "Notification Content comes here";

        //Build the Notification
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        mContext.getResources(),
                        R.mipmap.ic_launcher))
                .setContentText(notifContent)
                .setColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                .setContentTitle("Notification Title")
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setGroup(APP_GROUP_KEY)
                .setLights(Color.BLUE, 500, 500)
                .setAutoCancel(true);
        /*add other customizations*/

        try {
            notificationManager.notify(notifId++, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPendingNotifications() {

        //Check for Pending Notifications and pop them first before the current notification
        List<NotificationUtil.NotificationContent> notifList = prefs.getCachedNotifObject();

        for (int count = 0; count < (notifList.size() - 1); count++) {
            initializeValues(notifList.get(count));
            sendCustomNotification();
        }

        sendSummaryNotification(notifList);
    }

    private void sendSummaryNotification(List<NotificationContent> notifList) {

        int notifCount = prefs.getNotifCacheCount();

        NotificationCompat.Builder summaryNotifBuilder = new NotificationCompat.Builder(mContext, NOTIF_CHANNEL_ID)
                .setContentTitle("Notification Title")
                .setContentText(notifCount + " New " + (notifCount > 1 ? "Messages" : "Message"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(APP_GROUP_KEY)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        NotificationContent content;

        int loopCount;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            loopCount = notifList.size() - 1;
        else
            loopCount = notifList.size();

        for (int count = 0; count < loopCount; count++) {
            content = notifList.get(count);

            initializeValues(content);

            inboxStyle.addLine(notifContent);
        }

        inboxStyle.setSummaryText(notifCount + " New " + (notifCount > 1 ? "Messages" : "Message"));
        summaryNotifBuilder.setStyle(inboxStyle);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            summaryNotifBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        try {
            notificationManager.notify(notifId++, summaryNotifBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NotificationContent {

        private int notifId = -1;
        private int msgNotifType;
        private String msgBody;
        private String msgOptionalTitle;
        private String msgSummary;
        private boolean isBroadcastOnly;
        private String senderId;
        private String groupSenderName;
        private String groupSenderId;
        private String recipientId;
        private String recipientName;
        private String recipientType;
        private String messageId;
        private String profileUrl;
        private Long sentAt = -1L;


        NotificationContent(Builder builder) {
            this.notifId = builder.getNotifId();
            this.msgNotifType = builder.getMsgNotifType();
            this.msgBody = builder.getMsgBody();
            this.msgOptionalTitle = builder.getMsgOptionalTitle();
            this.msgSummary = builder.getMsgSummary();
            this.isBroadcastOnly = builder.isBroadcastOnly();
            this.senderId = builder.getSenderId();
            this.groupSenderName = builder.getGroupSenderName();
            this.groupSenderId = builder.getGroupSenderId();
            this.recipientId = builder.getRecipientId();
            this.recipientName = builder.getRecipientName();
            this.recipientType = builder.getRecipientType();
            this.messageId = builder.getMessageId();
            this.profileUrl = builder.getProfileUrl();
            this.sentAt = builder.getSentAt();
        }

        public static class Builder {

            private int notifId;
            private int msgNotifType;
            private String msgBody;
            private String msgOptionalTitle;
            private String msgSummary;
            private boolean isBroadcastOnly;
            private String senderId;
            private String groupSenderName;
            private String groupSenderId;
            private String recipientId;
            private String recipientName;
            private String recipientType;
            private String messageId;
            private String profileUrl;
            private Long sentAt;

            public Builder setNotifId(int notifId) {
                this.notifId = notifId;
                return this;
            }

            private int getNotifId() {
                return notifId;
            }

            private int getMsgNotifType() {
                return msgNotifType;
            }

            public Builder setMsgNotifType(int msgNotifType) {
                this.msgNotifType = msgNotifType;
                return this;
            }

            private String getMsgBody() {
                return msgBody;
            }

            public Builder setMsgBody(String msgBody) {
                this.msgBody = msgBody;
                return this;
            }

            private String getMsgOptionalTitle() {
                return msgOptionalTitle;
            }

            public Builder setMsgOptionalTitle(String msgOptionalTitle) {
                this.msgOptionalTitle = msgOptionalTitle;
                return this;
            }

            private String getMsgSummary() {
                return msgSummary;
            }

            public Builder setMsgSummary(String msgSummary) {
                this.msgSummary = msgSummary;
                return this;
            }

            private boolean isBroadcastOnly() {
                return isBroadcastOnly;
            }

            public Builder setBroadcastOnly(boolean broadcastOnly) {
                isBroadcastOnly = broadcastOnly;
                return this;
            }

            private String getSenderId() {
                return senderId;
            }

            public Builder setSenderId(String senderId) {
                this.senderId = senderId;
                return this;
            }

            private String getGroupSenderId() {
                return groupSenderId;
            }

            public Builder setGroupSenderId(String groupSenderId) {
                this.groupSenderId = groupSenderId;
                return this;
            }

            private String getRecipientId() {
                return recipientId;
            }

            public Builder setRecipientId(String recipientId) {
                this.recipientId = recipientId;
                return this;
            }

            private String getRecipientName() {
                return recipientName;
            }

            public Builder setRecipientName(String recipientName) {
                this.recipientName = recipientName;
                return this;
            }

            private String getRecipientType() {
                return recipientType;
            }

            public Builder setRecipientType(String recipientType) {
                this.recipientType = recipientType;
                return this;
            }

            private String getMessageId() {
                return messageId;
            }

            public Builder setMessageId(String messageId) {
                this.messageId = messageId;
                return this;
            }

            private String getProfileUrl() {
                return profileUrl;
            }

            public Builder setProfileUrl(String profileUrl) {
                this.profileUrl = profileUrl;
                return this;
            }

            private Long getSentAt() {
                return sentAt;
            }

            public Builder setSentAt(Long sentAt) {
                this.sentAt = sentAt;
                return this;
            }

            public NotificationContent build() {
                return new NotificationContent(this);
            }

            private String getGroupSenderName() {
                return groupSenderName;
            }

            public Builder setGroupSenderName(String groupSenderName) {
                this.groupSenderName = groupSenderName;
                return this;
            }
        }

        public int getNotifId() {
            return notifId;
        }

        public int getMsgNotifType() {
            return msgNotifType;
        }

        public String getMsgBody() {
            return msgBody;
        }

        public String getMsgOptionalTitle() {
            return msgOptionalTitle;
        }

        public String getMsgSummary() {
            return msgSummary;
        }

        public boolean isBroadcastOnly() {
            return isBroadcastOnly;
        }

        public String getSenderId() {
            return senderId;
        }

        public String getGroupSenderName() {
            return groupSenderName;
        }

        public String getGroupSenderId() {
            return groupSenderId;
        }

        public String getRecipientId() {
            return recipientId;
        }

        public String getRecipientName() {
            return recipientName;
        }

        public String getRecipientType() {
            return recipientType;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public Long getSentAt() {
            return sentAt;
        }

    }

    private void initializeValues(NotificationUtil.NotificationContent content) {
        /*Set the components of the NotificationBody with the content object value*/
    }

    private boolean isAppForeground() {

        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        if (mActivityManager == null)
            return false;

        List<ActivityManager.RunningAppProcessInfo> processList = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : processList) {
            if (info.uid == mContext.getApplicationInfo().uid && info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
