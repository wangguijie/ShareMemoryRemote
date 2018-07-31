package com.yinlib.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.yinlib.service.aidl.IStandardInterface;

import java.io.FileDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;



/**
 * Created by jayzwang on 7/12/18.
 */

public class LocalClient {

    public static final String TAG = LocalClient.class.getSimpleName();
    public static final String SERVICE_ACTION = "com.yinlib.service.remoteservice";
    public static final String SERVICE_PACKAGE = "com.yinlib.service";
    private IStandardInterface Client ;
    private Context mContext;
    private boolean mHasBind;
    private IServiceListener mServiceListener;
    private MemoryFile mServiceShareMemory;
    private FileDescriptor mServiceShareFile;
    private ParcelFileDescriptor mParceServiceShareFile;
    private int mFD = -1;
    int CONTENT_SIZE = 640*480;
//    int CONTENT_SIZE = 200;
    private byte[] mContent = new byte[CONTENT_SIZE];
    private byte[] mContentCopy = new byte[CONTENT_SIZE];
    public interface IServiceListener{
        void onServiceConnect();
        void onServiceDisconnect();
    }

    public LocalClient(Context context) {
        mContext = context.getApplicationContext();
        createMemFile();

    }

    private void createMemFile(){
        try {
            mServiceShareMemory = new MemoryFile("com.yinlib.service" + System.currentTimeMillis(), mContent.length);
            Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            FileDescriptor fd = (FileDescriptor) method.invoke(mServiceShareMemory);
            mParceServiceShareFile = ParcelFileDescriptor.dup(fd);
            if(mServiceShareMemory != null){
                mServiceShareMemory.allowPurging(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //connect to arservice, call onStart
    public boolean connectService(){
        boolean isBindService = mContext.getApplicationContext().bindService(new Intent(SERVICE_ACTION).setPackage(SERVICE_PACKAGE), mServiceConnect,  Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_WAIVE_PRIORITY | Context.BIND_ABOVE_CLIENT);
        Log.d(TAG,"bind services isBindService : " + isBindService);
        return isBindService;
    }

    //disconnet service, call onDestroy
    public void disConnectService(){
        if(!mHasBind){
            return;
        }
        mContext.unbindService(mServiceConnect);
        mHasBind = false;
    }

    public void setServiceListener(IServiceListener listener){
        mServiceListener = listener;
    }

    public void dataFlow(int value){
        Arrays.fill(mContent, (byte)value);
        if(mHasBind){
            try {
                Log.d(TAG, "Client  dataFlow start mContent: " + mContent[0]);
                long time = System.currentTimeMillis();
                mServiceShareMemory.writeBytes(mContent,0,0, mContent.length);
                Log.d(TAG, "Client  dataFlow start mContentCopy: " + mContentCopy[0]);
                Log.d(TAG, "Client  dataFlow writeBytes : " + (System.currentTimeMillis()- time));
                Client .dataFlow(mParceServiceShareFile, 2, 3, 4,5,6);
                Log.d(TAG, "Client  dataFlow create and flowTime : " + (System.currentTimeMillis()- time));
                time = System.currentTimeMillis();
                Log.d(TAG, "Client  dataFlow release : " + (System.currentTimeMillis()- time));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void dataFlowSharyMemeryOnce(int value){
        Arrays.fill(mContent, (byte)value);
        if(mHasBind){
            try {
                Log.d(TAG, "Client  dataFlow start mContent: " + mContent[0]);
                long time = System.currentTimeMillis();
                Log.d(TAG, "Client  dataFlow createTime : " + (System.currentTimeMillis()- time));
                mServiceShareMemory.writeBytes(mContent,0,0, mContent.length);
                Log.d(TAG, "Client  dataFlow start mContentCopy: " + mContentCopy[0]);
                Client .dataFlow(mParceServiceShareFile, 2, 3, 4,5,6);
                Log.d(TAG, "Client  dataFlow create and flowTime : " + (System.currentTimeMillis()- time));
                time = System.currentTimeMillis();
                Log.d(TAG, "Client  dataFlow release : " + (System.currentTimeMillis()- time));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getStatus(){
        int status = 0;
        if(mHasBind){
            try {
                status = Client .getStatus();
                Log.d(TAG, "Client  getStatus : " + status);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return status;
    }

    private ServiceConnection mServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Client  = IStandardInterface.Stub.asInterface(iBinder);
            mHasBind = true;
            Log.d(TAG, "onServiceConnected");
            onConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mHasBind = false;
            Log.d(TAG, "onServiceDisconnected");
            onDisonnect();
        }
    };

    private void onConnect(){
        if(mServiceListener == null){
            return;
        }
        mServiceListener.onServiceConnect();
    }

    private void onDisonnect(){
        releaseShareMemory();
        if(mServiceListener == null){
            return;
        }
        mServiceListener.onServiceDisconnect();
    }

    private void releaseShareMemory(){
        try {
            mParceServiceShareFile.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mServiceShareMemory == null){
            return;
        }
        mServiceShareMemory.close();
        mServiceShareMemory = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        releaseShareMemory();
    }
}
