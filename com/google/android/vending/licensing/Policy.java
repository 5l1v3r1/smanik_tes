package com.google.android.vending.licensing;

public interface Policy {
  public static final int LICENSED = 256;
  
  public static final int NOT_LICENSED = 561;
  
  public static final int RETRY = 291;
  
  boolean allowAccess();
  
  void processServerResponse(int paramInt, ResponseData paramResponseData);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/Policy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */