package com.google.android.vending.licensing;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILicensingService extends IInterface {
  void checkLicense(long paramLong, String paramString, ILicenseResultListener paramILicenseResultListener) throws RemoteException;
  
  public static abstract class Stub extends Binder implements ILicensingService {
    private static final String DESCRIPTOR = "com.android.vending.licensing.ILicensingService";
    
    static final int TRANSACTION_checkLicense = 1;
    
    public Stub() { attachInterface(this, "com.android.vending.licensing.ILicensingService"); }
    
    public static ILicensingService asInterface(IBinder param1IBinder) {
      if (param1IBinder == null)
        return null; 
      IInterface iInterface = param1IBinder.queryLocalInterface("com.android.vending.licensing.ILicensingService");
      return (iInterface != null && iInterface instanceof ILicensingService) ? (ILicensingService)iInterface : new Proxy(param1IBinder);
    }
    
    public IBinder asBinder() { return this; }
    
    public boolean onTransact(int param1Int1, Parcel param1Parcel1, Parcel param1Parcel2, int param1Int2) throws RemoteException {
      if (param1Int1 != 1) {
        if (param1Int1 != 1598968902)
          return super.onTransact(param1Int1, param1Parcel1, param1Parcel2, param1Int2); 
        param1Parcel2.writeString("com.android.vending.licensing.ILicensingService");
        return true;
      } 
      param1Parcel1.enforceInterface("com.android.vending.licensing.ILicensingService");
      checkLicense(param1Parcel1.readLong(), param1Parcel1.readString(), ILicenseResultListener.Stub.asInterface(param1Parcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy implements ILicensingService {
      private IBinder mRemote;
      
      Proxy(IBinder param2IBinder) { this.mRemote = param2IBinder; }
      
      public IBinder asBinder() { return this.mRemote; }
      
      public void checkLicense(long param2Long, String param2String, ILicenseResultListener param2ILicenseResultListener) throws RemoteException {
        parcel = Parcel.obtain();
        try {
          parcel.writeInterfaceToken("com.android.vending.licensing.ILicensingService");
          parcel.writeLong(param2Long);
          parcel.writeString(param2String);
          if (param2ILicenseResultListener != null) {
            IBinder iBinder = param2ILicenseResultListener.asBinder();
          } else {
            param2String = null;
          } 
          parcel.writeStrongBinder(param2String);
          this.mRemote.transact(1, parcel, null, 1);
          return;
        } finally {
          parcel.recycle();
        } 
      }
      
      public String getInterfaceDescriptor() { return "com.android.vending.licensing.ILicensingService"; }
    }
  }
  
  private static class Proxy implements ILicensingService {
    private IBinder mRemote;
    
    Proxy(IBinder param1IBinder) { this.mRemote = param1IBinder; }
    
    public IBinder asBinder() { return this.mRemote; }
    
    public void checkLicense(long param1Long, String param1String, ILicenseResultListener param1ILicenseResultListener) throws RemoteException {
      parcel = Parcel.obtain();
      try {
        parcel.writeInterfaceToken("com.android.vending.licensing.ILicensingService");
        parcel.writeLong(param1Long);
        parcel.writeString(param1String);
        if (param1ILicenseResultListener != null) {
          IBinder iBinder = param1ILicenseResultListener.asBinder();
        } else {
          param1String = null;
        } 
        parcel.writeStrongBinder(param1String);
        this.mRemote.transact(1, parcel, null, 1);
        return;
      } finally {
        parcel.recycle();
      } 
    }
    
    public String getInterfaceDescriptor() { return "com.android.vending.licensing.ILicensingService"; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/ILicensingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */