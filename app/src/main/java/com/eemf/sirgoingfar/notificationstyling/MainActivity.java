package com.eemf.sirgoingfar.notificationstyling;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import Util.NotificationUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void popNotifications(View view) {
        NotificationUtil notifObject = new NotificationUtil(this);
        notifObject.notifyUser();
    }
}
