package com.google.android.vending.licensing;

public class StrictPolicy implements Policy {
  private int mLastResponse = 291;
  
  public boolean allowAccess() { return (this.mLastResponse == 256); }
  
  public void processServerResponse(int paramInt, ResponseData paramResponseData) { this.mLastResponse = paramInt; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/StrictPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */