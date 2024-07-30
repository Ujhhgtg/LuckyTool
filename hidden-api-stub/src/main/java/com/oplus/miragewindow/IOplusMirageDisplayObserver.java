package com.oplus.miragewindow;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IOplusMirageDisplayObserver extends IInterface {
    
    public static final String DESCRIPTOR = "com.oplus.miragewindow.IOplusMirageDisplayObserver";
    
    void onMirageDisplayCastFailed(int i) throws RemoteException;
    
    void onMirageDisplayCastSuccess(OplusMirageDisplayCastInfo oplusMirageDisplayCastInfo, int i) throws RemoteException;
    
    void onMirageDisplayConfigChanged(OplusMirageDisplayCastInfo oplusMirageDisplayCastInfo, int i) throws RemoteException;
    
    void onMirageDisplayExit(int i) throws RemoteException;
    
    void onMirageDisplayToastEvent(int i, int i2, Bundle bundle) throws RemoteException;
    
    void onMirageDisplayTopActivityUidChanged(int i, int i2) throws RemoteException;
    
    public static class Default implements IOplusMirageDisplayObserver {
        @Override
        public void onMirageDisplayCastFailed(int i) throws RemoteException {
        
        }
        
        @Override
        public void onMirageDisplayCastSuccess(OplusMirageDisplayCastInfo oplusMirageDisplayCastInfo, int i) throws RemoteException {
        
        }
        
        @Override
        public void onMirageDisplayConfigChanged(OplusMirageDisplayCastInfo oplusMirageDisplayCastInfo, int i) throws RemoteException {
        
        }
        
        @Override
        public void onMirageDisplayExit(int i) throws RemoteException {
        
        }
        
        @Override
        public void onMirageDisplayToastEvent(int i, int i2, Bundle bundle) throws RemoteException {
        
        }
        
        @Override
        public void onMirageDisplayTopActivityUidChanged(int i, int i2) throws RemoteException {
        
        }
        
        @Override
        public IBinder asBinder() {
            return null;
        }
    }
    
    public static abstract class Stub extends Binder implements IOplusMirageDisplayObserver {
        
        public Stub() {
            attachInterface(this, IOplusMirageDisplayObserver.DESCRIPTOR);
        }
        
        @Override
        public IBinder asBinder() {
            return this;
        }
        
    }
}
