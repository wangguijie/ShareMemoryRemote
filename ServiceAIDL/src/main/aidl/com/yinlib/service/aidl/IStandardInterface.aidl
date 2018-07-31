// IStandardInterface.aidl
package com.yinlib.service.aidl;
import android.os.ParcelFileDescriptor;
// Declare any non-default types here with import statements

interface IStandardInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void dataFlow(in ParcelFileDescriptor data, int format , int width, int height, int stride, int oritention);
    int getStatus();
}
