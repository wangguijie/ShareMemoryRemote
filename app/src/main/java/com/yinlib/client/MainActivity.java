package com.yinlib.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LocalClient mLocalClient;
    private TextView mConnectStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalClient.disConnectService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isBind = mLocalClient.connectService();
        mConnectStatus.setText("Bind Success : " + isBind);
    }

    private void init(){
        mConnectStatus = findViewById(R.id.status);
        mLocalClient = new LocalClient(this);
        mLocalClient.setServiceListener(mServiceListener);
    }

    LocalClient.IServiceListener mServiceListener = new LocalClient.IServiceListener() {
        @Override
        public void onServiceConnect() {
            mConnectStatus.setText("Bind Success : true status : " + mLocalClient.getStatus());
            mLocalClient.dataFlow(1);
            mLocalClient.dataFlow(2);
            mLocalClient.dataFlow(3);
        }

        @Override
        public void onServiceDisconnect() {

        }
    };
}
