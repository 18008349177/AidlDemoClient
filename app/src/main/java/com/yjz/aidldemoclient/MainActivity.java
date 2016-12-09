package com.yjz.aidldemoclient;

import android.app.Service;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import android.content.Context;



import com.yjz.aidldemoserver.IMyAidlInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    public static final String TAG = "AidlDemoClient";

    private Button bindService;
    private Button unbindService;
    private boolean isBound = false;
    private IMyAidlInterface aidlService;
    private final String SERVICE_PACKAGE_NAME = "com.yjz.aidldemoserver";
    //用于启动MyService的Intent对应的action
    private final String SERVICE_ACTION_NAME = "com.yjz.aidldemoserver.MyService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService = (Button) findViewById(R.id.btnBindService);
        unbindService = (Button) findViewById(R.id.btnUnbindService);
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);
    }

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlService = IMyAidlInterface.Stub.asInterface(service);
            try {
                Log.i(TAG, "currentPID: " + Thread.currentThread().getName());
                Log.i(TAG, "Service version: " + aidlService.GetVersion());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "bind success! " + aidlService.toString());
            isBound = true;
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBindService:
                //Toast.makeText(MainActivity.this, "btnBindService", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "btnBindService+++");

                //单击了bindService按钮
                if(!isBound){
                    Log.i(TAG, "BindService start");

                    final Intent intent = new Intent();
                    intent.setAction(SERVICE_ACTION_NAME);
                    /**
                     * Service Intent must be explicit: Intent
                     * Android5.0+中service的intent一定要显性声明
                     */
                    //intent.setPackage(getPackageName());
                    intent.setPackage(SERVICE_PACKAGE_NAME);
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                }
                break;
            case R.id.btnUnbindService:
                //Toast.makeText(MainActivity.this, "btnUnbindService", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "btnUnbindService--");

                //单击了unbindService按钮
                if(isBound){
                    Log.i(TAG, "UnbindService end");
                    isBound = false;
                    unbindService(conn);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        isBound = false;
    }

}
