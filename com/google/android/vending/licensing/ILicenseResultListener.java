package com.google.android.vending.licensing;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILicenseResultListener extends IInterface {
  void verifyLicense(int paramInt, String paramString1, String paramString2) throws RemoteException;
  
  public static abstract class Stub extends Binder implements ILicenseResultListener {
    private static final String DESCRIPTOR = "com.android.vending.licensing.ILicenseResultListener";
    
    static final int TRANSACTION_verifyLicense = 1;
    
    public Stub() { attachInterface(this, "com.android.vending.licensing.ILicenseResultListener"); }
    
    public static ILicenseResultListener asInterface(IBinder param1IBinder) {
      if (param1IBinder == null)
        return null; 
      IInterface iInterface = param1IBinder.queryLocalInterface("com.android.vending.licensing.ILicenseResultListener");
      return (iInterface != null && iInterface instanceof ILicenseResultListener) ? (ILicenseResultListener)iInterface : new Proxy(param1IBinder);
    }
    
    public IBinder asBinder() { return this; }
    
    public boolean onTransact(int param1Int1, Parcel param1Parcel1, Parcel param1Parcel2, int param1Int2) throws RemoteException {
      if (param1Int1 != 1) {
        if (param1Int1 != 1598968902)
          return super.onTransact(param1Int1, param1Parcel1, param1Parcel2, param1Int2); 
        param1Parcel2.writeString("com.android.vending.licensing.ILicenseResultListener");
        return true;
      } 
      param1Parcel1.enforceInterface("com.android.vending.licensing.ILicenseResultListener");
      param1Parcel1.readInt();
      verifyLicense(0, param1Parcel1.readString(), param1Parcel1.readString());
      return true;
    }
    
    private static class Proxy implements ILicenseResultListener {
      private IBinder mRemote;
      
      Proxy(IBinder param2IBinder) { this.mRemote = param2IBinder; }
      
      public IBinder asBinder() { return this.mRemote; }
      
      public String getInterfaceDescriptor() { return "com.android.vending.licensing.ILicenseResultListener"; }
      
      public void verifyLicense(int param2Int, String param2String1, String param2String2) throws RemoteException {
        parcel = Parcel.obtain();
        try {
          parcel.writeInterfaceToken("com.android.vending.licensing.ILicenseResultListener");
          parcel.writeInt(param2Int);
          parcel.writeString(param2String1);
          parcel.writeString(param2String2);
          this.mRemote.transact(1, parcel, null, 1);
          return;
        } finally {
          parcel.recycle();
        } 
      }
    }
  }
  
  private static class Proxy implements ILicenseResultListener {
    private IBinder mRemote;
    
    Proxy(IBinder param1IBinder) { this.mRemote = param1IBinder; }
    
    public IBinder asBinder() { return this.mRemote; }
    
    public String getInterfaceDescriptor() { return "com.android.vending.licensing.ILicenseResultListener"; }
    
    public void verifyLicense(int param1Int, String param1String1, String param1String2) throws RemoteException {
      parcel = Parcel.obtain();
      try {
        parcel.writeInterfaceToken("com.android.vending.licensing.ILicenseResultListener");
        parcel.writeInt(param1Int);
        parcel.writeString(param1String1);
        parcel.writeString(param1String2);
        this.mRemote.transact(1, parcel, null, 1);
        return;
      } finally {
        parcel.recycle();
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/ILicenseResultListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */