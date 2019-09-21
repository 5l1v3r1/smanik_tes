package com.google.android.vending.licensing;

public interface LicenseCheckerCallback {
  public static final int ERROR_CHECK_IN_PROGRESS = 4;
  
  public static final int ERROR_INVALID_PACKAGE_NAME = 1;
  
  public static final int ERROR_INVALID_PUBLIC_KEY = 5;
  
  public static final int ERROR_MISSING_PERMISSION = 6;
  
  public static final int ERROR_NON_MATCHING_UID = 2;
  
  public static final int ERROR_NOT_MARKET_MANAGED = 3;
  
  void allow(int paramInt);
  
  void applicationError(int paramInt);
  
  void dontAllow(int paramInt);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/LicenseCheckerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */