package com.yinlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.os.MemoryFile;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yinlib.service.aidl.IStandardInterface;
import com.yinlib.service.util.MemoryFileHelper;

/**
 * $todo$
 *
 * @user Jay Wang
 * @date 2018-07-24 11:45
 */
public class RemoteService extends Service {

    public static final String TAG = RemoteService.class.getSimpleName();
    int size = 640*480;
    byte[] dataContent = new byte[size];
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"RemoteService onBind");
        return new MyBind();
    }

    native int getFdData(int fd, byte[] data);

    public class MyBind extends IStandardInterface.Stub{
        public MyBind() {
            super();
        }

        @Override
        public IBinder asBinder() {
            return super.asBinder();
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void dataFlow(ParcelFileDescriptor data, int format, int width, int height, int stride, int oritention) throws RemoteException {
            try {
//                ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(data);
                long time = System.currentTimeMillis();
                MemoryFile memoryFile = MemoryFileHelper.openMemoryFile(data, dataContent.length, MemoryFileHelper.OPEN_READWRITE);
                memoryFile.readBytes(dataContent,0,0, dataContent.length);
                memoryFile.close();

                Log.d(TAG, "Service dataFlow data: " + dataContent[dataContent.length - 1] + " time : " + (System.currentTimeMillis() - time) );
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getStatus() throws RemoteException {
            Log.d(TAG, "Service getStatus" );
            return 1;
        }
    }
}
