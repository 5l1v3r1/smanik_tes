package com.android.volley;

import android.content.Intent;

public class AuthFailureError extends VolleyError {
  private Intent mResolutionIntent;
  
  public AuthFailureError() {}
  
  public AuthFailureError(Intent paramIntent) { this.mResolutionIntent = paramIntent; }
  
  public AuthFailureError(NetworkResponse paramNetworkResponse) { super(paramNetworkResponse); }
  
  public AuthFailureError(String paramString) { super(paramString); }
  
  public AuthFailureError(String paramString, Exception paramException) { super(paramString, paramException); }
  
  public String getMessage() { return (this.mResolutionIntent != null) ? "User needs to (re)enter credentials." : super.getMessage(); }
  
  public Intent getResolutionIntent() { return this.mResolutionIntent; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/AuthFailureError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */